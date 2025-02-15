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

package com.maozi.bd.api.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.maozi.base.CodeData;
import com.maozi.base.error.code.SystemErrorCode;
import com.maozi.bd.api.BDService;
import com.maozi.bd.properties.BDProperties;
import com.maozi.common.BaseCommon;
import com.maozi.common.result.error.exception.BusinessResultException;
import com.maozi.mvc.config.rest.RestTemplate;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;



public class BDServiceImpl extends BaseCommon<SystemErrorCode> implements BDService{

	@Resource
	protected BDProperties bdProperties;
	
	@Resource
	private RestTemplate restClient;
	
	@Override
	public JSONObject bdRest(String uri,Map<String,Object> privateParam,HttpMethod method) {
		
		if(isNull(privateParam)) {
			privateParam = Maps.newHashMap();
		}
		
		privateParam.put("ak", bdProperties.getAk());
		
		ResponseEntity<String> bdResult = null;
		if(HttpMethod.GET.equals(method)) {
			
			bdResult = restClient.getForEntity(bdProperties.getUrl()+uri+"&ak={ak}&output=json", String.class,privateParam);
			
		}else {
			
			HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<Map<String, Object>>(privateParam, new HttpHeaders());
			bdResult = restClient.postForEntity(bdProperties.getUrl()+uri,requestEntity,String.class);
			
		}
		
		JSONObject response = JSONObject.parseObject(bdResult.getBody());
		
		if(isNull(bdResult)) {
			throw new BusinessResultException(new CodeData(500,"百度服务不可用",500));
		}
		
		if(isNull(bdResult) || bdResult.getStatusCodeValue() != 200 || response.getInteger("status")!=0) { 
			throw new BusinessResultException(new CodeData(response.getInteger("status"),response.getString("message")),bdResult.getStatusCodeValue());
		}
		
		return response; 
		
	}
	
}
