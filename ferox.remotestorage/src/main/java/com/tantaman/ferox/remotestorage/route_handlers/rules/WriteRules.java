package com.tantaman.ferox.remotestorage.route_handlers.rules;

import io.netty.handler.codec.http.HttpHeaders;

import java.util.Map;

public class WriteRules {
	private static enum EMatch {
			MATCH,
			MISMATCH,
			NIL
	}
	
	public abstract static class AbstractMatch {
		protected final EMatch e;
		public AbstractMatch(EMatch e) {
			this.e = e;
		}
		
		public abstract boolean passes();
		
		public boolean allPass(AbstractMatch m) {
			boolean passes = this.passes();
			
//			for (AbstractMatch m : matches) {
				passes = passes && m.passes();
//				if (!passes) break;
//			}
			
			return passes;
		}
	}
	
	private static class Match extends AbstractMatch {
		public Match(EMatch e) {
			super(e);
		}
		
		@Override
		public boolean passes() {
			return e == EMatch.MATCH || e == EMatch.NIL;
		}
	}
	
	public static class NoneMatch extends AbstractMatch {
		public NoneMatch(EMatch e) {
			super(e);
		}
		
		@Override
		public boolean passes() {
			return e != EMatch.MATCH;
		}
	}
	
	private static final AbstractMatch MATCH_NIL = new Match(EMatch.NIL);
	private static final AbstractMatch MATCH_MATCH = new Match(EMatch.MATCH);
	private static final AbstractMatch MATCH_MISMATCH = new Match(EMatch.MISMATCH);
	
	private static final AbstractMatch NONEMATCH_NIL = new NoneMatch(EMatch.NIL);
	private static final AbstractMatch NONEMATCH_MATCH = new NoneMatch(EMatch.MATCH);
	private static final AbstractMatch NONEMATCH_MISMATCH = new NoneMatch(EMatch.MISMATCH);
	
	public AbstractMatch match(HttpHeaders headers, Map<String, String> resourceMetadata) {
		String match = headers.get(HttpHeaders.Names.IF_MATCH);
		if (match == null)
			return MATCH_NIL;
		
		String etag = resourceMetadata.get(HttpHeaders.Names.ETAG);
		if (etag != null && match.equals(etag))
			return MATCH_MATCH;
		return MATCH_MISMATCH;
	}
	
	public AbstractMatch noneMatch(HttpHeaders headers, Map<String, String> resourceMetadata) {
		String match = headers.get(HttpHeaders.Names.IF_NONE_MATCH);
		if (match == null)
			return NONEMATCH_NIL;
		
		String etag = resourceMetadata.get(HttpHeaders.Names.ETAG);
		if (match.equals("*")) {
			if (etag == null)
				return NONEMATCH_MISMATCH;
			return NONEMATCH_MATCH;
		}
		
		if (match.equals(etag))
			return NONEMATCH_MATCH;
		
		return NONEMATCH_MISMATCH;
	}
}
