package cc.pp.lucene.demo.facet;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.facet.FacetResult;
import org.apache.lucene.facet.FacetsCollector;
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
 * 分类相关的使用示例
 * @author wgybzb
 *
 */
public class AssociationsFacetsExample {

	/**
	 * 每个文档中的分类，都有与其相关的值。
	 */
	public static CategoryPath[][] CATEGORIES = {
			// 文档1
			{ //
			new CategoryPath("tags", "lucene"), //
					new CategoryPath("genre", "computing") //
			},
			// 文档2
			{ //
			new CategoryPath("tags", "lucene"), //
					new CategoryPath("tags", "solr"), //
					new CategoryPath("genre", "computing"), //
					new CategoryPath("genre", "software") //
			} //
	};

	/**
	 * 每个类别的相关值
	 */
	public static CategoryAssociation[][] ASSOCIATIONS = {
			// 文档1相关
			{
					// 标签‘lucene’出现3次
					new CategoryIntAssociation(3),
					// 类型’computing‘的87%置信区间
					new CategoryFloatAssociation(0.87f) //
			}, //
				// 文档2相关
			{
					// 标签’lucene‘出现1次
					new CategoryIntAssociation(1),
					// 标签’solr‘出现2次
					new CategoryIntAssociation(2),
					// 类型'computing'的75%置信区间
					new CategoryFloatAssociation(0.75f),
					// 类型’software‘的34%置信区间
					new CategoryFloatAssociation(0.34f) } //
	};

	private final Directory indexDir = new RAMDirectory();
	private final Directory taxoDir = new RAMDirectory();

	public AssociationsFacetsExample() {
		//
	}

	/**
	 * 构建索引示例
	 */
	private void index() throws IOException {

		IndexWriter indexWriter = new IndexWriter(indexDir, new IndexWriterConfig(FacetExamples.EXAMPLES_VER,
				new WhitespaceAnalyzer(FacetExamples.EXAMPLES_VER)));
		// 将分类单词或者类别信息写到与主索引不同的目录下
		DirectoryTaxonomyWriter taxoWriter = new DirectoryTaxonomyWriter(taxoDir); // 写分类目录
		// 重用所有文档，增加必要的分类域
		FacetFields facetFields = new AssociationsFacetFields(taxoWriter);
		for (int i = 0; i < CATEGORIES.length; i++) {
			Document doc = new Document();
			CategoryAssociationsContainer associations = new CategoryAssociationsContainer();
			for (int j = 0; j < CATEGORIES[i].length; j++) {
				associations.setAssociation(CATEGORIES[i][j], ASSOCIATIONS[i][j]);
			}
			facetFields.addFields(doc, associations);
			indexWriter.addDocument(doc);
		}
		indexWriter.close();
		taxoWriter.close();
	}

	/**
	 * 用户运行查询，然后通过计算所有相关值的总和来统计所有分类。
	 */
	private List<FacetResult> sumAssociations() throws IOException {

		DirectoryReader indexReader = DirectoryReader.open(indexDir);
		IndexSearcher searcher = new IndexSearcher(indexReader);
		TaxonomyReader taxoReader = new DirectoryTaxonomyReader(taxoDir);

		CategoryPath tags = new CategoryPath("tags");
		CategoryPath genre = new CategoryPath("genre");
		FacetSearchParams fsp = new FacetSearchParams( //
				new SumIntAssociationFacetRequest(tags, 10), //
				new SumFloatAssociationFacetRequest(genre, 10));
		FacetsCollector fc = FacetsCollector.create(fsp, indexReader, taxoReader);

		// MatchAllDocsQuery用于匹配索引中的所有文档，通常情况下都使用正常的查询类，
		// 然后再使用MultiCollector类来封装收集所有查询结果和分类操作。
		searcher.search(new MatchAllDocsQuery(), fc);

		// 提取结果
		List<FacetResult> facetResults = fc.getFacetResults();

		indexReader.close();
		taxoReader.close();

		return facetResults;
	}

	/**
	 * 运行计算相关值总和的示例
	 */
	public List<FacetResult> runSumAssociations() throws IOException {
		index();
		return sumAssociations();
	}

	/**
	 * 测试函数
	 */
	public static void main(String[] args) throws IOException {

		System.out.println("Sum associations examples: ");
		System.out.println("---------------------------");
		List<FacetResult> results = new AssociationsFacetsExample().runSumAssociations();
		for (FacetResult res : results) {
			System.out.println(res);
		}

	}

}
