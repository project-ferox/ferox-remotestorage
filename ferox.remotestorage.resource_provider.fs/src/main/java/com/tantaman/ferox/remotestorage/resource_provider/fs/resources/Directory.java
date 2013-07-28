package com.tantaman.ferox.remotestorage.resource_provider.fs.resources;

import java.util.Map;

import com.tantaman.ferox.remotestorage.resource.IDirectoryResource;

public class Directory implements IDirectoryResource {
	private final Map<String, Object> listing;
	private final String name;
	
	public Directory(String name, Map<String, Object> listing) {
		this.name = name;
		this.listing = listing;
	}
	
	@Override
	public Map<String, Object> getListing() {
		return listing;
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getVersion() {
		return "";
	}
}
