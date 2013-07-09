package com.tantaman.ferox.remotestorage.server.standalone;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import com.tantaman.ferox.remotestorage.ConfigKeys;

public class Main {
	public void setConfigAdmin(ConfigurationAdmin configAdmin) {
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
		String authManagerRootUri = System.getProperty("ferox.remotestorage.authManagerRootUri");
		String authManagerPassword = System.getProperty("ferox.remotestorage.authManagerPassword");
		
		dict.put(ConfigKeys.FS_STORAGE_ROOT, fsRoot);
		
		if (rootUri == null)
			rootUri = "/remotestorage";
		
		dict.put(ConfigKeys.STORAGE_ROOT_URI, rootUri);
		dict.put(ConfigKeys.AUTH_DIALOG_URI, authDialogUri);
		
		if (authManagerRootUri == null)
			authManagerRootUri = "/remotestorage/auth_manager";
		
		dict.put(ConfigKeys.AUTH_MANAGER_URI, authManagerRootUri);
		
		if (authManagerPassword != null)		
			dict.put(ConfigKeys.AUTH_MANAGER_PASSWORD, authManagerPassword);
	}
}