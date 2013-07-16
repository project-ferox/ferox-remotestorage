package com.tantaman.ferox.remotestorage.auth_dummy.handlers;

import io.netty.handler.codec.http.HttpResponseStatus;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupDir;

import com.tantaman.ferox.api.request_response.IHttpContent;
import com.tantaman.ferox.api.request_response.IRequestChainer;
import com.tantaman.ferox.api.request_response.IResponse;
import com.tantaman.ferox.api.router.RouteHandlerAdapter;

public class RegistrationPageHandler extends RouteHandlerAdapter {
	private final STGroup templateGroup;

	public RegistrationPageHandler(String templateRoot) {
		templateGroup = new STGroupDir(templateRoot, "UTF-8", '{', '}');
	}

	@Override
	public void lastContent(IHttpContent content, IResponse response,
			IRequestChainer next) {
		ST tpl = templateGroup.getInstanceOf("RegistrationPage");
		
		String alert = content.getQueryParam("alert");
		tpl.add("alert", alert);
		
		response.send(tpl.render(), HttpResponseStatus.OK);
		
		next.content(content);
	}
}
