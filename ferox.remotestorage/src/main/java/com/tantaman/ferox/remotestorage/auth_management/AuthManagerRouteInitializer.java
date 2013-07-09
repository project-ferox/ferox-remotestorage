package com.tantaman.ferox.remotestorage.auth_management;

import java.util.Map;

import com.tantaman.ferox.api.router.IRouteHandler;
import com.tantaman.ferox.api.router.IRouteHandlerFactory;
import com.tantaman.ferox.api.router.IRouteInitializer;
import com.tantaman.ferox.api.router.IRouterBuilder;
import com.tantaman.ferox.remotestorage.ConfigKeys;

public class AuthManagerRouteInitializer implements IRouteInitializer {
	private volatile String authManagerRoot;
	private volatile String authManagerPassword;
	
	public void activate(Map<String, String> configuration) {
		authManagerRoot = configuration.get(ConfigKeys.AUTH_MANAGER_URI);
		authManagerPassword = configuration.get(ConfigKeys.AUTH_MANAGER_PASSWORD);
	}
	
	@Override
	public void addRoutes(IRouterBuilder routerBuilder) {
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
				return new RouteHandlers.AddTokenHandler();
			}
		});
		
		routerBuilder.delete(route, accessControl);
		routerBuilder.delete(route, new IRouteHandlerFactory() {
			@Override
			public IRouteHandler create() {
				return new RouteHandlers.RemoveTokenHandler();
			}
		});
	}

	@Override
	public int getPriority() {
		return 0;
	}

}
