package cc.pp.chap08and09.tools;

import junit.framework.TestCase;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.core.QueryNodeException;
import org.apache.lucene.queryParser.standard.StandardQueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.util.Version;

public class FlexibleQUeryParserTest extends TestCase {

	public void testNoFuzzyOrWildcard() {

		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
		StandardQueryParser parser = new CustomFlexibleQueryParser(analyzer);
		try {
			parser.parse("agil*", "subject");
			fail("didn't hit ecpected exception");
		} catch (QueryNodeException e) {
			//
		}

		try {
			parser.parse("agil~0.8", "subject");
			fail("didn't hit expected exception");
		} catch (QueryNodeException e) {
			//
		}
	}

	public void testPhraseQuery() throws QueryNodeException {

		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
		StandardQueryParser parser = new CustomFlexibleQueryParser(analyzer);

		Query query = parser.parse("singleTerm", "subject");
		assertTrue("TermQuery", query instanceof TermQuery);

		query = parser.parse("\"a phrase test\"", "subject");
		System.out.println("got query=" + query);
		assertTrue("SpanNearQuery", query instanceof SpanNearQuery);
	}

	public void testSimple() {

		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
		StandardQueryParser parser = new StandardQueryParser(analyzer);
		Query q = null;
		try {
			q = parser.parse("(agile OR extreme) AND methodology", "subject");
		} catch (QueryNodeException e) {
			//
		}
		System.out.println("Boolean parsed: " + q);

		try {
			q = parser.parse("agil*", "subject");
		} catch (QueryNodeException e) {
			//
		}
		System.out.println("parsed: " + q);

		try {
			q = parser.parse("agil~0.8", "subject");
		} catch (QueryNodeException e) {
			//
		}
		System.out.println("parsed: " + q);
	}


}
