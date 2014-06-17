package cc.pp.lucene.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import cc.pp.analyzer.ik.lucene.IKAnalyzer;

public class IndexUtils {

	private Directory dir;
	private IndexWriter writer;

	/**
	 * 初始化索引
	 */
	private void init(String indexDir) throws IOException {
		dir = FSDirectory.open(new File(indexDir));
		IndexWriterConfig config = new IndexWriterConfig(LuceneUtils.CURRENT_VERSION, new IKAnalyzer(
				LuceneUtils.CURRENT_VERSION));
		writer = new IndexWriter(dir, config);
	}

	/**
	 * 添加索引数据
	 */
	public void addIndexData(String indexDir) throws IOException {
		init(indexDir);
		try (BufferedReader br = new BufferedReader(new FileReader(new File("data/utilsdata/dataset")));) {
			String str = null;
			String[] strs = null;
			while ((str = br.readLine()) != null) {
				strs = str.split("\t");
				writer.addDocument(getDoc(Long.parseLong(strs[0]), strs[1]));
			}
		}
		close();
	}

	/**
	 * 获取组装好的文档
	 */
	private static Document getDoc(long id, String text) {
		Document doc = new Document();
		doc.add(new NumericDocValuesField("id", id));
		doc.add(new StoredField("id", id));
		doc.add(new TextField("content", text, Field.Store.YES));
		return doc;
	}

	/**
	 * 关闭索引
	 */
	public void close() throws IOException {
		writer.close();
		dir.close();
	}

}
