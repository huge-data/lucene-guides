package cc.pp.lucene.chap03.searching;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import cc.pp.lucene.common.TestUtil;

public class QueryParserTest extends TestCase {

	private Analyzer analyzer;
	private Directory dir;
	private IndexReader reader;
	private IndexSearcher searcher;

	@Override
	protected void setUp() throws Exception {
		analyzer = new WhitespaceAnalyzer(Version.LUCENE_46);
		dir = FSDirectory.open(new File("index/chap03index/"));
		reader = DirectoryReader.open(dir);
		searcher = new IndexSearcher(reader);
	}

	@Override
	protected void tearDown() throws IOException {
		reader.close();
		dir.close();
	}

	public void testToString() {

		BooleanQuery query = new BooleanQuery();
		query.add(new FuzzyQuery(new Term("field", "kountry")), BooleanClause.Occur.MUST);
		query.add(new TermQuery(new Term("title", "weatern")), BooleanClause.Occur.SHOULD);
		System.out.println(query.toString("field"));
		assertEquals("both kinds", "+kountry~0.5 title:weatern", query.toString("field"));
	}

	public void testPrefixQuery() throws ParseException {

		QueryParser parser = new QueryParser(Version.LUCENE_46, "category", new StandardAnalyzer(Version.LUCENE_46));
		parser.setLowercaseExpandedTerms(false);
		System.out.println(parser.parse("Computers/technology*").toString("category"));
	}

	public void testFuzzyQuery() throws Exception {

		QueryParser parser = new QueryParser(Version.LUCENE_46, "subject", analyzer);
		Query query = parser.parse("kountry~");
		System.out.println("fuzzy: " + query);
		query = parser.parse("kountry~0.7");
		System.out.println("fuzzy 2: " + query);
	}

	public void testGrouping() throws Exception {

		Query query = new QueryParser(Version.LUCENE_46, "subject", analyzer). //
				parse("(agile OR extreme) AND methodology");
		TopDocs docs = searcher.search(query, 10);
		assertTrue(TestUtil.hitsIncludeTitle(searcher, docs, "Extreme Programming Explained"));
		assertTrue(TestUtil.hitsIncludeTitle(searcher, docs, "The Pragmatic Programmer"));
	}

	public void testTermQuery() throws Exception {

		QueryParser parser = new QueryParser(Version.LUCENE_46, "subject", analyzer);
		Query query = parser.parse("computers");
		System.out.println("term: " + query);
	}

	public void testTermQueryMore() throws Exception {

		QueryParser parser = new QueryParser(Version.LUCENE_46, "subject", analyzer);
		Query query = parser.parse("computers science");
		System.out.println("term: " + query);
	}

	public void testTermRangeQuery() throws Exception {

		Query query = new QueryParser(Version.LUCENE_46, "sunject", analyzer). //
				parse("title2:[Q TO V]");
		assertTrue(query instanceof TermRangeQuery);
		TopDocs docs = searcher.search(query, 10);
		assertTrue(TestUtil.hitsIncludeTitle(searcher, docs, "Tapestry in Action"));

		query = new QueryParser(Version.LUCENE_46, "subject", analyzer). //
				parse("title2:{Q TO \"Tapestry in Action\"}");
		docs = searcher.search(query, 10);
		assertFalse(TestUtil.hitsIncludeTitle(searcher, docs, "Tapestry in Action"));
	}

	public void testPhraseQuery() throws Exception {

		Query query = new QueryParser(Version.LUCENE_46, "field", //
				new StandardAnalyzer(Version.LUCENE_46)).parse("\"This is Some Phrase*\"");
		assertEquals("analyzed", "\"? ? some phrase\"", query.toString("field"));
		query = new QueryParser(Version.LUCENE_46, "field", analyzer).parse("\"term\"");
		assertTrue("reduced to TermQuery", query instanceof TermQuery);
	}

	public void testSlop() throws ParseException {

		Query query = new QueryParser(Version.LUCENE_46, "field", analyzer). //
				parse("\"exact phrase\"");
		assertEquals("zero slop", "\"exact phrase\"", query.toString("field"));
		QueryParser parser = new QueryParser(Version.LUCENE_46, "field", analyzer);
		parser.setPhraseSlop(5);
		query = parser.parse("\"sloppy phrase\"");
		assertEquals("sloppy, implicity", "\"sloppy phrase\"~5", query.toString("field"));
	}

	public void testLowercasing() throws Exception {

		Query query = new QueryParser(Version.LUCENE_46, "field", analyzer).parse("PrefixQuery*");
		assertEquals("lowercased", "prefixquery*", query.toString("field"));
		QueryParser parser = new QueryParser(Version.LUCENE_46, "field", analyzer);
		parser.setLowercaseExpandedTerms(false);
		query = parser.parse("PrefixQuery*");
		assertEquals("lowercased", "PrefixQuery*", query.toString("field"));
	}

	public void testWildcard() throws ParserException {
		try {
			new QueryParser(Version.LUCENE_46, "feild", analyzer).parse("*xyz");
			fail("Leading wildcard character should not be allowed");
		} catch (ParseException e) {
			assertTrue(true);
			//			e.printStackTrace();
		}
	}

	public void testBoost() throws ParseException {

		Query query = new QueryParser(Version.LUCENE_46, "field", analyzer).parse("term^2");
		assertEquals("term^2.0", query.toString("field"));
	}

	public void testParserException() {
		try {
			new QueryParser(Version.LUCENE_46, "contents", analyzer).parse("^&#");
		} catch (ParseException e) {
			assertTrue(true);
			return;
		}
		fail("ParseException excepted, but not thrown");
	}

}
