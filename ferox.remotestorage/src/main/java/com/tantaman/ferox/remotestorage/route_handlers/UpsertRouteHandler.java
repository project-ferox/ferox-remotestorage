package com.tantaman.ferox.remotestorage.route_handlers;

import com.tantaman.ferox.api.request_response.IHttpContent;
import com.tantaman.ferox.api.request_response.IRequestChainer;
import com.tantaman.ferox.api.request_response.IResponse;
import com.tantaman.ferox.api.router.RouteHandlerAdapter;

public class UpsertRouteHandler extends RouteHandlerAdapter {
	@Override
	public void lastContent(IHttpContent content, IResponse response,
			IRequestChainer next) {
		content.dispose();
	}
}
