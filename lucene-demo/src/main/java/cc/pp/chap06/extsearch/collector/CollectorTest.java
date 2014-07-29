package cc.pp.chap06.extsearch.collector;

import java.io.File;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import cc.pp.common.TestUtil;

public class CollectorTest extends TestCase {

	public void testCollecting() throws Exception {

		Directory dir = FSDirectory.open(new File("index/chap03index/"));
		TermQuery query = new TermQuery(new Term("contents", "junit"));
		IndexSearcher searcher = new IndexSearcher(dir);

		BookLinkCollector collector = new BookLinkCollector();
		searcher.search(query, collector);

		Map<String, String> linkMap = collector.getLinks();
		assertEquals("ant in action", linkMap.get("http://www.manning.com/loughran"));
		assertEquals("junit in action, second edition", linkMap.get("http://www.manning.com/tahchiev"));

		TopDocs hits = searcher.search(query, 10);
		TestUtil.dumpHits(searcher, hits);

		searcher.close();
		dir.close();
	}

}
