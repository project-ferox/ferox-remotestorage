package com.tantaman.ferox.remotestorage.route_handlers;

import com.tantaman.ferox.api.request_response.IHttpContent;
import com.tantaman.ferox.api.request_response.IRequestChainer;
import com.tantaman.ferox.api.request_response.IResponse;
import com.tantaman.ferox.api.router.RouteHandlerAdapter;

public class UpsertRouteHandler extends RouteHandlerAdapter {
	
	@Override
	public void content(IHttpContent content, IResponse response,
			IRequestChainer next) {
		// just save each content chunk into the file as per the spec....
		// we should probably check for a custom header that'll indicate if it is a file upload...
		// and grab the file in that case.  It'll require doing the body parsing in that case.
		// So we can make a special case body parser for remotestorage that'll only do body parsing when that header
		// is present.
		
		// X-Rs-Subtype: file(s)
		// requests of this type will be "put" to a directory.
		// documents will be created for each file contained in the request.
	}
	
	@Override
	public void lastContent(IHttpContent content, IResponse response,
			IRequestChainer next) {
		content.dispose();
	}
}
