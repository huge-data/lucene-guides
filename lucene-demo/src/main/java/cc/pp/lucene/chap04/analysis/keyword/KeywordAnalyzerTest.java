package cc.pp.lucene.chap04.analysis.keyword;

import junit.framework.TestCase;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import cc.pp.lucene.common.LuceneConstant;
import cc.pp.lucene.common.TestUtil;

public class KeywordAnalyzerTest extends TestCase {

	private IndexSearcher searcher;

	@Override
	public void setUp() throws Exception {

		Directory dir = new RAMDirectory();
		Analyzer analyzer = new SimpleAnalyzer(LuceneConstant.LUCENE_VERSION);
		IndexWriterConfig conf = new IndexWriterConfig(LuceneConstant.LUCENE_VERSION, analyzer);
		IndexWriter writer = new IndexWriter(dir, conf);

		Document doc = new Document();
		doc.add(new StringField("partnum", "Q36", Field.Store.NO));
		doc.add(new TextField("description", "Illidium Space Modulator", Field.Store.YES));
		writer.addDocument(doc);
		writer.close();

		IndexReader reader = DirectoryReader.open(dir);
		searcher = new IndexSearcher(reader);
	}

	public void testTermQuery() throws Exception {

		Query query = new TermQuery(new Term("partnum", "Q36"));
		assertEquals(1, TestUtil.hitCount(searcher, query));
	}

	public void testBasicQueryParser() throws Exception {

		Query query = new QueryParser(LuceneConstant.LUCENE_VERSION, "description", new SimpleAnalyzer(
				LuceneConstant.LUCENE_VERSION)).parse("partnum:Q36 AND SPACE");
		assertEquals("note Q36 -> q", "+partnum:q +space", query.toString("description"));
		assertEquals("doc not found :(", 0, TestUtil.hitCount(searcher, query));
	}

	public void testPerFieldAnalyzer() throws Exception {

		PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(
				new SimpleAnalyzer(LuceneConstant.LUCENE_VERSION));
		//		analyzer.addAnalyzer("partnum", new KeywordAnalyzer());//新版本中丢弃
		Query query = new QueryParser(LuceneConstant.LUCENE_VERSION, "description", analyzer)
				.parse("partnum:Q36 AND SPACE");
		assertEquals("Q36 kept as-is", "+partnum:Q36 +space", query.toString("description"));
		assertEquals("doc found!", 1, TestUtil.hitCount(searcher, query));
	}

}
