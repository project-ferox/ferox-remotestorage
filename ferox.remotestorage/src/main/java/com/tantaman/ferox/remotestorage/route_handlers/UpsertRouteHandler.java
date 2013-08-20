package com.tantaman.ferox.remotestorage.route_handlers;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tantaman.ferox.api.request_response.IHttpContent;
import com.tantaman.ferox.api.request_response.IHttpReception;
import com.tantaman.ferox.api.request_response.IHttpRequest;
import com.tantaman.ferox.api.request_response.IRequestChainer;
import com.tantaman.ferox.api.request_response.IResponse;
import com.tantaman.ferox.api.router.RouteHandlerAdapter;
import com.tantaman.ferox.remotestorage.StatusResponses;
import com.tantaman.ferox.remotestorage.resource.IResourceIdentifier;
import com.tantaman.ferox.remotestorage.resource.IResourceProvider;
import com.tantaman.ferox.remotestorage.resource.IWritableDocument;
import com.tantaman.ferox.remotestorage.route_handlers.rules.WriteRules;
import com.tantaman.ferox.remotestorage.route_handlers.rules.WriteRules.AbstractMatch;
import com.tantaman.ferox.util.Hex;
import com.tantaman.lo4j.Lo;

public class UpsertRouteHandler extends RouteHandlerAdapter {
	private final IResourceProvider resourceProvider;

	private static final Logger log = LoggerFactory.getLogger(UpsertRouteHandler.class);
	private final List<IHttpReception> receptionQueue = new LinkedList<>();

	private DocumentWriter writer;
	private MessageDigest currentDigest;
	private boolean ignoreContent = false;
	private static final WriteRules writeRules = new WriteRules();

	public UpsertRouteHandler(IResourceProvider resourceProvider) {
		this.resourceProvider = resourceProvider;
	}

	@Override
	public void request(final IHttpRequest request, final IResponse response,
			final IRequestChainer next) {
		IResourceIdentifier identifier = response.getUserData();
		try {
			currentDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e1) {
			log.error("No MD5 algorithm.  Cannot compute document revisions.");
		}
		
		receptionQueue.clear();
		receptionQueue.add(request);

		try {
			resourceProvider.openForWrite(identifier, new Lo.VFn2<IWritableDocument, Throwable>() {
				@Override
				public void f(IWritableDocument p1, Throwable p2) {
					openCallback(p1, p2, request, response);
				}
			});
		} catch (IllegalStateException e) {
			log.error("Bad state", e);
			response.send(StatusResponses.BAD_REQUEST, "application/json", HttpResponseStatus.BAD_REQUEST);
		}

		next.request(request);
	}

	private void openCallback(final IWritableDocument p1, final Throwable p2, final IHttpRequest request, final IResponse response) {
		response.executor().execute(new Runnable() {
			@Override
			public void run() {
				if (p1 != null) {
					final AbstractMatch match = writeRules.match(request.getHeaders(), p1.getMetadata());
					final AbstractMatch noneMatch = writeRules.noneMatch(request.getHeaders(), p1.getMetadata());
					
					if (match.allPass(noneMatch)) {
						writer = new DocumentWriter(p1);
						processReceptionQueue(response);
					} else {
						matchConstraintsViolated(match, noneMatch, response);
					}
				} else {
					log.error("Couldn't get resource", p2);
					response.send(StatusResponses.INTERNAL_ERROR, "application/json", HttpResponseStatus.INTERNAL_SERVER_ERROR);
				}
			}
		});
	}
	
	private void matchConstraintsViolated(AbstractMatch match, AbstractMatch noneMatch, IResponse response) {
		ignoreContent = true;
		
		receptionQueue.clear();
		
		boolean matchFail = !match.passes();
		boolean noneMatchFail = !match.passes();
		
		if (matchFail && noneMatchFail) {
			response.send(StatusResponses.MATCH_NONEMATCH_FAILED, "application/json", HttpResponseStatus.PRECONDITION_FAILED);
		} else if (matchFail) {
			response.send(StatusResponses.MATCH_FAILED, "application/json", HttpResponseStatus.PRECONDITION_FAILED);
		} else if (noneMatchFail) {
			response.send(StatusResponses.NONEMATCH_FAILED, "application/json", HttpResponseStatus.PRECONDITION_FAILED);
		} else {
			log.error("This code should be impossible to reach. " + match + " " + noneMatch);
		}
	}

	// TODO: abstract out the reception queue stuff
	// as the AccessControlRouteHandler is doing the same thing.
	private void processReceptionQueue(IResponse response) {
		log.debug("Processing reception queue");
		List<IHttpReception> drainedQueue;
		drainedQueue = new LinkedList<>(receptionQueue);
		receptionQueue.clear();

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
		if (ignoreContent) return;
		
		boolean canProceed = false;
		content.getContent().retain();

		if (receptionQueue.isEmpty())
			canProceed = true;
		else {
			receptionQueue.add(content);
		}

		if (canProceed) {
			processContent(content, response);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void processContent(IHttpContent content, IResponse response) {
		if (currentDigest != null)
			currentDigest.update(content.getContent().array());
		// The Write will release netty byte buffer once it is done with it.
		// hence the lack of a release in this class.
		writer.write(content.getContent());
		if (content.isLast()) {
			String type = content.getHeaders().get(HttpHeaders.Names.CONTENT_TYPE);
			String digest = "";
			if (currentDigest != null)
				digest = Hex.getHex(currentDigest.digest());
			writer.updateMetadata((Map)Lo.createMap(HttpHeaders.Names.CONTENT_TYPE, type, HttpHeaders.Names.ETAG, digest));
			writer.close();

			// TODO: various headers and what not
			// TODO: wait for the actual completion before responding?
			// We may not actually be done writing at this point.
			response.headers().set(HttpHeaders.Names.ETAG, digest);
			response.send(StatusResponses.SUCCESS, "application/json", HttpResponseStatus.OK);
		}
	}

	@Override
	public void exceptionCaught(Throwable cause, IResponse response,
			IRequestChainer next) {
		List<IHttpReception> drainedQueue;
		drainedQueue = new LinkedList<>(receptionQueue);
		receptionQueue.clear();

		for (IHttpReception r : drainedQueue) {
			if (r instanceof IHttpContent) {
				((IHttpContent)r).getContent().release();
			}
		}

		next.exceptionCaught(cause);
	}
}
