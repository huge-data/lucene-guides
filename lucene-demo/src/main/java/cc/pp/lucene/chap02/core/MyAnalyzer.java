package cc.pp.lucene.chap02.core;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.miscellaneous.LengthFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

public class MyAnalyzer extends Analyzer {

	private final Version matchVersion;

	public MyAnalyzer(Version matchVersion) {
		this.matchVersion = matchVersion;
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
		//		return new TokenStreamComponents(new WhitespaceTokenizer(matchVersion, reader));
		final Tokenizer source = new WhitespaceTokenizer(matchVersion, reader);
		// 根据token的长度过滤
		TokenStream result = new LengthFilter(matchVersion, source, 3, Integer.MAX_VALUE);
		result = new PartOfSpeechTaggingFilter(result);
		return new TokenStreamComponents(source, result);
	}

	//	@Override
	//	protected Reader initReader(String fieldName, Reader reader) {
	//		// wrap the Reader in a CharFilter chain.
	//		return new SecondCharFilter(new FirstCharFilter(reader));
	//	}

	/**
	 * 测试函数
	 */
	public static void main(String[] args) throws IOException {

		final String text = "This is a demo of the new TokenStream API";
		MyAnalyzer analyzer = new MyAnalyzer(Version.LUCENE_46);
		TokenStream tokenSTream = analyzer.tokenStream("myfield", new StringReader(text));
		CharTermAttribute charTermAttribute = tokenSTream.addAttribute(CharTermAttribute.class);
		try {
			tokenSTream.reset();
			while (tokenSTream.incrementToken()) {
				System.out.println(charTermAttribute.toString());
			}
			tokenSTream.end();
		} finally {
			tokenSTream.close();
			analyzer.close();
		}
	}

}


