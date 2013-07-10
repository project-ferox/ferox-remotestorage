package com.tantaman.ferox.remotestorage.route_handlers;

import com.tantaman.ferox.api.request_response.IHttpRequest;
import com.tantaman.ferox.api.request_response.IRequestChainer;
import com.tantaman.ferox.api.request_response.IResponse;
import com.tantaman.ferox.api.router.RouteHandlerAdapter;
import com.tantaman.ferox.remotestorage.resource.DefaultResourceIdentifier;

public class IdentifierBuilderRouteHandler extends RouteHandlerAdapter {
	private final String rootUri;
	
	public IdentifierBuilderRouteHandler(String rootUri) {
		this.rootUri = rootUri;
	}
	
	@Override
	public void request(IHttpRequest request, IResponse response,
			IRequestChainer next) {
		response.setUserData(
				new DefaultResourceIdentifier(
						request.getUrlParam("user"),
						request.getUri().replace(rootUri, "")));
	}
}
