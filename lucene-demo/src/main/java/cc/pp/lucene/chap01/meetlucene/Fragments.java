package cc.pp.lucene.chap01.meetlucene;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Fragments {

	/**
	 * 主函数
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		Fragments fragments = new Fragments();
		fragments.simpleSearch();
	}

	public void simpleSearch() throws IOException {

		Directory dir = FSDirectory.open(new File("indexdir"));
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);
		Query q = new TermQuery(new Term("contents", "patent"));
		TopDocs hits = searcher.search(q, 10);
		for (ScoreDoc scoredoc : hits.scoreDocs) {
			Document doc = searcher.doc(scoredoc.doc);
			System.out.println(doc.get("fullpath"));
		}
		reader.close();
		dir.close();
	}

}
