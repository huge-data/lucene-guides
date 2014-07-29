package cc.pp.lucene.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

public class CreateTestIndex {

	/**
	 * 主函数
	 * @throws IOException 
	 * @throws LockObtainFailedException 
	 * @throws CorruptIndexException 
	 */
	public static void main(String[] args) throws IOException {

		String dataDir = "data/chap03data/";
		String indexDir = "index/chap03index/";
		List<File> results = new ArrayList<File>();
		findFiles(results, new File(dataDir));
		System.out.println(results.size() + " books to index");
		Directory dir = FSDirectory.open(new File(indexDir));
		IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_46, new StandardAnalyzer(Version.LUCENE_46));
		IndexWriter writer = new IndexWriter(dir, conf);
		for (File file : results) {
			Document doc = getDocument(dataDir, file);
			writer.addDocument(doc);
		}
		writer.close();
		dir.close();

	}

	public static Document getDocument(String rootDir, File file) throws FileNotFoundException, IOException {

		Properties props = new Properties();
		props.load(new FileInputStream(file));
		// 分类文件在本地路径下
		String category = file.getParent().substring(rootDir.length());
		category = category.replace(File.separatorChar, '/');
		// 拉出配置文件里面的信息
		String isbn = props.getProperty("isbn");
		String title = props.getProperty("title");
		String author = props.getProperty("author");
		String url = props.getProperty("url");
		String subject = props.getProperty("subject");
		String pubmonth = props.getProperty("pubmonth");

		System.out.println(title + "\n" + author + "\n" + subject + "\n" + //
				pubmonth + "\n" + category + "\n----------");
		// 把域添加到文档里面
		Document doc = new Document();
		doc.add(new StringField("isbn", isbn, Field.Store.YES));
		doc.add(new StringField("category", category, Field.Store.YES));
		doc.add(new StringField("title", title, Field.Store.YES));
		doc.add(new StringField("title2", title.toLowerCase(), Field.Store.YES));
		// 把多个作者分开插入到独立的域中
		String[] authors = author.split(",");
		for (String a : authors) {
			doc.add(new StringField("author", a, Field.Store.YES));
		}
		doc.add(new StringField("url", url, Field.Store.YES));
		doc.add(new TextField("subject", subject, Field.Store.YES));
		doc.add(new IntField("pubmonth", Integer.parseInt(pubmonth), Field.Store.YES));

		Date d;
		try {
			d = DateTools.stringToDate(pubmonth);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		doc.add(new IntField("pubmonthAsDay", (int) (d.getTime() / (1000 * 86400)), Field.Store.YES));

		for (String text : new String[] { title, subject, author, category }) {
			doc.add(new TextField("contents", text, Field.Store.NO));
		}

		return doc;
	}

	/**
	 * 字符串数组合并
	 * @param strins
	 * @return
	 */
	@SuppressWarnings("unused")
	private static String aggregate(String[] strings) {

		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < strings.length; i++) {
			buffer.append(strings[i]);
			buffer.append(" ");
		}
		return buffer.toString();
	}

	/**
	 * 遍历所有文件
	 * @param result
	 * @param dir
	 */
	private static void findFiles(List<File> result, File dir) {

		for (File file : dir.listFiles()) {
			if (file.getName().endsWith(".properties")) {
				result.add(file);
			} else if (file.isDirectory()) {
				findFiles(result, file);
			}
		}
	}

}
