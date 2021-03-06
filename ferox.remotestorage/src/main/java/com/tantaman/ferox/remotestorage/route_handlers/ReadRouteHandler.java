package com.tantaman.ferox.remotestorage.route_handlers;

import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedNioStream;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tantaman.ferox.api.request_response.IHttpContent;
import com.tantaman.ferox.api.request_response.IRequestChainer;
import com.tantaman.ferox.api.request_response.IResponse;
import com.tantaman.ferox.api.router.RouteHandlerAdapter;
import com.tantaman.ferox.remotestorage.StatusResponses;
import com.tantaman.ferox.remotestorage.resource.IDirectoryResource;
import com.tantaman.ferox.remotestorage.resource.IDocumentResource;
import com.tantaman.ferox.remotestorage.resource.IResource;
import com.tantaman.ferox.remotestorage.resource.IResourceIdentifier;
import com.tantaman.ferox.remotestorage.resource.IResourceProvider;
import com.tantaman.lo4j.Lo;

public class ReadRouteHandler extends RouteHandlerAdapter {
	public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String HTTP_DATE_GMT_TIMEZONE = "GMT";
    public static final int HTTP_CACHE_SECONDS = 60;
    private static final Logger log = LoggerFactory.getLogger(ReadRouteHandler.class);
    
    private final IResourceProvider resourceProvider;
	
	public ReadRouteHandler(IResourceProvider resourceProvider) {
		this.resourceProvider = resourceProvider;
	}
	
	@Override
	public void lastContent(final IHttpContent content, final IResponse response,
			IRequestChainer next) {
		IResourceIdentifier identifier = response.getUserData();
		try {
			resourceProvider.openForRead(identifier, new Lo.VFn2<IResource, Throwable>() {
				@Override
				public void f(IResource p1, Throwable p2) {
					getResourceCallback(p1, p2, response, content);
				}
			});
		} catch (IllegalStateException e) {
			content.dispose();
			response.send(e.getMessage(), HttpResponseStatus.BAD_REQUEST);
		}
	}
	
	private void getResourceCallback(final IResource p1, final Throwable p2, final IResponse response, final IHttpContent content) {
		response.executor().execute(new Runnable() {
			@Override
			public void run() {
				try {
					if (p2 != null) {
						if (p2 instanceof FileNotFoundException) {
							response.send(StatusResponses.NOT_FOUND, "application/json", HttpResponseStatus.NOT_FOUND);
						} else {
							log.error("Error getting resource", p2);
							response.send(p2.getMessage(), HttpResponseStatus.INTERNAL_SERVER_ERROR);
						}
					} else {
						respondWithResource(p1, response, content);
					}
				} finally {
					content.dispose();
				}
			}
		});
	}
	
	private void respondWithResource(IResource p1, IResponse response, IHttpContent request) {
		if (p1 instanceof IDocumentResource) {
			IDocumentResource doc = (IDocumentResource)p1;
			try {
				returnDocument(doc, response, request);
			} catch (ParseException e) {
				log.warn("Couldn't parse request");
				response.send(StatusResponses.BAD_REQUEST, "application/json", HttpResponseStatus.BAD_REQUEST);
			} catch (FileNotFoundException e) {
				log.warn("File not found");
				response.send(StatusResponses.NOT_FOUND, "application/json", HttpResponseStatus.NOT_FOUND);
			}
		} else if (p1 instanceof IDirectoryResource) {
			IDirectoryResource dir = (IDirectoryResource)p1;
			
			StringBuilder b = new StringBuilder();
			response.send(Lo.asJsonObject(dir.getListing(), b).toString(), "application/json");
		}
	}
	
	private void returnDocument(final IDocumentResource doc, IResponse response, IHttpContent request) throws ParseException, FileNotFoundException {
		// Cache Validation
        String ifModifiedSince = request.getHeaders().get(HttpHeaders.Names.IF_MODIFIED_SINCE);
        if (ifModifiedSince != null && !ifModifiedSince.isEmpty()) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
            Date ifModifiedSinceDate = dateFormatter.parse(ifModifiedSince);

            // Only compare up to the second because the datetime format we send to the client
            // does not have milliseconds
            long ifModifiedSinceDateSeconds = ifModifiedSinceDate.getTime() / 1000;
            long fileLastModifiedSeconds = doc.lastModified() / 1000;
            if (ifModifiedSinceDateSeconds == fileLastModifiedSeconds) {
                sendNotModified(response);
                return;
            }
        }

        long fileLength = doc.length();

        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, fileLength);
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, doc.getContentType());
        response.headers().set(HttpHeaders.Names.ETAG, doc.getVersion());
        setDateAndCacheHeaders(response, doc);

        response.fineGrained().addResponseHeaders();
        if (fileLength > 0)
        	response.fineGrained().add(new ChunkedNioStream(doc.getStream(), 8192));
//        response.fineGrained().add(new ChunkedStream(doc.getStream(), 8192));
        response.fineGrained().add(LastHttpContent.EMPTY_LAST_CONTENT);

        response.fineGrained().write().addListener(new GenericFutureListener<Future<? super Void>>() {
        	public void operationComplete(Future<? super Void> arg0) throws Exception {
        		doc.close();
        	};
		});
	}
	
	private static void sendNotModified(IResponse response) {
        setDateHeader(response);
        response.fineGrained().addResponseHeaders(HttpResponseStatus.NOT_MODIFIED);
        response.fineGrained().add(LastHttpContent.EMPTY_LAST_CONTENT);

        response.fineGrained().writePartial().addListener(ChannelFutureListener.CLOSE);
    }
	
	private static void setDateHeader(IResponse response) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        Calendar time = new GregorianCalendar();
        response.headers().set(HttpHeaders.Names.DATE, dateFormatter.format(time.getTime()));
    }
	
	private static void setDateAndCacheHeaders(IResponse response, IDocumentResource fileToCache) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        // Date header
        Calendar time = new GregorianCalendar();
        response.headers().set(HttpHeaders.Names.DATE, dateFormatter.format(time.getTime()));

        // Add cache headers
        time.add(Calendar.SECOND, HTTP_CACHE_SECONDS);
        response.headers().set(HttpHeaders.Names.EXPIRES, dateFormatter.format(time.getTime()));
        response.headers().set(HttpHeaders.Names.CACHE_CONTROL, "private, max-age=" + HTTP_CACHE_SECONDS);
        response.headers().set(
                HttpHeaders.Names.LAST_MODIFIED, dateFormatter.format(new Date(fileToCache.lastModified())));
    }
}
