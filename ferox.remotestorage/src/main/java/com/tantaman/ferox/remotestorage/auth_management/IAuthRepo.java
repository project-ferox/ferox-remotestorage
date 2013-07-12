package com.tantaman.ferox.remotestorage.auth_management;

import java.util.Set;

public interface IAuthRepo {
	public Set<String> getScopes(String bearerToken);
	public void addScopes(String beString, Set<String> scopes);
}
