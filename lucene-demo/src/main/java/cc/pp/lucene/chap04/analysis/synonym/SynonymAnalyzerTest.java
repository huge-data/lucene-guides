package cc.pp.lucene.chap04.analysis.synonym;

import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import cc.pp.lucene.common.LuceneConstant;
import cc.pp.lucene.common.TestUtil;

public class SynonymAnalyzerTest extends TestCase {

	private IndexSearcher searcher;
	private IndexReader reader;
	private Directory dir;
	private static SynonymAnalyzer synonymAnalyzer = new SynonymAnalyzer(new TestSynonymEngine(),
			LuceneConstant.LUCENE_VERSION);

	@Override
	public void setUp() throws Exception {

		dir = new RAMDirectory();
		IndexWriterConfig conf = new IndexWriterConfig(LuceneConstant.LUCENE_VERSION, synonymAnalyzer);
		IndexWriter writer = new IndexWriter(dir, conf);

		Document doc = new Document();
		doc.add(new TextField("content", "The quick brown fox jumps over the lazy dog", Field.Store.YES));
		writer.addDocument(doc);
		writer.close();

		reader = DirectoryReader.open(dir);
		searcher = new IndexSearcher(reader);
	}

	@Override
	public void tearDown() throws IOException {
		reader.close();
		dir.close();
	}

	public void testJumps() throws IOException {

		TokenStream stream = synonymAnalyzer.tokenStream("contents", // 用synonymAnalyzer分析器分析
				new StringReader("jumps"));
		CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);
		PositionIncrementAttribute posIncr = stream.addAttribute(PositionIncrementAttribute.class);

		int i = 0;
		String[] expected = new String[] { "jumps", "hops", "leaps" };
		while (stream.incrementToken()) {
			assertEquals(expected[i], term.toString());
			// 验证同义词位置
			int expectedPos;
			if (i == 0) {
				expectedPos = 1;
			} else {
				expectedPos = 0;
			}
			assertEquals(expectedPos, posIncr.getPositionIncrement());
			i++;
		}
	}

	public void testSearchByAPI() throws IOException {
		// 查询“hops”
		TermQuery tq = new TermQuery(new Term("content", "hops"));
		assertEquals(1, TestUtil.hitCount(searcher, tq));
		// 查询短语“fox hops”
		PhraseQuery pq = new PhraseQuery();
		pq.add(new Term("content", "fox"));
		pq.add(new Term("content", "hops"));
		assertEquals(1, TestUtil.hitCount(searcher, pq));
	}

	public void testWithQueryParser() throws Exception {

		Query query = new QueryParser(LuceneConstant.LUCENE_VERSION, "content", synonymAnalyzer).parse("\"fox hops\"");
		assertEquals(1, TestUtil.hitCount(searcher, query));
		System.out.println("With SynonymAnalyzer, \"fox jumps\" parses to " + query.toString("content"));

		query = new QueryParser(LuceneConstant.LUCENE_VERSION, "content", new StandardAnalyzer(
				LuceneConstant.LUCENE_VERSION)).parse("\"fox jumps\"");
		assertEquals(1, TestUtil.hitCount(searcher, query));
		System.out.println("With StandardAnalyzer, \"fox jumps\" parses to " + query.toString("content"));
	}

}
