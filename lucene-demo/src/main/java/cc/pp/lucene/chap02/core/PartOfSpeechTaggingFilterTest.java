package cc.pp.lucene.chap02.core;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

public class PartOfSpeechTaggingFilterTest {

	/**
	 * 测试函数
	 */
	public static void main(String[] args) throws IOException {

		// text to tokenize
		final String text = "This is a demo of the TokenStream API";
		MyAnalyzer analyzer = new MyAnalyzer(Version.LUCENE_46);
		TokenStream stream = analyzer.tokenStream("field", new StringReader(text));
		// get the CharTermAttribute from the TokenStream
		CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);
		// get the PartOfSpeechAttribute from the TokenStream
		PartOfSpeechAttribute posAtt = stream.addAttribute(PartOfSpeechAttribute.class);
		try {
			stream.reset();
			// print all tokens until stream is exhausted
			while (stream.incrementToken()) {
				System.out.println(termAtt.toString() + ": " + posAtt.getPartOfSpeech());
			}
			stream.end();
		} finally {
			stream.close();
			analyzer.close();
		}

	}

}
