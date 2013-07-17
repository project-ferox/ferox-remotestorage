package com.tantaman.ferox.remotestorage.resource_provider.fs;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import com.tantaman.ferox.remotestorage.resource.IResourceOutputQueue;
import com.tantaman.lo4j.Lo;

public class FileWriteQueue implements IResourceOutputQueue {
	private final AsynchronousFileChannel fileChannel;
	private final String absPath;
	private volatile int cursor = 0;
	
	public FileWriteQueue(File f, ExecutorService executor) throws IOException {
		absPath = f.getAbsolutePath();
		
		Path p = Paths.get(absPath);
		fileChannel = AsynchronousFileChannel.open(
				p,
				(Set)Lo.createSet(StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING),
				executor);
	}
	
	@Override
	public void add(ByteBuffer buffer) {
		int addition = buffer.limit() - buffer.position();
		fileChannel.write(buffer, cursor);
		cursor += addition;
	}

	@Override
	public void close() throws IOException {
		fileChannel.close();
	}
}
