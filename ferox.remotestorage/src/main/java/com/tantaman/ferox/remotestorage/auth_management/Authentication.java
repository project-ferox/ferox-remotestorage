package com.tantaman.ferox.remotestorage.auth_management;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Authentication {
	private final String username;
	private final String accessToken;
	private final List<String> scopes;
	
	public Authentication(String username, String accessToken) {
		scopes = new CopyOnWriteArrayList<>();
		this.username = username;
		this.accessToken = accessToken;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getAccessToken() {
		return accessToken;
	}
	
	public List<String> getScopes() {
		return Collections.unmodifiableList(scopes);
	}
}
