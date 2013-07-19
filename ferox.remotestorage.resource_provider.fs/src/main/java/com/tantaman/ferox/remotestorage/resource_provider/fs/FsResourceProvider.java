package com.tantaman.ferox.remotestorage.resource_provider.fs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
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
	private String fsRoot;

	public void activate(Map<String, String> configuration) {
		log.debug("Received configuration");
		fsRoot = configuration.get(ConfigKeys.FS_STORAGE_ROOT);
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
				retrieveResource(identifier, callback);
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
				retrieveForWrite(path, callback);
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
				doDelete(path, callback);
			}
		});
	}

	private void doDelete(IResourceIdentifier identifier,
			VFn2<Object, Throwable> callback) {
		String prefix = fsRoot + "/" + identifier.getUser();
		String path = prefix + "/" + identifier.getUserRelativerUri();

		prefix = new File(prefix).getAbsolutePath();
		File f = new File(path);
		if (!checkAccess(f, prefix, callback))
			return;

		Object version = Document.getVersion(f);
		boolean deleted = f.delete();

		if (deleted) {
			callback.f(version, null);
		} else {
			callback.f(null, null);
		}
	}

	private boolean checkAccess(File f, String prefix,
			VFn2<?, Throwable> callback) {
		String fullPath = f.getAbsolutePath();
		if (!fullPath.startsWith(prefix)) {
			callback.f(null, new IllegalAccessException("Denied path"));
			return false;
		}

		if (f.isDirectory()) {
			callback.f(null, new IllegalAccessException(
					"Can't write to a directory"));
			return false;
		}

		return true;
	}

	private void retrieveForWrite(IResourceIdentifier identifier,
			VFn2<IResourceOutputQueue, Throwable> callback) {
		String uri = identifier.getUserRelativerUri();

		String prefix = fsRoot + "/" + identifier.getUser();
		String path = prefix + "/" + uri;

		prefix = new File(prefix).getAbsolutePath();
		try {
			File f = new File(path);
			if (!checkAccess(f, prefix, callback)) {
				return;
			}

			if (!f.exists()) {
				File parent = new File(f.getParent());
				if (!parent.exists()) {
					boolean made = ensureDirectory(parent);
					if (!made) {
						callback.f(null, new IOException(
								"Failed creating required directories"));
						return;
					}
				}
			}

			callback.f(new FileWriteQueue(f, Workers.FS_POOL), null);
		} catch (IOException e) {
			callback.f(null, e);
		}
	}

	private boolean ensureDirectory(File f) {
		boolean made = f.mkdirs();
		if (!made)
			log.error("Unable to make the requested directories. "
					+ f.getParent() + " " + f.getAbsolutePath());
		return made;
	}

	// TODO: IDocument should just have streams for read and write
	private void retrieveResource(IResourceIdentifier identifier,
			Lo.VFn2<IResource, Throwable> callback) {
		String uri = identifier.getUserRelativerUri();

		String prefix = fsRoot + "/" + identifier.getUser();
		String path = prefix + "/" + uri;

		prefix = new File(prefix).getAbsolutePath();
		if (identifier.isDir()) {
			// do the directory listing
			// TODO: spec has some weird stuff about empty folders
			// (e.g., saying they don't exist and not listing them)
			File f = new File(path);
			if (!f.getAbsolutePath().startsWith(prefix)) {
				log.debug(prefix + " " + f.getAbsolutePath());
				callback.f(null, new IllegalAccessException("Denied path"));
				return;
			}

			if (f.exists()) {
				File[] listing = f.listFiles();

				Map<String, Object> documentListing = new LinkedHashMap<>(
						listing.length);
				for (File file : listing) {
					String name = file.getName();
					if (file.isDirectory())
						name += "/";
					documentListing.put(name, Document.getVersion(file));
				}

				callback.f(new Directory(f.getName(), documentListing), null);
			} else {
				callback.f(null, new FileNotFoundException(
						"File not found for: " + path));
			}
		} else {
			File f = new File(path);
			if (f.exists() && f.getAbsolutePath().startsWith(prefix)) {
				try {
					Document doc = new Document(f);
					callback.f(doc, null);
				} catch (FileNotFoundException e) {
					callback.f(null, e);
				}
			} else {
				callback.f(null, new FileNotFoundException(
						"File not found for: " + path));
			}
		}
	}
}
