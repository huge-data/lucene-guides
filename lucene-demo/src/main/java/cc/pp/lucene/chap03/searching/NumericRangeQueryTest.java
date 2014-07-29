package cc.pp.lucene.chap03.searching;

import java.io.File;

import junit.framework.TestCase;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class NumericRangeQueryTest extends TestCase {

	public void testInclusive() throws Exception {

		Directory dir = FSDirectory.open(new File("index/chap03index/"));
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);
		NumericRangeQuery<?> query = NumericRangeQuery. //
				newIntRange("pubmonth", 200605, 200609, true, true);
		TopDocs docs = searcher.search(query, 10);
		for (int i = 0; i < docs.totalHits; i++) {
			System.out.println("match: " + i + ": " + searcher.doc(docs.scoreDocs[i].doc).get("author"));
		}
		assertEquals(1, docs.totalHits);
		reader.close();
		dir.close();
	}

	public void testExclusive() throws Exception {

		Directory dir = FSDirectory.open(new File("index/chap03index/"));
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);
		NumericRangeQuery<?> query = NumericRangeQuery. //
				newIntRange("pubmonth", 200605, 200609, false, false);
		TopDocs docs = searcher.search(query, 10);
		assertEquals(0, docs.totalHits);
		reader.close();
		dir.close();
	}

}
