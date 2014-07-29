package cc.pp.chap07.tika;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SAXXMLDocument extends DefaultHandler {

	private final StringBuilder elementBuffer = new StringBuilder();
	private final Map<String, String> attributeMap = new HashMap<String, String>();

	private Document doc;

	/**
	 * 主函数
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		SAXXMLDocument handler = new SAXXMLDocument();
		//		InputStream is = new FileInputStream(new File("data/chap07data/addressbook.xml"));
		InputStream is = new FileInputStream(new File("data/chap07data/addressbook-entry.xml"));
		Document doc = handler.getDocument(is);
		System.out.println(doc);

	}

	/**
	 * 获取文档信息，并解析
	 */
	public Document getDocument(InputStream is) // 
			throws DocumentHandlerException {

		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			SAXParser parser = spf.newSAXParser();
			parser.parse(is, this);
		} catch (Exception e) {
			throw new DocumentHandlerException("Cannot parse XML document", e);
		}

		return doc;
	}

	/**
	 * 解析开始时调用
	 */
	@Override
	public void startDocument() {
		doc = new Document();
	}

	/**
	 * 添加新的XML元素
	 */
	@Override
	public void startElement(String uri, String localName, String qName, //
			Attributes atts) throws SAXException {

		elementBuffer.setLength(0);
		attributeMap.clear();
		int numAtts = atts.getLength();
		if (numAtts > 0) {
			for (int i = 0; i < numAtts; i++) {
				attributeMap.put(atts.getQName(i), atts.getValue(i));
			}
		}
	}

	@Override
	public void characters(char[] text, int start, int length) {
		elementBuffer.append(text, start, length);
	}

	/**
	 * 关闭被处理的XML元素
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		if (qName.equals("address-book")) {
			return;
		} else if (qName.equals("contact")) {
			for (Entry<String, String> attribute : attributeMap.entrySet()) {
				String attName = attribute.getKey();
				String attValue = attribute.getValue();
				doc.add(new Field(attName, attValue, Field.Store.YES, Field.Index.NOT_ANALYZED));
			}
		} else {
			doc.add(new Field(qName, elementBuffer.toString(), //
					Field.Store.YES, Field.Index.NOT_ANALYZED));
		}
	}

}
