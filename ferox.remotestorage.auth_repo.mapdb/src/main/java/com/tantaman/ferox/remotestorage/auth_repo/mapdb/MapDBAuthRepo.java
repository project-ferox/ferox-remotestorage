package com.tantaman.ferox.remotestorage.auth_repo.mapdb;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tantaman.ferox.remotestorage.auth_manager.IAuthRepo;
import com.tantaman.ferox.util.IPair;
import com.tantaman.ferox.util.Pair;
import com.tantaman.lo4j.Lo;

public class MapDBAuthRepo implements IAuthRepo {
	private static final Logger log = LoggerFactory.getLogger(MapDBAuthRepo.class);
	private volatile DB db;
	// Map from bearerToken -> (username, (scopes...))
	private volatile BTreeMap<String, IPair<String, Set<String>>> authMap;
	
	private static interface CacheTypes {
		public static final String SOFT = "soft";
		public static final String LRU = "lru";
	}
	
	public void activate(Map<String, String> configuration) {
		log.debug("Activating the auth repo");
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
		
//		authMap.put("token", new Pair<String, Set<String>>("matt", (Set)Lo.createSet("root:rw")));
//		db.commit();
		
		log.debug("Database made: " + db);
	}
	
	public void deactivate() {
		log.debug("Deactivating the auth repo");
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
		
		// Because mutating what is already in the map will break it.
		IPair<String, Set<String>> newAccess = new Pair<String, Set<String>>(username, scopes);
		
		if (access != null) {
			newAccess.getSecond().addAll(access.getSecond());
		}
		
		authMap.put(bearerToken, newAccess);
		db.commit();
	}
	
	@Override
	public synchronized void removeScopes(String bearerToken,
			Set<String> scopes) {
		IPair<String, Set<String>> access = authMap.get(bearerToken);
		if (access != null) {
			// Because mutating what is already in the map will break it.
			IPair<String, Set<String>> newAccess = new Pair<String, Set<String>>(access.getFirst(), new HashSet<>(access.getSecond()));
			newAccess.getSecond().removeAll(scopes);

			authMap.put(bearerToken, newAccess);
			db.commit();
		}
	}
	
	@Override
	public void revokeAccess(String bearerToken) {
		authMap.remove(bearerToken);
	}
}
