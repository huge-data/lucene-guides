package cc.pp.lucene.chap04.analysis.synonym;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import cc.pp.lucene.common.AllDocCollector;

public class WordNetSynonyEngine implements SynonymEngine {

	IndexSearcher searcher;
	Directory fsDir;
	IndexReader reader;

	public WordNetSynonyEngine(File index) throws IOException {
		fsDir = FSDirectory.open(index);
		reader = DirectoryReader.open(fsDir);
		searcher = new IndexSearcher(reader);
	}

	public void close() throws IOException {
		reader.close();
		fsDir.close();
	}

	@Override
	public String[] getSynonyms(String word) throws IOException {

		List<String> synList = new ArrayList<String>();
		AllDocCollector collector = new AllDocCollector(); // 把所有匹配到的文档放入文档集合器中

		searcher.search(new TermQuery(new Term("word", word)), collector);

		for (ScoreDoc hit : collector.getHits()) { // 递归处理匹配文档
			Document doc = searcher.doc(hit.doc);
			String[] values = doc.getValues("syn");
			for (String syn : values) { // 记录同义词
				synList.add(syn);
			}
		}

		return synList.toArray(new String[0]);
	}

}
