package com.tantaman.ferox.remotestorage.resource_provider.fs;

import java.io.File;

public class Utils {
	public static boolean allowedFile(File f, String prefix) {
		String fullPath = f.getAbsolutePath();
		return fullPath.startsWith(prefix);
	}
}
