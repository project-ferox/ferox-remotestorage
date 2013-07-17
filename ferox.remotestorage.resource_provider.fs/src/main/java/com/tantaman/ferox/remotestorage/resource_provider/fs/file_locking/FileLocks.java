package com.tantaman.ferox.remotestorage.resource_provider.fs.file_locking;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Apparently none of this is needed.  Lots of misinformation
 * on this topic floating around....  keeping the class for now.
 * 
 * @author tantaman
 *
 */
public class FileLocks {
	// File locks are partitioned into segments so
	// the lookup of locks can be done concurrently.
	private final AtomicReferenceArray<Map<String, LockReference>> locks;
	private static final Logger log = LoggerFactory.getLogger(FileLocks.class);
	
	private static volatile FileLocks INSTANCE;
	
	public static void initInstance(int concurrency) {
		synchronized (FileLocks.class) {
			if (INSTANCE != null)
				throw new IllegalStateException("Instance already initialized");
			INSTANCE = new FileLocks(concurrency);
		}
	}
	
	public static void lockForRead(String path) {
		INSTANCE._lockForRead(path);
	}
	
	public static void lockForWrite(String path) {
		INSTANCE._lockForWrite(path);
	}
	
	public static void releaseReadLock(String path) {
		INSTANCE._releaseReadLock(path);
	}
	
	public static void releaseWriteLock(String path) {
		INSTANCE._releaseWriteLock(path);
	}

	@SuppressWarnings("unchecked")
	private FileLocks(int concurrency) {
		Map<String, LockReference> [] locks = new HashMap[concurrency];
		for (int i = 0; i < locks.length; ++i) {
			locks[i] = new HashMap<>();
		}

		this.locks = new AtomicReferenceArray<>(locks);
	}
	
	public void _lockForRead(String path) {
		LockReference ref = retainReference(path);
		// do the actual file locking here so we don't freeze the entire segment just for one file within the segment.
		ref.getLock().readLock().lock();
	}
	
	public void _lockForWrite(String path) {
		LockReference ref = retainReference(path);
		ref.getLock().writeLock().lock();
	}
	
	public void _releaseReadLock(String path) {
		LockReference ref = releaseReference(path);
		// Its ok to do this out here since the caller is releasing their lock anyway
		// so it doesn't matter if a new lock (for the same file) gets created
		// and acquired before the existing lock is unlocked.
		ref.getLock().readLock().unlock();
	}
	
	public void _releaseWriteLock(String path) {
		LockReference ref = releaseReference(path);		
		ref.getLock().writeLock().unlock();
	}
	
	private LockReference retainReference(String path) {
		Map<String, LockReference> segment = getSegment(path);
		LockReference ref = null;
		synchronized (segment) {
			ref = retrieveReference(path, segment);
			ref.retain();
		}
		
		return ref;
	}
	
	private LockReference releaseReference(String path) {
		Map<String, LockReference> segment = getSegment(path);
		LockReference ref;
		
		synchronized (segment) {
			ref = segment.get(path);
			if (ref == null) { 
				String msg = "Unlocking a lock that doesn't exist";
				log.error(msg);
				throw new IllegalStateException(msg);
			}
			int count = ref.release();
			if (count == 0) {
				log.debug("Purging lock for: " + path);
				segment.remove(path);
			}
		}
		
		return ref;
	}
	
	private Map<String, LockReference> getSegment(String path) {
		int bucket = path.hashCode() % locks.length();
		return locks.get(bucket);
	}
	
	private LockReference retrieveReference(String path, Map<String, LockReference> segment) {
		LockReference ref = segment.get(path);
		if (ref == null) {
			ref = new LockReference(new ReentrantReadWriteLock());
			segment.put(path, ref);
		}
		return ref;
	}
}
