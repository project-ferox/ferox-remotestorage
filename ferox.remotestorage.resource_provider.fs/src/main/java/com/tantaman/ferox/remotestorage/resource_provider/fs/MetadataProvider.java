package com.tantaman.ferox.remotestorage.resource_provider.fs;

import java.util.Map;

import com.tantaman.ferox.remotestorage.resource.IResourceIdentifier;

public class MetadataProvider {
	private String fsRoot;
	
	public void setFsRoot(String fsRoot) {
		this.fsRoot = fsRoot;
	}
	
	public Map<String, String> getMetadata(IResourceIdentifier identifier) {
		return null;
	}
}
