package cc.pp.chap07.tika;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;

import cc.pp.chap01.meetlucene.Indexer;

public class TikaIndexer extends Indexer {

	/**
	 * 调试输出信息
	 */
	private final boolean DEBUG = false;

	static Set<String> texttualMetadataFields = new HashSet<String>();

	/**
	 * 主函数
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		//		if (args.length != 2) {
		//			throw new IllegalArgumentException("Usage: java " + TikaIndexer.class.getName() //
		//					+ " <index dir> <data dir>");
		//		}
		//		String indexDir = args[0];
		//		String dataDir = args[1];

		String indexDir = "index/chap07index/";
		String dataDir = "data/chap07data/";

		/**
		 * 列出所有目前可处理的文档类型
		 */
		TikaConfig config = TikaConfig.getDefaultConfig();
		List<String> parsers = new ArrayList<String>(config.getParsers().keySet());
		Collections.sort(parsers);
		Iterator<String> it = parsers.iterator();

		System.out.println("Mime type parsers: ");
		while (it.hasNext()) {
			System.out.println(" " + it.next());
		}
		System.out.println();

		long start = new Date().getTime();
		TikaIndexer indexer = new TikaIndexer(indexDir);
		int numIndexed = indexer.index(dataDir, null);
		indexer.close();
		long end = new Date().getTime();

		System.out.println("Indexing " + numIndexed + " files took " + //
				(end - start) + " milliseconds");
	}

	/**
	 * 存放文本的元数据域
	 */
	static {
		texttualMetadataFields.add(Metadata.TITLE);
		texttualMetadataFields.add(Metadata.AUTHOR);
		texttualMetadataFields.add(Metadata.COMMENTS);
		texttualMetadataFields.add(Metadata.KEYWORDS);
		texttualMetadataFields.add(Metadata.DESCRIPTION);
		texttualMetadataFields.add(Metadata.SUBJECT);
	}

	public TikaIndexer(String indexDir) throws IOException {
		super(indexDir);
	}

	@Override
	protected Document getDocument(File f) throws Exception {

		Metadata metadata = new Metadata();
		metadata.set(Metadata.RESOURCE_NAME_KEY, f.getName()); // 创建Metadata实例
		//		Metadata.CONTENT_TYPE
		//		Metadata.CONTENT_ENCODING
		InputStream is = new FileInputStream(f);
		Parser parser = new AutoDetectParser(); // 自动查询文档类型
		ContentHandler handler = new BodyContentHandler(); // 提取元数据和主体文本数据
		ParseContext context = new ParseContext();
		context.set(Parser.class, parser); // 设置Parser分析器

		try {
			parser.parse(is, handler, metadata, new ParseContext()); // 解析处理操作
		} finally {
			is.close();
		}

		Document doc = new Document();
		doc.add(new Field("contents", handler.toString(), // 索引主体内容
				Field.Store.NO, Field.Index.ANALYZED));

		if (DEBUG) {
			System.out.println(" all text: " + handler.toString());
		}

		for (String name : metadata.names()) { // 索引元数据域
			String value = metadata.get(name);
			if (texttualMetadataFields.contains(name)) {
				doc.add(new Field("contents", value, // 添加内容到域中
						Field.Store.NO, Field.Index.ANALYZED));
			}
			doc.add(new Field("name", value, Field.Store.YES, Field.Index.NO)); // 分开存储元数据域
			if (DEBUG) {
				System.out.println(" " + name + ": " + value);
			}
		}

		if (DEBUG) {
			System.out.println();
		}

		doc.add(new Field("filename", f.getCanonicalPath(), // 索引文件路径
				Field.Store.YES, Field.Index.NOT_ANALYZED));

		return doc;
	}
}
