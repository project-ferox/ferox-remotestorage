package com.tantaman.ferox.remotestorage.auth_management;

import io.netty.handler.codec.http.HttpMethod;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.tantaman.ferox.remotestorage.resource.IResourceIdentifier;
import com.tantaman.lo4j.Lo;


/**
 * Manages access tokens and scopes.
 * @author tantaman
 *
 */
public class AuthenticationManager {
	private static final ExecutorService AUTH_LOOKUP = Executors.newFixedThreadPool(1);
	
	public void addAuthentication(Authentication auth, Lo.Fn<Void, Void> callback) {
		
	}

	public void isAuthorized(final IResourceIdentifier resource,
			final String authorization, 
			final HttpMethod method,
			final Lo.VFn2<Boolean, Throwable> callback) {
		// TODO: convert parameters to:
		// 1. authorization
		// 2. requested scope
		// so resource identifier would have to get changed to a "scope"
		// authorization is fine as is.
		AUTH_LOOKUP.execute(new Runnable() {
			@Override
			public void run() {
				determineAuthorization(resource, authorization, method, callback);
			}
		});
	}
	
	private void determineAuthorization(final IResourceIdentifier resource,
			final String authorization, 
			final HttpMethod method,
			final Lo.VFn2<Boolean, Throwable> callback) {
		callback.f(false, null);
	}
}
