package cc.pp.lucene.chap04.analysis.stopanalyzer;

import junit.framework.TestCase;

import org.apache.lucene.analysis.core.StopAnalyzer;

import cc.pp.lucene.chap04.analysis.AnalyzerUtils;
import cc.pp.lucene.common.LuceneConstant;

public class StopAnalyzerTest extends TestCase {

	private final StopAnalyzer analyzer = new StopAnalyzer(LuceneConstant.LUCENE_VERSION);

	public void testHoles() throws Exception {

		String[] expected = { "one", "enough" };
		AnalyzerUtils.assertAnalyzesTo(analyzer, "one is not enough", expected);
		AnalyzerUtils.assertAnalyzesTo(analyzer, "one is enough", expected);
		AnalyzerUtils.assertAnalyzesTo(analyzer, "one enough", expected);
		AnalyzerUtils.assertAnalyzesTo(analyzer, "one but not enough", expected);
	}

}
