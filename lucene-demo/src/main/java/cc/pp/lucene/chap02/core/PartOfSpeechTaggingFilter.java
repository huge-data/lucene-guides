package cc.pp.lucene.chap02.core;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import cc.pp.lucene.chap02.core.PartOfSpeechAttribute.PartOfSpeech;

/**
 * 继承TokenFilter，将新的属性PartOfSpeechAttribute应用到每个token单元。
 * 该示例展示了一个朴素的过滤器，用于标记首字母为大写的单词为'Noun'，而其他字母为'Unknown'。
 * @author wgybzb
 *
 */
public class PartOfSpeechTaggingFilter extends TokenFilter {

	PartOfSpeechAttribute posAtt = addAttribute(PartOfSpeechAttribute.class);
	CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

	protected PartOfSpeechTaggingFilter(TokenStream input) {
		super(input);
	}

	@Override
	public boolean incrementToken() throws IOException {
		if (!input.incrementToken()) {
			return false;
		}
		posAtt.setPartOfSpeech(determinePOS(termAtt.buffer(), 0, termAtt.length()));
		return true;
	}

	// determine the part of speech for the given term
	protected PartOfSpeech determinePOS(char[] term, int offset, int length) {
		// naive implementation that tags every uppercased word as noun
		if (length > 0 && Character.isUpperCase(term[0])) {
			return PartOfSpeech.Noun;
		}
		return PartOfSpeech.Unknown;
	}

}

