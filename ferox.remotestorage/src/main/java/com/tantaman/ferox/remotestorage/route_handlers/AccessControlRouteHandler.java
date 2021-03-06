package com.tantaman.ferox.remotestorage.route_handlers;

import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tantaman.ferox.api.request_response.IHttpContent;
import com.tantaman.ferox.api.request_response.IHttpReception;
import com.tantaman.ferox.api.request_response.IHttpRequest;
import com.tantaman.ferox.api.request_response.IRequestChainer;
import com.tantaman.ferox.api.request_response.IResponse;
import com.tantaman.ferox.api.router.IRouteHandler;
import com.tantaman.ferox.remotestorage.StatusResponses;
import com.tantaman.ferox.remotestorage.auth_manager.IAuthManager;
import com.tantaman.ferox.remotestorage.resource.IResourceIdentifier;
import com.tantaman.ferox.util.IPair;
import com.tantaman.ferox.util.Pair;
import com.tantaman.lo4j.Lo;

public class AccessControlRouteHandler implements IRouteHandler {
	private static final Logger log = LoggerFactory.getLogger(AccessControlRouteHandler.class);

	private final IAuthManager authManager;

	// synch shouldn't be required anymore due to the user of response.executor to get the
	// event executor that belongs to this channel (I believe).
	private /*volatile*/ boolean authorized = false;
	private final List<IPair<IRequestChainer, IHttpReception>> receptionQueue = new LinkedList<>();

	public AccessControlRouteHandler(IAuthManager authManager) {
		this.authManager = authManager;
	}

	@Override
	public void request(final IHttpRequest request, final IResponse response,
			final IRequestChainer next) {
		IResourceIdentifier resourceIdentifier = response.getUserData();
		authorized = false;
		receptionQueue.clear();
		receptionQueue.add(new Pair<IRequestChainer, IHttpReception>(next, request));

		authManager.isAuthorized(resourceIdentifier,
				request.getHeaders().get(HttpHeaders.Names.AUTHORIZATION),
				request.getMethod(),
				new Lo.VFn2<Boolean, Throwable>() {
			@Override
			public void f(final Boolean authorized, final Throwable err) {
				authCallback(authorized, err, response, request);
			}
		});
	}

	private void authCallback(final Boolean authorized, final Throwable err, final IResponse response, final IHttpRequest request) {
		response.executor().execute(new Runnable() {
			@Override
			public void run() {
				if (authorized != null && authorized) {
					AccessControlRouteHandler.this.authorized = true;
					processReceptionQueue();
				} else {
					log.debug("Authorization failed");
					response.send(StatusResponses.NOT_AUTHORIZED, "application/json", HttpResponseStatus.UNAUTHORIZED)
					.addListener(ChannelFutureListener.CLOSE);
					request.dispose();
					receptionQueue.clear();
				}
			}
		});
	}

	private void processReceptionQueue() {
		try {
			for (IPair<IRequestChainer, IHttpReception> reception : receptionQueue) {
				if (reception.getSecond() instanceof IHttpRequest) {
					reception.getFirst().request((IHttpRequest)reception.getSecond());
				}

				if (reception.getSecond() instanceof IHttpContent) {
					IHttpContent content = (IHttpContent)reception.getSecond();

					if (content.isLast()) {
						reception.getFirst().lastContent(content);
					} else {
						reception.getFirst().content(content);
					}
				}
			}
		} finally {
			for (IPair<IRequestChainer, IHttpReception> reception : receptionQueue) {
				if (reception.getSecond() instanceof IHttpContent) {
					((IHttpContent)reception.getSecond()).getContent().release();
				}
			}
			receptionQueue.clear();
		}
	}

	@Override
	public void content(IHttpContent content, IResponse response,
			IRequestChainer next) {
		boolean canProceed = false;
		if (receptionQueue.isEmpty()) {
			canProceed = true;
		} else {
			content.getContent().retain();
			if (!(content instanceof IHttpRequest))
				receptionQueue.add(new Pair<IRequestChainer, IHttpReception>(next, content));
		}

		if (canProceed) {
			if (authorized) next.content(content);
			else content.dispose();
		}
	}

	@Override
	public void lastContent(IHttpContent content, IResponse response,
			IRequestChainer next) {
		boolean canProceed = false;
		if (receptionQueue.isEmpty()) {
			canProceed = true;
		} else {
			content.getContent().retain();
			if (!(content instanceof IHttpRequest))
				receptionQueue.add(new Pair<IRequestChainer, IHttpReception>(next, content));
		}

		if (canProceed) {
			if (authorized) next.lastContent(content);
			else content.dispose();
		}
	}

	@Override
	public void exceptionCaught(Throwable cause, IResponse response, IRequestChainer next) {
		try {
			for (IPair<IRequestChainer, IHttpReception> pair : receptionQueue) {
				pair.getFirst().exceptionCaught(cause);
			}
		} finally {
			for (IPair<IRequestChainer, IHttpReception> pair : receptionQueue) {
				IHttpReception r = pair.getSecond();
				if (r instanceof IHttpContent) {
					((IHttpContent)r).getContent().release();
				}
			}

			receptionQueue.clear();
			next.exceptionCaught(cause);
		}
	}
}
