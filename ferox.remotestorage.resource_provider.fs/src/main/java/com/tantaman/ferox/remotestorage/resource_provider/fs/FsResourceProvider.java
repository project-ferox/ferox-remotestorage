package com.tantaman.ferox.remotestorage.resource_provider.fs;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tantaman.ferox.remotestorage.ConfigKeys;
import com.tantaman.ferox.remotestorage.resource.IResource;
import com.tantaman.ferox.remotestorage.resource.IResourceIdentifier;
import com.tantaman.ferox.remotestorage.resource.IResourceProvider;
import com.tantaman.lo4j.Lo;

public class FsResourceProvider implements IResourceProvider {
	private String fsRoot;
	
	public void activate(Map<String, String> configuration) {
		fsRoot = configuration.get(ConfigKeys.FS_STORAGE_ROOT);
	}
	
	@Override
	public void getResource(final IResourceIdentifier identifier, final Lo.VFn2<IResource, Throwable> callback) throws IllegalStateException {
		if (identifier.getModule().equals(".metadata")) throw new IllegalStateException("Illegal module");
		
		Workers.FS_EVENT_QUEUE.execute(new Runnable() {
			@Override
			public void run() {
				retrieveResource(identifier, callback);
			}
		});
	}
	
	private void retrieveResource(IResourceIdentifier identifier, Lo.VFn2<IResource, Throwable> callback) {
		String uri = identifier.getUserRelativerUri();
		
		String path = fsRoot + "/" + identifier.getUser() + "/" + uri.replace("..", "");
		
		if (identifier.isDir()) {
			// do the directory listing
			// TODO: spec has some weird stuff about empty folders
			// (e.g., saying they don't exist and not listing them)
			File f = new File(path);
			if (f.exists()) {
				File [] listing = f.listFiles();
				
				List<IResource> documentListing = new ArrayList<>(listing.length);
				for (File file : listing) {
					documentListing.add(ResourceFactory.create(file));
				}
				
				callback.f(new Directory(f.getName(), documentListing), null);
			} else {
				callback.f(null, new FileNotFoundException("File not found for: " + path));
			}
		} else {
			File f = new File(path);
			if (f.exists()) {
				try {
					callback.f(new Document(f), null);
				} catch (Exception e) {
					callback.f(null, e);
				}
			} else {
				callback.f(null, new FileNotFoundException());
			}
		}
	}

}
