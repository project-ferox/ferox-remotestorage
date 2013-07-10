package com.tantaman.ferox.remotestorage.resource;

public interface IResourceIdentifier {
	public String getUser();
	public boolean isPublic();
	public String getModule();
	public String getUserRelativerUri();
	public String getModuleRelativeUri();
	public boolean isDir();
}
