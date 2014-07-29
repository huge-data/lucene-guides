package cc.pp.chap08and09.tools;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.regex.RegexQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import cc.pp.common.TestUtil;

public class RegexQueryTest extends TestCase {

	public void testRegexQuery() throws IOException {

		Directory dir = FSDirectory.open(new File("index/chap03index/"));
		IndexSearcher searcher = new IndexSearcher(dir);
		RegexQuery query = new RegexQuery(new Term("title", ".*st.*"));
		TopDocs hits = searcher.search(query, 10);
		assertEquals(2, hits.totalHits);
		assertTrue(TestUtil.hitsIncludeTitle(searcher, //
				hits, "Tapestry in Action"));
		assertTrue(TestUtil.hitsIncludeTitle(searcher, //
				hits, "Mindstorms: Children, Computers, And Powerful Ideas"));
		searcher.close();
		dir.close();
	}

}
