package cc.pp.lucene.chap02.core;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.CharFilter;

/**
 * 像FilterReader一样对输入做预处理，同时也保存了这些字符相关的原始偏移量。
 * 然而，封装FilterReader操作内容时，字符的偏移量与原始的文本并不一致。
 * @author wgybzb
 *
 */
public class FirstCharFilter extends CharFilter {

	public FirstCharFilter(Reader input) {
		super(input);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected int correct(int currentOff) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int read(char[] arg0, int arg1, int arg2) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

}
