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
	private IAuthRepo scopeRepository;
	
	public void addAuthentication(Authentication auth, Lo.Fn<Void, Void> callback) {
		
	}
	
	void setScopeRepository(IAuthRepo scopeRepository) {
		this.scopeRepository = scopeRepository;
	}

	public void isAuthorized(final IResourceIdentifier resource,
			final String authorization, 
			final HttpMethod method,
			final Lo.VFn2<Boolean, Throwable> callback) {
		if (resource.isPublic() && method == HttpMethod.GET) {
			callback.f(true, null);
			return;
		}
		
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
		// use the bearer token
		// to look up scopes
		// Set<Scopes> scopes = scopeRepository.getScopes(authorization);
		// Scope requestScope = new Scope(resource, method);
		// callback.f(scopes.contains(requestScope), null);
		
		callback.f(false, null);
	}
}
