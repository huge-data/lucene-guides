package cc.pp.lucene.chap04.analysis.codec;

import junit.framework.TestCase;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.RAMDirectory;

import cc.pp.lucene.chap04.analysis.AnalyzerUtils;
import cc.pp.lucene.common.LuceneConstant;

public class MetaphoneAnalyzerTest extends TestCase {

	public void testMetaphoneReplacementAnalyzer() throws Exception {

		MetaphoneReplacementAnalyzer analyzer = new MetaphoneReplacementAnalyzer(LuceneConstant.LUCENE_VERSION);
		AnalyzerUtils.displayTokens(analyzer, "The quick brown fox jumped over the lazy dog");

		System.out.println("-------------------");
		AnalyzerUtils.displayTokens(analyzer, "Tha quick brown phox jumpd ovvar tha lazi dag");
	}

	public void testKoolKat() throws Exception {

		RAMDirectory dir = new RAMDirectory();
		Analyzer analyzer = new MetaphoneReplacementAnalyzer(LuceneConstant.LUCENE_VERSION);
		IndexWriterConfig conf = new IndexWriterConfig(LuceneConstant.LUCENE_VERSION, analyzer);
		IndexWriter writer = new IndexWriter(dir, conf);

		Document doc = new Document();
		doc.add(new TextField("contents", "cool cat", Field.Store.YES));
		writer.addDocument(doc);
		writer.close();

		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);
		Query query = new QueryParser(LuceneConstant.LUCENE_VERSION, "contents", analyzer).parse("kool kat");
		TopDocs hits = searcher.search(query, 1);
		assertEquals(1, hits.totalHits);

		int docID = hits.scoreDocs[0].doc;
		doc = searcher.doc(docID);
		assertEquals("cool cat", doc.get("contents"));

		reader.close();
	}

}
