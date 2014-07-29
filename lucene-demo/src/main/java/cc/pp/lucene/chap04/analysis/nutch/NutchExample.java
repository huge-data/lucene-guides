package cc.pp.lucene.chap04.analysis.nutch;

//import java.io.StringReader;
//import java.io.StringReader;
//
//import org.apache.hadoop.conf.Configuration;
//import org.apache.lucene.analysis.TokenStream;
//import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
//import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
//import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
//import org.apache.lucene.analysis.tokenattributes.TermAttribute;
//import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
//import org.apache.nutch.analysis.NutchDocumentAnalyzer;
//import org.apache.nutch.searcher.Query;
//import org.apache.nutch.searcher.QueryFilters;
//
//public class NutchExample {
//
//	/**
//	 * 主函数
//	 * @throws Exception 
//	 */
//	@SuppressWarnings("resource")
//	public static void main(String[] args) throws Exception {
//
//		Configuration conf = new Configuration();
//		conf.addResource("conf/nutch-default.xml");
//		NutchDocumentAnalyzer analyzer = new NutchDocumentAnalyzer(conf);
//
//		TokenStream stream = analyzer.tokenStream("content", //
//				new StringReader("The quick brown fox..."));
//
//		CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);
//		PositionIncrementAttribute posIncr = stream.addAttribute(PositionIncrementAttribute.class);
//		OffsetAttribute offset = stream.addAttribute(OffsetAttribute.class);
//		TypeAttribute type = stream.addAttribute(TypeAttribute.class);
//
//		int position = 0;
//		while (stream.incrementToken()) {
//			int increment = posIncr.getPositionIncrement();
//			if (increment > 0) {
//				position = position + increment;
//				System.out.println();
//				System.out.println(position + ": ");
//			}
//			System.out.println("[" + term.toString() + ":" + offset.startOffset() + // 打印所以token详细洗洗脑
//					"->" + offset.endOffset() + ":" + type.type() + "]");
//		}
//		System.out.println();
//		
//		Query nutchquery = Query.parse("\"the quick brown\"", conf);
//		org.apache.lucene.search.Query luceneQuery = new QueryFilters(conf).filter(nutchquery);
//		System.out.println("Translated: " + luceneQuery);
//	}
//
//}

