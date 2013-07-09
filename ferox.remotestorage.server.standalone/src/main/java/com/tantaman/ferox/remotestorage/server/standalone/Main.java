package com.tantaman.ferox.remotestorage.server.standalone;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

public class Main {
	public void setConfigAdmin(ConfigurationAdmin configAdmin) {
		createConfig("ferox.remotestorage.WebfingerMiddlewareInitializer", configAdmin);
		createConfig("ferox.remotestorage.RouteInitializer", configAdmin);
	}
	
	private void createConfig(String id, ConfigurationAdmin configAdmin) {
		try {
			Configuration config = configAdmin.getConfiguration(id);
			
			Dictionary<String, Object> dict = config.getProperties();
			
			if (dict == null)
				dict = new Hashtable<>();
			
			update(dict);
			
			config.update(dict);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void update(Dictionary<String, Object> dict) {
		String fsRoot = System.getProperty("ferox.remotestorage.fsStorageRoot");
		String rootUri = System.getProperty("ferox.remotestorage.storageRootUri");
		String authDialogUri = System.getProperty("ferox.remotestorage.authDialogUri");
		
		dict.put("fsStorageRoot", fsRoot);
		dict.put("storageRootUri", rootUri);
		dict.put("authDialogUri", authDialogUri);
	}
}
