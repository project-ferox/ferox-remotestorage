package com.tantaman.ferox.remotestorage.resource_provider.fs.resources;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import com.tantaman.ferox.remotestorage.resource.IWritableDocument;
import com.tantaman.ferox.remotestorage.resource_provider.fs.MetadataUtils;
import com.tantaman.lo4j.Lo;

public class WritableDocument implements IWritableDocument {
	private final AsynchronousFileChannel fileChannel;
	private final String absPath;
	private final Map<String, String> metadata;
	private final AsynchronousFileChannel metadataChannel;
	private volatile int cursor = 0;
	
	public WritableDocument(File f, String metadataPath, ExecutorService executor) throws IOException {
		absPath = f.getAbsolutePath();
		
		Path p = Paths.get(absPath);
		fileChannel = AsynchronousFileChannel.open(
				p,
				(Set)Lo.createSet(StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE),
				executor);
		
		metadataChannel = AsynchronousFileChannel.open(Paths.get(metadataPath),
					(Set)Lo.createSet(StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE),
					executor);
		
		metadata = MetadataUtils.getMetadata(metadataPath);
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
		metadataChannel.close();
	}

	@Override
	public void updateMetadata(Map<String, String> newMetadata) {
		MetadataUtils.updateMetadata(newMetadata, metadata, metadataChannel);
	}
}
