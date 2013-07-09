package com.tantaman.ferox.remotestorage.webfinger;

import java.util.Map;

import com.tantaman.ferox.api.router.IRouteHandler;
import com.tantaman.ferox.api.router.IRouteHandlerFactory;
import com.tantaman.ferox.api.router.IRouteInitializer;
import com.tantaman.ferox.api.router.IRouterBuilder;
import com.tantaman.ferox.remotestorage.ConfigKeys;

public class WebfingerMiddlewareInitializer implements IRouteInitializer {
	private volatile String storageRootUri;
	private volatile String authDialog;
	
	public void activate(Map<String, String> configuration) {
		storageRootUri = configuration.get(ConfigKeys.STORAGE_ROOT_URI);
		authDialog = configuration.get(ConfigKeys.AUTH_DIALOG_URI);
	}
	
	@Override
	public void addRoutes(IRouterBuilder routerBuilder) {
		routerBuilder.get("/.well-known/webfinger", new IRouteHandlerFactory() {
			@Override
			public IRouteHandler create() {
				return new WebfingerUpdater(storageRootUri, authDialog);
			}
		}, 20);
	}

	@Override
	public int getPriority() {
		return 0;
	}
}
