package cc.pp.chap06.extsearch.payloads;

import org.apache.lucene.analysis.payloads.PayloadHelper;
import org.apache.lucene.search.DefaultSimilarity;

public class BoostingSimilarity extends DefaultSimilarity {

	private static final long serialVersionUID = 1L;

	@Override
	public float scorePayload(int docID, String fieldName, int start, //
			int end, byte[] payload, int offset, int length) {
		if (payload != null) {
			return PayloadHelper.decodeFloat(payload, offset);
		} else {
			return 1.0F;
		}
	}

}
