package com.tantaman.ferox.remotestorage.route_handlers;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tantaman.ferox.api.request_response.IHttpContent;
import com.tantaman.ferox.api.request_response.IRequestChainer;
import com.tantaman.ferox.api.request_response.IResponse;
import com.tantaman.ferox.api.router.RouteHandlerAdapter;
import com.tantaman.ferox.remotestorage.resource.IResourceIdentifier;
import com.tantaman.ferox.remotestorage.resource.IResourceProvider;
import com.tantaman.lo4j.Lo;

public class DeleteRouteHandler extends RouteHandlerAdapter {
	private static final Logger log = LoggerFactory.getLogger(DeleteRouteHandler.class);
	private final IResourceProvider resourceProvider;
	
	public DeleteRouteHandler(IResourceProvider resourceProvider) {
		this.resourceProvider = resourceProvider;
	}
	
	@Override
	public void lastContent(IHttpContent content, final IResponse response,
			IRequestChainer next) {
		IResourceIdentifier identifier = response.getUserData();
		resourceProvider.delete(identifier, new Lo.VFn2<Object, Throwable>() {
			@Override
			public void f(Object version, Throwable error) {
				deleteCallback(version, error, response);
			}
		});
	}
	
	private void deleteCallback(Object version, Throwable error, IResponse response) {
		if (error != null || version == null) {
			// TODO: better error reporting.
			response.send(Lo.asJsonObject("status", "not_found"), "application/json", HttpResponseStatus.NOT_FOUND);
		} else {
			response.headers().add(HttpHeaders.Names.ETAG, version);
			response.send(Lo.asJsonObject("status", version), "application/json");
		}
	}
}
