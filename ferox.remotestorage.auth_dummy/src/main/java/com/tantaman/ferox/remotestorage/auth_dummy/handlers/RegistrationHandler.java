package com.tantaman.ferox.remotestorage.auth_dummy.handlers;

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

public class RegistrationHandler extends RouteHandlerAdapter {
	private final UserRepo userRepo;
	
	public RegistrationHandler(UserRepo userRepo) {
		this.userRepo = userRepo;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public synchronized void lastContent(IHttpContent content, IResponse response,
			IRequestChainer next) {
		String responseType = content.getQueryParam("response_type");
		String clientId = content.getQueryParam("client_id");
		String scope = content.getQueryParam("scope", "root:rw");
		String state = content.getQueryParam("state");
		String redirect = content.getQueryParam("redirect_uri");
		
		Attribute username = content.getBody().get("username");
		Attribute password = content.getBody().get("password");
		
		Map<String, String> params = (Map)Lo.createMap(
				"client_id", clientId,
				"response_type", responseType,
				"scope", scope,
				"state", state,
				"redirect_uri", redirect);
		
		try {
			if ((username == null || username.length() == 0) || (password == null || password.length() == 0)) {
				params.put("alert", "username and password must be specified");
				response.redirect(content.getPath(), params);
			} else if (username.length() > 50 || password.length() > 50) {
				params.put("alert", "username or password is too long");
				response.redirect(content.getPath(), params);
			} else if (userRepo.exists(username.getValue())) {
				params.put("alert", "that user already exists");
				response.redirect(content.getPath(), params);
			} else {
				try {
					userRepo.addUser(username.getValue(), password.getValue());
					params.put("alert", "registration successful.  You may now log in.");
					response.redirect("dialog", params);
				} catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException e) {
					e.printStackTrace();
					params.put("alert", "server error");
					response.redirect(content.getPath(), params);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			content.dispose();
		}
	}

}
