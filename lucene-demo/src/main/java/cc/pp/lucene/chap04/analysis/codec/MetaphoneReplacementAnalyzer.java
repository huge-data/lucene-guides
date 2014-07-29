package cc.pp.lucene.chap04.analysis.codec;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.LetterTokenizer;
import org.apache.lucene.util.Version;

import cc.pp.lucene.common.LuceneConstant;

public class MetaphoneReplacementAnalyzer extends Analyzer {

	public MetaphoneReplacementAnalyzer(Version version) {
		super();
	}

	public MetaphoneReplacementAnalyzer(ReuseStrategy reuseStrategy) {
		super(reuseStrategy);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
		return new TokenStreamComponents(null, new MetaphoneReplacementFilter(new LetterTokenizer(
				LuceneConstant.LUCENE_VERSION, reader)));
	}

}
