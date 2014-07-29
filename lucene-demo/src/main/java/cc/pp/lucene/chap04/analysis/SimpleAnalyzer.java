package cc.pp.lucene.chap04.analysis;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;

public class SimpleAnalyzer extends Analyzer {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		AnalyzerUtils.displayTokensWithFullDetails(new SimpleAnalyzer(), //
				"The quick brown fox....");
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
		// TODO Auto-generated method stub
		return null;
	}

}
