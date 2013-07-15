package com.tantaman.ferox.remotestroage.auth_dummy.handlers;

import com.tantaman.ferox.api.request_response.IHttpContent;
import com.tantaman.ferox.api.request_response.IRequestChainer;
import com.tantaman.ferox.api.request_response.IResponse;
import com.tantaman.ferox.api.router.RouteHandlerAdapter;

public class DialogGetHandler extends RouteHandlerAdapter {
	private final String templateRoot;
	public DialogGetHandler(String templateRoot) {
		this.templateRoot = templateRoot;
	}
	
	@Override
	public void lastContent(IHttpContent content, IResponse response,
			IRequestChainer next) {
		
		next.content(content);
	}
}
