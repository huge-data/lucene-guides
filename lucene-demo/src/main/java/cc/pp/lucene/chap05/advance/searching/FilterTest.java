package cc.pp.lucene.chap05.advance.searching;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.CachingWrapperFilter;
import org.apache.lucene.search.FieldCacheRangeFilter;
import org.apache.lucene.search.FieldCacheTermsFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.NumericRangeFilter;
import org.apache.lucene.search.PrefixFilter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeFilter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import cc.pp.lucene.common.TestUtil;

public class FilterTest extends TestCase {

	private Query allBooks;
	private IndexSearcher searcher;
	private IndexReader reader;
	private Directory dir;

	@Override
	protected void setUp() throws IOException {

		allBooks = new MatchAllDocsQuery();
		dir = FSDirectory.open(new File("index/chap03index/"));
		reader = DirectoryReader.open(dir);
		searcher = new IndexSearcher(reader);
	}

	public void testTermRangeFilter() throws IOException {

		Filter filter = TermRangeFilter.newStringRange("title2", "d", "j", true, true);
		assertEquals(3, TestUtil.hitCount(searcher, allBooks, filter));
	}

	public void testNumericDateFilter() throws IOException {

		Filter filter = NumericRangeFilter.newIntRange("pubmonth", 201001, 201006, true, true);
		assertEquals(2, TestUtil.hitCount(searcher, allBooks, filter));
	}

	public void testFieldCacheRangeFilter() throws IOException {

		Filter filter = FieldCacheRangeFilter.newStringRange("title2", "d", "j", true, true);
		assertEquals(3, TestUtil.hitCount(searcher, allBooks, filter));

		filter = FieldCacheRangeFilter.newIntRange("pubmonth", 201001, 201006, true, true);
		assertEquals(2, TestUtil.hitCount(searcher, allBooks, filter));
	}

	public void testFieldCacheTermsFilter() throws IOException {

		Filter filter = new FieldCacheTermsFilter("category", new String[] { "health/alternative/chinese",
				"technology/computers/ai", "technology/computers/programming" });
		assertEquals("expected 7 hits", 7, TestUtil.hitCount(searcher, allBooks, filter));
	}

	public void testQueryWrapperFilter() throws IOException {

		TermQuery categoryQuery = new TermQuery(new Term("category", "philosophy/eastern"));
		Filter filter = new QueryWrapperFilter(categoryQuery);
		assertEquals("only tao te ching", 1, TestUtil.hitCount(searcher, allBooks, filter));
	}

	public void testSpanQueryFilter() throws IOException {

		//		SpanQuery categoryQuery = new SpanTermQuery(new Term("category", "philosophy/eastern"));
		//		SpanFilter filter = new SpanQueryFilter(categoryQuery);
		//		assertEquals("only tao de ching", 1, TestUtil.hitCount(searcher, allBooks, filter));
	}

	public void testFilterAlternative() throws IOException {

		TermQuery categoryQuery = new TermQuery(new Term("category", "philosophy/eastern"));
		BooleanQuery constrainedQuery = new BooleanQuery();
		constrainedQuery.add(allBooks, BooleanClause.Occur.MUST);
		constrainedQuery.add(categoryQuery, BooleanClause.Occur.MUST);
		assertEquals("only tao te ching", 1, TestUtil.hitCount(searcher, constrainedQuery));
	}

	public void testPrefixFilter() throws IOException {

		Filter filter = new PrefixFilter(new Term("category", "technology/computers"));
		//		System.out.println(TestUtil.hitCount(searcher, allBooks, filter));
		assertEquals("only /technology/computers/* books", 8, //
				TestUtil.hitCount(searcher, allBooks, filter));
	}

	public void testCachingWrapper() throws IOException {

		Filter filter = TermRangeFilter.newStringRange("title2", "d", "j", true, true);
		CachingWrapperFilter cachingFilter = new CachingWrapperFilter(filter);
		assertEquals(3, TestUtil.hitCount(searcher, allBooks, cachingFilter));
	}

}
