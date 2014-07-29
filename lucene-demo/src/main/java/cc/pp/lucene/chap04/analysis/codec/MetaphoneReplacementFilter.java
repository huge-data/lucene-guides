package cc.pp.lucene.chap04.analysis.codec;

import java.io.IOException;

import org.apache.commons.codec.language.Metaphone;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

@SuppressWarnings("unused")
public class MetaphoneReplacementFilter extends TokenFilter {

	public static final String METAPHONE = "metaphone";

	private final Metaphone metaphoner = new Metaphone();
	private final CharTermAttribute termAttr;
	private final TypeAttribute typeAttr;

	protected MetaphoneReplacementFilter(TokenStream input) {
		super(input);
		termAttr = addAttribute(CharTermAttribute.class);
		typeAttr = addAttribute(TypeAttribute.class);
	}

	@Override
	public boolean incrementToken() throws IOException {

		if (!input.incrementToken()) {
			return false;
		}
		//		String encoded = metaphoner.encode(termAttr.toString()); // 转换Metaphone编码
		//		termAttr.setTermBuffer(encoded); // 用编码后的项覆盖原来的项，新版本不支持
		typeAttr.setType(METAPHONE); // 设置项类型

		return true;
	}

}
