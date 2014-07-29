package cc.pp.chap06.extsearch.payloads;

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.payloads.AveragePayloadFunction;
import org.apache.lucene.search.payloads.PayloadTermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import cc.pp.common.TestUtil;

public class PayloadsTest extends TestCase {

	private Directory dir;
	private IndexWriter writer;
	private BulletinPayloadsAnalyzer analyzer;

	public void testPayloadTermQuery() throws Throwable {

		addDoc("Hurricane warning", "Bulletin: A hurricane warning was issued at " + //
				"6 AM for the outer great banks");
		addDoc("Warning label maker", "The warning label maker is a delightful " + //
				"toy for your precocious seven year old's warning nedds");
		addDoc("Tornado warning", "Bulletin: There is a tornado warning for " + //
				"Worcester country until 6 PM today");

		IndexReader reader = writer.getReader();
		writer.close();

		IndexSearcher searcher = new IndexSearcher(reader);
		searcher.setSimilarity(new BoostingSimilarity());

		Term term = new Term("contents", "warning");
		Query query1 = new TermQuery(term);
		System.out.println("\nTermQuery results:");
		TopDocs hits = searcher.search(query1, 10);
		TestUtil.dumpHits(searcher, hits);

		assertEquals("Warning label maker", //
				searcher.doc(hits.scoreDocs[0].doc).get("title"));

		Query query2 = new PayloadTermQuery(term, new AveragePayloadFunction());
		System.out.println("\nPayloadTermQuery results:");
		hits = searcher.search(query2, 10);
		TestUtil.dumpHits(searcher, hits);

		assertEquals("Warning label maker", //
				searcher.doc(hits.scoreDocs[2].doc).get("title"));

		reader.clone();
		searcher.close();
	}

	@Override
	protected void setUp() throws Exception {
		dir = new RAMDirectory();
		analyzer = new BulletinPayloadsAnalyzer(5.0F);
		writer = new IndexWriter(dir, analyzer, IndexWriter.MaxFieldLength.UNLIMITED);
	}

	@Override
	protected void tearDown() throws Exception {
		writer.close();
	}

	private void addDoc(String title, String contents) throws IOException {

		Document doc = new Document();
		doc.add(new Field("title", title, Field.Store.YES, Field.Index.NO));
		doc.add(new Field("contents", contents, Field.Store.NO, Field.Index.ANALYZED));
		analyzer.setIsBulletin(contents.startsWith("Bulletin:"));
		writer.addDocument(doc);
	}

}
