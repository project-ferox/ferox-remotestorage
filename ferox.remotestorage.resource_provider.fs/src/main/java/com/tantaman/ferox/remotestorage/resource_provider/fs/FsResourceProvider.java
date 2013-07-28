package com.tantaman.ferox.remotestorage.resource_provider.fs;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tantaman.ferox.remotestorage.ConfigKeys;
import com.tantaman.ferox.remotestorage.resource.IResource;
import com.tantaman.ferox.remotestorage.resource.IResourceIdentifier;
import com.tantaman.ferox.remotestorage.resource.IResourceOutputQueue;
import com.tantaman.ferox.remotestorage.resource.IResourceProvider;
import com.tantaman.lo4j.Lo;
import com.tantaman.lo4j.Lo.VFn2;

// TODO: clean this class up
public class FsResourceProvider implements IResourceProvider {
	private static final Logger log = LoggerFactory
			.getLogger(FsResourceProvider.class);
	
	private final FsResourceProviderInternal impl = new FsResourceProviderInternal();

	public void activate(Map<String, String> configuration) {
		log.debug("Received configuration");
		String fsRoot = configuration.get(ConfigKeys.FS_STORAGE_ROOT);
		impl.setFsRoot(fsRoot);
	}

	@Override
	public void openForRead(final IResourceIdentifier identifier,
			final Lo.VFn2<IResource, Throwable> callback)
			throws IllegalStateException {
		if (identifier.getModule().equals(".metadata")) {
			log.debug("Illegal module accessed");
			throw new IllegalStateException("Illegal module");
		}

		Workers.FS_POOL.execute(new Runnable() {
			@Override
			public void run() {
				impl.retrieveResource(identifier, callback);
			}
		});
	}

	@Override
	public void openForWrite(final IResourceIdentifier path,
			final VFn2<IResourceOutputQueue, Throwable> callback)
			throws IllegalStateException {
		if (path.isDir())
			throw new IllegalStateException("Can't write to a directory");
		Workers.FS_POOL.execute(new Runnable() {
			@Override
			public void run() {
				impl.retrieveForWrite(path, callback);
			}
		});
	}

	@Override
	public void delete(final IResourceIdentifier path,
			final VFn2<Object, Throwable> callback) {
		if (path.isDir())
			throw new IllegalStateException("Can't delete a directory"); // or
																			// can
																			// we?
		Workers.FS_POOL.execute(new Runnable() {
			@Override
			public void run() {
				impl.doDelete(path, callback);
			}
		});
	}
}
