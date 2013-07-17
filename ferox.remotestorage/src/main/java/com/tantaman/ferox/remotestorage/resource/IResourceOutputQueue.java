package com.tantaman.ferox.remotestorage.resource;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface IResourceOutputQueue {
	public void add(ByteBuffer buffer);
	public void close() throws IOException;
}
