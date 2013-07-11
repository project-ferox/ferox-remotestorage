package com.tantaman.ferox.remotestorage.resource_provider.fs;

import java.util.Collections;
import java.util.List;

import com.tantaman.ferox.remotestorage.resource.IDirectoryResource;
import com.tantaman.ferox.remotestorage.resource.IDocumentResource;

public class Directory implements IDirectoryResource {
	private final List<IDocumentResource> listing;
	
	public Directory(List<IDocumentResource> files) {
		listing = Collections.unmodifiableList(files);
	}
	
	@Override
	public List<IDocumentResource> getListing() {
		return listing;
	}

}
