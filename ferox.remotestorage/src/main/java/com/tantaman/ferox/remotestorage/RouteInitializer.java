package com.tantaman.ferox.remotestorage;

import java.util.Map;

import com.tantaman.ferox.api.router.IRouteHandlerFactory;
import com.tantaman.ferox.api.router.IRouteInitializer;
import com.tantaman.ferox.api.router.IRouterBuilder;
import com.tantaman.ferox.remotestorage.auth_management.AuthManagerRouteInitializer;
import com.tantaman.ferox.remotestorage.auth_management.AuthenticationRepository;
import com.tantaman.ferox.remotestorage.route_handlers.HandlerFactories;
import com.tantaman.ferox.remotestorage.webfinger.WebfingerMiddlewareInitializer;

// TODO: make sure this component has configuration-policy="required"
public class RouteInitializer implements IRouteInitializer {
	private volatile String storageRootUri;
	private final AuthManagerRouteInitializer authManagerInit;
	private final AuthenticationRepository authRepository;
	private final WebfingerMiddlewareInitializer webfingerMiddlewareInit;
	
	public RouteInitializer() {
		authRepository = new AuthenticationRepository();
		authManagerInit = new AuthManagerRouteInitializer();
		webfingerMiddlewareInit = new WebfingerMiddlewareInitializer();
	}
	
	public void activate(Map<String, String> configuration) {
		// configuration contains information such as the root of the routes.
		storageRootUri = configuration.get(ConfigKeys.STORAGE_ROOT_URI);
		authManagerInit.activate(configuration);
		webfingerMiddlewareInit.activate(configuration);
	}
	
	@Override
	public void addRoutes(IRouterBuilder routerBuilder) {
		String route = storageRootUri + "/:user/**";
		IRouteHandlerFactory identifierBuilderFactory = HandlerFactories.identifierBuilder(storageRootUri);
		IRouteHandlerFactory accessControl = HandlerFactories.accessControl(authRepository);
		routerBuilder.get(route, identifierBuilderFactory);		
		routerBuilder.get(route, accessControl);
		routerBuilder.get(route, HandlerFactories.READ);
		
		routerBuilder.put(route, identifierBuilderFactory);		
		routerBuilder.put(route, accessControl);
		routerBuilder.put(route, HandlerFactories.UPSERT);
		
		routerBuilder.delete(route, identifierBuilderFactory);		
		routerBuilder.delete(route, accessControl);
		routerBuilder.delete(route, HandlerFactories.DELETE);
		
		authManagerInit.addRoutes(routerBuilder);
		webfingerMiddlewareInit.addRoutes(routerBuilder);
	}

	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}

}
