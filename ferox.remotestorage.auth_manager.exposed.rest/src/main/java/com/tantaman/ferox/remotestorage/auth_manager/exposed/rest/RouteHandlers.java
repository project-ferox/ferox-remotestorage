package com.tantaman.ferox.remotestorage.auth_manager.exposed.rest;

import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.List;

import com.tantaman.ferox.api.request_response.IHttpContent;
import com.tantaman.ferox.api.request_response.IRequestChainer;
import com.tantaman.ferox.api.request_response.IResponse;
import com.tantaman.ferox.api.router.RouteHandlerAdapter;
import com.tantaman.ferox.remotestorage.auth_manager.IAuthManager;

public class RouteHandlers {
	public static class AddAuthorization extends RouteHandlerAdapter {
		private final IAuthManager authManager;
		
		public AddAuthorization(IAuthManager authManager) {
			this.authManager = authManager;
		}
		
		@Override
		public void lastContent(IHttpContent content, IResponse response,
				IRequestChainer next) {
			List<String> scopes = content.getQueryParams("scopes");
			String user = content.getQueryParam("username");
			String token = content.getQueryParam("token");
			
			
		}
	}
	
	public static class RemoveAuthorization extends RouteHandlerAdapter {
		private final IAuthManager authManager;
		
		public RemoveAuthorization(IAuthManager authManager) {
			this.authManager = authManager;
		}
		
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
				String password = content.getQueryParam("password");
				
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
