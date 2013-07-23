package com.tantaman.ferox.remotestorage.accounts_site;

import java.util.Map;

import com.tantaman.ferox.api.router.IRouteInitializer;
import com.tantaman.ferox.api.router.IRouterBuilder;
import com.tantaman.ferox.remotestorage.accounts_site.auth.UserRepo;
import com.tantaman.ferox.remotestorage.accounts_site.handlers.Factories;
import com.tantaman.ferox.remotestorage.auth_manager.IAuthManager;
import com.tantaman.ferox.route_middelware.RouteMiddleware;

public class RouteInitializer implements IRouteInitializer {
	private String uriRoot;
	private String staticFsRoot;
	private String templateFsRoot;
	private final UserRepo userRepo;
	private IAuthManager authManager;
	
	public RouteInitializer() {
		userRepo = new UserRepo();
	}
	
	public void activate(Map<String, String> configuration) {
		uriRoot = configuration.get(ConfigKeys.URI_ROOT);
		staticFsRoot = configuration.get(ConfigKeys.STATIC_FS_ROOT);
		templateFsRoot = configuration.get(ConfigKeys.TEMPLATE_FS_ROOT);
		userRepo.activate(configuration);
	}
	
	void setAuthManager(IAuthManager authManager) {
		this.authManager = authManager;
	}
	
	public void deactivate() {
		userRepo.deactivate();
	}
	
	@Override
	public void addRoutes(IRouterBuilder routerBuilder) {
		routerBuilder.get(uriRoot + "/static/**", RouteMiddleware.staticContent(staticFsRoot));
		
		routerBuilder.get(uriRoot + "/dialog", Factories.loginPage(templateFsRoot));
		routerBuilder.get(uriRoot + "/registrations", Factories.registrationPage(templateFsRoot));
		
		routerBuilder.post(uriRoot + "/dialog", RouteMiddleware.bodyParser());
		routerBuilder.post(uriRoot + "/dialog", Factories.authenticate(userRepo, authManager));
		
		routerBuilder.post(uriRoot + "/registrations", RouteMiddleware.bodyParser());
		routerBuilder.post(uriRoot + "/registrations", Factories.createUser(userRepo));
		
//		routerBuilder.get(uriRoot + "/registrations/clear", Factories.clearUsers(userRepo));
	}

	@Override
	public int getPriority() {
		return 0;
	}
}
