package cc.pp.lucene.chap03.searching;

import java.io.File;

import junit.framework.TestCase;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class TermRangeQueryTest extends TestCase {

	public void testTermRangeQuery() throws Exception {

		Directory dir = FSDirectory.open(new File("index/chap03index/"));
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);

		TermRangeQuery query = TermRangeQuery.newStringRange("title2", "d", "j", true, true);
		TopDocs docs = searcher.search(query, 100);
		for (int i = 0; i < docs.totalHits; i++) {
			System.out.println("match " + i + ": " + searcher.doc(docs.scoreDocs[i].doc).get("title2"));
		}
		assertEquals(3, docs.totalHits);

		reader.close();
		dir.close();
	}

}
