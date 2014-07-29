package cc.pp.lucene.chap03.searching;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import cc.pp.lucene.common.TestUtil;

public class BooleanQueryTest extends TestCase {

	/**
	 * 测试BooleanQuery类，And连接
	 * @throws IOException
	 */
	public void testAnd() throws IOException {

		TermQuery searchingBooks = new TermQuery(new Term("subject", "search"));
		Query books2010 = NumericRangeQuery.newIntRange("pubmonth", 201001, 201012, true, true);
		BooleanQuery searchingBooks2010 = new BooleanQuery();
		searchingBooks2010.add(searchingBooks, BooleanClause.Occur.MUST);
		searchingBooks2010.add(books2010, BooleanClause.Occur.MUST);

		Directory dir = FSDirectory.open(new File("index/chap03index/"));
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopDocs docs = searcher.search(searchingBooks2010, 10);
		assertTrue(TestUtil.hitsIncludeTitle(searcher, docs, "Lucene in Action, Second Edition"));

		reader.close();
		dir.close();
	}

	/**
	 * 测试BooleanQuery类，Or连接
	 * @throws IOException
	 */
	public void testOr() throws IOException {

		TermQuery methodologyBooks = new TermQuery(new Term("category", //
				"/technology/computers/programming/methodology"));
		TermQuery easternPhilosophyBooks = new TermQuery(new Term("category", //
				"/pholosophy/eastern"));
		BooleanQuery enlightenmentBooks = new BooleanQuery();
		enlightenmentBooks.add(methodologyBooks, BooleanClause.Occur.SHOULD);
		enlightenmentBooks.add(easternPhilosophyBooks, BooleanClause.Occur.SHOULD);

		Directory dir = FSDirectory.open(new File("index/chap03index/"));
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopDocs docs = searcher.search(enlightenmentBooks, 10);

		System.out.println("or = " + enlightenmentBooks);

		assertTrue(TestUtil.hitsIncludeTitle(searcher, docs, "Extreme Programming Explained"));
		assertTrue(TestUtil.hitsIncludeTitle(searcher, docs, "Tao Te Ching \u9053\u5FB7\u7D93"));

		reader.close();
		dir.close();
	}

}
