package com.tantaman.ferox.remotestorage.auth_repo.mapdb;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import com.tantaman.ferox.remotestorage.auth_management.IAuthRepo;
import com.tantaman.ferox.util.IPair;
import com.tantaman.ferox.util.Pair;

public class MapDBAuthRepo implements IAuthRepo {
	private volatile DB db;
	// Map from bearerToken -> (username, (scopes...))
	private volatile BTreeMap<String, IPair<String, Set<String>>> authMap;
	
	private static interface CacheTypes {
		public static final String SOFT = "soft";
		public static final String LRU = "lru";
	}
	
	public void activate(Map<String, String> configuration) {
		String password = configuration.get(ConfigKeys.PASSWORD);
		String file = configuration.get(ConfigKeys.FILE);
		String cacheSize = configuration.get(ConfigKeys.CACHE_SIZE);
		String cacheType = configuration.get(ConfigKeys.CACHE_TYPE);
		String collectionName = configuration.get(ConfigKeys.COLLECTION_NAME);
		
		if (collectionName == null)
			collectionName = this.getClass().getName();
		
		int cacheSizeI = 32768;
		try {
			cacheSizeI = Integer.parseInt(cacheSize);
		} catch (Exception e) {}
		
		if (cacheType == null) cacheType = CacheTypes.LRU;
		
		if (file == null) throw new IllegalStateException("Map db must have a file specified in its configuration");
		
		DBMaker maker = DBMaker.newFileDB(new File(file))
					.closeOnJvmShutdown()
					.cacheSize(cacheSizeI);
		
		if (password != null) {
			maker.encryptionEnable(password);
		}
		
		switch (cacheType) {
		case CacheTypes.SOFT:
			maker.cacheSoftRefEnable();
			break;
		case CacheTypes.LRU:
			maker.cacheLRUEnable();
			break;
		}
		
		db = maker.make();
		authMap = db.getTreeMap(collectionName);
	}
	
	public void deactivate() {
		db.close();
	}
	
	@Override
	public IPair<String, Set<String>> getScopes(String bearerToken) {
		return authMap.get(bearerToken);
	}

	@Override
	public synchronized void addScopes(String bearerToken, String username,
			Set<String> scopes) {
		IPair<String, Set<String>> access = authMap.get(bearerToken);
		
		if (access == null) {
			access = new Pair<String, Set<String>>(username, new HashSet<String>());
			authMap.put(bearerToken, access);
		}
		
		access.getSecond().addAll(scopes);
	}
	
	@Override
	public synchronized void removeScopes(String bearerToken,
			Set<String> scopes) {
		IPair<String, Set<String>> access = authMap.get(bearerToken);
		
		if (access != null) {
			access.getSecond().removeAll(scopes);
		}
	}
	
	@Override
	public void revokeAccess(String bearerToken) {
		authMap.remove(bearerToken);
	}
}
