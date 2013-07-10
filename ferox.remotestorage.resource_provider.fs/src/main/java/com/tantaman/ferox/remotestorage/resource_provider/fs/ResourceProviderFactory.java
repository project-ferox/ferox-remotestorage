package com.tantaman.ferox.remotestorage.resource_provider.fs;

import java.util.Map;

import com.tantaman.ferox.remotestorage.resource.IResourceProvider;
import com.tantaman.ferox.remotestorage.resource.IResourceProviderFactory;

public class ResourceProviderFactory implements IResourceProviderFactory {
	@Override
	public IResourceProvider createResourceProvider(
			Map<String, String> configuration) {
		return new FsResourceProvider(configuration);
	}
}
