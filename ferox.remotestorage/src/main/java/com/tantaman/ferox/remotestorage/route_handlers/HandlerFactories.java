package com.tantaman.ferox.remotestorage.route_handlers;

import com.tantaman.ferox.api.router.IRouteHandler;
import com.tantaman.ferox.api.router.IRouteHandlerFactory;
import com.tantaman.ferox.remotestorage.auth_management.AuthenticationManager;

public class HandlerFactories {
	
	public static IRouteHandlerFactory identifierBuilder(final String rootUri) {
		return new IRouteHandlerFactory() {
			
			@Override
			public IRouteHandler create() {
				return new IdentifierBuilderRouteHandler(rootUri);
			}
		};
	}
	
	public static IRouteHandlerFactory accessControl(final AuthenticationManager authRepo) {
		return new IRouteHandlerFactory() {
			@Override
			public IRouteHandler create() {
				return new AccessControlRouteHandler(authRepo);
			}
		};
	} 
	
	public static final IRouteHandlerFactory READ = new IRouteHandlerFactory() {
		@Override
		public IRouteHandler create() {
			return new ReadRouteHandler();
		}
	};
	
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
