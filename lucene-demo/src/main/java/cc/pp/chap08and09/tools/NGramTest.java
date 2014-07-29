package cc.pp.chap08and09.tools;

import java.io.IOException;
import java.io.Reader;

import junit.framework.TestCase;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordTokenizer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;

import cc.pp.lucene.chap04.analysis.AnalyzerUtils;

public class NGramTest extends TestCase {

	private static class BackEdgeNGramAnalyzer extends Analyzer {
		@Override
		public TokenStream tokenStream(String fieldName, Reader reader) {
			return new EdgeNGramTokenFilter(new KeywordTokenizer(reader), //
					EdgeNGramTokenFilter.Side.BACK, 1, 4);
		}
	}

	private static class FrontEdgeNGramAnalyzer extends Analyzer {
		@Override
		public TokenStream tokenStream(String fieldName, Reader reader) {
			return new EdgeNGramTokenFilter(new KeywordTokenizer(reader), //
					EdgeNGramTokenFilter.Side.FRONT, 1, 4);
		}
	}

	private static class NGramAnalyzer extends Analyzer {
		@Override
		public TokenStream tokenStream(String fieldName, Reader reader) {
			return new NGramTokenFilter(new KeywordTokenizer(reader), 2, 4);
		}
	}

	public void testEdgeNGramTokenFilterBack() throws IOException {
		AnalyzerUtils.displayTokensWithPositions(new BackEdgeNGramAnalyzer(), "lettuce");
	}

	public void testEdgeNGramTokenFilterFront() throws IOException {
		AnalyzerUtils.displayTokensWithPositions(new FrontEdgeNGramAnalyzer(), "lettuce");
	}

	public void testNGramTokenFilter24() throws IOException {
		AnalyzerUtils.displayTokensWithPositions(new NGramAnalyzer(), "lettuce");
	}

}
