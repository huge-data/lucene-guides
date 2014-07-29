package cc.pp.lucene.chap02.index;

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import cc.pp.lucene.common.TestUtil;

public class IndexingTest extends TestCase {

	protected String[] ids = { "1", "2" };
	protected String[] unindexed = { "Netherlands", "Italy" };
	protected String[] unstored = { "Amsterdam has lots of bridges", "Venice has lots of canals" };
	protected String[] text = { "Amsterdam", "Venice" };

	private Directory directory;

	public void close() throws IOException {
		if (directory != null) {
			directory.close();
		}
	}

	/**
	 * 测试之前的运行函数，类似构造函数
	 */
	@Override
	protected void setUp() throws IOException {

		directory = new RAMDirectory();
		IndexWriter writer = getWriter(); // 创建一个写索引类

		for (int i = 0; i < ids.length; i++) { // 添加文档
			Document doc = new Document();
			doc.add(new StringField("id", ids[i], Field.Store.YES));
			doc.add(new StringField("country", unindexed[i], Field.Store.YES));
			doc.add(new TextField("contents", unstored[i], Field.Store.NO));
			doc.add(new TextField("city", text[i], Field.Store.YES));
			writer.addDocument(doc);
		}
		writer.close();
	}

	/**
	 * 获取Writer类
	 * @return
	 * @throws CorruptIndexException
	 * @throws LockObtainFailedException
	 * @throws IOException
	 */
	private IndexWriter getWriter() throws IOException {

		IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_46, new WhitespaceAnalyzer(Version.LUCENE_46));
		return new IndexWriter(directory, conf);
	}

	protected int getHitCount(String fieldName, String searchString) throws IOException {

		IndexReader reader = DirectoryReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(reader); // 创建搜索类
		Term t = new Term(fieldName, searchString);
		Query query = new TermQuery(t); // 创建词条查询
		int hitCount = TestUtil.hitCount(searcher, query); // 获取查询结果数
		reader.close();
		return hitCount;
	}

	/**
	 * 验证写索引数
	 * @throws IOException
	 */
	public void testIndexWriter() throws IOException {

		IndexWriter writer = getWriter();
		assertEquals(ids.length, writer.numDocs());
		writer.close();
	}

	/**
	 * 验证读索引数
	 * @throws IOException
	 */
	public void testIndexReader() throws IOException {

		IndexReader reader = DirectoryReader.open(directory);
		assertEquals(ids.length, reader.maxDoc());
		assertEquals(ids.length, reader.numDocs());
		reader.close();
	}

	/**
	 * 删除验证，未优化
	 * @throws IOException
	 */
	public void testDeleteBeforeOptimize() throws IOException {

		IndexWriter writer = getWriter();
		assertEquals(2, writer.numDocs()); // 2个文档在索引中
		writer.deleteDocuments(new Term("id", "1")); // 删除第1个文档
		writer.commit();
		assertTrue(writer.hasDeletions()); // 判断是否删除操作，也就是删除操作被标记
		// 1个索引文档，一个删除文档
		assertEquals(2, writer.maxDoc());
		assertEquals(1, writer.numDocs());
		writer.close();
	}

	public void testDeleteAfterOptimize() throws IOException {

		IndexWriter writer = getWriter();
		assertEquals(2, writer.numDocs());
		writer.deleteDocuments(new Term("id", "1"));
		//		writer.optimize(); // 使用优化操作作用于删除标记（新版本中不存在）
		writer.commit();
		assertFalse(writer.hasDeletions());
		// 1个索引文档，0个删除标记文档
		assertEquals(1, writer.maxDoc());
		assertEquals(1, writer.numDocs());
		writer.close();
	}

	public void testUpdate() throws IOException {

		assertEquals(1, getHitCount("city", "Amsterdam"));
		IndexWriter writer = getWriter();

		Document doc = new Document();
		doc.add(new StringField("id", "1", Field.Store.YES));
		doc.add(new StringField("country", "Netherlands", Field.Store.YES));
		doc.add(new TextField("contents", "Den Hagg has a lot of museums", Field.Store.NO));
		doc.add(new TextField("city", "Den Haag", Field.Store.YES));
		writer.updateDocument(new Term("id", "1"), doc);
		writer.close();

		assertEquals(0, getHitCount("city", "Amsteradm"));
		assertEquals(1, getHitCount("city", "Haag"));
	}

	public void testMaxFieldLength() throws IOException {

		assertEquals(1, getHitCount("contents", "bridges"));
		IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_46, new WhitespaceAnalyzer(Version.LUCENE_46));
		IndexWriter writer = new IndexWriter(directory, conf); // 设置最大域长度为1
		Document doc = new Document();
		doc.add(new TextField("contents", "these bridges can't be found", Field.Store.NO));
		writer.addDocument(doc);
		writer.close();

		assertEquals(1, getHitCount("contents", "bridges"));
	}

}
