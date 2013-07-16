package com.tantaman.ferox.remotestorage.route_handlers;

import com.tantaman.ferox.api.router.IRouteHandler;
import com.tantaman.ferox.api.router.IRouteHandlerFactory;
import com.tantaman.ferox.remotestorage.auth_manager.AuthorizationManager;
import com.tantaman.ferox.remotestorage.auth_manager.IAuthManager;
import com.tantaman.ferox.remotestorage.resource.IResourceProvider;

public class HandlerFactories {
	
	public static IRouteHandlerFactory identifierBuilder(final String rootUri) {
		return new IRouteHandlerFactory() {
			
			@Override
			public IRouteHandler create() {
				return new IdentifierBuilderRouteHandler(rootUri);
			}
		};
	}
	
	public static IRouteHandlerFactory accessControl(final IAuthManager authManager) {
		return new IRouteHandlerFactory() {
			@Override
			public IRouteHandler create() {
				return new AccessControlRouteHandler(authManager);
			}
		};
	}
	
	public static IRouteHandlerFactory read(final IResourceProvider resourceProvider) {
		return new IRouteHandlerFactory() {
			
			@Override
			public IRouteHandler create() {
				return new ReadRouteHandler(resourceProvider);
			}
		};
	}
	
	public static final IRouteHandlerFactory UPSERT =  new IRouteHandlerFactory() {
		@Override
		public IRouteHandler create() {
			return new UpsertRouteHandler();
		}
	};
	
	public static final IRouteHandlerFactory DELETE = new IRouteHandlerFactory() {
		@Override
		public IRouteHandler create() {
			return null;
		}
	};
}
