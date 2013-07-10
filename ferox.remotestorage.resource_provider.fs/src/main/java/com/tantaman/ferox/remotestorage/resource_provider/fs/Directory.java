package com.tantaman.ferox.remotestorage.resource_provider.fs;

import java.io.File;
import java.util.Collections;
import java.util.List;

import com.tantaman.ferox.remotestorage.resource.IDirectoryResource;

public class Directory implements IDirectoryResource {
	private final List<File> listing;
	
	public Directory(List<File> files) {
		listing = Collections.unmodifiableList(files);
	}
	
	@Override
	public List<File> getListing() {
		return listing;
	}

}
