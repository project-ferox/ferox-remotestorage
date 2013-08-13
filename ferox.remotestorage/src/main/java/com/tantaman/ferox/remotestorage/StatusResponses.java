package com.tantaman.ferox.remotestorage;

import com.tantaman.lo4j.Lo;

public class StatusResponses {
	public static final String SUCCESS = Lo.asJsonObject("success", true);
	
	public static final String NOT_AUTHORIZED = Lo.asJsonObject
			("success", false,
			"error", "not_authorized");
	
	public static final String NOT_FOUND = Lo.asJsonObject
			("success", false,
			"error", "not_found");
	
	public static final String INTERNAL_ERROR = Lo.asJsonObject
			("success", false,
			"error", "internal");
	
	public static final String RESOURCE_TOO_LARGE = Lo.asJsonObject
			("success", false,
			"error", "resource_size_limit");
	public static final String QUOTA_REACHED = Lo.asJsonObject
			("success", false,
			"error", "quota");
	public static final String BAD_REQUEST = Lo.asJsonObject
			("success", false,
			"error", "bad_request");
	
	public static final String MATCH_NONEMATCH_FAILED = Lo.asJsonObject
			("success", false,
			"error", "precondition",
			"failures", new String [] {"match", "none_match"});
	
	public static final String MATCH_FAILED = Lo.asJsonObject
			("success", false,
			"error", "precondition",
			"failures", new String [] {"match", "none_match"});
	
	public static final String NONEMATCH_FAILED = Lo.asJsonObject
			("success", false,
			"error", "precondition",
			"failures", new String [] {"match", "none_match"});
}
