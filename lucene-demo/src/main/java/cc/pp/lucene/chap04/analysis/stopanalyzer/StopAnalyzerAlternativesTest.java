package cc.pp.lucene.chap04.analysis.stopanalyzer;

import java.io.IOException;

import junit.framework.TestCase;
import cc.pp.lucene.chap04.analysis.AnalyzerUtils;
import cc.pp.lucene.common.LuceneConstant;

public class StopAnalyzerAlternativesTest extends TestCase {

	public void testOutput() throws IOException {

		String text = "The quick aa2233@ss brown Q36...";
		AnalyzerUtils.displayTokens(new StopAnalyzer1(LuceneConstant.LUCENE_VERSION), text);
		System.out.println();
		AnalyzerUtils.displayTokens(new StopAnalyzer2(LuceneConstant.LUCENE_VERSION), text);
		//		AnalyzerUtils.displayTokens(new StopAnalyzerFlawed(), "The quick brown...");
	}

	public void testStopAnalyzer1() throws IOException {

		AnalyzerUtils.assertAnalyzesTo(new StopAnalyzer1(LuceneConstant.LUCENE_VERSION), //
				"The quick brown...", new String[] { "quick", "brown" });
	}

	public void testStopAnalyzer2() throws IOException {

		AnalyzerUtils.assertAnalyzesTo(new StopAnalyzer2(LuceneConstant.LUCENE_VERSION), //
				"The quick brown...", new String[] { "quick", "brown" });
	}

	public void testStopAnalyzerFlawed() throws IOException {

		AnalyzerUtils.assertAnalyzesTo(new StopAnalyzerFlawed(LuceneConstant.LUCENE_VERSION), //
				"The quick brown...", new String[] { "the", "quick", "brown" });
	}

}
