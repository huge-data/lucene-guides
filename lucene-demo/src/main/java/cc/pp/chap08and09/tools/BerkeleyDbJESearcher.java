package cc.pp.chap08and09.tools;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.je.JEDirectory;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

public class BerkeleyDbJESearcher {

	/**
	 * 测试函数
	 * @param args
	 * @throws DatabaseException
	 * @throws IOException
	 * @throws CorruptIndexException
	 */
	public static void main(String[] args) throws DatabaseException, IOException {

		//		if (args.length != 1) {
		//			System.err.println("Usgae: BerkeleyDbSearcher <index dir>");
		//			System.exit(-1);
		//		}
		//		File indexFile = new File(args[0]);
		File indexFile = new File("index/berkeleyindex");

		EnvironmentConfig envConfig = new EnvironmentConfig();
		DatabaseConfig dbConfig = new DatabaseConfig();

		envConfig.setTransactional(true);
		envConfig.setAllowCreate(true);
		dbConfig.setTransactional(true);
		dbConfig.setAllowCreate(true);

		Environment env = new Environment(indexFile, envConfig);
		Database index = env.openDatabase(null, "__index__", dbConfig);
		Database blocks = env.openDatabase(null, "__blocks__", dbConfig);

		JEDirectory dir = new JEDirectory(null, index, blocks);

		IndexSearcher searcher = new IndexSearcher(dir, true);
		TermQuery query = new TermQuery(new Term("contents", "fox"));
		TopDocs hits = searcher.search(query, 10);
		System.out.println(hits.totalHits + " documents found");
		searcher.close();

		index.close();
		blocks.close();
		env.close();

	}

}
