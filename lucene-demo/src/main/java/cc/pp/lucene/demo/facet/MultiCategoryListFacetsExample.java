package cc.pp.lucene.demo.facet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.index.FacetFields;
import org.apache.lucene.facet.params.CategoryListParams;
import org.apache.lucene.facet.params.FacetIndexingParams;
import org.apache.lucene.facet.params.FacetSearchParams;
import org.apache.lucene.facet.params.PerDimensionIndexingParams;
import org.apache.lucene.facet.search.CountFacetRequest;
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
 * 将分类索引到不同的分类列表中
 * @author wgybzb
 *
 */
public class MultiCategoryListFacetsExample {

	private final FacetIndexingParams indexingParams;
	private final Directory indexDir = new RAMDirectory();
	private final Directory taxoDir = new RAMDirectory();

	/**
	 * 创建一个新的实例，然后填入分类列表参数映射
	 */
	public MultiCategoryListFacetsExample() {
		// 将所有的Author索引到一个分类下，而将所有的Publish索引到另一个分类下
		Map<CategoryPath, CategoryListParams> categoryListParams = new HashMap<>();
		categoryListParams.put(new CategoryPath("Author"), new CategoryListParams("author"));
		categoryListParams.put(new CategoryPath("Publish Date"), new CategoryListParams("pubdate"));
		indexingParams = new PerDimensionIndexingParams(categoryListParams);
	}

	/**
	 * 添加文档
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
		// 将分类数据写到与索引目录不同的目录下
		DirectoryTaxonomyWriter taxoWriter = new DirectoryTaxonomyWriter(taxoDir);

		FacetFields facetFields = new FacetFields(taxoWriter, indexingParams);

		add(indexWriter, facetFields, "Author/Bob", "Publish Date/2010/10/15");
		add(indexWriter, facetFields, "Author/Lisa", "Publish Date/2010/10/20");
		add(indexWriter, facetFields, "Author/Lisa", "Publish Date/2012/1/1");
		add(indexWriter, facetFields, "Author/Susan", "Publish Date/2012/1/7");
		add(indexWriter, facetFields, "Author/Frank", "Publish Date/1999/5/5");

		indexWriter.close();
		taxoWriter.close();
	}

	/**
	 * 进行查询和计算每个分类数目
	 */
	private List<FacetResult> search() throws IOException {

		DirectoryReader indexReader = DirectoryReader.open(indexDir);
		IndexSearcher searcher = new IndexSearcher(indexReader);
		TaxonomyReader taxoReader = new DirectoryTaxonomyReader(taxoDir);

		// 计算Publish Date和Author两个维度
		FacetSearchParams fsp = new FacetSearchParams(indexingParams, //
				new CountFacetRequest(new CategoryPath("Publish Date"), 10), //
				new CountFacetRequest(new CategoryPath("Author"), 10)//
		);

		// 收集分类数
		FacetsCollector fc = FacetsCollector.create(fsp, indexReader, taxoReader);

		// 匹配所有文档
		searcher.search(new MatchAllDocsQuery(), fc);
		List<FacetResult> results = fc.getFacetResults();

		indexReader.close();
		taxoReader.close();

		return results;
	}

	/**
	 * 执行搜索
	 */
	public List<FacetResult> runSearch() throws IOException {
		index();
		return search();
	}

	/**
	 * 测试函数
	 */
	public static void main(String[] args) throws IOException {

		System.out.println("Facet counting over multiple category lists example: ");
		System.out.println("-----------------------------------------------------");
		List<FacetResult> results = new MultiCategoryListFacetsExample().runSearch();
		for (FacetResult res : results) {
			System.out.println(res);
		}
	}

}
