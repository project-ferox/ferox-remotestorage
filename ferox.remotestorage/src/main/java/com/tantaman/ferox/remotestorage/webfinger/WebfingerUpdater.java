package com.tantaman.ferox.remotestorage.webfinger;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.tantaman.ferox.api.request_response.IHttpContent;
import com.tantaman.ferox.api.request_response.IRequestChainer;
import com.tantaman.ferox.api.request_response.IResponse;
import com.tantaman.ferox.api.router.RouteHandlerAdapter;
import com.tantaman.ferox.webfinger.entry.IDynamicWebfingerEntry;

public class WebfingerUpdater extends RouteHandlerAdapter {
	private final String storageRootUri;
	private final String authDialog;
	
	public WebfingerUpdater(String storageRootUri, String authDialog) {
		this.storageRootUri = storageRootUri;
		this.authDialog = authDialog;
	}
	
	@Override
	public void lastContent(IHttpContent content, IResponse response,
			IRequestChainer next) {
		IDynamicWebfingerEntry entry = response.getUserData();
		
		List<Map<String,Object>> links = entry.getLinks();
		
		Map<String, Object> link = version00(entry); //version01
		
		links.add(link);
		
		next.lastContent(content);
	}
	
	private Map<String, Object> version00(IDynamicWebfingerEntry entry) {
		/*
		 *  {
      href: <storage_root>,
      rel: "remotestorage",
      type: <storage_api>,
      properties: {
        'auth-method': "http://tools.ietf.org/html/rfc6749#section-4.2",
        'auth-endpoint': <auth_endpoint>
      }
    }
		 */
		Map<String, Object> link = new LinkedHashMap<>();
		
		link.put("href", getUserScopedStorageRoot(entry.getSubject()));
		link.put("rel", "remotestorage");
		link.put("type", "draft-dejong-remotestorage-00");
		
		Map<String, String> properties = new LinkedHashMap<>();
		properties.put("auth-method", "http://tools.ietf.org/html/rfc6749#section-4.2");
		properties.put("auth-endpoint", authDialog);
		link.put("properties", properties);
		
		return link;
	}
	
	private Map<String, Object> version01(IDynamicWebfingerEntry entry) {
		Map<String, Object> link = new LinkedHashMap<>();
		
		link.put("href", getUserScopedStorageRoot(entry.getSubject()));
		link.put("rel", "remotestorage");
		link.put("type", "draft-dejong-remotestorage-01");
		
		Map<String, String> properties = new LinkedHashMap<>();
		properties.put("http://tools.ietf.org/html/rfc6749#section-4.2", authDialog);
		link.put("properties", properties);
		
		return link;
	}
	
	private String getUserScopedStorageRoot(String subject) {
		String user = subject.split("@")[0];
		
		int colon = subject.indexOf(":");
		if (colon >= 0) {
			user = user.substring(colon+1);
		}
		
		return storageRootUri + "/" + user;
	}
}
