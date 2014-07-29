package cc.pp.chap08and09.tools;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.vectorhighlight.BaseFragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.FastVectorHighlighter;
import org.apache.lucene.search.vectorhighlight.FieldQuery;
import org.apache.lucene.search.vectorhighlight.FragListBuilder;
import org.apache.lucene.search.vectorhighlight.FragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.ScoreOrderFragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.SimpleFragListBuilder;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class FastVectorHighlighterSample {

	private static final String[] DOCS = { //
	"the quick brown fox jumps over the lazy dog", //
			"the quick gold fox jumped over the lazy black dog", //
			"the quick fox jumps over the black dog", //
			"the red fox jumpedover the lazy dark gray dog" };

	private static final String QUERY = "quick OR fox OR \"lazy dog\"~1";
	private static final String F = "f";
	private static Directory dir = new RAMDirectory();
	private static Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);

	public static FastVectorHighlighter getHighlighter() {

		FragListBuilder fragListBuilder = new SimpleFragListBuilder();
		FragmentsBuilder fragmentBuilder = new ScoreOrderFragmentsBuilder(//
				BaseFragmentsBuilder.COLORED_PRE_TAGS, //
				BaseFragmentsBuilder.COLORED_POST_TAGS);
		return new FastVectorHighlighter(true, true, //
				fragListBuilder, fragmentBuilder);
	}

	/**
	 * 测试函数
	 * @throws IOException
	 * @throws LockObtainFailedException
	 * @throws CorruptIndexException
	 */
	public static void main(String[] args) throws Exception {

		//		if (args.length != 1) {
		//			System.err.println("Usage: FastVectorHighlighterSample <filename>");
		//			System.exit(-1);
		//		}
		//		String fileName = args[0];
		String fileName = "data/chap08data/fvhighlighted.html";
		makeIndex();
		searchIndex(fileName);
		System.out.println("Hightlight Complete!");
	}

	public static void makeIndex() throws Exception {

		IndexWriter writer = new IndexWriter(dir, analyzer, true, //
				IndexWriter.MaxFieldLength.UNLIMITED);
		for (String d : DOCS) {
			Document doc = new Document();
			doc.add(new Field(F, d, Field.Store.YES, Field.Index.ANALYZED, //
					Field.TermVector.WITH_POSITIONS_OFFSETS));
			writer.addDocument(doc);
		}
		writer.close();
	}

	public static void searchIndex(String fileName) throws IOException, ParseException {

		QueryParser parser = new QueryParser(Version.LUCENE_30, F, analyzer);
		Query query = parser.parse(QUERY);
		FastVectorHighlighter highlighter = getHighlighter();
		FieldQuery fieldQuery = highlighter.getFieldQuery(query);
		IndexSearcher searcher = new IndexSearcher(dir);
		TopDocs hits = searcher.search(query, 10);

		FileWriter writer = new FileWriter(fileName);
		writer.write("<html>\n");
		writer.write("<body>\n");
		writer.write("<p>QUERY : " + QUERY + "</p>\n");
		for (ScoreDoc sd : hits.scoreDocs) {
			String snippet = highlighter.getBestFragment(fieldQuery, //
					searcher.getIndexReader(), sd.doc, F, 100);
			if (snippet != null) {
				writer.write(sd.doc + " : " + snippet + "<br/>");
			}
		}
		writer.write("\n</body>\n</html>");
		writer.close();
		searcher.close();
	}

}
