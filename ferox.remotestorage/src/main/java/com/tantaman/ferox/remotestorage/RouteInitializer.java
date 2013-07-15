package com.tantaman.ferox.remotestorage;

import java.util.Map;

import com.tantaman.ferox.api.router.IRouteHandlerFactory;
import com.tantaman.ferox.api.router.IRouteInitializer;
import com.tantaman.ferox.api.router.IRouterBuilder;
import com.tantaman.ferox.remotestorage.auth_management.AuthManagerRouteInitializer;
import com.tantaman.ferox.remotestorage.auth_management.AuthorizationManager;
import com.tantaman.ferox.remotestorage.auth_management.IAuthRepo;
import com.tantaman.ferox.remotestorage.resource.IResourceProvider;
import com.tantaman.ferox.remotestorage.route_handlers.HandlerFactories;
import com.tantaman.ferox.remotestorage.webfinger.WebfingerMiddlewareInitializer;

// TODO: make sure this component has configuration-policy="required"
public class RouteInitializer implements IRouteInitializer {
	// TODO: look over the OSGi spec and see what kind of "happens-before" relationships 
	// get set up for statically bound services.
	private String storageRootUri;
	private IResourceProvider resourceProvider;
	private final AuthManagerRouteInitializer authManagerInit;
	private final AuthorizationManager authManager;
	private final WebfingerMiddlewareInitializer webfingerMiddlewareInit;
	
	public RouteInitializer() {
		authManager = new AuthorizationManager();
		authManagerInit = new AuthManagerRouteInitializer();
		webfingerMiddlewareInit = new WebfingerMiddlewareInitializer();
	}
	
	void setResourceProvider(IResourceProvider resourceProvider) {
		this.resourceProvider = resourceProvider;
	}
	
	void setScopeRepository(IAuthRepo scopeRepo) {
		authManager.setScopeRepository(scopeRepo);
	}
	
	void unsetScopeRepository() {
		authManager.unsetScopeRepository();
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
		IRouteHandlerFactory accessControl = HandlerFactories.accessControl(authManager);
		IRouteHandlerFactory read = HandlerFactories.read(resourceProvider);
		
		routerBuilder.get(route, identifierBuilderFactory);		
		routerBuilder.get(route, accessControl);
		routerBuilder.get(route, read);
		
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
		return 0;
	}

}
