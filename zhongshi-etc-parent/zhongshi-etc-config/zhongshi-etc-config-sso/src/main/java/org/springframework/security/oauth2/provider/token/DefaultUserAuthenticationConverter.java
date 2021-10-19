/*
 * Cloud Foundry 2012.02.03 Beta
 * Copyright (c) [2009-2012] VMware, Inc. All Rights Reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0 (the "License").
 * You may not use this product except in compliance with the License.
 *
 * This product includes a number of subcomponents with
 * separate copyright notices and license terms. Your use of these
 * subcomponents is subject to the terms and conditions of the
 * subcomponent's license, as noted in the LICENSE file.
 */

package org.springframework.security.oauth2.provider.token;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import com.google.common.collect.Maps;
import com.zhongshi.sso.OauthUserDetails;

/**
 * Default implementation of {@link UserAuthenticationConverter}. Converts to and from an Authentication using only its
 * name and authorities.
 * 
 * @author Dave Syer
 * 
 */

public class DefaultUserAuthenticationConverter implements UserAuthenticationConverter {

	private Collection<? extends GrantedAuthority> defaultAuthorities;

	private UserDetailsService userDetailsService;

	/**
	 * Optional {@link UserDetailsService} to use when extracting an {@link Authentication} from the incoming map.
	 * 
	 * @param userDetailsService the userDetailsService to set
	 */
	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	/**
	 * Default value for authorities if an Authentication is being created and the input has no data for authorities.
	 * Note that unless this property is set, the default Authentication created by {@link #extractAuthentication(Map)}
	 * will be unauthenticated.
	 * 
	 * @param defaultAuthorities the defaultAuthorities to set. Default null.
	 */
	public void setDefaultAuthorities(String[] defaultAuthorities) {
		this.defaultAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(StringUtils
				.arrayToCommaDelimitedString(defaultAuthorities));
	}

	public Map<String, ?> convertUserAuthentication(Authentication authentication) {
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		response.put(USERNAME, authentication.getName());
		if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
			response.put(AUTHORITIES, AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
		}
		return response;
	}
	
	public Authentication extractAuthentication(Map<String, ?> map) {
		
		if (map.containsKey(USERNAME)) {
			String username = (String) map.get(USERNAME);
			Collection<? extends GrantedAuthority> authorities = getAuthorities(map);
			Map<String, Map<String,Object>> users = Maps.newHashMap();
			if(map.containsKey("user_infos")) {
				users=(Map<String, Map<String,Object>>) map.get("user_infos");
			}else {
				Map<String, Object> ssoUserInfoMap = Maps.newHashMap();
				ssoUserInfoMap.put("username", username);
				users.put("com.zhongshi.user.SsoUserInfo",ssoUserInfoMap); 
			}
			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(new OauthUserDetails(username,"",authorities,users), null, authorities);
			usernamePasswordAuthenticationToken.setDetails(map.get("client_id"));
			return usernamePasswordAuthenticationToken;
		}
		return null;
	}

	private Collection<? extends GrantedAuthority> getAuthorities(Map<String, ?> map) {
		if (!map.containsKey(AUTHORITIES)) {
			return defaultAuthorities;
		}
		Object authorities = map.get(AUTHORITIES);
		if (authorities instanceof String) {
			return AuthorityUtils.commaSeparatedStringToAuthorityList((String) authorities);
		}
		if (authorities instanceof Collection) {
			return AuthorityUtils.commaSeparatedStringToAuthorityList(StringUtils
					.collectionToCommaDelimitedString((Collection<?>) authorities));
		}
		throw new IllegalArgumentException("Authorities must be either a String or a Collection");
	}
}
