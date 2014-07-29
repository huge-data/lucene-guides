package cc.pp.chap09.tools.remote;

import java.rmi.Naming;
import java.util.Date;
import java.util.HashMap;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.MultiSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searchable;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

public class SearchClient {

	private static HashMap<Object, Object> searcherCache = new HashMap<>();

	/**
	 * 测试函数
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		//		if (args.length != 1) {
		//			System.err.println("Usage: SearchClient <query>");
		//			System.exit(-1);
		//		}
		//		String word = args[0];
		String word = "java";

		for (int i = 0; i < 5; i++) {
			search("LIA_Multi", word);
			search("LIA_Parallel", word);
		}

	}

	/**
	 * 查找远程接口
	 */
	private static Searchable lookupRemote(String name) throws Exception {

		return (Searchable) Naming.lookup("//localhost/" + name);
	}

	/**
	 * 搜索功能
	 */
	private static void search(String name, String word) throws Exception {

		TermQuery query = new TermQuery(new Term("word", word));
		MultiSearcher searcher = (MultiSearcher) searcherCache.get(name);
		if (searcher == null) {
			searcher = new MultiSearcher(new Searchable[] { lookupRemote(name) });
			searcherCache.put(name, searcher);
		}

		long begin = new Date().getTime();
		TopDocs hits = searcher.search(query, 10);
		long end = new Date().getTime();

		System.out.println("Searched " + name + " for '" + word + //
				"' (" + (end - begin) + " ms): ");

		if (hits.scoreDocs.length == 0) {
			System.out.println("<none found>");
		}

		for (ScoreDoc sd : hits.scoreDocs) {
			Document doc = searcher.doc(sd.doc);
			String[] values = doc.getValues("syn");
			for (String syn : values) {
				System.out.print(syn + " ");
			}
		}
		System.out.println();
		System.out.println();

	}

}
