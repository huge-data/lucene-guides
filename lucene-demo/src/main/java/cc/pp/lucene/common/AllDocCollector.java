package cc.pp.lucene.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Scorer;

/**
 * 从search中聚集所有文档集
 * @author Administrator
 *
 */
public class AllDocCollector extends Collector {

	List<ScoreDoc> docs = new ArrayList<ScoreDoc>();
	private Scorer scorer;
	private int docBase;

	@Override
	public boolean acceptsDocsOutOfOrder() {
		return true;
	}

	@Override
	public void collect(int doc) throws IOException {
		docs.add(new ScoreDoc(doc + docBase, // 创建绝对的docID
				scorer.score())); // 记录的score
	}

	@Override
	public void setScorer(Scorer scorer) throws IOException {
		this.scorer = scorer;
	}

	public void reset() {
		docs.clear();
	}

	public List<ScoreDoc> getHits() {
		return docs;
	}

	@Override
	public void setNextReader(AtomicReaderContext context) throws IOException {
		//
	}

	public void setDocBase(int docBase) {
		this.docBase = docBase;
	}

}
