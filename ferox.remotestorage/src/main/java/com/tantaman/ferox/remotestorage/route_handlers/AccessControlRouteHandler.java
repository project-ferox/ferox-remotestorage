package com.tantaman.ferox.remotestorage.route_handlers;

import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;

import com.tantaman.ferox.api.request_response.IHttpContent;
import com.tantaman.ferox.api.request_response.IHttpRequest;
import com.tantaman.ferox.api.request_response.IRequestChainer;
import com.tantaman.ferox.api.request_response.IResponse;
import com.tantaman.ferox.api.router.IRouteHandler;
import com.tantaman.ferox.remotestorage.auth_management.AuthenticationRepository;
import com.tantaman.ferox.remotestorage.resource.IResourceIdentifier;
import com.tantaman.lo4j.Lo;

public class AccessControlRouteHandler implements IRouteHandler {
	private final AuthenticationRepository authRepo;
	private boolean authorized = false;
	
	public AccessControlRouteHandler(AuthenticationRepository authRepo) {
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
							response.send("{\"status\": \"unauthorized\"}", "application/json", HttpResponseStatus.UNAUTHORIZED)
							.addListener(ChannelFutureListener.CLOSE);
						}
					}
				});
	}

	@Override
	public void content(IHttpContent content, IResponse response,
			IRequestChainer next) {
		if (authorized) next.content(content);
	}

	@Override
	public void lastContent(IHttpContent content, IResponse response,
			IRequestChainer next) {
		if (authorized) next.lastContent(content);
	}
}
