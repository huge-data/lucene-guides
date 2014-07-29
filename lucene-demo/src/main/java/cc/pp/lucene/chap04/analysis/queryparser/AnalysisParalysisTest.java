package cc.pp.lucene.chap04.analysis.queryparser;

import junit.framework.TestCase;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;

import cc.pp.lucene.common.LuceneConstant;

public class AnalysisParalysisTest extends TestCase {

	public void testAnalyzer() throws Exception {

		Analyzer analyzer = new StandardAnalyzer(LuceneConstant.LUCENE_VERSION);
		String queryString = "category:/philosophy/eastern";

		Query query = new QueryParser(LuceneConstant.LUCENE_VERSION, "contents", analyzer).parse(queryString);
		assertEquals("path got split, yikes!", "category:\"philosophy eastern\"", //
				query.toString("contents"));

		PerFieldAnalyzerWrapper perFieldAnalyzer = new PerFieldAnalyzerWrapper(analyzer);
		// 新版本中已经丢弃
		//		perFieldAnalyzer.addAnalyzer("category", new WhitespaceAnalyzer(LuceneConstant.LUCENE_VERSION));
		query = new QueryParser(LuceneConstant.LUCENE_VERSION, "contents", perFieldAnalyzer).parse(queryString);
		assertEquals("leave category field alone", "category:/philosophy/eastern", //
				query.toString("contents"));
	}

}
