package com.tantaman.ferox.remotestorage.resource_provider.fs.file_locking;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;

public class LockReference {
	private final ReadWriteLock lock;
	private AtomicInteger references;
	
	public LockReference(ReadWriteLock lock) {
		this.lock = lock;
		references = new AtomicInteger(0);
	}
	
	public ReadWriteLock getLock() {
		return lock;
	}
	
	public void retain() {
		references.incrementAndGet();
	}
	
	public int release() {
		return references.decrementAndGet();
	}
}
