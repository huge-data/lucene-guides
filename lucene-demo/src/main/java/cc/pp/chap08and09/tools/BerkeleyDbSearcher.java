package cc.pp.chap08and09.tools;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.db.DbDirectory;

import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseConfig;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.DatabaseType;
import com.sleepycat.db.Environment;
import com.sleepycat.db.EnvironmentConfig;

public class BerkeleyDbSearcher {

	/**
	 * 测试函数
	 * @param args
	 * @throws DatabaseException
	 * @throws IOException
	 * @throws CorruptIndexException
	 */
	public static void main(String[] args) throws DatabaseException, IOException {

		//		if (args.length != 1) {
		//			System.err.println("Usage: BerkeleyDbSearcher <index dir>");
		//			System.exit(-1);
		//		}
		//		File indexFile = new File(args[0]);
		File indexFile = new File("index/berkeleyindex");

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
		Database index = env.openDatabase(null, "__index__", null, dbConfig);
		Database blocks = env.openDatabase(null, "__blocks__", null, dbConfig);

		DbDirectory dir = new DbDirectory(null, index, blocks, 0);

		IndexSearcher searcher = new IndexSearcher(dir, true);
		TermQuery query = new TermQuery(new Term("contents", "fox"));
		TopDocs hits = searcher.search(query, 10);
		System.out.println(hits.totalHits + " documents found!");
		searcher.close();

		index.close();
		blocks.close();
		env.close();
	}

}
