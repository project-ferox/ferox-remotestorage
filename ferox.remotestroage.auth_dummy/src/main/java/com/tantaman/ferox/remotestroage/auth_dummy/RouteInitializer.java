package com.tantaman.ferox.remotestroage.auth_dummy;

import java.util.Map;

import com.tantaman.ferox.api.router.IRouteInitializer;
import com.tantaman.ferox.api.router.IRouterBuilder;
import com.tantaman.ferox.remotestroage.auth_dummy.handlers.Factories;
import com.tantaman.ferox.route_middelware.RouteMiddleware;

public class RouteInitializer implements IRouteInitializer {
	private String uriRoot;
	private String staticFsRoot;
	private String templateFsRoot;
	
	public void activate(Map<String, String> configuration) {
		uriRoot = configuration.get(ConfigKeys.URI_ROOT);
		staticFsRoot = configuration.get(ConfigKeys.STATIC_FS_ROOT);
		templateFsRoot = configuration.get(ConfigKeys.TEMPLATE_FS_ROOT);
	}
	
	@Override
	public void addRoutes(IRouterBuilder routerBuilder) {
		routerBuilder.get(uriRoot + "/static/**", RouteMiddleware.staticContent(staticFsRoot));
		
		routerBuilder.get(uriRoot + "/dialog", Factories.dialogGet(templateFsRoot));
		routerBuilder.post(uriRoot + "/dialog", Factories.dialogPost());
	}

	@Override
	public int getPriority() {
		return 0;
	}
}
