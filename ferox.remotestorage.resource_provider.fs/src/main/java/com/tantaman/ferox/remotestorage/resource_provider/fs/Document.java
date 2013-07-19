package com.tantaman.ferox.remotestorage.resource_provider.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.tantaman.ferox.remotestorage.resource.IDocumentResource;

public class Document implements IDocumentResource {
	private final InputStream stream;
	private final File file;
	private final long lastModified;
	private final long fileLength;
	
	public Document(File file) throws FileNotFoundException {
		this.file = file;
		// Cache all the parameters because methods on this object
		// should all be non blocking.
		stream = new FileInputStream(file);
		lastModified = file.lastModified();
		fileLength = file.length();
	}
	
	@Override
	public InputStream getStream() throws FileNotFoundException {
		return stream;
	}
	
	public void close() {
		Workers.FS_POOL.execute(new Runnable() {
			@Override
			public void run() {
				if (stream != null) {
					try {
						stream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	@Override
	public long lastModified() {
		return lastModified;
	}

	@Override
	public long length() {
		return fileLength;
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
		return getVersion(file);
	}
	
	public static String getVersion(File f) {
		return Long.toString(f.lastModified());
	}

	@Override
	public String getName() {
		return file.getName();
	}
}
