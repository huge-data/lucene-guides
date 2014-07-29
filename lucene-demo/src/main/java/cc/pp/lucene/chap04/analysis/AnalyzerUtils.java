package cc.pp.lucene.chap04.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.AttributeSource;

import cc.pp.lucene.common.LuceneConstant;

public class AnalyzerUtils {

	/**
	 * 测试函数
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		System.out.println("SimpleAnalyzer");
		displayTokensWithFullDetails(new SimpleAnalyzer(), "I'll email you at xyz@example.com");

		System.out.println("\n----");
		System.out.println("StandardAnalyzer");
		displayTokensWithFullDetails(new StandardAnalyzer(LuceneConstant.LUCENE_VERSION), //
				"I'll email you at xyz@example.com");
	}

	public static void displayTokens(Analyzer analyzer, String text) throws IOException {
		displayTokens(analyzer.tokenStream("contents", new StringReader(text))); // 引用分析过程
	}

	public static void displayTokens(TokenStream tokenStream) throws IOException {

		CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
		try {
			tokenStream.reset();
			while (tokenStream.incrementToken()) {
				System.out.println("[" + charTermAttribute.toString() + "]");// 打印同类项的token文本
			}
			tokenStream.end();
		} finally {
			tokenStream.close();
		}
	}

	public static int getPositionIncrement(AttributeSource source) {
		PositionIncrementAttribute attr = source.addAttribute(PositionIncrementAttribute.class);
		return attr.getPositionIncrement();
	}

	public static String getTerm(AttributeSource source) {
		CharTermAttribute attr = source.addAttribute(CharTermAttribute.class);
		return attr.toString();
	}

	public static String getType(AttributeSource source) {
		TypeAttribute attr = source.addAttribute(TypeAttribute.class);
		return attr.type();
	}

	public static void setPositionIncrement(AttributeSource source, int posIncr) {
		PositionIncrementAttribute attr = source.addAttribute(PositionIncrementAttribute.class);
		attr.setPositionIncrement(posIncr);
	}

	public static void setTerm(AttributeSource source, String term) {
		CharTermAttribute attr = source.addAttribute(CharTermAttribute.class);
		attr.append(term);
	}

	public static void setType(AttributeSource source, String type) {
		TypeAttribute attr = source.addAttribute(TypeAttribute.class);
		attr.setType(type);
	}

	public static void displayTokensWithPositions(Analyzer analyzer, //
			String text) throws IOException {

		TokenStream stream = analyzer.tokenStream("contents", new StringReader(text));
		CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);
		PositionIncrementAttribute posIncr = stream.addAttribute(PositionIncrementAttribute.class);

		int position = 0;
		while (stream.incrementToken()) {
			int increment = posIncr.getPositionIncrement();
			if (increment > 0) {
				position = position + increment;
				System.out.println();
				System.out.println(position + ": ");
			}
			System.out.println("[" + term.toString() + "]");
		}
		System.out.println();
	}

	public static void displayTokensWithFullDetails(Analyzer analyzer, //
			String text) throws IOException {

		TokenStream stream = analyzer.tokenStream("contents", new StringReader(text)); // 分析过程
		CharTermAttribute term = stream.addAttribute(CharTermAttribute.class); // 获取感兴趣的属性，这里是四个
		PositionIncrementAttribute posIncr = stream.addAttribute( //
				PositionIncrementAttribute.class);
		OffsetAttribute offset = stream.addAttribute(OffsetAttribute.class);
		TypeAttribute type = stream.addAttribute(TypeAttribute.class);

		int position = 0;
		while (stream.incrementToken()) { // 循环token
			int increment = posIncr.getPositionIncrement(); // 计算位置并打印
			if (increment > 0) {
				position = position + increment;
				System.out.println();
				System.out.println(position + ":");
			}
			System.out.println("[" + term.toString() + ":" + offset.startOffset() + // 打印所以token详细洗洗脑
					"->" + offset.endOffset() + ":" + type.type() + "]");
		}
		System.out.println();
	}

	public static void assertAnalyzesTo(Analyzer analyzer, String input, //
			String[] output) throws IOException {

		TokenStream stream = analyzer.tokenStream("field", new StringReader(input));

		CharTermAttribute termAttr = stream.addAttribute(CharTermAttribute.class);
		for (String expected : output) {
			assertTrue(stream.incrementToken());
			assertEquals(expected, termAttr.toString());
		}
		assertFalse(stream.incrementToken());
		stream.close();
	}

	public static void displayPositionIncrement(Analyzer analyzer, //
			String text) throws IOException {

		TokenStream stream = analyzer.tokenStream("contents", new StringReader(text));
		PositionIncrementAttribute posIncr = stream.addAttribute(PositionIncrementAttribute.class);
		while (stream.incrementToken()) {
			System.out.println("posIncr=" + posIncr.getPositionIncrement());
		}
	}

}
