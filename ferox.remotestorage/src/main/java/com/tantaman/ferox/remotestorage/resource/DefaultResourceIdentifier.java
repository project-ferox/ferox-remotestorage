package com.tantaman.ferox.remotestorage.resource;

public class DefaultResourceIdentifier implements IResourceIdentifier {
	private final String relativeUri;
	private final String user;
	private final boolean isPublic;
	private final String module;
	private final boolean isDir;
	private final String moduleRelativeUri;
	
	public DefaultResourceIdentifier(String user, String relativeUri) {
		if (relativeUri.startsWith("/")) {
			relativeUri = relativeUri.substring(1);
		}
		
		this.user = user;
		this.relativeUri = relativeUri;
		isPublic = relativeUri.startsWith("public");
		
		if (isPublic) {
			int len = "public".length();
			int endIndex = relativeUri.indexOf("/", len+1);
			module = relativeUri.substring(len + 1, endIndex);
			
			moduleRelativeUri = relativeUri.substring(endIndex);
		} else {
			int endIndex = relativeUri.indexOf("/");
			module = relativeUri.substring(0, endIndex);
			moduleRelativeUri = relativeUri.substring(endIndex);
		}
		
		isDir = relativeUri.endsWith("/") ? true : false;
	}
	
	@Override
	public String getUser() {
		return user;
	}

	@Override
	public boolean isPublic() {
		return isPublic;
	}

	@Override
	public String getModule() {
		return module;
	}

	@Override
	public String getUserRelativerUri() {
		return relativeUri;
	}
	
	@Override
	public String getModuleRelativeUri() {
		return moduleRelativeUri;
	}
	
	@Override
	public boolean isDir() {
		return isDir;
	}
}
