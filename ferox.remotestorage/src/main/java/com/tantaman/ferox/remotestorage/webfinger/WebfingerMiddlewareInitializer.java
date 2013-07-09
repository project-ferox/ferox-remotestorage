package com.tantaman.ferox.remotestorage.webfinger;

import java.util.Map;

import com.tantaman.ferox.api.router.IRouteHandler;
import com.tantaman.ferox.api.router.IRouteHandlerFactory;
import com.tantaman.ferox.api.router.IRouteInitializer;
import com.tantaman.ferox.api.router.IRouterBuilder;

public class WebfingerMiddlewareInitializer implements IRouteInitializer {
	private volatile String storageRootUri;
	private volatile String authDialog;
	
	public void activate(Map<String, String> configuration) {
		storageRootUri = configuration.get("storageRootUri");
		authDialog = configuration.get("authDialog");
	}
	
	@Override
	public void addRoutes(IRouterBuilder routerBuilder) {
		routerBuilder.get("/.well-known/webfinger", new IRouteHandlerFactory() {
			@Override
			public IRouteHandler create() {
				return new WebfingerUpdater(storageRootUri, authDialog);
			}
		});
	}

	@Override
	public int getPriority() {
		return 0;
	}

}
