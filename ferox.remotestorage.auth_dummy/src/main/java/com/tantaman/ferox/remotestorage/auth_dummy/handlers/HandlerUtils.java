package com.tantaman.ferox.remotestorage.auth_dummy.handlers;

import java.util.Map;

import com.tantaman.ferox.api.request_response.IHttpReception;
import com.tantaman.lo4j.Lo;

public class HandlerUtils {
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, String> extractOauthParams(IHttpReception content) {
		String responseType = content.getQueryParam("response_type"); // required, must be "token"
		String clientId = content.getQueryParam("client_id"); // required, for?
		String scope = content.getQueryParam("scope", "root:rw"); // optional, desired scope
		String state = content.getQueryParam("state"); // recommended. for csrf protection
		String redirect = content.getQueryParam("redirect_uri");
		
		return (Map)Lo.createMap(
				"client_id", clientId,
				"response_type", responseType,
				"scope", scope,
				"state", state,
				"redirect_uri", redirect);
	}
}
