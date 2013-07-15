package com.tantaman.ferox.remotestorage.auth_dummy.handlers;

import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.List;

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
		templateGroup = new STGroupDir(templateRoot, '{', '}');
	}

	@Override
	public void lastContent(IHttpContent content, IResponse response,
			IRequestChainer next) {
		ST tpl = templateGroup.getInstanceOf("RegistrationPage");
		
		List<String> alerts = content.getQueryParam("alert");
		if (alerts != null && alerts.size() > 0) {
			tpl.add("alert", alerts.get(0));
		} else {
			tpl.add("alert", "");
		}
		
		response.send(tpl.render(), HttpResponseStatus.OK);
		
		next.content(content);
	}
}
