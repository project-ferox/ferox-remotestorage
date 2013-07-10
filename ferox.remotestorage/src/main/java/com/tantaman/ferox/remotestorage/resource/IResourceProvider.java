package com.tantaman.ferox.remotestorage.resource;

import com.tantaman.lo4j.Lo;

public interface IResourceProvider {
	public void getResource(IResourceIdentifier path, Lo.Fn<Void, IResource> callback);
}
