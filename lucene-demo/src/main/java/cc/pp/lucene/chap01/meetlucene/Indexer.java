package cc.pp.lucene.chap01.meetlucene;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * 索引类
 * @author wgybzb
 *
 */
public class Indexer {

	private final IndexWriter writer;

	public Indexer(String indexDir) throws IOException {
		Directory dir = FSDirectory.open(new File(indexDir));
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_46);
		IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_46, analyzer);
		writer = new IndexWriter(dir, conf);
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		//		if (args.length != 2) {
		//			throw new IllegalArgumentException("Usage: java " + Indexer.class.getName() //
		//					+ " <index dir> <data dir>");
		//		}
		//		// 索引存放路径
		//		String indexDir = args[0];
		//		// 待搜索数据存放路径
		//		String dataDir = args[1];

		String indexDir = "index/chap01index/";
		String dataDir = "data/chap01data/";

		long start = System.currentTimeMillis();
		Indexer indexer = new Indexer(indexDir);
		int numIndexed;
		try {
			numIndexed = indexer.index(dataDir, new TextFilesFilter());
		} finally {
			indexer.close();
		}
		long end = System.currentTimeMillis();

		System.out.println("Indexing " + numIndexed + " files took " //
				+ (end - start) + " milliseconds");
	}

	public void close() throws CorruptIndexException, IOException {
		writer.close();
	}

	public int index(String dataDir, FileFilter filter) throws Exception {

		File[] files = new File(dataDir).listFiles();

		for (File f : files) {
			if (!f.isDirectory() && !f.isHidden() && f.exists() && f.canRead() //
					&& (filter == null || filter.accept(f))) {
				indexFile(f);
			}
		}

		return writer.numDocs();
	}

	protected Document getDocument(File f) throws Exception {

		Document doc = new Document();
		doc.add(new TextField("contents", new FileReader(f)));
		doc.add(new StringField("filename", f.getName(), Field.Store.YES));
		doc.add(new StringField("fullpath", f.getCanonicalPath(), Field.Store.YES));

		return doc;
	}

	private void indexFile(File f) throws Exception {

		System.out.println("Indexing " + f.getCanonicalPath()); // 输出绝对路径
		Document doc = getDocument(f);
		writer.addDocument(doc);
	}

	private static class TextFilesFilter implements FileFilter {

		@Override
		public boolean accept(File path) {
			return path.getName().toLowerCase().endsWith(".txt");
		}
	}

}
