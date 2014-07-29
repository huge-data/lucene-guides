package cc.pp.chap08and09.tools;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.db.DbDirectory;
import org.apache.lucene.util.Version;

import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseConfig;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.DatabaseType;
import com.sleepycat.db.Environment;
import com.sleepycat.db.EnvironmentConfig;
import com.sleepycat.db.Transaction;

/**
 * 有问题，数据库版本不一样
 * @author wanggang
 *
 */
public class BerkeleyDbIndexer {

	/**
	 * 测试函数
	 * @param args
	 * @throws DatabaseException
	 * @throws IOException
	 * @throws CorruptIndexException
	 */
	public static void main(String[] args) throws DatabaseException, IOException {

		//		if (args.length != 1) {
		//			System.err.println("Usage: BerkeleyDbIndexer <index dir>");
		//			System.exit(-1);
		//		}
		//		String indexDir = args[0];
		String indexDir = "index/berkeleyindex";
		File indexFile = new File(indexDir);

		// 删除索引目录及文件
		if (indexFile.exists()) {
			File[] files = indexFile.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].getName().startsWith("__")) {
					files[i].delete();
				}
			}
			indexFile.delete();
		}

		indexFile.mkdir();

		// BerkeleyDB环境、数据库配置类
		EnvironmentConfig envConfig = new EnvironmentConfig();
		DatabaseConfig dbConfig = new DatabaseConfig();

		envConfig.setTransactional(true);
		envConfig.setInitializeCache(true);
		envConfig.setInitializeLocking(true);
		envConfig.setInitializeLogging(true);
		envConfig.setAllowCreate(true);
		envConfig.setThreaded(true);
		dbConfig.setAllowCreate(true);
		dbConfig.setType(DatabaseType.BTREE);

		Environment env = new Environment(indexFile, envConfig);

		Transaction txn = env.beginTransaction(null, null);
		Database index = env.openDatabase(txn, "__index__", null, dbConfig);
		Database blocks = env.openDatabase(txn, "__blocks__", null, dbConfig);
		txn.commit();

		txn = env.beginTransaction(null, null);
		DbDirectory dir = new DbDirectory(txn, index, blocks);

		IndexWriter writer = new IndexWriter(dir, //
				new StandardAnalyzer(Version.LUCENE_30), true, //
				IndexWriter.MaxFieldLength.UNLIMITED);

		Document doc = new Document();
		doc.add(new Field("contents", "The quick brown fox...", //
				Field.Store.YES, Field.Index.ANALYZED));
		writer.addDocument(doc);

		writer.optimize();
		writer.close();

		dir.close();
		txn.commit();

		index.close();
		blocks.close();
		env.close();

		System.out.println("Indexing Complete");

	}

}
