package com.tantaman.ferox.remotestorage.auth_management;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.tantaman.lo4j.Lo;


/**
 * Manages access tokens and scopes.
 * @author tantaman
 *
 */
public class AuthenticationRepository {
	private static final ExecutorService AUTH_LOOKUP = Executors.newFixedThreadPool(1);
	
	public void getAuthentication(String username, Lo.Fn<Void, Authentication> callback) {
	}
	
	public void addAuthentication(Authentication auth, Lo.Fn<Void, Void> callback) {
		
	}
}
