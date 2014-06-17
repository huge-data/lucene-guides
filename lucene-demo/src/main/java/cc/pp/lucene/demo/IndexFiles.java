package cc.pp.lucene.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * 索引一个目录下的所有文件
 * @author wgybzb
 *
 */
public class IndexFiles {

	private IndexFiles() {
		//
	}

	/**
	 * 主函数
	 */
	public static void main(String[] args) {

		String usage = "IndexFiles [-index INDEX_PATH] [-docs DOCS_PATH] [-update]\n\n"
				+ "This indexes the documents in DOCS_PATH, creating a Lucene index in INDEX_PATH that"
				+ " can be searched with SearchFiles";
		String indexPath = "index";
		String docsPath = null;
		boolean create = true;
		for (int i = 0; i < args.length; i++) {
			if ("-index".equals(args[i])) {
				indexPath = args[i + 1];
				i++;
			} else if ("-docs".equals(args[i])) {
				docsPath = args[i + 1];
				i++;
			} else if ("-update".equals(args[i])) {
				create = false;
			}
		}

		if (docsPath == null) {
			System.err.println("Usage: " + usage);
			System.exit(1);
		}

		final File docDir = new File(docsPath);
		if (!docDir.exists() || !docDir.canRead()) {
			System.err.println("Document directory '" + docDir.getAbsolutePath()
					+ " does not exist or is not readable, please check the path.");
			System.exit(1);
		}

		Date start = new Date();
		try {
			System.out.println("Indexing to directory '" + indexPath + "'...");

			Directory dir = FSDirectory.open(new File(indexPath));
			//			Analyzer analyzer = new SmartChineseAnalyzer(Version.LUCENE_46);
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);
			IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_46, analyzer);

			if (create) {
				// 创建新索引，删除旧索引
				conf.setOpenMode(OpenMode.CREATE);
			} else {
				// 添加新的文档到已存在的索引中
				conf.setOpenMode(OpenMode.CREATE_OR_APPEND);
			}

			/*
			 * 为了使索引性能更好，当索引很多文档时，增加RAM缓存大小，同时还需要JVM的最大堆大小（-Xmx512m或其他数值）
			 */
			conf.setRAMBufferSizeMB(256.0);

			IndexWriter writer = new IndexWriter(dir, conf);
			indexDocs(writer, docDir);

			/*
			 * 如果想最大化搜索性能，可以选择性地调用forceMerge设置。不过这样会极大地增加性能消耗，
			 * 所以一般情况下，当索引相对静止时是值得这样操作的（例如，已经添加了索引文档）。
			 */
			writer.forceMerge(1);

			writer.close();

			Date end = new Date();
			System.out.println(end.getTime() - start.getTime() + " total milliseconds.");

		} catch (IOException e) {
			System.err.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
		}
	}

	/**
	 * 递归添加索引
	 */
	public static void indexDocs(IndexWriter writer, File file) throws IOException {

		if (file.canRead()) {
			if (file.isDirectory()) {
				String[] files = file.list();
				// IO异常可能抛出
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						indexDocs(writer, new File(file, files[i]));
					}
				}
			} else {
				FileInputStream fis;
				try {
					fis = new FileInputStream(file);
				} catch (FileNotFoundException e) {
					// 在windows上一些临时文件可能会引起这个异常，异常信息是“access denied”，检查文件是否可读也不起作用。
					System.err.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
					return;
				}

				try {
					Document doc = new Document();
					doc.add(new StringField("path", file.getPath(), Field.Store.YES));
					doc.add(new LongField("modified", file.lastModified(), Field.Store.NO));
					doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(fis, "UTF-8"))));

					if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
						System.out.println("adding " + file);
						writer.addDocument(doc);
					} else {
						System.out.println("updating " + file);
						writer.updateDocument(new Term("path", file.getPath()), doc);
					}
				} finally {
					fis.close();
				}
			}
		}
	}

}
