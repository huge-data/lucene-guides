package cc.pp.chap08and09.tools;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.util.Version;

public class HighlightIt {

	private static final String text = "In this section we'll show you how to make the simplest "
			+ "programmatic query, searching for a single term, and then "
			+ "we'll see how to use QueryParser to accept textual queries. "
			+ "In the sections that follow, we’ll take this simple example "
			+ "further by detailing all the query types built into Lucene. "
			+ "We begin with the simplest search of all: searching for all " + "documents that contain a single term.";

	/**
	 * 测试函数
	 * @throws InvalidTokenOffsetsException
	 * @throws IOException
	 * @throws ParseException
	 */
	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {

		//		if (args.length != 1) {
		//			System.err.println("Usage: HighlightIt <filename-out>");
		//			System.exit(-1);
		//		}
		//
		//		String fileName = args[0];
		String fileName = "data/chap08data/highlighted.html";

		// 创建查询
		String searchText = "term";
		QueryParser parser = new QueryParser(Version.LUCENE_30, "f", //
				new StandardAnalyzer(Version.LUCENE_30));
		Query query = parser.parse(searchText);

		// 自定义环境标签
		SimpleHTMLFormatter formatter = new SimpleHTMLFormatter( //
				"<span class=\"highlight\">", "</span>");

		// 语汇单元化文本（或者语法分析）
		TokenStream tokenStream = new StandardAnalyzer(Version.LUCENE_30).//
				tokenStream("f", new StringReader(text));

		// 创建查询评分
		QueryScorer scorer = new QueryScorer(query, "f");
		// 创建高亮显示
		Highlighter highlighter = new Highlighter(formatter, scorer);
		// 使用SimpleSpanFragmenter来分割片段
		Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
		highlighter.setTextFragmenter(fragmenter);
		// 高亮显示最好的3个片段
		String result = highlighter.getBestFragments(tokenStream, //
				text, 3, "...");

		// 写入被高亮显示的片段到Html中
		FileWriter writer = new FileWriter(fileName);
		writer.write("<html>\n");
		writer.write("<style>\n .highlight{\n background: yellow;\n}\n</style>\n");
		writer.write("<body>\n");
		writer.write(result + "\n");
		writer.write("</body>\n</html>");
		writer.close();

		System.out.println("Highlight Complete!");
	}

}
