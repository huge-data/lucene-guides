package cc.pp.lucene.chap03.searching;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class PrefixQueryTest extends TestCase {

	public void testPrefix() throws IOException {

		Directory dir = FSDirectory.open(new File("index/chap03index/"));
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);

		Term term = new Term("category", "technology/computers/programming");
		PrefixQuery query = new PrefixQuery(term);

		TopDocs docs = searcher.search(query, 10);
		int programmingAndBelow = docs.totalHits;
		System.out.println(programmingAndBelow);

		docs = searcher.search(new TermQuery(term), 10);
		int justProgramming = docs.totalHits;
		System.out.println(justProgramming);

		assertTrue(programmingAndBelow > justProgramming);

		reader.close();
		dir.close();
	}

}
