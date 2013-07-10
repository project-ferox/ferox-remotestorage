package com.tantaman.ferox.remotestorage.resource;

import java.io.File;
import java.util.List;

public interface IDirectoryResource extends IResource {
	public List<File> getListing();
}
