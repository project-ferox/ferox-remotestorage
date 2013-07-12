package com.tantaman.ferox.remotestorage.webfinger;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tantaman.ferox.api.router.IRouteHandler;
import com.tantaman.ferox.api.router.IRouteHandlerFactory;
import com.tantaman.ferox.api.router.IRouteInitializer;
import com.tantaman.ferox.api.router.IRouterBuilder;
import com.tantaman.ferox.remotestorage.ConfigKeys;

public class WebfingerMiddlewareInitializer implements IRouteInitializer {
	private String storageRootUri;
	private String authDialog;
	private static final Logger log = LoggerFactory.getLogger(WebfingerMiddlewareInitializer.class);
	
	public void activate(Map<String, String> configuration) {
		log.debug("Activated webfinger mw initializer");
		storageRootUri = configuration.get(ConfigKeys.STORAGE_ROOT_URI);
		authDialog = configuration.get(ConfigKeys.AUTH_DIALOG_URI);
	}
	
	@Override
	public void addRoutes(IRouterBuilder routerBuilder) {
		log.debug("Adding webfinger mw routes");
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
