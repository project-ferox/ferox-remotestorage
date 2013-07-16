package com.tantaman.ferox.remotestorage.auth_manager.route_handlers;

import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.List;

import com.tantaman.ferox.api.request_response.IHttpContent;
import com.tantaman.ferox.api.request_response.IRequestChainer;
import com.tantaman.ferox.api.request_response.IResponse;
import com.tantaman.ferox.api.router.RouteHandlerAdapter;

public class RouteHandlers {
	public static class AddAuthorizationHandler extends RouteHandlerAdapter {
		@Override
		public void lastContent(IHttpContent content, IResponse response,
				IRequestChainer next) {
		}
	}
	
	public static class RemoveAuthorizationHandler extends RouteHandlerAdapter {
		@Override
		public void lastContent(IHttpContent content, IResponse response,
				IRequestChainer next) {
		}
	}
	
	public static class AccessControl extends RouteHandlerAdapter {
		private static final Exception AUTH_ERR = new IllegalStateException();
		private final String expectedPassword;
		
		public AccessControl(String password) {
			expectedPassword = password;
		}
		
		@Override
		public void lastContent(IHttpContent content, IResponse response,
				IRequestChainer next) {
			try {
				List<String> passwords = content.getQueryParam("password");
				String password = passwords.size() > 0 ? passwords.get(0) : null;
				
				if (expectedPassword.equals(password))
					next.lastContent(content);
				else
					throw AUTH_ERR;
			} catch (Exception e) {
				response.send("{\"status\": \"Invalid credentials.\"}", "application/json", HttpResponseStatus.UNAUTHORIZED);
			}
		}
	}
}
