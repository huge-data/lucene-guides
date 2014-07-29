package cc.pp.chap08and09.tools;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class HighlightTest extends TestCase {

	@SuppressWarnings("resource")
	public void testHighlighting() throws IOException, InvalidTokenOffsetsException {

		String text = "The quick brown fox jumps over the lazy dog";
		TermQuery query = new TermQuery(new Term("field", "fox"));
		TokenStream tokenStream = new SimpleAnalyzer().//
				tokenStream("field", new StringReader(text));

		QueryScorer scorer = new QueryScorer(query, "field");
		Highlighter highlighter = new Highlighter(scorer);
		Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
		highlighter.setTextFragmenter(fragmenter);

		assertEquals("The quick brown <B>fox</B> jumps over the lazy dog", //
				highlighter.getBestFragment(tokenStream, text));
	}

	@SuppressWarnings("resource")
	public void testHits() throws IOException, InvalidTokenOffsetsException {

		Directory dir = FSDirectory.open(new File("index/chap03index/"));
		IndexSearcher searcher = new IndexSearcher(dir);
		TermQuery query = new TermQuery(new Term("title", "action"));
		TopDocs hits = searcher.search(query, 10);

		QueryScorer scorer = new QueryScorer(query, "title");
		Highlighter highlighter = new Highlighter(scorer);
		Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
		highlighter.setTextFragmenter(fragmenter);

		Analyzer analyzer = new SimpleAnalyzer();

		for (ScoreDoc sd : hits.scoreDocs) {
			Document doc = searcher.doc(sd.doc);
			String title = doc.get("title");
			TokenStream stream = TokenSources.getAnyTokenStream( //
					searcher.getIndexReader(), sd.doc, "title", doc, analyzer);
			String fragment = highlighter.getBestFragment(stream, title);
			System.out.println(fragment);
		}
	}

}
