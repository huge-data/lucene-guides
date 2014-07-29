package cc.pp.chap06.extsearch.sorting;

import junit.framework.TestCase;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FieldDoc;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.store.RAMDirectory;

public class DistanceSortingTest extends TestCase {

	private RAMDirectory dir;
	private IndexSearcher searcher;
	private Query query;

	public void testNearestRestaurantToHome() throws Exception {

		Sort sort = new Sort(new SortField("unused", //
				new DistanceComparatorSource(0, 0)));
		TopDocs hits = searcher.search(query, null, 10, sort);
		assertEquals("closest", "El Charro", //
				searcher.doc(hits.scoreDocs[0].doc).get("name"));
		assertEquals("furthest", "Los Betos", //
				searcher.doc(hits.scoreDocs[3].doc).get("name"));
	}

	public void testNearestRestaurantToWork() throws Exception {

		Sort sort = new Sort(new SortField("unused", //
				new DistanceComparatorSource(10, 10)));
		TopFieldDocs docs = searcher.search(query, null, 3, sort);
		assertEquals(4, docs.totalHits);
		assertEquals(3, docs.scoreDocs.length);

		FieldDoc fieldDoc = (FieldDoc) docs.scoreDocs[0];
		assertEquals("(10,10) -> (9,6) = sqrt(17)", new Float(Math.sqrt(17)), //
				fieldDoc.fields[0]);

		Document doc = searcher.doc(fieldDoc.doc);
		assertEquals("Los Betos", doc.get("name"));

		dumpDocs(sort, docs);
	}

	@Override
	protected void setUp() throws Exception {

		dir = new RAMDirectory();
		IndexWriter writer = new IndexWriter(dir, new WhitespaceAnalyzer(), //
				IndexWriter.MaxFieldLength.UNLIMITED);
		addPoint(writer, "El Charro", "restaurant", 1, 2);
		addPoint(writer, "Cafe Poca Cosa", "restaurant", 5, 9);
		addPoint(writer, "Los Betos", "restaurant", 9, 6);
		addPoint(writer, "Nico's Taco Shop", "restaurant", 3, 8);

		writer.close();
		searcher = new IndexSearcher(dir);
		query = new TermQuery(new Term("type", "restaurant"));
	}

	private void addPoint(IndexWriter writer, String name, String type, //
			int x, int y) throws Exception {

		Document doc = new Document();
		doc.add(new Field("name", name, Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field("type", type, Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field("x", Integer.toString(x), Field.Store.YES, //
				Field.Index.NOT_ANALYZED_NO_NORMS));
		doc.add(new Field("y", Integer.toString(y), Field.Store.YES, //
				Field.Index.NOT_ANALYZED_NO_NORMS));
		writer.addDocument(doc);
	}

	private void dumpDocs(Sort sort, TopFieldDocs docs) throws Exception {

		System.out.println("Sorted by: " + sort);
		ScoreDoc[] scoreDocs = docs.scoreDocs;
		for (int i = 0; i < scoreDocs.length; i++) {
			FieldDoc fieldDoc = (FieldDoc) scoreDocs[i];
			Float distance = (Float) fieldDoc.fields[0];
			Document doc = searcher.doc(fieldDoc.doc);
			System.out.println("    " + doc.get("name") + " @ (" + //
					doc.get("x") + "," + doc.get("y") + ") -> " + distance);
		}
	}

}
