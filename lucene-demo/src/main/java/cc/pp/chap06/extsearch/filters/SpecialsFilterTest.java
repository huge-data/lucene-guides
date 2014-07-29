package cc.pp.chap06.extsearch.filters;

import java.io.File;

import junit.framework.TestCase;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.FSDirectory;

public class SpecialsFilterTest extends TestCase {

	private Query allBooks;
	private IndexSearcher searcher;

	public void testCustomFilter() throws Exception {

		String[] isbns = new String[] { "9780061142666", "9780394756820" };
		SpecialsAccessor accessor = new TestSpecialsAccessor(isbns);
		Filter filter = new SpecialsFilter(accessor);
		TopDocs hits = searcher.search(allBooks, filter, 10);
		assertEquals("the specials", isbns.length, hits.totalHits);
	}

	public void testFilteredQuery() throws Exception {

		String[] isbns = new String[] {"9780880105118"};
		SpecialsAccessor accessor = new TestSpecialsAccessor(isbns);
		Filter filter = new SpecialsFilter(accessor);

		WildcardQuery educationBooks = new WildcardQuery(//
				new Term("category", "*education*"));
		FilteredQuery edBooksOnSpecial = new FilteredQuery(educationBooks, filter);

		TermQuery logoBooks = new TermQuery(new Term("subject", "logo"));

		BooleanQuery logoOrEdBooks = new BooleanQuery();
		logoOrEdBooks.add(logoBooks, BooleanClause.Occur.SHOULD);
		logoOrEdBooks.add(edBooksOnSpecial, BooleanClause.Occur.SHOULD);
		System.out.println(logoOrEdBooks.toString());

		TopDocs hits = searcher.search(logoOrEdBooks, 10);
		assertEquals("Papert and Steiner", 2, hits.totalHits);
	}

	@Override
	protected void setUp() throws Exception {
		allBooks = new MatchAllDocsQuery();
		searcher = new IndexSearcher(FSDirectory.open(new File("index/chap03index/")), true);
	}

}
