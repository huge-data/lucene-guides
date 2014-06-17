package cc.pp.lucene.simple.examples;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import cc.pp.lucene.utils.PathUtils;

public class SimpleExampleCh {

	public static void main(String[] args) throws IOException, ParseException {

		// 索引存储在内存中
		//		Directory dir = new RAMDirectory();
		// 索引存储的本地文件中
		String filePath = "index/simple_example_ch";
		Directory dir = FSDirectory.open(new File(filePath));

		/**
		 * 建立索引文件
		 */
		// 定义分析器
		Analyzer analyzer = new SmartChineseAnalyzer(Version.LUCENE_46);
		// 写索引初始化配置文件
		IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_46, analyzer);
		// 写索引类实例化
		IndexWriter writer = new IndexWriter(dir, conf);

		// 待索引文本
		String text1 = "这个中文文本用来测试简单的索引搜索功能的。";
		String text2 = "我去年买了个表，今天又在做文本分析工作。";

		// 文档实例化
		Document doc1 = new Document();
		// 文本添加到content域，并且被存储；默认TextField类型是被分析的。
		doc1.add(new Field("content", text1, TextField.TYPE_STORED));
		// 添加文档到写索引中
		writer.addDocument(doc1);

		Document doc2 = new Document();
		doc2.add(new Field("content", text2, TextField.TYPE_STORED));
		writer.addDocument(doc2);

		// 关闭写索引
		writer.close();

		/**
		 * 搜索索引文件
		 */
		// 读索引目录实例化
		DirectoryReader reader = DirectoryReader.open(dir);
		// 搜索实例化
		IndexSearcher searcher = new IndexSearcher(reader);

		// 查询解析器实例化
		QueryParser parser = new QueryParser(Version.LUCENE_46, "content", analyzer);
		// 查询解析
		Query query = parser.parse("分析");

		// 结果文档的评分数组
		ScoreDoc[] hits = searcher.search(query, null, 1000).scoreDocs;

		System.out.println(hits.length);

		for (ScoreDoc hit : hits) {
			Document hitDoc = searcher.doc(hit.doc);
			System.out.println(hitDoc.get("content"));
		}

		// 关闭读索引和目录对象
		reader.close();
		dir.close();

		// 删除索引目录
		PathUtils.deletePath(filePath);
	}

}
