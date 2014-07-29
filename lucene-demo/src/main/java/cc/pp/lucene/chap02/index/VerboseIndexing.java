package cc.pp.lucene.chap02.index;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class VerboseIndexing {

	public static void main(String[] args) throws IOException {

		VerboseIndexing vi = new VerboseIndexing();
		vi.index();

	}

	public void index() throws IOException {

		Directory dir = new RAMDirectory();
		Analyzer analyzer = new WhitespaceAnalyzer(Version.LUCENE_46);
		IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_46, analyzer);

		conf.setInfoStream(System.out);

		IndexWriter writer = new IndexWriter(dir, conf);

		for (int i = 0; i < 100; i++) {
			Document doc = new Document();
			doc.add(new StringField("keyword", "goober", Field.Store.YES));
			writer.addDocument(doc);
		}
		writer.close();
	}

}
