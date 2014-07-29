package cc.pp.lucene.chap05.advance.searching;

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import cc.pp.lucene.common.LuceneConstant;
import cc.pp.lucene.common.TestUtil;

public class SecurityFilterTest extends TestCase {

	private IndexSearcher searcher;

	@Override
	protected void setUp() throws Exception {

		Directory dir = new RAMDirectory();

		Analyzer analyzer = new WhitespaceAnalyzer(LuceneConstant.LUCENE_VERSION);
		IndexWriterConfig conf = new IndexWriterConfig(LuceneConstant.LUCENE_VERSION, analyzer);
		IndexWriter writer = new IndexWriter(dir, conf);

		Document doc = new Document();
		doc.add(new StringField("owner", "elwood", Field.Store.YES));
		doc.add(new TextField("keywords", "elwood's sensitive info", Field.Store.YES));
		writer.addDocument(doc);

		doc = new Document();
		doc.add(new StringField("owner", "jake", Field.Store.YES));
		doc.add(new TextField("keywords", "jake's sensitive info", Field.Store.YES));
		writer.addDocument(doc);

		writer.close();
		IndexReader reader = DirectoryReader.open(dir);
		searcher = new IndexSearcher(reader);
	}

	public void testSecurityFilter() throws IOException {

		TermQuery query = new TermQuery(new Term("keywords", "info"));
		assertEquals("Both documents match", 2, TestUtil.hitCount(searcher, query));
		/**
		 * 把owner为jake的文档过滤出来，作为筛选条件
		 */
		Filter jakeFilter = new QueryWrapperFilter(new TermQuery(new Term("owner", "jake")));
		TopDocs hits = searcher.search(query, jakeFilter, 10);
		assertEquals(1, hits.totalHits);
		assertEquals("elwood is safe", "jake's sensitive info", //
				searcher.doc(hits.scoreDocs[0].doc).get("keywords"));
	}

}
