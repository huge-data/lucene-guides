package cc.pp.lucene.chap05.advance.searching;

import java.io.StringReader;

import junit.framework.TestCase;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.spans.SpanFirstQuery;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanNotQuery;
import org.apache.lucene.search.spans.SpanOrQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.search.spans.Spans;
import org.apache.lucene.store.RAMDirectory;

import cc.pp.lucene.common.LuceneConstant;

public class SpanQueryTest extends TestCase {

	private RAMDirectory dir;
	private IndexSearcher searcher;
	private IndexReader reader;

	private SpanTermQuery quick;
	private SpanTermQuery brown;
	private SpanTermQuery red;
	private SpanTermQuery fox;
	private SpanTermQuery lazy;
	private SpanTermQuery sleepy;
	private SpanTermQuery dog;
	private SpanTermQuery cat;
	private Analyzer analyzer;

	@Override
	protected void setUp() throws Exception {

		dir = new RAMDirectory();

		analyzer = new WhitespaceAnalyzer(LuceneConstant.LUCENE_VERSION);
		IndexWriterConfig conf = new IndexWriterConfig(LuceneConstant.LUCENE_VERSION, analyzer);
		IndexWriter writer = new IndexWriter(dir, conf);

		Document doc = new Document();
		doc.add(new TextField("f", "the quick brown fox jumps over the lazy dog", Field.Store.YES));
		writer.addDocument(doc);

		doc = new Document();
		doc.add(new TextField("f", "the quick red fox jumps over the sleepy cat", Field.Store.YES));
		writer.addDocument(doc);

		writer.close();

		reader = DirectoryReader.open(dir);
		searcher = new IndexSearcher(reader);
		//		reader = searcher.getIndexReader();

		quick = new SpanTermQuery(new Term("f", "quick"));
		brown = new SpanTermQuery(new Term("f", "brown"));
		red = new SpanTermQuery(new Term("f", "red"));
		fox = new SpanTermQuery(new Term("f", "fox"));
		lazy = new SpanTermQuery(new Term("f", "lazy"));
		sleepy = new SpanTermQuery(new Term("f", "sleepy"));
		dog = new SpanTermQuery(new Term("f", "dog"));
		cat = new SpanTermQuery(new Term("f", "cat"));
	}

	private void assertOnlyBrownFox(Query query) throws Exception {

		TopDocs hits = searcher.search(query, 10);
		assertEquals(1, hits.totalHits);
		assertEquals("wrong doc", 0, hits.scoreDocs[0].doc);
	}

	private void assertBothFoxes(Query query) throws Exception {

		TopDocs hits = searcher.search(query, 10);
		assertEquals(2, hits.totalHits);
	}

	private void assertNoMatches(Query query) throws Exception {

		TopDocs hits = searcher.search(query, 10);
		assertEquals(0, hits.totalHits);
	}

	public void testSpanTermQuery() throws Exception {

		assertOnlyBrownFox(brown);
		dumpSpans(brown);
	}

	public void testSpanFirstQuery() throws Exception {

		SpanFirstQuery sfq = new SpanFirstQuery(brown, 2);
		assertNoMatches(sfq);

		dumpSpans(sfq);

		sfq = new SpanFirstQuery(brown, 3);
		dumpSpans(sfq);
		assertOnlyBrownFox(sfq);
	}

	public void testSpanNearQuery() throws Exception {

		SpanQuery[] quick_brown_dog = new SpanQuery[] { quick, brown, dog };
		SpanNearQuery snq = new SpanNearQuery(quick_brown_dog, 0, true);
		assertNoMatches(snq);
		dumpSpans(snq);

		snq = new SpanNearQuery(quick_brown_dog, 4, true);
		assertNoMatches(snq);
		dumpSpans(snq);

		snq = new SpanNearQuery(quick_brown_dog, 5, true);
		assertOnlyBrownFox(snq);
		dumpSpans(snq);

		snq = new SpanNearQuery(new SpanQuery[] { lazy, fox }, 3, false);
		assertOnlyBrownFox(snq);
		dumpSpans(snq);

		PhraseQuery pq = new PhraseQuery();
		pq.add(new Term("f", "lazy"));
		pq.add(new Term("f", "fox"));
		pq.setSlop(4);
		assertNoMatches(pq);

		pq.setSlop(5);
		assertOnlyBrownFox(pq);
	}

	public void testSpanQueryFilter() throws Exception {

		//		SpanQuery[] quick_brown_dog = new SpanQuery[] { quick, brown, dog };
		//		SpanQuery snq = new SpanNearQuery(quick_brown_dog, 5, true);
		//		Filter filter = new SpanQueryFilter(snq);
		//
		//		Query query = new MatchAllDocsQuery();
		//		TopDocs hits = searcher.search(query, filter, 10);
		//		assertEquals(1, hits.totalHits);
		//		assertEquals("wrong doc", 0, hits.scoreDocs[0].doc);
	}

	public void testSpanNotQuery() throws Exception {

		SpanNearQuery quick_fox = new SpanNearQuery(new SpanQuery[] { quick, fox }, 1, true);
		assertBothFoxes(quick_fox);
		dumpSpans(quick_fox);

		SpanNotQuery quick_fox_dog = new SpanNotQuery(quick_fox, dog);
		assertBothFoxes(quick_fox_dog);

		SpanNotQuery no_quick_red_dog = new SpanNotQuery(quick_fox, red);
		assertOnlyBrownFox(no_quick_red_dog);
		dumpSpans(no_quick_red_dog);
	}

	public void testSpanOrQuery() throws Exception {

		SpanNearQuery quick_fox = new SpanNearQuery(new SpanQuery[] { quick, fox }, 1, true);

		SpanNearQuery lazy_dog = new SpanNearQuery(new SpanQuery[] { lazy, dog }, 0, true);

		SpanNearQuery sleepy_cat = new SpanNearQuery(new SpanQuery[] { sleepy, cat }, 0, true);

		SpanNearQuery qf_near_ld = new SpanNearQuery(new SpanQuery[] { quick_fox, lazy_dog }, 3, true);
		assertOnlyBrownFox(qf_near_ld);
		dumpSpans(qf_near_ld);

		SpanNearQuery qf_near_sc = new SpanNearQuery(new SpanQuery[] { quick_fox, sleepy_cat }, 3, true);
		dumpSpans(qf_near_sc);

		SpanOrQuery or = new SpanOrQuery(new SpanQuery[] { qf_near_ld, qf_near_sc });
		assertBothFoxes(or);
		dumpSpans(or);
	}

	public void testPlay() throws Exception {

		SpanOrQuery or = new SpanOrQuery(new SpanQuery[] { quick, fox });
		dumpSpans(or);

		SpanNearQuery quick_fox = new SpanNearQuery(new SpanQuery[] { quick, fox }, 1, true);
		SpanFirstQuery sfq = new SpanFirstQuery(quick_fox, 4);
		dumpSpans(sfq);

		dumpSpans(new SpanTermQuery(new Term("f", "the")));

		SpanNearQuery quick_brown = new SpanNearQuery(new SpanQuery[] { quick, brown }, 0, false);
		dumpSpans(quick_brown);
	}

	private void dumpSpans(SpanQuery query) throws Exception {

		// 需要根据新版本重新设置
		Spans spans = query.getSpans((AtomicReaderContext) reader.getContext(), null, null);

		System.out.println(query + ":");
		int numSpans = 0;

		TopDocs hits = searcher.search(query, 10);
		float[] scores = new float[2];
		for (ScoreDoc sd : hits.scoreDocs) {
			scores[sd.doc] = sd.score;
		}

		while (spans.next()) {
			numSpans++;

			int id = spans.doc();
			Document doc = reader.document(id);

			TokenStream stream = analyzer.tokenStream("contents", new StringReader(doc.get("f")));
			CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);

			StringBuilder buffer = new StringBuilder();
			buffer.append("   ");
			int i = 0;
			while (stream.incrementToken()) {
				if (i == spans.start()) {
					buffer.append("<");
				}
				buffer.append(term.toString());
				if (i + 1 == spans.end()) {
					buffer.append(">");
				}
				buffer.append(" ");
				i++;
			}
			buffer.append("(").append(scores[id]).append("");
			System.out.println(buffer);
		}

		if (numSpans == 0) {
			System.out.println("    No spans");
		}
		System.out.println();
	}

}
