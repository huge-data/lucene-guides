package cc.pp.lucene.demo.facet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.params.FacetSearchParams;
import org.apache.lucene.facet.search.CountFacetRequest;
import org.apache.lucene.facet.search.DrillDownQuery;
import org.apache.lucene.facet.search.FacetResult;
import org.apache.lucene.facet.search.FacetsCollector;
import org.apache.lucene.facet.sortedset.SortedSetDocValuesAccumulator;
import org.apache.lucene.facet.sortedset.SortedSetDocValuesFacetFields;
import org.apache.lucene.facet.sortedset.SortedSetDocValuesReaderState;
import org.apache.lucene.facet.taxonomy.CategoryPath;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

/**
 * 分类索引和搜索的示例，使用{@link SortedSetDocValuesFacetFields}
 * 和 {@link SortedSetDocValuesAccumulator}.
 * @author wgybzb
 *
 */
public class SimpleSortedSetFacetsExample {

	private final Directory indexDir = new RAMDirectory();

	public SimpleSortedSetFacetsExample() {
		//
	}

	/**
	 * 添加文档
	 */
	private void add(IndexWriter indexWriter, SortedSetDocValuesFacetFields facetFields, String... categoryPaths)
			throws IOException {

		Document doc = new Document();
		List<CategoryPath> paths = new ArrayList<>();
		for (String categoryPath : categoryPaths) {
			paths.add(new CategoryPath(categoryPath, '/'));
		}
		facetFields.addFields(doc, paths);
		indexWriter.addDocument(doc);
	}

	/**
	 * 建立索引
	 */
	private void index() throws IOException {

		IndexWriter indexWriter = new IndexWriter(indexDir, new IndexWriterConfig(FacetExamples.EXAMPLES_VER,
				new WhitespaceAnalyzer(FacetExamples.EXAMPLES_VER)));
		SortedSetDocValuesFacetFields facetFields = new SortedSetDocValuesFacetFields();
		add(indexWriter, facetFields, "Author/Bob", "Publish Year/2010");
		add(indexWriter, facetFields, "Author/Lisa", "Publish Year/2010");
		add(indexWriter, facetFields, "Author/Lisa", "Publish Year/2012");
		add(indexWriter, facetFields, "Author/Susan", "Publish Year/2012");
		add(indexWriter, facetFields, "Author/Frank", "Publish Year/1999");
		indexWriter.close();
	}

	/**
	 * 进行搜索和计算分类数
	 */
	private List<FacetResult> search() throws IOException {

		DirectoryReader indexReader = DirectoryReader.open(indexDir);
		IndexSearcher searcher = new IndexSearcher(indexReader);
		SortedSetDocValuesReaderState state = new SortedSetDocValuesReaderState(indexReader);
		// 计算"Publish Year"和"Author"两个维度的数量
		FacetSearchParams fsp = new FacetSearchParams(//
				new CountFacetRequest(new CategoryPath("Publish Year"), 10), //
				new CountFacetRequest(new CategoryPath("Author"), 10));
		// 计算分类数
		FacetsCollector fc = FacetsCollector.create(new SortedSetDocValuesAccumulator(state, fsp));
		// 匹配所有文档
		searcher.search(new MatchAllDocsQuery(), fc);
		List<FacetResult> results = fc.getFacetResults();

		indexReader.close();

		return results;
	}

	/**
	 * 向下挖掘'Publish Year/2010'下的分类数据
	 */
	private List<FacetResult> drillDown() throws IOException {

		DirectoryReader indexReader = DirectoryReader.open(indexDir);
		IndexSearcher searcher = new IndexSearcher(indexReader);
		SortedSetDocValuesReaderState state = new SortedSetDocValuesReaderState(indexReader);
		// 向下挖掘
		FacetSearchParams fsp = new FacetSearchParams(//
				new CountFacetRequest(new CategoryPath("Author"), 10) //
		);
		DrillDownQuery query = new DrillDownQuery(fsp.indexingParams, new MatchAllDocsQuery());
		query.add(new CategoryPath("Publish Year/2010", '/'));
		FacetsCollector fc = FacetsCollector.create(new SortedSetDocValuesAccumulator(state, fsp));
		searcher.search(query, fc);
		List<FacetResult> results = fc.getFacetResults();

		indexReader.close();

		return results;
	}

	public List<FacetResult> runSearch() throws IOException {
		index();
		return search();
	}

	public List<FacetResult> runDrillDown() throws IOException {
		index();
		return drillDown();
	}

	/**
	 * 测试函数
	 */
	public static void main(String[] args) throws IOException {

		System.out.println("Facet counting example: ");
		System.out.println("------------------------");
		List<FacetResult> results = new SimpleSortedSetFacetsExample().runSearch();
		for (FacetResult res : results) {
			System.out.println(res);
		}

		System.out.println("\n");
		System.out.println("Facet drill-down example (Publish Year/2010): ");
		results = new SimpleSortedSetFacetsExample().runDrillDown();
		for (FacetResult res : results) {
			System.out.println(res);
		}
	}

}
