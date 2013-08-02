package com.tantaman.ferox.remotestorage.resource_provider.fs;

import io.netty.handler.codec.http.HttpHeaders;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tantaman.ferox.remotestorage.resource.IResource;
import com.tantaman.ferox.remotestorage.resource.IResourceIdentifier;
import com.tantaman.ferox.remotestorage.resource.IWritableDocument;
import com.tantaman.ferox.remotestorage.resource_provider.fs.resources.Directory;
import com.tantaman.ferox.remotestorage.resource_provider.fs.resources.Document;
import com.tantaman.ferox.remotestorage.resource_provider.fs.resources.WritableDocument;
import com.tantaman.lo4j.Lo;
import com.tantaman.lo4j.Lo.VFn2;

public class FsResourceProviderInternal {
	private static final String DENIED_PATH = "Denied path";
	private static final String DIR_WRITE_ERR = "Directory exists with the same name";
	
	private static final Logger log = LoggerFactory
			.getLogger(FsResourceProviderInternal.class);
	
	private String fsRoot;
	
	public void setFsRoot(String fsRoot) {
		this.fsRoot = fsRoot;
	}
	
	void doDelete(IResourceIdentifier identifier,
			VFn2<Object, Throwable> callback) {
		String prefix = fsRoot + "/" + identifier.getUser();
		String path = prefix + "/" + identifier.getUserRelativerUri();

		prefix = new File(prefix).getAbsolutePath();
		File f = new File(path);
		if (!checkWriteAccessOrFail(f, prefix, callback))
			return;

		boolean deleted = f.delete();
		String mdPath = Utils.constructMetadataPath(fsRoot, identifier);
		File mdFile = new File(mdPath);
		Object version = MetadataUtils.getMetadata(mdPath).get(HttpHeaders.Names.ETAG);
		if (version == null)
			version = "";
		
		mdFile.delete();
		
		// TODO: delete containing directorie(s) if empty.

		if (deleted) {
			callback.f(version, null);
		} else {
			callback.f(null, null);
		}
	}
	
	private boolean checkAccessOrFail(File f, String prefix, VFn2<?, Throwable> callback) {
		if (!Utils.allowedFile(f, prefix)) {
			callback.f(null, new IllegalAccessException(DENIED_PATH));
			return false;
		}
		
		return true;
	}

	private boolean checkWriteAccessOrFail(File f, String prefix,
			VFn2<?, Throwable> callback) {
		if (!checkAccessOrFail(f, prefix, callback))
			return false;

		if (f.isDirectory()) {
			callback.f(null, new IllegalAccessException(
					DIR_WRITE_ERR));
			return false;
		}

		return true;
	}

	void retrieveForWrite(IResourceIdentifier identifier,
			VFn2<IWritableDocument, Throwable> callback) {
		String uri = identifier.getUserRelativerUri();

		String prefix = fsRoot + "/" + identifier.getUser();
		String path = prefix + "/" + uri;

		prefix = new File(prefix).getAbsolutePath();
		try {
			File f = new File(path);
			if (!checkWriteAccessOrFail(f, prefix, callback))
				return;

			if (!ensureDirectoriesOrFail(f, callback))
				return;
			
			File mdFile = new File(Utils.constructMetadataPath(fsRoot, identifier));
			if (!ensureDirectoriesOrFail(mdFile, callback))
				return;

			callback.f(new WritableDocument(f, Utils.constructMetadataPath(fsRoot, identifier), Workers.FS_POOL), null);
		} catch (IOException e) {
			callback.f(null, e);
		}
	}
	
	private boolean ensureDirectoriesOrFail(File f, VFn2<IWritableDocument, Throwable> callback) {
		if (!f.exists()) {
			File parent = new File(f.getParent());
			if (!parent.exists()) {
				boolean made = ensureDirectories(parent);
				if (!made) {
					callback.f(null, new IOException(
							"Failed creating required directories"));
					return false;
				}
			}
		}
		
		return true;
	}

	private boolean ensureDirectories(File f) {
		boolean made = f.mkdirs();
		if (!made)
			log.error("Unable to make the requested directories. "
					+ f.getParent() + " " + f.getAbsolutePath());
		return made;
	}

	void retrieveResource(IResourceIdentifier identifier,
			Lo.VFn2<IResource, Throwable> callback) {
		String uri = identifier.getUserRelativerUri();
		String prefix = fsRoot + "/" + identifier.getUser();
		String path = prefix + "/" + uri;

		prefix = new File(prefix).getAbsolutePath();
		if (identifier.isDir()) {
			retrieveDirectoryResource(path, prefix, callback);
		} else {
			retrieveFileResource(path, prefix, Utils.constructMetadataPath(fsRoot, identifier), callback);
		}
	}
	
	private void retrieveDirectoryResource(String path, String prefix, VFn2<IResource, Throwable> callback) {
		File f = new File(path);
		if (!checkAccessOrFail(f, prefix, callback))
			return;

		if (f.exists()) {
			callback.f(new Directory(f.getName(), buildDirectoryListing(f)), null);
		} else {
			callback.f(null, new FileNotFoundException(
					"File not found for: " + path));
		}
	}
	
	private void retrieveFileResource(String path, String prefix, String mdPath, VFn2<IResource, Throwable> callback) {
		File f = new File(path);
		if (f.exists() && f.getAbsolutePath().startsWith(prefix)) {
			try {
				Document doc = new Document(f, mdPath);
				callback.f(doc, null);
			} catch (FileNotFoundException e) {
				callback.f(null, e);
			}
		} else {
			callback.f(null, new FileNotFoundException(
					"File not found for: " + path));
		}
	}
	
	// do the directory listing
	// TODO: spec has some weird stuff about empty folders
	// (e.g., saying they don't exist and not listing them)
	private Map<String, Object> buildDirectoryListing(File f) {
		File[] listing = f.listFiles();

		Map<String, Object> documentListing = new LinkedHashMap<>(
				listing.length);
		for (File file : listing) {
			String name = file.getName();
			if (file.isDirectory())
				name += "/";
			String version = MetadataUtils.getMetadata(Utils.getMetadataPathForFile(f, fsRoot)).get(HttpHeaders.Names.ETAG);
			if (version == null)
				version = "";
			
			documentListing.put(name, version);
		}

		return documentListing;
	}
}
