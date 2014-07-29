package cc.pp.lucene.chap05.advance.searching;

import java.io.File;
import java.io.PrintStream;
import java.text.DecimalFormat;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import cc.pp.lucene.common.LuceneConstant;

public class SortingExample {

	private final Directory dir;

	public SortingExample(Directory dir) {
		this.dir = dir;
	}

	/**
	 * 主函数
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		Query allBooks = new MatchAllDocsQuery();
		QueryParser parser = new QueryParser(LuceneConstant.LUCENE_VERSION, "contents", new StandardAnalyzer(
				LuceneConstant.LUCENE_VERSION));
		BooleanQuery query = new BooleanQuery();
		query.add(allBooks, BooleanClause.Occur.SHOULD);
		query.add(parser.parse("java OR action"), BooleanClause.Occur.SHOULD);

		Directory dir = FSDirectory.open(new File("index/chap03index/"));
		SortingExample example = new SortingExample(dir);

		example.displayResults(query, Sort.RELEVANCE);

		example.displayResults(query, Sort.INDEXORDER);

		example.displayResults(query, new Sort(new SortField("category", SortField.Type.STRING)));

		example.displayResults(query, new Sort(new SortField("pubmonth", SortField.Type.INT, true)));

		example.displayResults(query, new Sort(new SortField("category", SortField.Type.STRING),//
				SortField.FIELD_SCORE, new SortField("pubmonth", SortField.Type.INT, true)));

		example.displayResults(query, new Sort(new SortField[] { SortField.FIELD_SCORE, //
				new SortField("category", SortField.Type.STRING) }));

		example.displayResults(query, new Sort(new SortField[] { //
				new SortField("category", SortField.Type.STRING), SortField.FIELD_SCORE }));

		dir.close();
	}

	public void displayResults(Query query, Sort sort) throws Exception {

		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);
		//		searcher.setDefaultFieldSortScoring(true, false); // 计算score
		TopDocs results = searcher.search(query, null, 20, sort); // 添加排序

		System.out.println("\nResults for: " + //
				query.toString() + " sorted by " + sort);
		System.out.println(StringUtils.rightPad("Title", 30) + //
				StringUtils.rightPad("pubmonth", 10) + //
				StringUtils.center("id", 4) + //
				StringUtils.center("score", 15));

		PrintStream out = new PrintStream(System.out, true, "UTF-8");

		DecimalFormat scoreFormatter = new DecimalFormat("0.######");
		for (ScoreDoc sd : results.scoreDocs) {
			int docID = sd.doc;
			float score = sd.score;
			Document doc = searcher.doc(docID);
			out.println(StringUtils.rightPad( //
					StringUtils.abbreviate(doc.get("title"), 29), 30) + //
					StringUtils.rightPad(doc.get("pubmonth"), 10) + //
					StringUtils.center("" + docID, 4) + //
					StringUtils.leftPad(scoreFormatter.format(score), 12));
			out.println("    " + doc.get("category"));
			//			out.println(searcher.explain(query, docID));
		}

		reader.close();
	}

}
