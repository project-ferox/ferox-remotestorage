package com.tantaman.ferox.remotestorage.resource;

import java.io.FileNotFoundException;
import java.io.InputStream;

public interface IDocumentResource extends IResource {
	public InputStream getStream()  throws FileNotFoundException;
	public void close();
	public long lastModified();
	public long length();
	public String getContentType();
}
