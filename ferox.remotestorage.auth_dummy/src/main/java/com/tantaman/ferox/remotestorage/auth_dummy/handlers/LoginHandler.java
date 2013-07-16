package com.tantaman.ferox.remotestorage.auth_dummy.handlers;

import io.netty.handler.codec.http.multipart.Attribute;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.tantaman.ferox.api.request_response.IHttpContent;
import com.tantaman.ferox.api.request_response.IRequestChainer;
import com.tantaman.ferox.api.request_response.IResponse;
import com.tantaman.ferox.api.router.RouteHandlerAdapter;
import com.tantaman.ferox.remotestorage.auth_dummy.auth.UserRepo;
import com.tantaman.ferox.remotestorage.auth_manager.Authorization;
import com.tantaman.ferox.remotestorage.auth_manager.IAuthManager;
import com.tantaman.lo4j.Lo;

// TODO: we'll have to do body parsing here... / request decoding.
public class LoginHandler extends RouteHandlerAdapter {
	private final UserRepo userRepo;
	private final IAuthManager authManager;

	public LoginHandler(UserRepo userRepo, IAuthManager authManager) {
		this.userRepo = userRepo;
		this.authManager = authManager;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void lastContent(IHttpContent content, IResponse response,
			IRequestChainer next) {
		try {
			Attribute username = content.getBody().get("username");
			Attribute password = content.getBody().get("password");

			if (username == null || password == null) {
				response.redirect(content.getPath(), (Map)Lo.createMap("alert", "username and password are required"));
			} if (username.length() > 50 || password.length() > 50) {
				response.redirect(content.getPath(), (Map)Lo.createMap("alert", "username or password is too long"));
			} else {
				try {
					String usernameValue = username.getValue();
					boolean authed = userRepo.authenticate(usernameValue, password.getValue());
					if (authed) {
						// this needs to redirect back to the client that requested the authentication
						// and add in all the token, token_type, etc. fields.
						// so we'll have to generate a token and save it in the token repo.
						//						response.redirect(get, uri, queryParams);
						handleSuccessfulAuthentication(content, response, usernameValue);
					} else {
						response.redirect(content.getPath(), (Map)Lo.createMap("alert", "invalid username or password"));
					}
				} catch (InvalidKeySpecException | NoSuchAlgorithmException
						| IOException e) {
					response.redirect(content.getPath(), (Map)Lo.createMap("alert", "server error"));
				}
			}
		} finally {
			content.dispose();
		}		
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void handleSuccessfulAuthentication(IHttpContent content, IResponse response, String username) throws UnsupportedEncodingException {
		String responseType = content.getQueryParam("response_type"); // required, must be "token"
		String clientId = content.getQueryParam("client_id"); // required, for?
		String scope = content.getQueryParam("scope", "root:rw"); // optional, desired scope
		String state = content.getQueryParam("state"); // recommended. for csrf protection
		String redirect = content.getQueryParam("redirect_uri");
		String toMatch;
		
		Map<String, String> params = (Map)Lo.createMap(
				"client_id", clientId,
				"response_type", responseType,
				"scope", scope,
				"state", state,
				"redirect_uri", redirect);
		
		if (!responseType.toLowerCase().equals("token")) {
			params.put("alert", "bad token type");
			response.redirect(content.getPath(), params);
			return;
		}

		if (redirect.equals("")) {
			params.put("alert", "bad redirect uri");
			response.redirect(content.getPath(), params);
			return;
		}

		if(redirect.split("://").length < 2) {
			toMatch = redirect;
		} else {
			toMatch = redirect.split("://")[1].split("/")[0];
		}
		
		if (!toMatch.equals(clientId)) {
			params.put("alert", "bad client id or redirect uri");
			response.redirect(content.getPath(), params);
			return;
		}

		// generate the token
		String token = UUID.randomUUID().toString();
		Authorization auth = new Authorization(token, username, (Set)Lo.createSet(scope));
		authManager.addAuthorization(auth, null);

		response.redirect(redirect + "#access_token=" + URLEncoder.encode(token, "UTF-8"), null);
	}
}
