package com.tantaman.ferox.remotestorage.auth_manager;

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
public class AuthorizationManager implements IAuthManager {
	private static final Logger log = LoggerFactory.getLogger(AuthorizationManager.class);
	private static final ExecutorService AUTH_LOOKUP = Executors.newFixedThreadPool(1);
	private volatile IAuthRepo scopeRepository; // TODO: may need to be AtomicReference?  Could it be getting set and unset at the same time and require a CAS? does it matter?
	
	public void addAuthorization(Authorization auth, Lo.Fn<Void, Void> callback) {
		IAuthRepo scopeRepository = this.scopeRepository;
		if (scopeRepository != null)
			scopeRepository.addScopes(auth.getAccessToken(), auth.getUsername(), auth.getScopes());
	}
	
	public void removeAuthorization(Authorization auth) {
		IAuthRepo scopeRepository = this.scopeRepository;
		if (scopeRepository != null)
			scopeRepository.removeScopes(auth.getAccessToken(), auth.getScopes());
	}
	
	public void revokeAuthorization(Authorization auth) {
		IAuthRepo scopeRepository = this.scopeRepository;
		if (scopeRepository != null)
			scopeRepository.revokeAccess(auth.getAccessToken());
	}
	
	public void setScopeRepository(IAuthRepo scopeRepository) {
		log.debug("Scope repository set " + this);
		this.scopeRepository = scopeRepository;
	}
	
	public void unsetScopeRepository() {
		log.debug("Scope repository unset");
		this.scopeRepository = null;
	}

	public void isAuthorized(final IResourceIdentifier resource,
			final String authorization, 
			final HttpMethod method,
			final Lo.VFn2<Boolean, Throwable> callback) {
		if (resource.isPublic() && method == HttpMethod.GET) {
			callback.f(true, null);
			return;
		}
		
		String temp = "";
		try {
			temp = authorization.split(" ")[1];
		} catch (Exception e) {
			callback.f(false, null);
			return;
		}
		
		final String bearerToken = temp;
		AUTH_LOOKUP.execute(new Runnable() {
			@Override
			public void run() {
				determineAuthorization(resource, bearerToken, method, callback);
			}
		});
	}
	
	private void determineAuthorization(final IResourceIdentifier resource,
			final String bearerToken, 
			final HttpMethod method,
			final Lo.VFn2<Boolean, Throwable> callback) {		
		IAuthRepo scopeRepository = this.scopeRepository;
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
			log.warn("No authorization repo " + this);
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
