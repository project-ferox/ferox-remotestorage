package com.tantaman.ferox.remotestorage.route_handlers;

import com.tantaman.ferox.api.request_response.IHttpContent;
import com.tantaman.ferox.api.request_response.IRequestChainer;
import com.tantaman.ferox.api.request_response.IResponse;
import com.tantaman.ferox.api.router.RouteHandlerAdapter;
import com.tantaman.ferox.remotestorage.resource.IResource;
import com.tantaman.ferox.remotestorage.resource.IResourceIdentifier;
import com.tantaman.ferox.remotestorage.resource.IResourceProvider;
import com.tantaman.lo4j.Lo;

public class ReadRouteHandler extends RouteHandlerAdapter {
	private final IResourceProvider resourceProvider;
	
	public ReadRouteHandler(IResourceProvider resourceProvider) {
		this.resourceProvider = resourceProvider;
	}
	
	
	@Override
	public void lastContent(IHttpContent content, IResponse response,
			IRequestChainer next) {
		IResourceIdentifier identifier = response.getUserData();
		
		resourceProvider.getResource(identifier, new Lo.VFn2<IResource, Throwable>() {
			@Override
			public void f(IResource p1, Throwable p2) {
				
			}
		});
	}
}
