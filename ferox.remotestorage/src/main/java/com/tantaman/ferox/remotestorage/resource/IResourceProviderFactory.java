package com.tantaman.ferox.remotestorage.resource;

import java.util.Map;

public interface IResourceProviderFactory {
	public IResourceProvider createResourceProvider(Map<String, String> configuration);
}
