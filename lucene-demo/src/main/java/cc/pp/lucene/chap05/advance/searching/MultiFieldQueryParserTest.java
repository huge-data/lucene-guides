package cc.pp.lucene.chap05.advance.searching;

import java.io.File;

import junit.framework.TestCase;

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import cc.pp.lucene.common.LuceneConstant;
import cc.pp.lucene.common.TestUtil;

public class MultiFieldQueryParserTest extends TestCase {

	public void testDefaultOperator() throws Exception {

		Query query = new MultiFieldQueryParser(LuceneConstant.LUCENE_VERSION, //
				new String[] { "title", "subject" }, //
				new SimpleAnalyzer(LuceneConstant.LUCENE_VERSION)).parse("development");
		Directory dir = FSDirectory.open(new File("index/chap03index/"));

		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);

		TopDocs hits = searcher.search(query, 10);

		assertTrue(TestUtil.hitsIncludeTitle(searcher, hits, "Ant in Action"));

		assertTrue(TestUtil.hitsIncludeTitle(searcher, hits, "Extreme Programming Explained"));

		reader.close();
		dir.close();
	}

	public void testSpecifiedOperator() throws Exception {

		Query query = MultiFieldQueryParser.parse(LuceneConstant.LUCENE_VERSION, "lucene", //
				new String[] { "title", "subject" }, //
				new BooleanClause.Occur[] { BooleanClause.Occur.MUST, BooleanClause.Occur.MUST }, //
				new SimpleAnalyzer(LuceneConstant.LUCENE_VERSION));
		Directory dir = FSDirectory.open(new File("index/chap03index/"));
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);

		TopDocs hits = searcher.search(query, 10);

		assertTrue(TestUtil.hitsIncludeTitle(searcher, hits, "Lucene in Action, Second Edition"));

		assertEquals("one and only one", 1, hits.scoreDocs.length);

		reader.close();
		dir.close();
	}

}
