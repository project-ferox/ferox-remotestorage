package com.tantaman.ferox.remotestorage.resource_provider.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.tantaman.ferox.remotestorage.resource.IDocumentResource;

public class Document implements IDocumentResource {
	private final InputStream stream;
	
	public Document(File file) throws FileNotFoundException {
		stream = new FileInputStream(file);
	}
	
	@Override
	public InputStream getStream() {
		return stream;
	}
	
	public void close() throws IOException {
		stream.close();
	}
}
