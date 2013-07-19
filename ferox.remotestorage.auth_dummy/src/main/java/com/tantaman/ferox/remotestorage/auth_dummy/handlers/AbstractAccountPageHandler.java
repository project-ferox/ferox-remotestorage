package com.tantaman.ferox.remotestorage.auth_dummy.handlers;

import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.Map;

import org.stringtemplate.v4.ST;

import com.tantaman.ferox.api.request_response.IHttpContent;
import com.tantaman.ferox.api.request_response.IRequestChainer;
import com.tantaman.ferox.api.request_response.IResponse;
import com.tantaman.ferox.api.router.RouteHandlerAdapter;

public abstract class AbstractAccountPageHandler extends RouteHandlerAdapter  {
	protected abstract ST getTemplate();

	@Override
	public void lastContent(IHttpContent content, IResponse response,
			IRequestChainer next) {
		ST tpl = getTemplate();

		Map<String, String> params = HandlerUtils.extractOauthParams(content);

		String alert = content.getQueryParam("alert");
		tpl.add("alert", alert);
		tpl.add("params", params);

		response.send(tpl.render(), HttpResponseStatus.OK);

		next.content(content);
	}
}
