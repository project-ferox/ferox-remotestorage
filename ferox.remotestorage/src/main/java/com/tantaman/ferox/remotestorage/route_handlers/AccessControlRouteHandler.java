package com.tantaman.ferox.remotestorage.route_handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;

import com.tantaman.ferox.api.request_response.IHttpContent;
import com.tantaman.ferox.api.request_response.IHttpRequest;
import com.tantaman.ferox.api.request_response.IRequestChainer;
import com.tantaman.ferox.api.request_response.IResponse;
import com.tantaman.ferox.api.router.IRouteHandler;
import com.tantaman.ferox.remotestorage.auth_management.AuthorizationManager;
import com.tantaman.ferox.remotestorage.resource.IResourceIdentifier;
import com.tantaman.lo4j.Lo;

public class AccessControlRouteHandler implements IRouteHandler {
	private static final Logger log = LoggerFactory.getLogger(AccessControlRouteHandler.class);
	
	private final AuthorizationManager authRepo;
	private boolean authorized = false;
	
	public AccessControlRouteHandler(AuthorizationManager authRepo) {
		this.authRepo = authRepo;
	}
	
	@Override
	public void request(final IHttpRequest request, final IResponse response,
			final IRequestChainer next) {
		IResourceIdentifier resourceIdentifier = response.getUserData();
		
		authRepo.isAuthorized(resourceIdentifier,
				request.getHeaders().get(HttpHeaders.Names.AUTHORIZATION),
				request.getMethod(),
				new Lo.VFn2<Boolean, Throwable>() {
					@Override
					public void f(Boolean authorized, Throwable err) {
						if (authorized != null && authorized) {
							AccessControlRouteHandler.this.authorized = true;
							next.request(request);
						} else {
							log.debug("Authorization failed");
							response.send("{\"status\": \"unauthorized\"}", "application/json", HttpResponseStatus.UNAUTHORIZED)
							.addListener(ChannelFutureListener.CLOSE);
							request.dispose();
						}
					}
				});
	}

	@Override
	public void content(IHttpContent content, IResponse response,
			IRequestChainer next) {
		if (authorized) next.content(content);
		else content.dispose();
	}

	@Override
	public void lastContent(IHttpContent content, IResponse response,
			IRequestChainer next) {
		if (authorized) next.lastContent(content);
		else content.dispose();
	}
}
