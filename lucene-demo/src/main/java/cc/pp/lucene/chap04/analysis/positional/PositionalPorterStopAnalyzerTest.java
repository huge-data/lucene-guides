package cc.pp.lucene.chap04.analysis.positional;

import junit.framework.TestCase;

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
import org.apache.lucene.store.RAMDirectory;

import cc.pp.lucene.common.LuceneConstant;
import cc.pp.lucene.common.TestUtil;

public class PositionalPorterStopAnalyzerTest extends TestCase {

	private static PositionalPorterStopAnalyzer porterAnalyzer = new PositionalPorterStopAnalyzer(
			LuceneConstant.LUCENE_VERSION);

	private IndexSearcher searcher;
	private QueryParser parser;

	@Override
	public void setUp() throws Exception {

		RAMDirectory dir = new RAMDirectory();
		IndexWriterConfig conf = new IndexWriterConfig(LuceneConstant.LUCENE_VERSION, porterAnalyzer);
		IndexWriter writer = new IndexWriter(dir, conf);

		Document doc = new Document();
		doc.add(new TextField("contents", "The quick brown fox jumps over the lazy dog", Field.Store.YES));
		writer.addDocument(doc);
		writer.close();
		IndexReader reader = DirectoryReader.open(dir);
		searcher = new IndexSearcher(reader);
		parser = new QueryParser(LuceneConstant.LUCENE_VERSION, "contents", porterAnalyzer);
	}

	public void testWithSlop() throws Exception {

		parser.setPhraseSlop(1);
		Query query = parser.parse("\"over the lazy\"");
		assertEquals("hole accounted for", 1, TestUtil.hitCount(searcher, query));
	}

	public void testStems() throws Exception {

		Query query = new QueryParser(LuceneConstant.LUCENE_VERSION, "contents", //
				porterAnalyzer).parse("laziness");
		assertEquals("lazi", 1, TestUtil.hitCount(searcher, query));

		query = parser.parse("\"fox jumped\"");
		assertEquals("jump jumps jumped jumping", 1, TestUtil.hitCount(searcher, query));
	}

}
