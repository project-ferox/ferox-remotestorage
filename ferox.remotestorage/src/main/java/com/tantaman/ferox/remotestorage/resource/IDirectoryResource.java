package com.tantaman.ferox.remotestorage.resource;

import java.util.List;

public interface IDirectoryResource extends IResource {
	public List<IDocumentResource> getListing();
}
