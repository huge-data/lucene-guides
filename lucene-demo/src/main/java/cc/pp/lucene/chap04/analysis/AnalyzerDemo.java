package cc.pp.lucene.chap04.analysis;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import cc.pp.lucene.common.LuceneConstant;

public class AnalyzerDemo {

	private static final Analyzer[] analyzers = new Analyzer[] { new WhitespaceAnalyzer(LuceneConstant.LUCENE_VERSION),
			new SimpleAnalyzer(), new StopAnalyzer(LuceneConstant.LUCENE_VERSION),
			new StandardAnalyzer(LuceneConstant.LUCENE_VERSION) };

	/**
	 * 测试函数
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		String[] examples = { "The quick brown fox jumped over the lazy dog", //
				"XY&Z Corporation - xyz@example.com" };

		for (String text : examples) {
			analyze(text);
		}

	}

	private static void analyze(String text) throws IOException {

		System.out.println("Analyzing \"" + text + "\"");
		for (Analyzer analyzer : analyzers) {
			String name = analyzer.getClass().getSimpleName();
			System.out.println("  " + name + ":");
			System.out.println("   ");
			AnalyzerUtils.displayTokens(analyzer, text);
			System.out.println("\n");
		}
	}

}
