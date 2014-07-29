package cc.pp.chap09.tools.remote;

import java.io.File;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiSearcher;
import org.apache.lucene.search.ParallelMultiSearcher;
import org.apache.lucene.search.RemoteSearchable;
import org.apache.lucene.search.Searchable;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class SearchServer {

	private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

	/**
	 * 测试函数
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		//		if (args.length != 1) {
		//			System.err.println("Usage: SearchServer <basedir>");
		//			System.exit(-1);
		//		}
		//		String baseDir = args[0];
		// 存放索引的根目录
		String baseDir = "index/chap09index/remoteindex/"; // 注意需要创建26个索引目录
		Directory[] dirs = new Directory[ALPHABET.length()];
		Searchable[] searchables = new Searchable[ALPHABET.length()];
		for (int i = 0; i < ALPHABET.length(); i++) {
			dirs[i] = FSDirectory.open(new File(baseDir, "" + ALPHABET.charAt(i)));
			searchables[i] = new IndexSearcher(dirs[i]); // 为每个索引打开一个IndexSearcher
		}

		// 建立RMI注册
		LocateRegistry.createRegistry(1099);

		Searcher multiSearcher = new MultiSearcher(searchables);
		RemoteSearchable multiImpl = new RemoteSearchable(multiSearcher);
		Naming.rebind("//localhost/LIA_Multi", multiImpl);

		Searcher parallelSearcher = new ParallelMultiSearcher(searchables);
		RemoteSearchable parallelImpl = new RemoteSearchable(parallelSearcher);
		Naming.rebind("//localhost//LIA_Parallel", parallelImpl);

		System.out.println("Server started");

		for (int i = 0; i < ALPHABET.length(); i++) {
			dirs[i].close();
		}
	}

}
