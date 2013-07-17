package com.tantaman.ferox.remotestorage.resource_provider.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.tantaman.ferox.remotestorage.resource.IDocumentResource;
import com.tantaman.ferox.remotestorage.resource_provider.fs.file_locking.FileLocks;

public class Document implements IDocumentResource {
	private InputStream stream = null;
	private final File file;
	private final String absPath;
	
	public Document(File file) {
		this.file = file;
		absPath = file.getAbsolutePath();
	}
	
	@Override
	public InputStream getStream() throws FileNotFoundException {
		if (stream == null) {
			stream = new FileInputStream(file);
		}
		return stream;
	}
	
	public void close() throws IOException {
		if (stream != null) {
			stream.close();
		}
	}

	@Override
	public long lastModified() {
		return file.lastModified();
	}

	@Override
	public long length() throws FileNotFoundException {
		return file.length();
	}

	@Override
	public String getContentType() {
		// TODO: fill this in correctly.
		// this would involve looking up the corresponding md entry...
//		return "text/plain";
		return "application/json";
	}

	@Override
	public String getVersion() {
		// TODO
		return "";
	}

	@Override
	public String getName() {
		return file.getName();
	}
}
