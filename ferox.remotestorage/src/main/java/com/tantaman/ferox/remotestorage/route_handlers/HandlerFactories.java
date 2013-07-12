package com.tantaman.ferox.remotestorage.route_handlers;

import com.tantaman.ferox.api.router.IRouteHandler;
import com.tantaman.ferox.api.router.IRouteHandlerFactory;
import com.tantaman.ferox.remotestorage.auth_management.AuthorizationManager;
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
	
	public static IRouteHandlerFactory accessControl(final AuthorizationManager authRepo) {
		return new IRouteHandlerFactory() {
			@Override
			public IRouteHandler create() {
				return new AccessControlRouteHandler(authRepo);
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
