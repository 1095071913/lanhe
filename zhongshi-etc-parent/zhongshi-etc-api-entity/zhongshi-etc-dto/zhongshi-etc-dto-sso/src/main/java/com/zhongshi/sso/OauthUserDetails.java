package com.zhongshi.sso;

import java.util.Collection;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;

@Data
public class OauthUserDetails<T> extends User {

	private static final long serialVersionUID = 1L;
	
	private Map<String,Map<String,Object>> userInfos=Maps.newHashMap();
	
	public OauthUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities,Map<String,Map<String,Object>> userInfos) {
		
		super(username, password, authorities);
		this.userInfos=userInfos;
		
	}
	
	public OauthUserDetails() {
		super("null", "null",Lists.newCopyOnWriteArrayList());
	} 
	
	
}
