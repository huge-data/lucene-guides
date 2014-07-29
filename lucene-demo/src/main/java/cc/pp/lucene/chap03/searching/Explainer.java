package cc.pp.lucene.chap03.searching;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import cc.pp.lucene.common.LuceneConstant;

public class Explainer {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws Exception {

		String indexDir = "index/chap03index/";
		String queryExpression = "junit";
		Directory dir = FSDirectory.open(new File(indexDir));
		QueryParser parser = new QueryParser(LuceneConstant.LUCENE_VERSION, "contents", new SimpleAnalyzer(
				LuceneConstant.LUCENE_VERSION));
		Query query = parser.parse(queryExpression);

		System.out.println("Query: " + queryExpression);

		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopDocs docs = searcher.search(query, 10);
		for (ScoreDoc match : docs.scoreDocs) {
			Explanation explanation = searcher.explain(query, match.doc);
			System.out.println("---------------");
			Document doc = searcher.doc(match.doc);
			System.out.println(doc.get("title"));
			System.out.println(explanation.toString());
		}

		reader.close();
		dir.close();
	}

}
