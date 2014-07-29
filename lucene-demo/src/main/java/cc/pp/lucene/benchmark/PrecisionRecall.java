package cc.pp.lucene.benchmark;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

import org.apache.lucene.benchmark.quality.Judge;
import org.apache.lucene.benchmark.quality.QualityBenchmark;
import org.apache.lucene.benchmark.quality.QualityQuery;
import org.apache.lucene.benchmark.quality.QualityQueryParser;
import org.apache.lucene.benchmark.quality.QualityStats;
import org.apache.lucene.benchmark.quality.trec.TrecJudge;
import org.apache.lucene.benchmark.quality.trec.TrecTopicsReader;
import org.apache.lucene.benchmark.quality.utils.SimpleQQParser;
import org.apache.lucene.benchmark.quality.utils.SubmissionReport;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * 从Lucene contrib/benchmark中提取的源代码
 * @author wanggang
 *
 */
public class PrecisionRecall {

	/**
	 * 测试函数
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		File topicsFile = new File("src/cc/pp/benchmark/topics.txt");
		File qrelsFile = new File("src/cc/pp/benchmark/qrels.txt");
		Directory dir = FSDirectory.open(new File("index/chap01index/"));
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);

		String docNameField = "filename";

		PrintWriter logger = new PrintWriter(System.out, true);

		// 读取TREC话题到QualityQuery中
		TrecTopicsReader qReader = new TrecTopicsReader();
		QualityQuery qqs[] = qReader.readQueries(new BufferedReader(new FileReader(topicsFile)));

		// 从TREC Qrel文件中创建Judge
		Judge judge = new TrecJudge(new BufferedReader(new FileReader(qrelsFile)));
		// 验证query和Judge匹配
		judge.validateData(qqs, logger);

		// 创建解析器，把查询语句转换成Lucene查询语法
		QualityQueryParser qqParser = new SimpleQQParser("title", "contents");

		QualityBenchmark qrun = new QualityBenchmark(qqs, qqParser, searcher, docNameField);
		SubmissionReport submitLog = null;
		// 执行benchmark
		QualityStats stats[] = qrun.execute(judge, submitLog, logger);

		// 打印精度和相似度
		QualityStats avg = QualityStats.average(stats);
		avg.log("SUMMARY", 2, logger, "  ");
		dir.close();

	}

}
