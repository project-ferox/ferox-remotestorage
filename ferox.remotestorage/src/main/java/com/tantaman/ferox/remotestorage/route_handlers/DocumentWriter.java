package com.tantaman.ferox.remotestorage.route_handlers;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.nio.channels.CompletionHandler;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tantaman.ferox.remotestorage.resource.IWritableDocument;

public class DocumentWriter {
	private static final Logger log = LoggerFactory.getLogger(DocumentWriter.class);
	
	private final IWritableDocument resource;
	private final AtomicInteger outstandingRequests = new AtomicInteger(0);
	private volatile boolean closeRequested = false; 
	
	public DocumentWriter(IWritableDocument document) {
		resource = document;
	}

	public void write(ByteBuf content) {
		outstandingRequests.incrementAndGet();
		
		resource.add(content.nioBuffer(), content, new OpCompletionHandler());
	}

	public void updateMetadata(Map<String, String> newMeta) {
		outstandingRequests.incrementAndGet();
		
		resource.updateMetadata(newMeta, null, new OpCompletionHandler());
	}

	public void close() {
		closeRequested = true;
	}
	
	private class OpCompletionHandler implements CompletionHandler<Integer, ByteBuf> {
		@Override
		public void completed(Integer result, ByteBuf attachment) {
			if (attachment != null)
				attachment.release();
			
			closeIfAble();
		}
		
		@Override
		public void failed(Throwable exc, ByteBuf attachment) {
			if (attachment != null)
				attachment.release();
			
			closeIfAble();
		}
		
		private void closeIfAble() {
			int requests = outstandingRequests.decrementAndGet();
			if (requests < 0)
				log.error("Imbalanced number of closeIfAble and update/write calls");
			
			if (requests <= 0 && closeRequested) {
				try {
					log.debug("Closed resource");
					resource.close();
				} catch (IOException e) {
					log.error("Unable to close resource");
					throw new IllegalStateException(e);
				}
			}
		}
	}
}
