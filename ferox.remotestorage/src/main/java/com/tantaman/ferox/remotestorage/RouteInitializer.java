package com.tantaman.ferox.remotestorage;

import java.util.Map;

import com.tantaman.ferox.api.request_response.IHttpContent;
import com.tantaman.ferox.api.request_response.IRequestChainer;
import com.tantaman.ferox.api.request_response.IResponse;
import com.tantaman.ferox.api.router.IRouteHandler;
import com.tantaman.ferox.api.router.IRouteHandlerFactory;
import com.tantaman.ferox.api.router.IRouteInitializer;
import com.tantaman.ferox.api.router.IRouterBuilder;
import com.tantaman.ferox.api.router.RouteHandlerAdapter;
import com.tantaman.ferox.remotestorage.auth_manager.IAuthManager;
import com.tantaman.ferox.remotestorage.resource.IResourceProvider;
import com.tantaman.ferox.remotestorage.route_handlers.HandlerFactories;
import com.tantaman.ferox.remotestorage.webfinger.WebfingerMiddlewareInitializer;

// TODO: make sure this component has configuration-policy="required"
public class RouteInitializer implements IRouteInitializer {
	// TODO: look over the OSGi spec and see what kind of "happens-before" relationships 
	// get set up for statically bound services.
	private String storageRootUri;
	private IResourceProvider resourceProvider;
	private IAuthManager authManager;
	private final WebfingerMiddlewareInitializer webfingerMiddlewareInit;
	
	public RouteInitializer() {
		webfingerMiddlewareInit = new WebfingerMiddlewareInitializer();
	}
	
	void setResourceProvider(IResourceProvider resourceProvider) {
		this.resourceProvider = resourceProvider;
	}
	
	void setAuthManager(IAuthManager authManager) {
		this.authManager = authManager;
	}
	
	public void activate(Map<String, String> configuration) {
		// configuration contains information such as the root of the routes.
		storageRootUri = configuration.get(ConfigKeys.STORAGE_ROOT_URI);
		webfingerMiddlewareInit.activate(configuration);
	}
	
	@Override
	public void addRoutes(IRouterBuilder routerBuilder) {
		String route = storageRootUri + "/:user/**";
		IRouteHandlerFactory identifierBuilderFactory = HandlerFactories.identifierBuilder(storageRootUri);
		IRouteHandlerFactory accessControl = HandlerFactories.accessControl(authManager);
		IRouteHandlerFactory read = HandlerFactories.read(resourceProvider);
		
//		routerBuilder.get("/.well-known/host-meta.json", new IRouteHandlerFactory() {
//			
//			@Override
//			public IRouteHandler create() {
//				return new RouteHandlerAdapter() {
//					@Override
//					public void lastContent(IHttpContent content,
//							IResponse response, IRequestChainer next) {
//					}
//				};
//			}
//		});
		
		routerBuilder.options(route, HandlerFactories.OPTIONS);
		
		routerBuilder.get(route, identifierBuilderFactory);		
		routerBuilder.get(route, accessControl);
		routerBuilder.get(route, read);
		
		routerBuilder.put(route, identifierBuilderFactory);		
		routerBuilder.put(route, accessControl);
		routerBuilder.put(route, HandlerFactories.upsert(resourceProvider));
		
		routerBuilder.delete(route, identifierBuilderFactory);		
		routerBuilder.delete(route, accessControl);
		routerBuilder.delete(route, HandlerFactories.DELETE);
		
		webfingerMiddlewareInit.addRoutes(routerBuilder);
	}

	@Override
	public int getPriority() {
		return 0;
	}

}
