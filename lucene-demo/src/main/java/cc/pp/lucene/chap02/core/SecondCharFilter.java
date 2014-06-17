package cc.pp.lucene.chap02.core;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.CharFilter;

public class SecondCharFilter extends CharFilter {

	public SecondCharFilter(Reader input) {
		super(input);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected int correct(int currentOff) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

}
