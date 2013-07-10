package com.tantaman.ferox.remotestorage.resource;

public interface IResourceIdentifier {
	public String getUriRoot();
	public String getUser();
	public boolean isPublic();
	public String getModule();
	public String getUserRelativerUri();
	public boolean isDir();
}
