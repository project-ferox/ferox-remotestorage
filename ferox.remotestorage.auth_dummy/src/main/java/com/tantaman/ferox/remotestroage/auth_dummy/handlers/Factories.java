package com.tantaman.ferox.remotestroage.auth_dummy.handlers;

import com.tantaman.ferox.api.router.IRouteHandler;
import com.tantaman.ferox.api.router.IRouteHandlerFactory;

public class Factories {
	public static IRouteHandlerFactory dialogGet(final String templateRoot) {
		return new IRouteHandlerFactory() {
			
			@Override
			public IRouteHandler create() {
				return new DialogGetHandler(templateRoot);
			}
		};
	}
	
	public static IRouteHandlerFactory dialogPost() {
		return new IRouteHandlerFactory() {
			
			@Override
			public IRouteHandler create() {
				return new DialogPostHandler();
			}
		};
	}
}
