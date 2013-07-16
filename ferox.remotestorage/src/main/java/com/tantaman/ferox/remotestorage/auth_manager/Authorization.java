package com.tantaman.ferox.remotestorage.auth_manager;

import java.util.Collections;
import java.util.Set;

public class Authorization {
	private final String accessToken;
	private final Set<String> scopes;
	private final String username;
	
	public Authorization(String accessToken, String username, Set<String> scopes) {
		this.scopes = scopes;
		this.accessToken = accessToken;
		this.username = username;
	}
	
	public String getAccessToken() {
		return accessToken;
	}
	
	public String getUsername() {
		return username;
	}
	
	public Set<String> getScopes() {
		return Collections.unmodifiableSet(scopes);
	}
}
