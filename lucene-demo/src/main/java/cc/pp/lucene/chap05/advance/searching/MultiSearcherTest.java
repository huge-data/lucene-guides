package cc.pp.lucene.chap05.advance.searching;

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import cc.pp.lucene.common.LuceneConstant;

public class MultiSearcherTest extends TestCase {

	private IndexSearcher[] searchers;

	@Override
	public void setUp() throws Exception {

		String[] animals = { "aardvark", "beaver", "coati", "dog", "elephant", //
				"frog", "gila monster", "horse", "iguana", "javelina", //
				"kangaroo", "lemur", "moose", "nematode", "orca", "python", //
				"quokka", "rat", "scorpion", "tarantula", "uromastyx", //
				"vicuna", "walrus", "xiphias", "yak", "zebra" };

		Analyzer analyzer = new WhitespaceAnalyzer(LuceneConstant.LUCENE_VERSION);

		Directory adir = new RAMDirectory();
		Directory bdir = new RAMDirectory();

		IndexWriterConfig conf = new IndexWriterConfig(LuceneConstant.LUCENE_VERSION, analyzer);
		IndexWriter awriter = new IndexWriter(adir, conf);
		IndexWriter bwriter = new IndexWriter(bdir, conf);

		for (int i = animals.length - 1; i >= 0; i--) {
			Document doc = new Document();
			String animal = animals[i];
			doc.add(new StringField("animal", animal, Field.Store.YES));
			if (animal.charAt(0) < 'n') {
				awriter.addDocument(doc);
			} else {
				bwriter.addDocument(doc);
			}
		}

		awriter.close();
		bwriter.close();

		searchers = new IndexSearcher[2];
		IndexReader areader = DirectoryReader.open(adir);
		IndexReader breader = DirectoryReader.open(bdir);
		searchers[0] = new IndexSearcher(areader);
		searchers[1] = new IndexSearcher(breader);
	}

	public void testMulti() throws IOException {

		//		MultiSearcher searcher = new MultiSearcher(searchers);
		//		TermRangeQuery query = new TermRangeQuery("animal", "h", "t", true, true);
		//		TopDocs hits = searcher.search(query, 10);
		//		assertEquals("tarantula not included", 12, hits.totalHits);
	}

}
