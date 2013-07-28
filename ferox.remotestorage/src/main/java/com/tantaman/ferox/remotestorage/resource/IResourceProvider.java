package com.tantaman.ferox.remotestorage.resource;

import com.tantaman.lo4j.Lo;

public interface IResourceProvider {
	public void openForRead(IResourceIdentifier path, Lo.VFn2<IResource, Throwable> callback) throws IllegalStateException;
	public void openForWrite(IResourceIdentifier path, Lo.VFn2<IWritableDocument, Throwable> callback);
	public void delete(IResourceIdentifier path, Lo.VFn2<Object, Throwable> callback);
}
