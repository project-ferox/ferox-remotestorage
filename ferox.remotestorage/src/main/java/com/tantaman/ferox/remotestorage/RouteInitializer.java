package com.tantaman.ferox.remotestorage;

import java.util.Map;

import com.tantaman.ferox.api.router.IRouteHandler;
import com.tantaman.ferox.api.router.IRouteHandlerFactory;
import com.tantaman.ferox.api.router.IRouteInitializer;
import com.tantaman.ferox.api.router.IRouterBuilder;
import com.tantaman.ferox.remotestorage.route_handlers.PrivateReadRouteHandler;
import com.tantaman.ferox.remotestorage.route_handlers.PublicReadRouteHandler;

// TODO: make sure this component has configuration-policy="required"
public class RouteInitializer implements IRouteInitializer {
	private volatile String storageRootUri;
	
	public void activate(Map<String, String> configuration) {
		// configuration contains information such as the root of the routes.
		storageRootUri = configuration.get("storageRootUri");
	}
	
	@Override
	public void addRoutes(IRouterBuilder routerBuilder) {
		routerBuilder.get(storageRootUri + "/:user/public/**", new IRouteHandlerFactory() {
			@Override
			public IRouteHandler create() {
				return new PublicReadRouteHandler();
			}
		});
		
		routerBuilder.get(storageRootUri + "/:user/**", new IRouteHandlerFactory() {
			@Override
			public IRouteHandler create() {
				return new PrivateReadRouteHandler();
			}
		});
	}

	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}

}
