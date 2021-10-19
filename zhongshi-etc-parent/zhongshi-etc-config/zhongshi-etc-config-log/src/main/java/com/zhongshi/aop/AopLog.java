
/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.zhongshi.aop;

import java.util.Arrays;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.apache.dubbo.rpc.RpcContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.node.Node;
import com.zhongshi.log.LogUtils;
import com.zhongshi.factory.BaseResultFactory;
import com.zhongshi.factory.result.AbstractBaseResult;
import com.zhongshi.factory.result.error.ErrorResult;
import com.zhongshi.factory.result.error.exception.BusinessResultException;
import com.zhongshi.sso.OauthUserDetails;

/**
 * 功能说明：日志收集
 * <p>
 * 功能作者：彭晋龙 ( 联系方式QQ/微信：1095071913 )
 * <p>
 * 创建日期：2019-08-03 ：1:32:00
 * <p>
 * 版权归属：蓝河团队
 * <p>
 * 协议说明：Apache2.0（ 文件顶端 ）
 */ 

@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE+1)
public class AopLog extends BaseResultFactory {
	
	
	@Resource
	private LogUtils logUtils;

	
	private final static String LogPonit = "execution(com.zhongshi.factory.result.AbstractBaseResult com.zhongshi.*.api.impl..*Rpc*.*(..)) || execution(com.zhongshi.factory.result.AbstractBaseResult com.zhongshi.*.api.impl..*Rest*.*(..))";

    @Around(LogPonit) 
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable { 
    	
    	Long startTime = System.currentTimeMillis();
    	
    	String tid = BaseResultFactory.getTraceId();
    	
    	HttpServletRequest request = getRequest();
    	
    	RpcContext rpcContext = RpcContext.getContext(); 
    	
    	String rpcUrl = rpcContext.getRemoteHost();
    	
    	String arg = Arrays.toString(proceedingJoinPoint.getArgs()); 
    	
    	OauthUserDetails oauthUserDetails = getOauthUserDetails();
    	
    	Node curNode = ContextUtil.getContext().getCurNode();
        
    	Map<String, String> logs = logUtils.logRequest(proceedingJoinPoint, request, rpcUrl);
    	
        functionParam(arg);
        
        
        
        Long returnDate = null;
        AbstractBaseResult<?> resultData = null;
        
        
        
        
        try {
        	
        	resultData = (AbstractBaseResult<?>) proceedingJoinPoint.proceed();
        	
        }catch (BusinessResultException businessResultException) {
        	
        	resultData = businessResultException.getErrorResult();
        	
    	}catch (Throwable e) {

        	String stackTrace = getStackTrace(e);
        	
        	StackTraceElement stackTraceElement = stackTraceElement = e.getStackTrace()[0];
        	
            resultData = error(code(500),500);
            
            functionError(stackTrace);
            
            log.error(stackTrace);
            
            logs.put("errorParam", arg);
            
            logs.put("errorUser",(isNull(oauthUserDetails) ? "游客":oauthUserDetails.getUserInfos().toString()));
            
            logs.put("errorDesc", e.getLocalizedMessage());
            
            logs.put("errorLine", stackTraceElement.toString());
            
        } finally {
        	
        	returnDate = (System.currentTimeMillis() - startTime);
        	
            logs.put("respSql", sql.get());
        	logs.put("respTime", returnDate + " ms");
            
            functionReturn(resultData); 
            
            if(!resultData.isSuccess()) { 
            	
            	curNode.increaseBlockQps(1);
            	
            	ErrorResult errorResult=resultData.getResult();
            	
            	if(errorResult.autoIdentifyHttpCode().ifBusinessError()) {log.warn(appendLog(logs).toString());}
            	
            	else {log.error(appendLog(logs).toString());}
            	
            	if(resultData.getCode()==500) {logUtils.errorLogAlarm(proceedingJoinPoint,arg,tid,logs);}
            	
            	if(isNull(request)) {clean();}
            	
            	return resultData;
            
            }else { 
            	
            	curNode.addPassRequest(1); 
            	
            	log.info(appendLog(logs).toString());  
            	
            }   
        }
        
        if(isNull(request)) {clean();}
        
        return resultData;
    
    }
    

}
