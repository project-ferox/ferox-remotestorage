package com.tantaman.ferox.remotestorage.auth_manager.exposed.rest;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tantaman.ferox.api.router.IRouteHandler;
import com.tantaman.ferox.api.router.IRouteHandlerFactory;
import com.tantaman.ferox.api.router.IRouteInitializer;
import com.tantaman.ferox.api.router.IRouterBuilder;
import com.tantaman.ferox.remotestorage.auth_manager.IAuthManager;

public class AuthManagerRouteInitializer implements IRouteInitializer {
	private String authManagerRoot;
	private String authManagerPassword;
	private static final Logger log = LoggerFactory.getLogger(AuthManagerRouteInitializer.class);
	private IAuthManager authManager;
	
	public void activate(Map<String, String> configuration) {
		log.debug("Activated");
		authManagerRoot = configuration.get(ConfigKeys.AUTH_MANAGER_URI);
		authManagerPassword = configuration.get(ConfigKeys.AUTH_MANAGER_PASSWORD);
	}
	
	void setAuthManager(IAuthManager authManager) {
		this.authManager = authManager;
	}
	
	@Override
	public void addRoutes(IRouterBuilder routerBuilder) {
		if (authManagerRoot == null || authManagerPassword == null) {
			log.info("Auth manager not started due to missing authManagerRoot and authManagerPassword configuration variables");
			return;
		}
		
		IRouteHandlerFactory accessControl = new IRouteHandlerFactory() {
			@Override
			public IRouteHandler create() {
				return new RouteHandlers.AccessControl(authManagerPassword);
			}
		};
		
		String route = authManagerRoot + "/:user";
		
		routerBuilder.put(route, accessControl);
		routerBuilder.put(route, new IRouteHandlerFactory() {
			@Override
			public IRouteHandler create() {
				return new RouteHandlers.AddAuthorization(authManager);
			}
		});
		
		routerBuilder.delete(route, accessControl);
		routerBuilder.delete(route, new IRouteHandlerFactory() {
			@Override
			public IRouteHandler create() {
				return new RouteHandlers.RemoveAuthorization(authManager);
			}
		});
	}

	@Override
	public int getPriority() {
		return 0;
	}

}
