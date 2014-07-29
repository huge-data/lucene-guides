package cc.pp.lucene.chap04.analysis;

import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;

import cc.pp.lucene.common.LuceneConstant;

@SuppressWarnings({ "null", "unused", "resource" })
public class Fragments {

	public void frag1() throws Exception {

		Directory dir = null;
		Analyzer analyzer = new StandardAnalyzer(LuceneConstant.LUCENE_VERSION);
		IndexWriterConfig conf = new IndexWriterConfig(LuceneConstant.LUCENE_VERSION, analyzer);
		IndexWriter writer = new IndexWriter(dir, conf);
	}

	public void frag2() throws Exception {

		IndexWriter writer = null;
		Document doc = new Document();
		doc.add(new TextField("title", "This is the title", Field.Store.YES));
		doc.add(new TextField("contents", "...some document contentd...", Field.Store.YES));
		writer.addDocument(doc);
	}

	public void frag3() throws Exception {

		Analyzer analyzer = null;
		String text = null;
		TokenStream stream = analyzer.tokenStream("contents", new StringReader(text));
		PositionIncrementAttribute posIncr = stream.addAttribute(PositionIncrementAttribute.class);
		while (stream.incrementToken()) {
			System.out.println("posIncr=" + posIncr.getPositionIncrement());
		}
	}

}
