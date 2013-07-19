package com.tantaman.ferox.remotestorage.resource_provider.fs;

import java.util.Map;

import com.tantaman.ferox.remotestorage.resource.IDirectoryResource;

public class Directory implements IDirectoryResource {
	private final Map<String, String> listing;
	private final String name;
	
	public Directory(String name, Map<String, String> listing) {
		this.name = name;
		this.listing = listing;
	}
	
	@Override
	public Map<String, String> getListing() {
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
