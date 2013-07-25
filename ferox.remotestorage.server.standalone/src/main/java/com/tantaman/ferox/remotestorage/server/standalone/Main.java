package com.tantaman.ferox.remotestorage.server.standalone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tantaman.ferox.api.server.IPluggableServer;

public class Main {
	private static final Logger log = LoggerFactory.getLogger(Main.class);
	private volatile IPluggableServer server;
	
	void setPluggableServer(IPluggableServer server) {
		// the remoteStorage spec calls to just save the entire request body as the contents
		// of the document.  This seems rather restrictive as
		// remoteStorage will never be able to support file uploads in a sensible manner.
		
		// We don't want to ever use the body parser since in 99% of the cases 
		// where a user is putting to RS it won't be applicable.
//		server.use(ChannelMiddleware.BODY_PARSER);
		
		int port;
		try {
			port = Integer.parseInt(System.getProperty("remotestorage.server.port"));
		} catch (NumberFormatException e) {
			port = 8443;
		}
		log.debug("RemoteStorage listening on " + port);
		this.server = server;
		server.listen(port, true);
	}
	
	void deactivate() {
		server.shutdown();
	}
}
