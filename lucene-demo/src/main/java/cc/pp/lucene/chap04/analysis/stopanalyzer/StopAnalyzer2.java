package cc.pp.lucene.chap04.analysis.stopanalyzer;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.LetterTokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

public class StopAnalyzer2 extends Analyzer {

	private final CharArraySet stopWords;

	private Version version;

	public StopAnalyzer2(Version version) {
		this.stopWords = StopAnalyzer.ENGLISH_STOP_WORDS_SET;
		this.version = version;
	}

	public StopAnalyzer2(String[] stopWords) {
		this.stopWords = StopFilter.makeStopSet(version, stopWords);
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
		return new TokenStreamComponents(null, new StopFilter(version, new LowerCaseFilter(version,
				new LetterTokenizer(version, reader)), stopWords));
	}

}
