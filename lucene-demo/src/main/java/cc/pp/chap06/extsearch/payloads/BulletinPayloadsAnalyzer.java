package cc.pp.chap06.extsearch.payloads;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;

public class BulletinPayloadsAnalyzer extends Analyzer {

	private boolean isBulletin;
	private final float boost;

	public BulletinPayloadsAnalyzer(float boost) {
		this.boost = boost;
	}

	public void setIsBulletin(boolean v) {
		isBulletin = v;
	}

	@SuppressWarnings("resource")
	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {
		BulletinPayloadsFilter stream = new BulletinPayloadsFilter(//
				new StandardAnalyzer(Version.LUCENE_30).tokenStream(fieldName, reader), //
				boost);
		stream.setIsBulletin(isBulletin);
		return stream;
	}

}
