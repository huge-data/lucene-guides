package cc.pp.lucene.chap05.advance.searching;

import java.io.File;

import junit.framework.TestCase;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TimeLimitingCollector;
import org.apache.lucene.search.TimeLimitingCollector.TimeExceededException;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import cc.pp.lucene.common.TestUtil;

public class TimeLimitingCollectorTest extends TestCase {

	public void testTimeLimitingCollector() throws Exception {

		Directory dir = FSDirectory.open(new File("index/chap03index/"));
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);
		Query query = new MatchAllDocsQuery();

		int numAllBooks = TestUtil.hitCount(searcher, query);

		TopScoreDocCollector topDocs = TopScoreDocCollector.create(10, false);
		/**
		 * 设置允许超时时间为1s
		 */
		Collector collector = new TimeLimitingCollector(topDocs, null, 1000); // 存放任何存在的集合

		try {
			searcher.search(query, collector);
			assertEquals(numAllBooks, topDocs.getTotalHits()); // 如果不超时，将得到所有集合
		} catch (TimeExceededException e) {
			System.out.println("Too much time token."); // 超时
		}

		reader.close();
		dir.close();
	}

}
