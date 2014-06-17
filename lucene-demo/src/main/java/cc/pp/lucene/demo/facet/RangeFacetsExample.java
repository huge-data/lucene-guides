package cc.pp.lucene.demo.facet;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.facet.params.FacetIndexingParams;
import org.apache.lucene.facet.range.LongRange;
import org.apache.lucene.facet.range.RangeAccumulator;
import org.apache.lucene.facet.range.RangeFacetRequest;
import org.apache.lucene.facet.search.DrillDownQuery;
import org.apache.lucene.facet.search.FacetResult;
import org.apache.lucene.facet.search.FacetsCollector;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

/**
 * 动态的范围分类示例
 * @author wgybzb
 *
 */
public class RangeFacetsExample implements Closeable {

	private final Directory indexDir = new RAMDirectory();
	private IndexSearcher searcher;
	private final long nowSec = System.currentTimeMillis();

	public RangeFacetsExample() {
		//
	}

	/**
	 * 建立索引
	 */
	public void index() throws IOException {

		IndexWriter indexWriter = new IndexWriter(indexDir, new IndexWriterConfig(FacetExamples.EXAMPLES_VER,
				new WhitespaceAnalyzer(FacetExamples.EXAMPLES_VER)));
		// 使用自定义的时间戳添加文档
		for (int i = 0; i < 100; i++) {
			Document doc = new Document();
			long then = nowSec - i * 1000;
			// 添加doc值的域，方便计算范围分类
			doc.add(new NumericDocValuesField("timestamp", then));
			// 添加数值域，方便向下挖掘
			doc.add(new LongField("timestamp", then, Field.Store.NO));
			indexWriter.addDocument(doc);
		}
		// 打开进实时搜索
		searcher = new IndexSearcher(DirectoryReader.open(indexWriter, true));
	}

	/**
	 * 进行查询，并且计算分类数
	 */
	public List<FacetResult> search() throws IOException {

		RangeFacetRequest<LongRange> rangeFacetRequest = new RangeFacetRequest<LongRange>("timestamp", //
				new LongRange("Past hour", nowSec - 3600, true, nowSec, true), //
				new LongRange("Past six hours", nowSec - 6 * 3600, true, nowSec, true), //
				new LongRange("Past day", nowSec - 24 * 3600, true, nowSec, true));
		// 计算总的分类数
		FacetsCollector fc = FacetsCollector.create(new RangeAccumulator(rangeFacetRequest));
		// 匹配所有文档
		searcher.search(new MatchAllDocsQuery(), fc);

		return fc.getFacetResults();
	}

	/**
	 * 向下挖掘指定的范围
	 */
	public TopDocs drillDown(LongRange range) throws IOException {

		DrillDownQuery query = new DrillDownQuery(FacetIndexingParams.DEFAULT);
		// 使用FieldCacheRangeFilter过滤器，它将使用NumericDocValues
		query.add("timestamp", NumericRangeQuery.newLongRange("timestamp", range.min, range.max, range.minInclusive,
				range.maxInclusive));

		return searcher.search(query, 10);
	}

	@Override
	public void close() throws IOException {
		searcher.getIndexReader().close();
		indexDir.close();
	}

	/**
	 * 测试函数
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {

		RangeFacetsExample example = new RangeFacetsExample();
		example.index();

		System.out.println("Facet counting example.");
		System.out.println("-----------------------");
		List<FacetResult> results = example.search();
		for (FacetResult res : results) {
			System.out.println(res);
		}

		System.out.println("\n");
		System.out.println("Facet drill-down example (timestamp/Past six hours): ");
		System.out.println("-----------------------------------------------------");
		TopDocs hits = example
				.drillDown((LongRange) ((RangeFacetRequest<LongRange>) results.get(0).getFacetRequest()).ranges[1]);
		System.out.println(hits.totalHits + " totalHits");

		example.close();
	}

}
