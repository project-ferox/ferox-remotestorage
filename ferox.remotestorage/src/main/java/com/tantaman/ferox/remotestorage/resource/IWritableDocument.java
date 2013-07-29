package com.tantaman.ferox.remotestorage.resource;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.Map;

public interface IWritableDocument {
	public <A> void updateMetadata(Map<String, String> metadata, A attachment, CompletionHandler<Integer, A> handler);
	public <A> void add(ByteBuffer buffer, A attachment, CompletionHandler<Integer, A> handler);
	public void close() throws IOException;
}
