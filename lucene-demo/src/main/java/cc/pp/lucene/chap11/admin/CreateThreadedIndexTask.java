package cc.pp.lucene.chap11.admin;

import java.io.IOException;

import org.apache.lucene.benchmark.byTask.PerfRunData;
import org.apache.lucene.benchmark.byTask.tasks.CreateIndexTask;
import org.apache.lucene.benchmark.byTask.utils.Config;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;

import cc.pp.lucene.common.LuceneConstant;

/**
 * 可以使用contrib/benchmark算法创建ThreadedIndexWriter
 * @author wanggang
 *
 */
public class CreateThreadedIndexTask extends CreateIndexTask {

	public CreateThreadedIndexTask(PerfRunData runData) {
		super(runData);
	}

	@Override
	public int doLogic() throws IOException {

		PerfRunData runData = getRunData();
		Config config = runData.getConfig();

		IndexWriterConfig conf = new IndexWriterConfig(LuceneConstant.LUCENE_VERSION, runData.getAnalyzer());

		IndexWriter writer = new ThreadedIndexWriter(runData.getDirectory(), runData.getAnalyzer(), config.get(
				"writer.num.threads", 4), config.get("writer.max.threads.queue.size", 20), conf);
		// 需要根据新版本来设置
		CreateIndexTask.configureWriter(config, runData, IndexWriterConfig.OpenMode.CREATE, null);
		runData.setIndexWriter(writer);

		return -1;
	}

}
