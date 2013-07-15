package com.tantaman.ferox.remotestorage.auth_dummy.handlers;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.multipart.Attribute;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import com.tantaman.ferox.api.request_response.IHttpContent;
import com.tantaman.ferox.api.request_response.IRequestChainer;
import com.tantaman.ferox.api.request_response.IResponse;
import com.tantaman.ferox.api.router.RouteHandlerAdapter;
import com.tantaman.ferox.remotestorage.auth_dummy.auth.UserRepo;
import com.tantaman.lo4j.Lo;

// TODO: we'll have to do body parsing here... / request decoding.
public class LoginAuthenticationHandler extends RouteHandlerAdapter {
	private final UserRepo userRepo;
	
	public LoginAuthenticationHandler(UserRepo userRepo) {
		this.userRepo = userRepo;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void lastContent(IHttpContent content, IResponse response,
			IRequestChainer next) {
		try {
			Attribute username = content.getBody().get("username");
			Attribute password = content.getBody().get("password");
			
			if (username == null || password == null) {
				response.redirect(HttpMethod.GET, content.getUri(), (Map)Lo.createMap("alert", "username and password are required"));
			} if (username.length() > 50 || password.length() > 50) {
				response.redirect(HttpMethod.GET, content.getUri(), (Map)Lo.createMap("alert", "username or password is too long"));
			} else {
				try {
					boolean authed = userRepo.authenticate(username.getValue(), password.getValue());
					if (authed) {
						// this needs to redirect back to the client that requested the authentication
						// and add in all the token, token_type, etc. fields.
						// so we'll have to generate a token and save it in the token repo.
//						response.redirect(get, uri, queryParams);
					} else {
						response.redirect(HttpMethod.GET, content.getUri(), (Map)Lo.createMap("alert", "invalid username or password"));
					}
				} catch (InvalidKeySpecException | NoSuchAlgorithmException
						| IOException e) {
					response.redirect(HttpMethod.GET, content.getUri(), (Map)Lo.createMap("alert", "server error"));
				}
			}
		} finally {
			content.dispose();
		}		
	}
}
