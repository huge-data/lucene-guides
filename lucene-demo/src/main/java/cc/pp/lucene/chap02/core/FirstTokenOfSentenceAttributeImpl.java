package cc.pp.lucene.chap02.core;

import org.apache.lucene.util.AttributeImpl;

public class FirstTokenOfSentenceAttributeImpl extends AttributeImpl implements FirstTokenOfSentenceAttribute {

	private boolean firstToken = false;

	@Override
	public void setFirstToken(boolean firstToken) {
		this.firstToken = firstToken;
	}

	@Override
	public boolean getFirstToken() {
		return firstToken;
	}

	@Override
	public void clear() {
		firstToken = false;
	}

	@Override
	public void copyTo(AttributeImpl target) {
		((FirstTokenOfSentenceAttribute) target).setFirstToken(firstToken);
	}

}
