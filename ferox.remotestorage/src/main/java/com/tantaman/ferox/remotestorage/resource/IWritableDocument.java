package com.tantaman.ferox.remotestorage.resource;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

public interface IWritableDocument {
	public void updateMetadata(Map<String, String> metadata);
	public void add(ByteBuffer buffer);
	public void close() throws IOException;
}
