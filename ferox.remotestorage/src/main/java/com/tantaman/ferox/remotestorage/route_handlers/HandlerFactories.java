package com.tantaman.ferox.remotestorage.route_handlers;

import io.netty.handler.codec.http.HttpHeaders;

import com.tantaman.ferox.api.request_response.IHttpContent;
import com.tantaman.ferox.api.request_response.IRequestChainer;
import com.tantaman.ferox.api.request_response.IResponse;
import com.tantaman.ferox.api.router.IRouteHandler;
import com.tantaman.ferox.api.router.IRouteHandlerFactory;
import com.tantaman.ferox.api.router.RouteHandlerAdapter;
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

//	Access-Control-Allow-Origin: http://hello-world.example
//	Access-Control-Max-Age: 3628800
//	Access-Control-Allow-Methods: PUT, DELETE
	public static final IRouteHandlerFactory OPTIONS = new IRouteHandlerFactory() {
		@Override
		public IRouteHandler create() {
			return new RouteHandlerAdapter() {
				@Override
				public void lastContent(IHttpContent content,
						IResponse response, IRequestChainer next) {
					response.headers().add(HttpHeaders.Names.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
					response.headers().add(HttpHeaders.Names.ACCESS_CONTROL_MAX_AGE, "172800");
					response.headers().add(HttpHeaders.Names.ACCESS_CONTROL_ALLOW_METHODS, "GET, PUT, DELETE");
					response.headers().add(HttpHeaders.Names.ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type, Authorization, Origin");
					
					response.send("");
				}
			};
		}
	};
}
