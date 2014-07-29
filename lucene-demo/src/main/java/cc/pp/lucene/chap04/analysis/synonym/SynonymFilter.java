package cc.pp.lucene.chap04.analysis.synonym;

import java.io.IOException;
import java.util.Stack;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.AttributeSource;

public class SynonymFilter extends TokenFilter {

	public static final String TOKEN_TYPE_SYNONYM = "SYNONYM";

	private final Stack<String> synonymStack;
	private final SynonymEngine engine;
	private AttributeSource.State current;

	private final CharTermAttribute termAttr;
	private final PositionIncrementAttribute posIncrAttr;

	protected SynonymFilter(TokenStream in, SynonymEngine engine) {
		super(in);
		synonymStack = new Stack<String>(); // 定义同义词缓冲区
		this.engine = engine;
		this.termAttr = addAttribute(CharTermAttribute.class);
		this.posIncrAttr = addAttribute(PositionIncrementAttribute.class);
	}

	@Override
	public boolean incrementToken() throws IOException {
		if (synonymStack.size() > 0) {
			String syn = synonymStack.pop();
			restoreState(current);
			termAttr.append(syn); // 添加
			//			termAttr.setTermBuffer(syn); // 新版本中没有该项设置
			posIncrAttr.setPositionIncrement(0);
			return true;
		}

		if (!input.incrementToken()) { // 读下一个token
			return false;
		}

		if (addAliasesToStack()) {
			current = captureState();
		}

		return true;
	}

	private boolean addAliasesToStack() throws IOException {

		String[] synonyms = engine.getSynonyms(termAttr.toString()); // 提取同义词
		if (synonyms == null) {
			return false;
		}
		for (String synonym : synonyms) { // 将同义词依次压入栈
			synonymStack.push(synonym);
		}

		return true;
	}

}
