package cc.pp.lucene.chap03.searching;

import junit.framework.TestCase;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import cc.pp.lucene.common.LuceneConstant;

public class NearRealTimeTest extends TestCase {

	public void testNearRealTime() throws Exception {

		Directory dir = new RAMDirectory();
		Analyzer analyzer = new StandardAnalyzer(LuceneConstant.LUCENE_VERSION);
		IndexWriterConfig conf = new IndexWriterConfig(LuceneConstant.LUCENE_VERSION, analyzer);
		IndexWriter writer = new IndexWriter(dir, conf);
		for (int i = 0; i < 10; i++) {
			Document doc = new Document();
			doc.add(new StringField("id", "" + i, Field.Store.NO));
			doc.add(new TextField("text", "aaa", Field.Store.NO));
			writer.addDocument(doc);
		}

		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);

		Query query = new TermQuery(new Term("text", "aaa"));
		TopDocs docs = searcher.search(query, 1);
		assertEquals(10, docs.totalHits);

		writer.deleteDocuments(new Term("id", "7"));

		Document doc = new Document();
		doc.add(new StringField("id", "11", Field.Store.NO));
		doc.add(new TextField("text", "bbb", Field.Store.NO));
		writer.addDocument(doc);

		IndexReader newReader = DirectoryReader.open(dir);
		assertFalse(reader == newReader);
		reader.close();
		searcher = new IndexSearcher(newReader);

		docs = searcher.search(query, 10);
		assertEquals(9, docs.totalHits);

		query = new TermQuery(new Term("text", "bbb"));
		docs = searcher.search(query, 1);
		assertEquals(1, docs.totalHits);

		newReader.close();
		writer.close();
		dir.close();
	}

}
