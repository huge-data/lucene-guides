package cc.pp.chap06.extsearch.payloads;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.payloads.PayloadHelper;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.index.Payload;

public class BulletinPayloadsFilter extends TokenFilter {

	private final TermAttribute termAttr;
	private final PayloadAttribute payloadAttr;
	private boolean isBulletin;
	private final Payload boostPayload;

	protected BulletinPayloadsFilter(TokenStream in, float warningBoost) {
		super(in);
		payloadAttr = addAttribute(PayloadAttribute.class);
		termAttr = addAttribute(TermAttribute.class);
		boostPayload = new Payload(PayloadHelper.encodeFloat(warningBoost));
	}

	@Override
	public boolean incrementToken() throws IOException {

		if (input.incrementToken()) {
			if (isBulletin && termAttr.term().equals("warning")) {
				payloadAttr.setPayload(boostPayload);
			} else {
				payloadAttr.setPayload(null);
			}
			return true;
		} else {
			return false;
		}
	}

	public void setIsBulletin(boolean v) {
		isBulletin = v;
	}

}
