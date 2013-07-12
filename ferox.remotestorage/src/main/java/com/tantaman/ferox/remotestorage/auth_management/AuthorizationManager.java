package com.tantaman.ferox.remotestorage.auth_management;

import io.netty.handler.codec.http.HttpMethod;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tantaman.ferox.remotestorage.resource.IResourceIdentifier;
import com.tantaman.ferox.util.IPair;
import com.tantaman.lo4j.Lo;


/**
 * Manages access tokens and scopes.
 * @author tantaman
 *
 */
public class AuthorizationManager {
	private static final Logger log = LoggerFactory.getLogger(AuthorizationManager.class);
	private static final ExecutorService AUTH_LOOKUP = Executors.newFixedThreadPool(1);
	private IAuthRepo scopeRepository;
	
	public void addAuthorization(Authorization auth, Lo.Fn<Void, Void> callback) {		
		scopeRepository.addScopes(auth.getAccessToken(), auth.getUsername(), auth.getScopes());
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

		AUTH_LOOKUP.execute(new Runnable() {
			@Override
			public void run() {
				determineAuthorization(resource, authorization, method, callback);
			}
		});
	}
	
	private void determineAuthorization(final IResourceIdentifier resource,
			final String bearerToken, 
			final HttpMethod method,
			final Lo.VFn2<Boolean, Throwable> callback) {
		
		if (scopeRepository != null) {
			IPair<String, Set<String>> userAndScopes = scopeRepository.getScopes(bearerToken);
			
			if (!resource.getUser().equals(userAndScopes.getFirst())) {
				callback.f(false, null);
				return;
			}
			
			Set<String> scopes = userAndScopes.getSecond();
			
			if (method == HttpMethod.GET) {
				if (hasModuleReadAccess(scopes, resource.getModule())) {
					callback.f(true, null);
					return;
				}
			} else if (method == HttpMethod.DELETE || method == HttpMethod.PUT) {
				if (hasModuleWriteAccess(scopes, resource.getModule())) {
					callback.f(true, null);
					return;
				}
			} else {
				log.warn("Unknown http verb received");
				callback.f(false, null);
				return;
			}
		} else {
			log.warn("No authorization repo");
			callback.f(false, null);
			return;
		}
		
		callback.f(false, null);
	}
	
	private boolean hasRootReadWriteAccess(Set<String> scopes) {
		return scopes.contains("root:rw");
	}
	
	private boolean hasRootReadAccess(Set<String> scopes) {
		return scopes.contains("root:r");
	}
	
	private boolean hasRootWriteAccess(Set<String> scopes) {
		return scopes.contains("root:w");
	}
	
	private boolean hasModuleReadAccess(Set<String> scopes, String module) {
		return hasRootReadWriteAccess(scopes) || hasRootReadAccess(scopes) || scopes.contains(module + ":r") || scopes.contains(module + ":rw");
	}
	
	private boolean hasModuleWriteAccess(Set<String> scopes, String module) {
		return hasRootReadWriteAccess(scopes) || hasRootWriteAccess(scopes) || scopes.contains(module + ":w") || scopes.contains(module + ":rw");
	}
}
