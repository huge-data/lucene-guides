package cc.pp.lucene.chap04.analysis.stopanalyzer;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.LowerCaseTokenizer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

public class StopAnalyzer1 extends Analyzer {

	private final CharArraySet stopWords;

	private Version version;

	public StopAnalyzer1(Version version) {
		this.stopWords = StopAnalyzer.ENGLISH_STOP_WORDS_SET;
		this.version = version;
	}

	public StopAnalyzer1(String[] stopWords) {
		this.stopWords = StopFilter.makeStopSet(version, stopWords);
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
		return new TokenStreamComponents(null, new StopFilter(version, new LowerCaseTokenizer(version, reader),
				stopWords));
	}

}
