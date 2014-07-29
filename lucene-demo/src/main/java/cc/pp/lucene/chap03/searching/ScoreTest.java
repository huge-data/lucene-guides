package cc.pp.lucene.chap03.searching;

import java.io.IOException;
import java.util.Vector;

import junit.framework.TestCase;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import cc.pp.lucene.chap01.meetlucene.Searcher;

public class ScoreTest extends TestCase {

	private Directory dir;

	@Override
	public void setUp() {
		dir = new RAMDirectory();
	}

	@Override
	public void tearDown() throws IOException {
		dir.close();
	}

	public void testSimple() throws Exception {

		indexSingleFieldDocs(new Field[] { new TextField("contents", "x", Field.Store.YES) });
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);
		searcher.setSimilarity(new SimpleSimilarity());

		Query query = new TermQuery(new Term("contents", "x"));
		Explanation explanation = searcher.explain(query, 0);
		System.out.println(explanation);

		TopDocs docs = searcher.search(query, 10);
		assertEquals(1, docs.totalHits);
		assertEquals(1F, docs.scoreDocs[0].score, 0.0);

		reader.close();
	}

	public void testWildcard() throws Exception {

		indexSingleFieldDocs(new Field[] { new TextField("contents", "wild", Field.Store.YES),
				new TextField("contents", "child", Field.Store.YES),
				new TextField("contents", "mild", Field.Store.YES),
				new TextField("contentd", "mildew", Field.Store.YES) });
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);
		Query query = new WildcardQuery(new Term("contents", "?ild*"));
		TopDocs docs = searcher.search(query, 10);
		assertEquals("child no match", 3, docs.totalHits);
		assertEquals("score the same", docs.scoreDocs[0].score, //
				docs.scoreDocs[1].score, 0.0);
		assertEquals("score the same", docs.scoreDocs[1].score, //
				docs.scoreDocs[2].score, 0.0);

		reader.close();
	}

	public void testFuzzy() throws Exception {

		indexSingleFieldDocs(new Field[] { new TextField("contents", "fuzzy", Field.Store.YES),
				new TextField("contents", "wuzzy", Field.Store.YES) });
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);
		Query query = new FuzzyQuery(new Term("contents", "wuzza"));
		TopDocs docs = searcher.search(query, 10);
		assertEquals("both close enough", 2, docs.totalHits);
		assertTrue("wuzzy closer than fuzzy", docs.scoreDocs[0].score != docs.scoreDocs[1].score);

		Document doc = searcher.doc(docs.scoreDocs[0].doc);
		assertEquals("wuzza bear", "wuzzy", doc.get("contents"));
		reader.close();
	}

	private void indexSingleFieldDocs(Field[] fields) throws Exception {

		IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_46, new WhitespaceAnalyzer(Version.LUCENE_46));
		IndexWriter writer = new IndexWriter(dir, conf);
		for (Field f : fields) {
			Document doc = new Document();
			doc.add(f);
			writer.addDocument(doc);
		}
		writer.close();
	}

	public static class SimpleSimilarity extends Similarity {

		@Override
		public float coord(int overlap, int maxOverlap) {
			return 1.0f;
		}

		@SuppressWarnings("rawtypes")
		public float idf(Vector terms, Searcher searcher) {
			return 1.0f;
		}

		public float lengthNorm(String field, int numTerms) {
			return 1.0f;
		}

		@Override
		public float queryNorm(float sumOfSquaredWeights) {
			return 1.0f;
		}

		public float sloppyFreq(int distance) {
			return 2.0f;
		}

		public float tf(float freq) {
			return freq;
		}

		public float idf(int docFreq, int numDocs) {
			return 1.0f;
		}

		@Override
		public long computeNorm(FieldInvertState state) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public SimWeight computeWeight(float queryBoost, CollectionStatistics collectionStats,
				TermStatistics... termStats) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SimScorer simScorer(SimWeight weight, AtomicReaderContext context) throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

	}

}
