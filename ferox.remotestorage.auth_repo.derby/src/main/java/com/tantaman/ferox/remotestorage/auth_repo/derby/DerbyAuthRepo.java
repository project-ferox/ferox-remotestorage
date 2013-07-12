package com.tantaman.ferox.remotestorage.auth_repo.derby;

import java.util.Set;

import com.tantaman.ferox.remotestorage.auth_management.IAuthRepo;

public class DerbyAuthRepo implements IAuthRepo {

	@Override
	public Set<String> getScopes(String bearerToken) {
		return null;
	}

	@Override
	public void addScopes(String beString, Set<String> scopes) {
		
	}
}
