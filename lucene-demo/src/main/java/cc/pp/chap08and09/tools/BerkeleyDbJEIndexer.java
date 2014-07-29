package cc.pp.chap08and09.tools;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.je.JEDirectory;
import org.apache.lucene.util.Version;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.Transaction;

public class BerkeleyDbJEIndexer {

	/**
	 * 测试函数
	 * @param args
	 * @throws IOException
	 * @throws LockObtainFailedException
	 * @throws CorruptIndexException
	 * @throws DatabaseException
	 */
	public static void main(String[] args) throws LockObtainFailedException, IOException,
			DatabaseException {

		//		if (args.length != 1) {
		//			System.err.println("Usage: BerkeleyDbJEIndex <index dir>");
		//			System.exit(-1);
		//		}
		//		File indexFile = new File(args[0]);
		File indexFile = new File("index/berkeleyindex");

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

		EnvironmentConfig envConfig = new EnvironmentConfig();
		DatabaseConfig dbConfig = new DatabaseConfig();

		envConfig.setTransactional(true);
		envConfig.setAllowCreate(true);
		dbConfig.setTransactional(true);
		dbConfig.setAllowCreate(true);

		Environment env = new Environment(indexFile, envConfig);

		Transaction txn = env.beginTransaction(null, null);
		Database index = env.openDatabase(txn, "__index__", dbConfig);
		Database blocks = env.openDatabase(txn, "__blocks__", dbConfig);
		txn.commit();

		txn = env.beginTransaction(null, null);
		JEDirectory dir = new JEDirectory(txn, index, blocks);

		IndexWriter writer = new IndexWriter(dir, //
				new StandardAnalyzer(Version.LUCENE_30), true, IndexWriter.MaxFieldLength.UNLIMITED);

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

		System.out.println("Indexing Complete!");

	}

}
