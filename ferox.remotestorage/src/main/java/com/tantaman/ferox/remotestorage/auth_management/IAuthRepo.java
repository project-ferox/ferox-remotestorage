package com.tantaman.ferox.remotestorage.auth_management;

import java.util.Set;

import com.tantaman.ferox.util.IPair;

public interface IAuthRepo {
	public IPair<String, Set<String>> getScopes(String bearerToken);
	public void addScopes(String bearerToken, String username, Set<String> scopes);
	public void removeScopes(String bearerToken, Set<String> scopes);
	public void revokeAccess(String bearerToken);
}
