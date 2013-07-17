package com.tantaman.ferox.remotestorage.resource_provider.fs;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Workers {
	public static final ExecutorService FS_POOL = Executors.newFixedThreadPool(4);
}
