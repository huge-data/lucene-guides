package cc.pp.lucene.chap02.core;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.Version;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 报名：org.apache.lucene.analysis
 * 功能：该包将文本转换成可索引和可搜索的token单元。
 * <p>
 * 1、Analyzer： 用于构建TokenStreams来分析文本。
 * 2、Analyzer.ReuseStrategy： 定义每次调用Analyzer.tokenStream(String, java.io.Reader)的时，
 *                            TokenStreamComponents重用的策略。
 * 3、Analyzer.TokenStreamComponents：该类封装了一个token stream的外部组件。
 * 4、AnalyzerWrapper： 扩展Analyzer，使得适合于其他分词器。
 * 5、CachingTokenFilter： 如果TokenStream的token属性使用多次时，使用该类。
 * 6、CharFilter： CharFilter的子类可以被用作过滤Reader，这些子类使用额外的便宜校正时可以被用作Reader。
 * 7、NumericTokenStream： 专用类，该类提供索引数值型数据（在NumericRangeQuery或者NumericRangeFilter中使用到）的TokenStream。
 * 8、NumericTokenStream.NumericTermAttributeImpl： 接口NumericTokenStream.NumericTermAttribute的实现类。
 * 9、Token： 域中文本的token单元。
 * 10、Token.TokenAttributeFactory： 专用类，创建一个TokenAttributeFactory类，
 *                                           返回Token作为基本属性的实例，并且作为所有其他属性调用给定的代理工厂类。
 * 11、TokenFilter： 该类是一个输入参数为另一个TokenStream的TokenStream类。
 * 12、Tokenizer： 该类是一个输入参数为Reader的TokenStream。
 * 13、TokenStream： 该类计算出来自文档的域或者查询文本的token单元序列。
 * 14、TokenStreamToAutomaton	Consumes： 该类是一个TokenStream，从TermToBytesRefAttribute中创建一个过度标签为UTF8字节
 *                                              （如果unicodeArcs为true，则是Unicode代码点)的自动机。
 * </p>
 * @author wgybzb
 *
 */
public class LuceneAnalysis {

	/**
	 * 调用Analysis用于文本解析
	 */
	@Ignore
	public void testInvokingTheAnalyzer() throws IOException {

		//		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);
		Analyzer analyzer = new SmartChineseAnalyzer(Version.LUCENE_46);
		TokenStream tokenStream = analyzer.tokenStream("testfield", new StringReader("我今年买了块手表，但是我去年买了个表！"));
		OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
		try {
			// 重置stream到初始状态
			tokenStream.reset();
			while (tokenStream.incrementToken()) {
				// 使用AttributeSource.reflectAsString(boolean)用于token流调式
				System.out.println("token: " + tokenStream.reflectAsString(true));
				System.out.println("token start offset: " + offsetAttribute.startOffset());
				System.out.println("token end offset: " + offsetAttribute.endOffset());
			}
			// 设置最终偏移量
			tokenStream.end();
		} finally {
			tokenStream.close();
			analyzer.close();
		}
	}

	/**
	 * 短语查询，有问题
	 */
	@Test
	public void testPhraseSearch() throws IOException {

		//		Analyzer analyzer = new SmartChineseAnalyzer(Version.LUCENE_46);
		Analyzer analyzer = new WhitespaceAnalyzer(Version.LUCENE_46);
		IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_46, analyzer);
		Directory dir = new RAMDirectory();
		IndexWriter writer = new IndexWriter(dir, conf);

		Document doc = new Document();
		//		doc.add(new Field("myfield", "数据挖掘 工作", TextField.TYPE_STORED));
		//		doc.add(new Field("myfield", "很辛苦 的职位", TextField.TYPE_STORED));
		doc.add(new Field("myfield", "first ends", StringField.TYPE_STORED));
		//		doc.add(new Field("myfield", "starts two", StringField.TYPE_STORED));
		System.out.println(doc.get("myfield"));
		writer.addDocument(doc);
		writer.close();

		DirectoryReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);
		PhraseQuery query = new PhraseQuery();
		//		query.add(new Term("工作 很辛苦"));
		query.add(new Term("myfield", "first"));
		query.setSlop(10);
		System.out.println(query.toString());

		TopDocs topDocs = searcher.search(query, null, 10);
		System.out.println(topDocs.totalHits);
		for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
			Document d = searcher.doc(scoreDoc.doc);
			System.out.println(d.get("myfield"));
		}

		reader.close();
		dir.close();
	}

	@Test
	public void testTopWords() throws IOException {
		Reader reader = IOUtils.getDecodingReader(MyChineseAnalyzer.class, "stopwords.txt", IOUtils.CHARSET_UTF_8);
		System.out.println(reader.read());
	}
}
