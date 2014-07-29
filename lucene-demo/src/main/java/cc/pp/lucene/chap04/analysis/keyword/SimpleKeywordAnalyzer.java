package cc.pp.lucene.chap04.analysis.keyword;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.util.CharTokenizer;

import cc.pp.lucene.common.LuceneConstant;

/**
 * 该类限制了token域的宽度不大于255，实现前提是假设关键词长度不大于255.
 * @author Administrator
 *
 */
public class SimpleKeywordAnalyzer extends Analyzer {

	@Override
	protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
		return new TokenStreamComponents(null, new CharTokenizer(LuceneConstant.LUCENE_VERSION, reader) {
			@Override
			protected boolean isTokenChar(int c) {
				return true;
			}
		});
	}

}
