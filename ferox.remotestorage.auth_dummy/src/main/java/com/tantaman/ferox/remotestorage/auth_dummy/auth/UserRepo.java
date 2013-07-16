package com.tantaman.ferox.remotestorage.auth_dummy.auth;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Map;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import com.tantaman.ferox.remotestorage.auth_dummy.ConfigKeys;

public class UserRepo {
	private volatile DB db;
	private volatile BTreeMap<String, byte[]> userMap;
	private static final byte [] worthless = new byte [] {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
	
	public UserRepo() {
	}
	
	public void activate(Map<String, String> configuration) {
		String path = configuration.get(ConfigKeys.DB_FILE);
		db = DBMaker.newFileDB(new File(path)).cacheDisable().closeOnJvmShutdown().make();
		
		userMap = db.getTreeMap("users");
	}
	
	public void deactivate() {
		db.close();
	}
	
	private static byte [] hashPassword(String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
		KeySpec spec = new PBEKeySpec(password.toCharArray(), worthless, 65536, 128);
		SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		return f.generateSecret(spec).getEncoded();
	}
	
	public void addUser(String username, String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
		userMap.put(username, hashPassword(password));
		db.commit();
	}
	
	public boolean exists(String username) {
		return userMap.get(username) != null;
	}
	
	public void clearUsers() {
		userMap.clear();
		db.commit();
	}
	
	public boolean authenticate(String username, String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
		byte [] expected = userMap.get(username);
		
		if (expected == null) return false;
		
		byte [] actual = hashPassword(password);
		
		return Arrays.equals(expected, actual);
	}
}
