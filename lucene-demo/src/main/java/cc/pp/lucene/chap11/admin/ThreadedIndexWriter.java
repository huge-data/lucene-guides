package cc.pp.lucene.chap11.admin;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;

/**
 * 继承IndexWriter，使用多线程、在引擎罩下索引增加的文档。
 * @author wanggang
 *
 */
public class ThreadedIndexWriter extends IndexWriter {

	private class Job implements Runnable { // 保持一个文档被添加

		Document doc;
		Analyzer analyzer;
		Term delTerm;

		public Job(Document doc, Term delTerm, Analyzer analyzer) {
			this.doc = doc;
			this.analyzer = analyzer;
			this.delTerm = delTerm;
		}

		@Override
		public void run() { // 执行增加或更新文档
			try {
				if (delTerm != null) {
					ThreadedIndexWriter.super.updateDocument(delTerm, doc, analyzer);
				} else {
					ThreadedIndexWriter.super.addDocument(doc, analyzer);
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private final ExecutorService threadPool;

	private final Analyzer defaultAnalyzer;

	public ThreadedIndexWriter(Directory d, Analyzer a, // 创建线程池
			int numThreads, int maxQueueSize, IndexWriterConfig conf) //
			throws CorruptIndexException, IOException {
		super(d, conf);
		defaultAnalyzer = a;
		threadPool = new ThreadPoolExecutor(numThreads, numThreads, 0L, //
				TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(maxQueueSize, false), //
				new ThreadPoolExecutor.CallerRunsPolicy());
	}

	/**
	 * 以下使用线程池执行任务
	 */
	public void addDocument(Document doc) {
		threadPool.execute(new Job(doc, null, defaultAnalyzer));
	}

	public void addDocument(Document doc, Analyzer a) {
		threadPool.execute(new Job(doc, null, a));
	}

	@Override
	public void close() throws CorruptIndexException, IOException {
		finish();
		super.close();
	}

	@Override
	public void close(boolean doWait) throws CorruptIndexException, IOException {
		finish();
		super.close(doWait);
	}

	public void roolback() throws IOException {
		finish();
		super.rollback();
	}

	public void updateDocument(Term term, Document doc) {
		threadPool.execute(new Job(doc, term, defaultAnalyzer));
	}

	public void updateDocument(Term term, Document doc, Analyzer a) {
		threadPool.execute(new Job(doc, term, a));
	}

	/**
	 * 关闭线程池
	 */
	private void finish() {
		threadPool.shutdown();
		while (true) {
			try {
				if (threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS)) {
					break;
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new RuntimeException(e);
			}
		}
	}

}
