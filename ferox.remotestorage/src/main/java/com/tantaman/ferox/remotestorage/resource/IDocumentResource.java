package com.tantaman.ferox.remotestorage.resource;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;

public interface IDocumentResource extends IResource {
	public ReadableByteChannel getStream()  throws FileNotFoundException;
	public void close();
	public long lastModified();
	public long length();
	public String getContentType();
}
