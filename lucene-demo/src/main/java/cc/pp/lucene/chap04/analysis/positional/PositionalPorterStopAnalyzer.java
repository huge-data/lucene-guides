package cc.pp.lucene.chap04.analysis.positional;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.LowerCaseTokenizer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

public class PositionalPorterStopAnalyzer extends Analyzer {

	private final CharArraySet stopWords;

	private Version version;

	public PositionalPorterStopAnalyzer(Version version) {
		this(StopAnalyzer.ENGLISH_STOP_WORDS_SET);
		this.version = version;
	}

	public PositionalPorterStopAnalyzer(CharArraySet stopWords) {
		this.stopWords = stopWords;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
		LowerCaseTokenizer lowerCaseTokenizer = new LowerCaseTokenizer(version, reader);
		StopFilter stopFilter = new StopFilter(version, lowerCaseTokenizer, stopWords);
		stopFilter.setEnablePositionIncrements(true);
		return new TokenStreamComponents(null, new PorterStemFilter(stopFilter));
	}
}
