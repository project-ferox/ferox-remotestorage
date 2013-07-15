package com.tantaman.ferox.remotestroage.auth_dummy.handlers;

import com.tantaman.ferox.api.router.RouteHandlerAdapter;

public class DialogGetHandler extends RouteHandlerAdapter {
	private final String templateRoot;
	public DialogGetHandler(String templateRoot) {
		this.templateRoot = templateRoot;
	}
}
