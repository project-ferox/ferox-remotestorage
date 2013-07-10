package com.tantaman.ferox.remotestorage.auth_repo.derby;

import java.util.List;

import com.tantaman.ferox.remotestorage.auth_management.IAuthRepo;

public class DerbyAuthRepo implements IAuthRepo {

	@Override
	public List<String> getScopes(String bearerToken) {
		return null;
	}

}
