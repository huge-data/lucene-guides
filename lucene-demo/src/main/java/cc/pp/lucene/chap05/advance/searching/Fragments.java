package cc.pp.lucene.chap05.advance.searching;

import org.apache.lucene.search.Filter;
import org.apache.lucene.search.TermRangeFilter;
import org.apache.lucene.util.BytesRef;

public class Fragments {

	@SuppressWarnings({ "null", "unused" })
	public void frags1() {

		String lowerTerm = null;
		String upperTerm = null;
		String fieldName = null;
		Filter filter;
		// 开始
		filter = TermRangeFilter.newStringRange(fieldName, null, upperTerm, false, true);
		filter = TermRangeFilter.newStringRange(fieldName, lowerTerm, null, true, false);
		filter = TermRangeFilter.Less(fieldName, new BytesRef(upperTerm.getBytes()));
		filter = TermRangeFilter.More(fieldName, new BytesRef(lowerTerm.getBytes()));
		// 结束
	}

}
