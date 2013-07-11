package com.tantaman.ferox.remotestorage.resource_provider.fs;

import java.util.List;

import com.tantaman.ferox.remotestorage.resource.IDirectoryResource;
import com.tantaman.ferox.remotestorage.resource.IResource;

public class Directory implements IDirectoryResource {
	private final List<IResource> listing;
	private final String name;
	
	public Directory(String name, List<IResource> listing) {
		this.name = name;
		this.listing = listing;
	}
	
	@Override
	public List<IResource> getListing() {
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
