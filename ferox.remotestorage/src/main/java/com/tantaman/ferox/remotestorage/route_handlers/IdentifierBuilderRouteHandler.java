package com.tantaman.ferox.remotestorage.route_handlers;

import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.HttpResponseStatus;

import com.tantaman.ferox.api.request_response.IHttpRequest;
import com.tantaman.ferox.api.request_response.IRequestChainer;
import com.tantaman.ferox.api.request_response.IResponse;
import com.tantaman.ferox.api.router.RouteHandlerAdapter;
import com.tantaman.ferox.remotestorage.resource.DefaultResourceIdentifier;
import com.tantaman.ferox.remotestorage.resource.IResourceIdentifier;

public class IdentifierBuilderRouteHandler extends RouteHandlerAdapter {
	private final String rootUri;
	
	public IdentifierBuilderRouteHandler(String rootUri) {
		this.rootUri = rootUri;
	}
	
	@Override
	public void request(IHttpRequest request, IResponse response,
			IRequestChainer next) {
		try {
			IResourceIdentifier identifier = new DefaultResourceIdentifier(
				request.getUrlParam("user"),
				request.getUri().replace(rootUri + "/" + request.getUrlParam("user"), ""));
			response.setUserData(identifier);
			next.request(request);
		} catch (Exception e) {
			response.send("{\"status\": \"not_found\"}", "application/json", HttpResponseStatus.NOT_FOUND)
				.addListener(ChannelFutureListener.CLOSE);
		}
	}
}