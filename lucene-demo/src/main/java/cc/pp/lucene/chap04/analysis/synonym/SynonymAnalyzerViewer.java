package cc.pp.lucene.chap04.analysis.synonym;

import java.io.IOException;

import cc.pp.lucene.chap04.analysis.AnalyzerUtils;
import cc.pp.lucene.common.LuceneConstant;

public class SynonymAnalyzerViewer {

	/**
	 * 主函数
	 */
	public static void main(String[] args) throws IOException {

		SynonymEngine engine = new TestSynonymEngine();

		AnalyzerUtils.displayTokensWithPositions(new SynonymAnalyzer(engine, LuceneConstant.LUCENE_VERSION), //
				"The quick brown fox jumps over the lazy dog");

		//		AnalyzerUtils.displayTokensWithPositions(new SynonymAnalyzer(engine), //
		//				"\"Oh, we get both kinds - country AND western!\" - B.B.");
	}

}
