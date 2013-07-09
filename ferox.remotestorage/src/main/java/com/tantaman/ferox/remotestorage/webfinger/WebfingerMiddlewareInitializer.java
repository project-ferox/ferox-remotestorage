package com.tantaman.ferox.remotestorage.webfinger;

import com.tantaman.ferox.api.router.IRouteInitializer;
import com.tantaman.ferox.api.router.IRouterBuilder;

public class WebfingerMiddlewareInitializer implements IRouteInitializer {

	@Override
	public void addRoutes(IRouterBuilder routerBuilder) {
		
	}

	@Override
	public int getPriority() {
		return 0;
	}

}
