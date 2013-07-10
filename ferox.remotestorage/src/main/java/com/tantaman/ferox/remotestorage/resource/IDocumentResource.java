package com.tantaman.ferox.remotestorage.resource;

import java.io.IOException;
import java.io.InputStream;

public interface IDocumentResource extends IResource {
	public InputStream getStream();
	public void close() throws IOException;
}
