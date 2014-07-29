package cc.pp.lucene.chap01.meetlucene;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Searcher {

	/**
	 * @param args
	 * @throws ParseException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, ParseException {

		//		if (args.length != 2) {
		//			throw new IllegalArgumentException("Usgae: java " + //
		//					Searcher.class.getName() + " <index dir> <query>");
		//		}
		//		// 索引路径
		//		String indexDir = args[0];
		//		// 待查询字符串
		//		String q = args[1];

		String indexDir = "index/chap01index";
		String q = "patent";

		search(indexDir, q);
	}

	public static void search(String indexDir, String q) throws IOException, ParseException {

		Directory dir = FSDirectory.open(new File(indexDir));
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher is = new IndexSearcher(reader);

		QueryParser parser = new QueryParser(Version.LUCENE_46, "contents", new StandardAnalyzer(Version.LUCENE_46));
		Query query = parser.parse(q);

		long start = System.currentTimeMillis();
		TopDocs hits = is.search(query, 10);
		long end = System.currentTimeMillis();

		System.err.println("Found " + hits.totalHits + " document(s) (in " + (end - start)
				+ " milliseconds) that matched query '" + q + "':");

		for (ScoreDoc scoreDoc : hits.scoreDocs) {
			Document doc = is.doc(scoreDoc.doc);
			System.out.println(doc.get("fullpath"));
		}

		reader.close();
		dir.close();
	}

}
