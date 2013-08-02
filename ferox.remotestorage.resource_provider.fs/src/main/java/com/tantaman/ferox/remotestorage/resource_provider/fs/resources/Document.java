package com.tantaman.ferox.remotestorage.resource_provider.fs.resources;

import io.netty.handler.codec.http.HttpHeaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.util.Map;

import com.tantaman.ferox.remotestorage.resource.IDocumentResource;
import com.tantaman.ferox.remotestorage.resource_provider.fs.MetadataUtils;
import com.tantaman.ferox.remotestorage.resource_provider.fs.Utils;
import com.tantaman.ferox.remotestorage.resource_provider.fs.Workers;

public class Document implements IDocumentResource {
	private final ReadableByteChannel channel;
	private final File file;
	private final long lastModified;
	private final long fileLength;
	private final Map<String, String> metadata;
	
	public Document(File file, String mdPath) throws FileNotFoundException {
		this.file = file;
		// Cache all the parameters because methods on this object
		// should all be non blocking.
		lastModified = file.lastModified();
		fileLength = file.length();
		
		if (fileLength > 0)
			channel = new FileInputStream(file).getChannel(); // the stream is closed by the channel.
		else
			channel = null;
		
		metadata = MetadataUtils.getMetadata(mdPath);
	}
	
	@Override
	public ReadableByteChannel getStream() throws FileNotFoundException {
		return channel;
	}
	
	public void close() {
		Workers.FS_POOL.execute(new Runnable() {
			@Override
			public void run() {
				if (channel != null) {
					try {
						channel.close();
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
//		return getVersion(file);
		String version = metadata.get(HttpHeaders.Names.ETAG);
		return version == null ? "" : version;
	}

	@Override
	public String getName() {
		return file.getName();
	}
}
