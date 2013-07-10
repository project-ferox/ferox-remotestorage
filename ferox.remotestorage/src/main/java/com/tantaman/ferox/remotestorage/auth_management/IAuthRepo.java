package com.tantaman.ferox.remotestorage.auth_management;

import java.util.List;

public interface IAuthRepo {
	public List<String> getScopes(String bearerToken);
}
