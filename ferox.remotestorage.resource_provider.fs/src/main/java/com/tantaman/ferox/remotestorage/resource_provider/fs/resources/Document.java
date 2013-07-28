package com.tantaman.ferox.remotestorage.resource_provider.fs.resources;

import io.netty.handler.codec.http.HttpHeaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.tantaman.ferox.remotestorage.resource.IDocumentResource;
import com.tantaman.ferox.remotestorage.resource_provider.fs.MetadataUtils;
import com.tantaman.ferox.remotestorage.resource_provider.fs.Workers;

public class Document implements IDocumentResource {
	private final InputStream stream;
	private final File file;
	private final long lastModified;
	private final long fileLength;
	private final Map<String, String> metadata;
	
	public Document(File file, String mdPath) throws FileNotFoundException {
		this.file = file;
		// Cache all the parameters because methods on this object
		// should all be non blocking.
		stream = new FileInputStream(file);
		lastModified = file.lastModified();
		fileLength = file.length();
		
		metadata = MetadataUtils.getMetadata(mdPath);
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
		String type = metadata.get(HttpHeaders.Names.CONTENT_TYPE);
		return type == null ? "application/json" : type;
	}

	@Override
	public Object getVersion() {
		return getVersion(file);
	}
	
	public static Object getVersion(File f) {
		// TODO: this version calculation depends on the version of the remotestorage protocol
		// we are using (unfortunately)...
		return f.lastModified(); //Long.toString(f.lastModified());
	}

	@Override
	public String getName() {
		return file.getName();
	}
}
