package com.tantaman.ferox.remotestorage.resource;

import java.util.Map;

public interface IDirectoryResource extends IResource {
	// where object is a String or Long.  Due to incompatibilities between RemoateStorage-00 and 01 and associated clients it can't just be a String.
	public Map<String, Object> getListing();
}
