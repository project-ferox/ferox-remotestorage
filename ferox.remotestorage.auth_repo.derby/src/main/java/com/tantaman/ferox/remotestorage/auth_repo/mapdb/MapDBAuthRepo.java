package com.tantaman.ferox.remotestorage.auth_repo.mapdb;

import java.util.Map;
import java.util.Set;

import com.tantaman.ferox.remotestorage.auth_management.IAuthRepo;
import com.tantaman.ferox.util.IPair;

public class MapDBAuthRepo implements IAuthRepo {

	public void activate(Map<String, String> configuration) {
		
	}
	
	@Override
	public IPair<String, Set<String>> getScopes(String bearerToken) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addScopes(String bearerToken, String username,
			Set<String> scopes) {
		// TODO Auto-generated method stub
		
	}
}
