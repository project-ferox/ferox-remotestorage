package com.tantaman.ferox.remotestorage.auth_management;

import java.util.Collections;
import java.util.Set;

public class Authorization {
	private final String accessToken;
	private final Set<String> scopes;
	
	public Authorization(String accessToken, Set<String> scopes) {
		this.scopes = scopes;
		this.accessToken = accessToken;
	}
	
	public String getAccessToken() {
		return accessToken;
	}
	
	public Set<String> getScopes() {
		return Collections.unmodifiableSet(scopes);
	}
}
