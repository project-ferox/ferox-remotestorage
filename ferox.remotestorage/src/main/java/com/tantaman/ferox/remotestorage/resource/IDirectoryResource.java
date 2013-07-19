package com.tantaman.ferox.remotestorage.resource;

import java.util.Map;

public interface IDirectoryResource extends IResource {
	public Map<String, String> getListing();
}
