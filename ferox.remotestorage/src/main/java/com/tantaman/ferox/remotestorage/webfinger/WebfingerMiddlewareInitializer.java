package com.tantaman.ferox.remotestorage.webfinger;

import com.tantaman.ferox.api.router.IRouteInitializer;
import com.tantaman.ferox.api.router.IRouterBuilder;

public class WebfingerMiddlewareInitializer implements IRouteInitializer {

	@Override
	public void addRoutes(IRouterBuilder routerBuilder) {
		// TODO: Split webfinger impl. up into two sets of handlers.
		// one to get the initial identities that sits at the start of the pipeline
		// another to send the final data that sits at the end of the pipeline
	}

	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}

}
