package com.tantaman.ferox.remotestorage.auth_manager;

import io.netty.handler.codec.http.HttpMethod;

import com.tantaman.ferox.remotestorage.resource.IResourceIdentifier;
import com.tantaman.lo4j.Lo;
import com.tantaman.lo4j.Lo.VFn2;

public interface IAuthManager {
	void isAuthorized(IResourceIdentifier resourceIdentifier, String string,
			HttpMethod method, VFn2<Boolean, Throwable> vFn2);

	public void addAuthorization(Authorization auth, Lo.Fn<Void, Void> callback);
	public void removeAuthorization(Authorization auth);
	public void revokeAuthorization(Authorization auth);
}
