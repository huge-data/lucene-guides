package cc.pp.lucene.chap04.analysis.chinese;

import java.io.File;

import junit.framework.TestCase;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import cc.pp.lucene.common.TestUtil;

public class ChineseTest extends TestCase {

	public void testChinese() throws Exception {

		Directory dir = FSDirectory.open(new File("index/chap03index/"));
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);
		Query query = new TermQuery(new Term("contents", "道"));
		assertEquals("tao", 1, TestUtil.hitCount(searcher, query));
		reader.close();
		dir.close();
	}

}
