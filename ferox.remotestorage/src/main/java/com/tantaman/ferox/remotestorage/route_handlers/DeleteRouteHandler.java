package com.tantaman.ferox.remotestorage.route_handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tantaman.ferox.api.request_response.IHttpContent;
import com.tantaman.ferox.api.request_response.IRequestChainer;
import com.tantaman.ferox.api.request_response.IResponse;
import com.tantaman.ferox.api.router.RouteHandlerAdapter;

public class DeleteRouteHandler extends RouteHandlerAdapter {
	private static final Logger log = LoggerFactory.getLogger(DeleteRouteHandler.class);
	
	@Override
	public void lastContent(IHttpContent content, IResponse response,
			IRequestChainer next) {
	}
}
