package com.tantaman.ferox.remotestorage.server.standalone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tantaman.ferox.api.server.IPluggableServer;

public class Main {
	private static final Logger log = LoggerFactory.getLogger(Main.class); 
	
	void setPluggableServer(IPluggableServer server) {
		// the remoteStorage spec calls to just save the entire request body as the contents
		// of the document.  This seems rather restrictive as
		// remoteStorage will never be able to support file uploads in a sensible manner.
		
		// We don't want to ever use the body parser since in 99% of the cases 
		// where a user is putting to RS it won't be applicable.
//		server.use(ChannelMiddleware.BODY_PARSER);
		log.debug("RemoteStorage listening on 8080");
		server.listen(8080, false);
	}
}
