package cc.pp.lucene.demo.facet;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.expressions.Expression;
import org.apache.lucene.expressions.SimpleBindings;
import org.apache.lucene.expressions.js.JavascriptCompiler;
import org.apache.lucene.facet.index.FacetFields;
import org.apache.lucene.facet.params.FacetSearchParams;
import org.apache.lucene.facet.search.FacetResult;
import org.apache.lucene.facet.search.FacetsCollector;
import org.apache.lucene.facet.search.SumValueSourceFacetRequest;
import org.apache.lucene.facet.taxonomy.CategoryPath;
import org.apache.lucene.facet.taxonomy.TaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.SortField;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

/**
 * 表达式分类聚合示例，即通过自定义的聚合表达式计算聚合结果。
 * @author wgybzb
 *
 */
public class ExpressionAggregationFacetsExample {

	private final Directory indexDir = new RAMDirectory();
	private final Directory taxoDir = new RAMDirectory();

	public ExpressionAggregationFacetsExample() {
		//
	}

	/**
	 * 添加文档
	 */
	private void add(IndexWriter indexWriter, FacetFields facetFields, String text, String category, long popularity)
			throws IOException {

		Document doc = new Document();
		doc.add(new TextField("c", text, Field.Store.NO));
		doc.add(new NumericDocValuesField("popularity", popularity));
		facetFields.addFields(doc, Collections.singletonList(new CategoryPath(category, '/')));
		indexWriter.addDocument(doc);
	}

	/**
	 * 建立索引
	 */
	private void index() throws IOException {

		IndexWriter indexWriter = new IndexWriter(indexDir, new IndexWriterConfig(FacetExamples.EXAMPLES_VER,
				new WhitespaceAnalyzer(FacetExamples.EXAMPLES_VER)));
		DirectoryTaxonomyWriter taxoWriter = new DirectoryTaxonomyWriter(taxoDir);

		FacetFields facetFields = new FacetFields(taxoWriter);
		add(indexWriter, facetFields, "foo bar", "A/B", 6L);
		add(indexWriter, facetFields, "foo foo bar", "A/C", 3L);

		indexWriter.close();
		taxoWriter.close();
	}

	/**
	 * 进行搜索，并聚合分类
	 */
	private List<FacetResult> search() throws IOException, ParseException {

		DirectoryReader indexReader = DirectoryReader.open(indexDir);
		IndexSearcher searcher = new IndexSearcher(indexReader);
		TaxonomyReader taxoReader = new DirectoryTaxonomyReader(taxoDir);

		// 通过结合文档的分数和它的popularity域的表达式来聚合分类
		Expression expr = JavascriptCompiler.compile("_score * sqrt(popularity)");
		SimpleBindings bindings = new SimpleBindings();
		bindings.add(new SortField("_score", SortField.Type.SCORE));
		bindings.add(new SortField("popularity", SortField.Type.LONG));

		FacetSearchParams fsp = new FacetSearchParams(//
				new SumValueSourceFacetRequest(new CategoryPath("A"), 10, expr.getValueSource(bindings), true)//
		);

		FacetsCollector fc = FacetsCollector.create(fsp, indexReader, taxoReader);
		searcher.search(new MatchAllDocsQuery(), fc);
		List<FacetResult> facetResults = fc.getFacetResults();

		indexReader.close();
		taxoReader.close();

		return facetResults;
	}

	public List<FacetResult> runSearch() throws IOException, ParseException {
		index();
		return search();
	}

	/**
	 * 测试函数
	 */
	public static void main(String[] args) throws IOException, ParseException {

		System.out.println("Facet counting example: ");
		System.out.println("------------------------");
		List<FacetResult> results = new ExpressionAggregationFacetsExample().runSearch();
		for (FacetResult res : results) {
			System.out.println(res);
		}
	}

}
