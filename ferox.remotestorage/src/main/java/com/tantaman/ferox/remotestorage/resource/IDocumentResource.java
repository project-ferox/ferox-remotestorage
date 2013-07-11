package com.tantaman.ferox.remotestorage.resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public interface IDocumentResource extends IResource {
	public InputStream getStream()  throws FileNotFoundException;
	public void close() throws IOException;
	public long lastModified();
	public long length() throws FileNotFoundException;
	public String getContentType();
	public String getVersion();
	public String getName();
}
