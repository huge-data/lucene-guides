package cc.pp.lucene.chap02.index;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

@SuppressWarnings({ "resource", "unused" })
public class Fragments {

	Analyzer analyzer = new WhitespaceAnalyzer(Version.LUCENE_46);
	IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_46, analyzer);

	public static void indexNumbersMethod() {
		new StringField("size", "4096", Field.Store.YES);
		new StringField("price", "10.99", Field.Store.YES);
		new StringField("author", "Arthur C. Clark", Field.Store.YES);
	}

	public static final String COMPANY_DOMAIN = "example.com";
	public static final String BAD_DOMAIN = "yucky-domain.com";

	private String getSenderEmail() {
		return "bob@smith.com";
	}

	private String getSenderName() {
		return "Bob Smith";
	}

	private String getSenderDomain() {
		return COMPANY_DOMAIN;
	}

	private String getSubject() {
		return "Hi there Lisa";
	}

	private String getBody() {
		return "I don't have much to say";
	}

	private boolean isImportant(String lowerDomain) {
		return lowerDomain.endsWith(COMPANY_DOMAIN);
	}

	private boolean isUnimportant(String lowerDomain) {
		return lowerDomain.endsWith(BAD_DOMAIN);
	}

	public void ramDirExample() throws IOException {
		Directory ramDir = new RAMDirectory();
		IndexWriter writer = new IndexWriter(ramDir, conf);
	}

	public void dirCopy() throws IOException {
		Directory otherDir = null;
		Directory ramDir = new RAMDirectory(otherDir, null);
	}

	public void addIndexes() throws IOException {
		Directory otherDir = null;
		Directory ramDir = null;
		Analyzer analyzer = null;
		IndexWriter writer = new IndexWriter(otherDir, conf);
		writer.addIndexes(new Directory[] { ramDir });
	}

	public void docBoostMethod() throws IOException {

		Directory dir = new RAMDirectory();
		IndexWriter writer = new IndexWriter(dir, conf);

		Document doc = new Document();
		String senderEmail = getSenderEmail();
		String senderName = getSenderName();
		String subject = getSubject();
		String body = getBody();
		doc.add(new StringField("senderEmail", senderEmail, Field.Store.YES));
		doc.add(new TextField("senderName", senderName, Field.Store.YES));
		doc.add(new TextField("subject", subject, Field.Store.YES));
		doc.add(new TextField("body", body, Field.Store.NO));
		String lowerDomain = getSenderDomain().toLowerCase();
		if (isImportant(lowerDomain)) {
			// 新版本中没有该项设置
			//			doc.setBoost(1.5F);
		} else if (isUnimportant(lowerDomain)) {
			//			doc.setBoost(0.1F);
		}
		writer.addDocument(doc);
		writer.close();
	}

	public void fieldBoostMethod() {

		String senderName = getSenderName();
		String subject = getSubject();
		Field subjectField = new TextField("subject", subject, Field.Store.YES);
		subjectField.setBoost(1.2F);
	}

	public void numberField() {
		Document doc = new Document();
		doc.add(new FloatField("price", 19.99f, Field.Store.YES));
	}

	public void numberTimestamp() {
		Document doc = new Document();
		doc.add(new LongField("timestamp", new Date().getTime(), Field.Store.YES));
		doc.add(new IntField("day", (int) (new Date().getTime() / 24 / 3600), Field.Store.YES));
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		doc.add(new IntField("dayOfMonth", cal.get(Calendar.DAY_OF_MONTH), Field.Store.YES));
	}

	public void setInfoStream() throws IOException {
		Directory dir = null;
		Analyzer analyzer = null;
		IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_46, analyzer);
		conf.setInfoStream(System.out);
		IndexWriter writer = new IndexWriter(dir, conf);
	}

	public void dateMethod() {
		Document doc = new Document();
		doc.add(new StringField("indexDate", DateTools.dateToString(new Date(), DateTools.Resolution.DAY),
				Field.Store.YES));
	}

	public void numericField() {

		Document doc = new Document();
		FloatField price = new FloatField("price", 19.99f, Field.Store.YES);
		doc.add(price);

		LongField timestamp = new LongField("timestamp", new Date().getTime(), Field.Store.YES);
		doc.add(timestamp);

		Date b = new Date();
		String v = DateTools.dateToString(b, DateTools.Resolution.DAY);
		IntField birthday = new IntField("birthday", Integer.parseInt(v), Field.Store.YES);
		doc.add(birthday);
	}

	public void indexAuthors() {
		String[] authors = new String[] { "lisa", "tom" };
		Document doc = new Document();
		for (String author : authors) {
			doc.add(new TextField("author", author, Field.Store.YES));
		}
	}

}
