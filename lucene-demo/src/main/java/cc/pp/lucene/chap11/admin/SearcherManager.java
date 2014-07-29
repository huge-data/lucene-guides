package cc.pp.lucene.chap11.admin;

import java.io.IOException;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;

/**
 * 获取或者刷新searchers的工具类（针对多线程）
 * @author wanggang
 *
 */
public class SearcherManager {

	private IndexSearcher currentSearcher; // 当前搜索
	private IndexWriter writer;

	private boolean reopening;

	/**
	 * 从索引目录创建搜索
	 */
	public SearcherManager(Directory dir) throws IOException {
		currentSearcher = new IndexSearcher(DirectoryReader.open(dir));
		warm(currentSearcher);
	}

	/**
	 * 从近实时reader中创建搜索
	 * @param writer
	 * @throws IOException
	 */
	public SearcherManager(IndexWriter writer) throws IOException {
		//		currentSearcher = new IndexSearcher(writer.getReader());
		warm(currentSearcher);
		//		writer.setMergedSegmentWarmer(new IndexWriter.IndexReaderWarmer() {
		//			@Override
		//			public void warm(IndexReader reader) throws IOException {
		//				SearcherManager.this.warm(new IndexSearcher(reader));
		//			}
		//		});
	}

	public void close() throws IOException {
		swapSearcher(null);
	}

	/**
	 * 返回当前的searcher
	 */
	public synchronized IndexSearcher get() {
		currentSearcher.getIndexReader().incRef();
		return currentSearcher;
	}

	/**
	 * 重新打开searcher
	 */
	public void maybeReopen() throws Exception {

		startReopen();

		try {
			final IndexSearcher searcher = get();
			try {
				IndexReader newReader = DirectoryReader.open(writer, Boolean.FALSE);
				if (newReader != currentSearcher.getIndexReader()) {
					IndexSearcher newSearcher = new IndexSearcher(newReader);
					if (writer == null) {
						warm(newSearcher);
					}
					swapSearcher(newSearcher);
				}
			} finally {
				release(searcher);
			}
		} finally {
			doneReopen();
		}
	}

	/**
	 * 释放searcher
	 */
	public synchronized void release(IndexSearcher searcher) throws IOException {
		searcher.getIndexReader().decRef();
	}

	/**
	 * 实现子类
	 */
	public void warm(IndexSearcher searcher) throws IOException {
		//
	}

	private synchronized void doneReopen() {
		reopening = false;
		notifyAll();
	}

	private synchronized void startReopen() throws InterruptedException {
		while (reopening) {
			wait();
		}
		reopening = true;
	}

	private synchronized void swapSearcher(IndexSearcher newSearcher) throws IOException {
		release(currentSearcher);
		currentSearcher = newSearcher;
	}

}
