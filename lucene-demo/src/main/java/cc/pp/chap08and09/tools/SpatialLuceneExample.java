package cc.pp.chap08and09.tools;

import java.io.IOException;
import java.util.Map;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.spatial.tier.DistanceFieldComparatorSource;
import org.apache.lucene.spatial.tier.DistanceQueryBuilder;
import org.apache.lucene.spatial.tier.projections.CartesianTierPlotter;
import org.apache.lucene.spatial.tier.projections.IProjector;
import org.apache.lucene.spatial.tier.projections.SinusoidalProjector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.NumericUtils;

public class SpatialLuceneExample {

	/**
	 * 测试函数
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		SpatialLuceneExample spatial = new SpatialLuceneExample();
		spatial.addData();
		spatial.findNear("Restaurant", 38.8725000, -77.3829000, 8);

	}

	String latField = "lat";
	String lngField = "lon";

	String tierPrefix = "_localTier";
	private final Directory dir;

	private final IndexWriter writer;

	SpatialLuceneExample() throws IOException {
		dir = new RAMDirectory();
		writer = new IndexWriter(dir, new WhitespaceAnalyzer(), //
				IndexWriter.MaxFieldLength.UNLIMITED);
	}

	@SuppressWarnings("resource")
	public void findNear(String what, double latitude, double longitude, //
			double radius) throws IOException {

		IndexSearcher searcher = new IndexSearcher(dir);
		// 创建距离查询
		DistanceQueryBuilder dq = new DistanceQueryBuilder(latitude, longitude, //
				radius, latField, lngField, tierPrefix, true);
		Query tq;
		if (what == null) {
			tq = new TermQuery(new Term("metafile", "doc")); // 匹配所有文档
		} else {
			tq = new TermQuery(new Term("name", what));
		}

		// 创建距离排序
		DistanceFieldComparatorSource dsort;
		dsort = new DistanceFieldComparatorSource(dq.getDistanceFilter());
		Sort sort = new Sort(new SortField("foo", dsort));

		TopDocs hits = searcher.search(tq, dq.getFilter(), 10, sort);

		// 获取距离映射表
		Map<Integer, Double> distances = dq.getDistanceFilter().getDistances();

		System.out.println("Number of Results: " + hits.totalHits);
		System.out.println("Found:");
		for (ScoreDoc sd : hits.scoreDocs) {
			int docID = sd.doc;
			Document d = searcher.doc(docID);
			String name = d.get("name");
			double rsLat = NumericUtils.prefixCodedToDouble(d.get(latField));
			double rsLng = NumericUtils.prefixCodedToDouble(d.get(lngField));
			Double geo_distance = distances.get(docID);
			System.out.printf(name + ": %.2f Miles\n", geo_distance);
			System.out.println("\t\t(" + rsLat + "," + rsLng + ")");
		}
	}

	private void addData() throws IOException {

		addLocation(writer, "McCormick & Schmick's Seafood Restaurant", //
				38.9579000, -77.3572000);
		addLocation(writer, "Jimmy's Old Town Tavern", 38.9690000, -77.3862000);
		addLocation(writer, "Ned Devine's", 38.9510000, -77.4107000);
		addLocation(writer, "Old Brogue Irish Pub", 38.9955000, -77.2884000);
		addLocation(writer, "Alf Laylah Wa Laylah", 38.8956000, -77.4258000);
		addLocation(writer, "Sully's Restaurant & Supper", 38.9003000, -77.4467000);
		addLocation(writer, "TGIFriday", 38.8725000, -77.3829000);
		addLocation(writer, "Potomac Swing Dance Club", 38.9027000, -77.2639000);
		addLocation(writer, "White Tiger Restaurant", 38.9027000, -77.2638000);
		addLocation(writer, "Jammin' Java", 38.9039000, -77.2622000);
		addLocation(writer, "Potomac Swing Dance Club", 38.9027000, -77.2639000);
		addLocation(writer, "WiseAcres Comedy Club", 38.9248000, -77.2344000);
		addLocation(writer, "Glen Echo Spanish Ballroom", 38.9691000, -77.1400000);
		addLocation(writer, "Whitlow's on Wilson", 38.8889000, -77.0926000);
		addLocation(writer, "Iota Club and Cafe", 38.8890000, -77.0923000);
		addLocation(writer, "Hilton Washington Embassy Row", 38.9103000,
				-77.0451000);
		addLocation(writer, "HorseFeathers, Bar & Grill", 39.01220000000001,
				-77.3942);
		writer.close();

	}

	private void addLocation(IndexWriter writer, String name, double lat, //
			double lng) throws IOException {

		Document doc = new Document();
		doc.add(new Field("name", name, Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field(latField, NumericUtils.doubleToPrefixCoded(lat), // 经纬度编码成Double
				Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field(lngField, NumericUtils.doubleToPrefixCoded(lng), //
				Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field("metafile", "doc", Field.Store.YES, Field.Index.ANALYZED));

		// 正弦曲线映射
		IProjector projector = new SinusoidalProjector();

		// 索引约1～1000英里
		int startTier = 5;
		int endTier = 15;

		for (; startTier <= endTier; startTier++) {
			CartesianTierPlotter ctp = new CartesianTierPlotter(//
					startTier, projector, tierPrefix);
			double boxId = ctp.getTierBoxId(lat, lng); // 计算边界框ID
			System.out.println("Adding field " + ctp.getTierFieldName() + //
					":" + boxId);
			doc.add(new Field(ctp.getTierFieldName(), // 添加等级域
					NumericUtils.doubleToPrefixCoded(boxId), Field.Store.YES, //
					Field.Index.NOT_ANALYZED_NO_NORMS));
		}

		writer.addDocument(doc);
		System.out.println("==== Added Doc to index ====");
	}

}
