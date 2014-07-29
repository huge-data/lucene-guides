package cc.pp.lucene.chap04.analysis.synonym;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

import cc.pp.lucene.common.LuceneConstant;

public class SynonymAnalyzer extends Analyzer {

	private final SynonymEngine engine;

	private final Version version;

	public SynonymAnalyzer(SynonymEngine engine, Version version) {
		this.engine = engine;
		this.version = version;
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
		TokenStream tokenStream = new SynonymFilter(new StopFilter(version, new LowerCaseFilter(version,
				new StandardFilter(version, new StandardTokenizer(LuceneConstant.LUCENE_VERSION, reader))),
				StopAnalyzer.ENGLISH_STOP_WORDS_SET), engine);
		return new TokenStreamComponents(null, tokenStream);
	}

}
