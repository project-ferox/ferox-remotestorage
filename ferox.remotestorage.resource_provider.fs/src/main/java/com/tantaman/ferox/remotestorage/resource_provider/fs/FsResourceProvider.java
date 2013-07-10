package com.tantaman.ferox.remotestorage.resource_provider.fs;

import java.io.File;
import java.util.Map;

import com.tantaman.ferox.remotestorage.ConfigKeys;
import com.tantaman.ferox.remotestorage.resource.IResource;
import com.tantaman.ferox.remotestorage.resource.IResourceIdentifier;
import com.tantaman.ferox.remotestorage.resource.IResourceProvider;
import com.tantaman.lo4j.Lo;

public class FsResourceProvider implements IResourceProvider {
	private final String fsRoot;
	
	public FsResourceProvider(Map<String, String> configuration) {
		fsRoot = configuration.get(ConfigKeys.FS_STORAGE_ROOT);
	}
	
	// TODO: do authentication in an earlier layer
	@Override
	public void getResource(final IResourceIdentifier identifier, final Lo.Fn<Void, IResource> callback) {
		Workers.FS_EVENT_QUEUE.execute(new Runnable() {
			@Override
			public void run() {
				retrieveResource(identifier, callback);
			}
		});
	}
	
	private void retrieveResource(IResourceIdentifier identifier, Lo.Fn<Void, IResource> callback) {
		String uri = identifier.getUserRelativerUri();
		
		String path = fsRoot + "/" + identifier.getUser() + "/" + uri.replace("..", "");
		
		IResource result;
		if (identifier.isDir()) {
			// do the directory listing
			// TODO: spec has some weird stuff about empty folders
			// (e.g., saying they don't exist and not listing them)
			File f = new File(path);
			f.listFiles();
		} else {
			File f = new File(path);
			
		}
	}

}
