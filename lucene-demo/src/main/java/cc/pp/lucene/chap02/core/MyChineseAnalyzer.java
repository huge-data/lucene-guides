package cc.pp.lucene.chap02.core;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.cn.smart.SentenceTokenizer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.cn.smart.WordTokenFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

public class MyChineseAnalyzer extends Analyzer {

	@SuppressWarnings("unused")
	private final Version matchVersion;

	public MyChineseAnalyzer(Version matchVersion) {
		this.matchVersion = matchVersion;
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
		Tokenizer tokenizer = new SentenceTokenizer(reader);
		TokenStream result = new WordTokenFilter(tokenizer);
		result = new PorterStemFilter(result);
		return new TokenStreamComponents(tokenizer, result);
	}

	/**
	 * 测试函数
	 */
	public static void main(String[] args) throws IOException {

		final String text = "我去年买了个表！";
		//		MyChineseAnalyzer analyzer = new MyChineseAnalyzer(Version.LUCENE_46);
		SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer(Version.LUCENE_46);
		TokenStream tokenStream = analyzer.tokenStream("myfiled", text);
		CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
		try {
			tokenStream.reset();
			while (tokenStream.incrementToken()) {
				System.out.println(charTermAttribute.toString());
			}
			tokenStream.end();
		} finally {
			tokenStream.close();
			analyzer.close();
		}

	}

}
