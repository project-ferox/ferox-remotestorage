package com.tantaman.ferox.remotestorage.server.standalone;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

public class Main {
	public void setConfigAdmin(ConfigurationAdmin configAdmin) {
		Configuration config;
		try {
			config = configAdmin.createFactoryConfiguration("ferox.remotestorage.FilesystemResourceProvider");
			Dictionary<String, Object> dict = config.getProperties();
			
			if (dict == null)
				dict = new Hashtable<>();
			
			dict.put("fsStorageRoot", "resources/remotestorage");
			
			config.update(dict);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
