package cc.pp.lucene.chap04.analysis;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.RAMDirectory;

import cc.pp.lucene.common.LuceneConstant;

/**
 * 该类不做任何事，只求正确编译。
 * 用来展示Analyzers分析器被使用时的快照。
 */
public class UsingAnalyzersExample {

	/**
	 * 测试函数
	 * @param args
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 */
	public static void main(String[] args) throws Exception {

		RAMDirectory dir = new RAMDirectory();

		Analyzer analyzer = new StandardAnalyzer(LuceneConstant.LUCENE_VERSION);
		IndexWriterConfig conf = new IndexWriterConfig(LuceneConstant.LUCENE_VERSION, analyzer);
		IndexWriter writer = new IndexWriter(dir, conf);

		Document doc = new Document();
		doc.add(new TextField("title", "This is the title", Field.Store.YES));
		doc.add(new TextField("contents", "...some document contents...", Field.Store.YES));
		writer.addDocument(doc);

		writer.addDocument(doc, analyzer);
		writer.close();

		String expression = "some document";

		QueryParser parser = new QueryParser(LuceneConstant.LUCENE_VERSION, "contents", analyzer);
		Query query = parser.parse(expression);

		System.out.println("the query is: " + query.toString());

	}

}
