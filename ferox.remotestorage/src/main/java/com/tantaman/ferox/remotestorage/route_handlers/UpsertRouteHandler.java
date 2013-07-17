package com.tantaman.ferox.remotestorage.route_handlers;

import io.netty.handler.codec.http.HttpResponseStatus;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tantaman.ferox.api.request_response.IHttpContent;
import com.tantaman.ferox.api.request_response.IHttpReception;
import com.tantaman.ferox.api.request_response.IHttpRequest;
import com.tantaman.ferox.api.request_response.IRequestChainer;
import com.tantaman.ferox.api.request_response.IResponse;
import com.tantaman.ferox.api.router.RouteHandlerAdapter;
import com.tantaman.ferox.remotestorage.resource.IResourceIdentifier;
import com.tantaman.ferox.remotestorage.resource.IResourceOutputQueue;
import com.tantaman.ferox.remotestorage.resource.IResourceProvider;
import com.tantaman.lo4j.Lo;

public class UpsertRouteHandler extends RouteHandlerAdapter {
	private final IResourceProvider resourceProvider;

	private static final Logger log = LoggerFactory.getLogger(UpsertRouteHandler.class);
	private final List<IHttpReception> receptionQueue = new LinkedList<>();
	private volatile IResourceOutputQueue resource;

	public UpsertRouteHandler(IResourceProvider resourceProvider) {
		this.resourceProvider = resourceProvider;
	}

	@Override
	public void request(final IHttpRequest request, final IResponse response,
			final IRequestChainer next) {
		IResourceIdentifier identifier = response.getUserData();

		synchronized (receptionQueue) {
			receptionQueue.clear();
			receptionQueue.add(request);
		}

		try {
			resourceProvider.openForWrite(identifier, new Lo.VFn2<IResourceOutputQueue, Throwable>() {
				@Override
				public void f(IResourceOutputQueue p1, Throwable p2) {
					if (p1 != null) {
						resource = p1;
						processReceptionQueue(response);
					} else {
						log.error("Couldn't get resource", p2);
						response.send(Lo.asJsonObject("status", "error"), "application/json", HttpResponseStatus.INTERNAL_SERVER_ERROR);
					}
				}
			});
		} catch (IllegalStateException e) {
			log.error("Bad state", e);
			response.send(Lo.asJsonObject("status", "bad_request"), "application/json", HttpResponseStatus.BAD_REQUEST);
		}

		next.request(request);
	}

	// TODO: abstract out the reception queue stuff
	// as the AccessControlRouteHandler is doing the same thing.
	private void processReceptionQueue(IResponse response) {
		log.debug("Processing reception queue");
		List<IHttpReception> drainedQueue;
		synchronized (receptionQueue) {
			drainedQueue = new LinkedList<>(receptionQueue);
			receptionQueue.clear();
		}

		for (IHttpReception reception : drainedQueue) {
			if (reception instanceof IHttpContent) {
				IHttpContent content = (IHttpContent)reception;
				processContent(content, response);
			}
		}
	}

	@Override
	public void content(IHttpContent content, IResponse response,
			IRequestChainer next) {
		preprocess(content, next, response);
	}

	@Override
	public void lastContent(IHttpContent content, IResponse response,
			IRequestChainer next) {
		preprocess(content, next, response);

		// hmm.. this dumb thing...
		content.dispose();
	}

	private void preprocess(IHttpContent content, IRequestChainer next, IResponse response) {
		boolean canProceed = false;
		synchronized (receptionQueue) {
			if (receptionQueue.isEmpty())
				canProceed = true;
			else {
				content.getContent().retain();
				receptionQueue.add(content);
			}
		}

		if (canProceed) {
			processContent(content, response);
		}
	}

	private void processContent(IHttpContent content, IResponse response) {
		try {
			resource.add(content.getContent().nioBuffer());
			if (content.isLast()) {
				try {
					resource.close();
					// TODO: various headers and what not
					response.send(Lo.asJsonObject("status", "ok"), "application/json", HttpResponseStatus.OK);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} finally {
			content.getContent().release();
		}
	}
	
	@Override
	public void exceptionCaught(Throwable cause, IResponse response,
			IRequestChainer next) {
		List<IHttpReception> drainedQueue;
		synchronized (receptionQueue) {
			drainedQueue = new LinkedList<>(receptionQueue);
			receptionQueue.clear();
		}
		
		for (IHttpReception r : drainedQueue) {
			if (r instanceof IHttpContent) {
				((IHttpContent)r).getContent().release();
			}
		}
		
		next.exceptionCaught(cause);
	}

	// just save each content chunk into the file as per the spec....
	// we should probably check for a custom header that'll indicate if it is a file upload...
	// and grab the file in that case.  It'll require doing the body parsing in that case.
	// So we can make a special case body parser for remotestorage that'll only do body parsing when that header
	// is present.

	// X-Rs-Subtype: file(s)
	// requests of this type will be "put" to a directory.
	// documents will be created for each file contained in the request.

	//content.getContent();
}
