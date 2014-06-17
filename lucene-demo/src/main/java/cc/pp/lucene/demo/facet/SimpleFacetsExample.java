package cc.pp.lucene.demo.facet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.index.FacetFields;
import org.apache.lucene.facet.params.FacetSearchParams;
import org.apache.lucene.facet.search.CountFacetRequest;
import org.apache.lucene.facet.search.DrillDownQuery;
import org.apache.lucene.facet.search.FacetResult;
import org.apache.lucene.facet.search.FacetsCollector;
import org.apache.lucene.facet.taxonomy.CategoryPath;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

/**
 * 分类索引和搜索的简单使用示例
 * @author wgybzb
 *
 */
public class SimpleFacetsExample {

	private final Directory indexDir = new RAMDirectory();
	private final Directory taxoDir = new RAMDirectory();

	public SimpleFacetsExample() {
		//
	}

	/**
	 * 添加分类文档
	 */
	private void add(IndexWriter indexWriter, FacetFields facetFields, String... categoryPaths) throws IOException {

		Document doc = new Document();
		List<CategoryPath> paths = new ArrayList<>();
		for (String categoryPath : categoryPaths) {
			paths.add(new CategoryPath(categoryPath, '/'));
		}
		facetFields.addFields(doc, paths);
		indexWriter.addDocument(doc);
	}

	/**
	 * 构建索引
	 */
	private void index() throws IOException {

		IndexWriter indexWriter = new IndexWriter(indexDir, new IndexWriterConfig(FacetExamples.EXAMPLES_VER,
				new WhitespaceAnalyzer(FacetExamples.EXAMPLES_VER)));
		// 将分类单词写到与索引文件不同的目录下
		DirectoryTaxonomyWriter taxoWriter = new DirectoryTaxonomyWriter(taxoDir);
		// 重用所有文档来添加必要的分类域
		FacetFields facetFields = new FacetFields(taxoWriter);
		add(indexWriter, facetFields, "Author/Bob", "Publish Date/2010/10/15");
		add(indexWriter, facetFields, "Author/Lisa", "Publish Date/2010/10/20");
		add(indexWriter, facetFields, "Author/Lisa", "Publish Date/2012/1/1");
		add(indexWriter, facetFields, "Author/Susan", "Publish Date/2012/1/7");
		add(indexWriter, facetFields, "Author/Frank", "Publish Date/1999/5/5");

		indexWriter.close();
		taxoWriter.close();
	}

	/**
	 * 运行查询，并且计算分类数
	 */
	private List<FacetResult> search() throws IOException {

		DirectoryReader indexReader = DirectoryReader.open(indexDir);
		IndexSearcher searcher = new IndexSearcher(indexReader);
		TaxonomyReader taxoReader = new DirectoryTaxonomyReader(taxoDir);

		// 计算"Publish Date"和"Author"的维数
		FacetSearchParams fsp = new FacetSearchParams( //
				new CountFacetRequest(new CategoryPath("Publish Date"), 10),
				new CountFacetRequest(new CategoryPath("Author"), 10));

		// 计算分类总数
		FacetsCollector fc = FacetsCollector.create(fsp, indexReader, taxoReader);

		// 匹配所有文档
		searcher.search(new MatchAllDocsQuery(), fc);

		// 提取结果
		List<FacetResult> facetResults = fc.getFacetResults();

		indexReader.close();
		taxoReader.close();

		return facetResults;
	}

	/**
	 * 向下挖掘'Publish Date/2010'中的数据
	 */
	private List<FacetResult> drillDown() throws IOException {

		DirectoryReader indexReader = DirectoryReader.open(indexDir);
		IndexSearcher searcher = new IndexSearcher(indexReader);
		TaxonomyReader taxoReader = new DirectoryTaxonomyReader(taxoDir);

		// 向下挖掘'Publish Date/2010'
		FacetSearchParams fsp = new FacetSearchParams(new CountFacetRequest(new CategoryPath("Author"), 10));

		// 不使用基本查询意味着将对所有文档进行向下挖掘
		DrillDownQuery query = new DrillDownQuery(fsp.indexingParams);
		query.add(new CategoryPath("Publish Date/2010", '/'));
		FacetsCollector fc = FacetsCollector.create(fsp, indexReader, taxoReader);
		searcher.search(query, fc);

		// 提取结果
		List<FacetResult> facetResults = fc.getFacetResults();

		indexReader.close();
		taxoReader.close();

		return facetResults;
	}

	/**
	 * 运行搜索
	 */
	public List<FacetResult> runSearch() throws IOException {
		index();
		return search();
	}

	/**
	 * 运行drill down
	 */
	public List<FacetResult> runDrillDown() throws IOException {
		index();
		return drillDown();
	}

	/**
	 * 测试函数
	 */
	public static void main(String[] args) throws IOException {

		System.out.println("Facet counting example.");
		System.out.println("-----------------------");
		List<FacetResult> results = new SimpleFacetsExample().runSearch();
		for (FacetResult res : results) {
			System.out.println(res);
		}

		System.out.println("\n");
		System.out.println("Facet drill-down example (Publish Date/2010): ");
		System.out.println("----------------------------------------------");
		results = new SimpleFacetsExample().runDrillDown();
		for (FacetResult res : results) {
			System.out.println(res);
		}
	}

}
