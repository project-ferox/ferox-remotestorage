package com.tantaman.ferox.remotestorage.resource_provider.fs;

import java.io.File;
import java.io.FileNotFoundException;

import com.tantaman.ferox.remotestorage.resource.IResource;
import com.tantaman.ferox.remotestorage.resource_provider.fs.resources.Directory;
import com.tantaman.ferox.remotestorage.resource_provider.fs.resources.Document;

public class ResourceFactory {
	public static IResource create(File f) throws FileNotFoundException {
		if (f.isDirectory()) {
			return new Directory(f.getName(), null);
		} else {
			return new Document(f);
		}
	}
}
