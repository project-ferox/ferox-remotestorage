package com.tantaman.ferox.remotestorage.resource_provider.fs;

import java.io.File;

import com.tantaman.ferox.remotestorage.resource.IResourceIdentifier;

public class Utils {
	public static boolean allowedFile(File f, String prefix) {
		String fullPath = f.getAbsolutePath();
		return fullPath.startsWith(prefix);
	}
	
	public static String constructFullPath(String fsRoot, IResourceIdentifier identifier) {
		return fsRoot + "/" + identifier.getUser() + "/" + identifier.getUserRelativerUri();
	}
	
	public static String constructMetadataPath(String fsRoot, IResourceIdentifier identifier) {
		return fsRoot + "/.metadata/" + identifier.getUser() + "/" + identifier.getUserRelativerUri();
	}
	
	public static String getMetadataPathForFile(File f, String fsRoot) {
		String p = f.getAbsolutePath();
		return p.replace(fsRoot, fsRoot + "/.metadata");
	}
}
