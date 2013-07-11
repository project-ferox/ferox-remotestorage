package com.tantaman.ferox.remotestorage.resource_provider.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import com.tantaman.ferox.remotestorage.resource.IDocumentResource;

public class Document implements IDocumentResource {
	private InputStream stream = null;
	private File file;
	private RandomAccessFile randomAccessFile;
	
	public Document(File file) {
		this.file = file;
	}
	
	@Override
	public InputStream getStream() throws FileNotFoundException {
		if (stream == null) 
			stream = new FileInputStream(file);
		return stream;
	}
	
	public void close() throws IOException {
		if (stream != null)
			stream.close();
		if (randomAccessFile != null)
			randomAccessFile.close();
	}

	@Override
	public long lastModified() {
		return file.lastModified();
	}

	@Override
	public long length() throws FileNotFoundException {
		if (randomAccessFile == null) {
			randomAccessFile = new RandomAccessFile(file, "r");
		}
		return file.length();
	}

	@Override
	public String getContentType() {
		// TODO: fill this in correctly.
		// this would involve looking up the corresponding md entry...
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
