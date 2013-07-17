package com.tantaman.ferox.remotestorage.route_handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tantaman.ferox.api.request_response.IHttpContent;
import com.tantaman.ferox.api.request_response.IHttpRequest;
import com.tantaman.ferox.api.request_response.IRequestChainer;
import com.tantaman.ferox.api.request_response.IResponse;
import com.tantaman.ferox.api.router.RouteHandlerAdapter;
import com.tantaman.ferox.remotestorage.resource.IResourceProvider;

public class UpsertRouteHandler extends RouteHandlerAdapter {
	private final IResourceProvider resourceProvider;
	
	private static final Logger log = LoggerFactory.getLogger(UpsertRouteHandler.class);
	
	public UpsertRouteHandler(IResourceProvider resourceProvider) {
		this.resourceProvider = resourceProvider;
	}
	
	@Override
	public void request(IHttpRequest request, IResponse response,
			IRequestChainer next) {
		
	}
	
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
		
		//content.getContent();
	}
	
	@Override
	public void lastContent(IHttpContent content, IResponse response,
			IRequestChainer next) {
		content.dispose();
	}
}
