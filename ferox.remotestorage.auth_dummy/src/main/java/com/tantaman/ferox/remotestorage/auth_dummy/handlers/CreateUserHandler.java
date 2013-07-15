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

public class CreateUserHandler extends RouteHandlerAdapter {
	private final UserRepo userRepo;
	
	public CreateUserHandler(UserRepo userRepo) {
		this.userRepo = userRepo;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public synchronized void lastContent(IHttpContent content, IResponse response,
			IRequestChainer next) {
		Attribute username = content.getBody().get("username");
		Attribute password = content.getBody().get("password");
		
		try {
			if (username == null || password == null) {
				response.redirect(HttpMethod.GET, content.getUri(), (Map)Lo.createMap("alert", "username and password must be specified"));
			} else if (username.length() > 50 || password.length() > 50) {
				response.redirect(HttpMethod.GET, content.getUri(), (Map)Lo.createMap("alert", "username or password is too long"));
			} else if (userRepo.exists(username.getValue())) {
				response.redirect(HttpMethod.GET, content.getUri(), (Map)Lo.createMap("alert", "that user already exists"));
			} else {
				try {
					userRepo.addUser(username.getValue(), password.getValue());
				} catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException e) {
					response.redirect(HttpMethod.GET, content.getUri(), (Map)Lo.createMap("alert", "server error"));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			content.dispose();
		}
	}

}
