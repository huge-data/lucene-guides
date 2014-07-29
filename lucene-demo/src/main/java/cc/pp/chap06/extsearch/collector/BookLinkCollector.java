package cc.pp.chap06.extsearch.collector;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.Scorer;

public class BookLinkCollector extends Collector {

	private final Map<String, String> documents = new HashMap<>();
	private Scorer scorer;
	private String[] urls;
	private String[] titles;

	@Override
	public boolean acceptsDocsOutOfOrder() { // 允许无序的文档ID
		return true;
	}

	@Override
	public void collect(int docID) throws IOException {
		try {
			String url = urls[docID]; // 保存匹配细节
			String title = titles[docID];
			documents.put(url, title);
			System.out.println(title + ":" + scorer.score());
		} catch (IOException e) {
			// ignore
		}
	}

	public Map<String, String> getLinks() {
		return Collections.unmodifiableMap(documents);
	}

	@Override
	public void setNextReader(IndexReader reader, int docBase) throws IOException {
		urls = FieldCache.DEFAULT.getStrings(reader, "url"); // 加载FieldCache值
		titles = FieldCache.DEFAULT.getStrings(reader, "title2");
	}

	@Override
	public void setScorer(Scorer scorer) throws IOException {
		this.scorer = scorer;
	}

}
