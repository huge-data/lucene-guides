package cc.pp.lucene.chap04.analysis.chinese;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Label;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.ChineseAnalyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import cc.pp.lucene.common.LuceneConstant;

@SuppressWarnings("deprecation")
public class ChineseDemo {

	private static Analyzer[] analyzers = { new SimpleAnalyzer(LuceneConstant.LUCENE_VERSION), //
			new StandardAnalyzer(LuceneConstant.LUCENE_VERSION),//
			new ChineseAnalyzer(), //
			new CJKAnalyzer(LuceneConstant.LUCENE_VERSION), //
			new SmartChineseAnalyzer(LuceneConstant.LUCENE_VERSION) };

	/**
	 * 测试函数
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		String[] strs = { "道德经", "我在测试中文分词" };
		for (String str : strs) {
			for (Analyzer analyzer : analyzers) {
				analyze(str, analyzer);
			}
		}

	}

	private static void analyze(String text, Analyzer analyzer) throws Exception {

		StringBuffer buffer = new StringBuffer();
		TokenStream stream = analyzer.tokenStream("contents", new StringReader(text));
		CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);
		while (stream.incrementToken()) {
			buffer.append("[");
			buffer.append(term.toString());
			buffer.append("]");
		}
		String output = buffer.toString();

		Frame f = new Frame();
		f.setTitle(analyzer.getClass().getSimpleName() + " : " + text);
		f.setResizable(true);
		Font font = new Font(null, Font.PLAIN, 14);
		int width = getWidth(f.getFontMetrics(font), output);
		f.setSize((width < 250) ? 250 : width + 50, 75);

		Label label = new Label(output);
		label.setSize(width, 75);
		label.setAlignment(Label.CENTER);
		label.setFont(font);

		f.add(label);
		f.setVisible(true);
	}

	private static int getWidth(FontMetrics metrics, String s) {

		int size = 0;
		int length = s.length();
		for (int i = 0; i < length; i++) {
			size += metrics.charWidth(s.charAt(i));
		}

		return size;
	}

}
