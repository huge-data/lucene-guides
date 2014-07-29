package cc.pp.lucene.chap03.searching;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.search.IndexSearcher;

/**
 * 第6章
 * @author wanggang
 *
 */
public class SearchServletFragment extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private IndexSearcher searcher;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {

		//		QueryParser parser = new NumericDateRangeQueryParser(Version.LUCENE_30, //
		//				"contents", new StandardAnalyzer(Version.LUCENE_30));

	}

}
