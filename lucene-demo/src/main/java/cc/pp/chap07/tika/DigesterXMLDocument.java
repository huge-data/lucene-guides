package cc.pp.chap07.tika;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.digester.Digester;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.xml.sax.SAXException;

public class DigesterXMLDocument {

	private final Digester dig;
	private static Document doc;

	/**
	 * 主函数
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		DigesterXMLDocument handler = new DigesterXMLDocument();
		InputStream is = new FileInputStream(new File("data/chap07data/addressbook.xml"));
		Document doc = handler.getDocument(is);
		System.out.println(doc);

	}

	public DigesterXMLDocument() {

		dig = new Digester();
		dig.setValidating(false);

		dig.addObjectCreate("address-book", DigesterXMLDocument.class); // 创建DigesterXMLDocument
		dig.addObjectCreate("address-book/contact", Contact.class); // 创建Contact

		dig.addSetProperties("address-book/contact", "type", "type"); // 设置type属性

		dig.addCallMethod("address-book/contact/name", "setName", 0); // 设置name属性
		dig.addCallMethod("address-book/contact/address", "setAddress", 0);
		dig.addCallMethod("address-book/contact/city", "setCity", 0);
		dig.addCallMethod("address-book/contact/province", "setProvince", 0);
		dig.addCallMethod("address-book/contact/postalcode", "setPostalcode", 0);
		dig.addCallMethod("address-book/contact/country", "setCountry", 0);
		dig.addCallMethod("address-book/contact/telephone", "setTelephone", 0);

		dig.addSetNext("address-book/contact", "populateDocument"); // 调用populateDocument方法
	}

	public synchronized Document getDocument(InputStream is) //
			throws DocumentHandlerException {

		try {
			dig.parse(is); // 解析输入文档
		} catch (IOException e) {
			throw new DocumentHandlerException("Cannot parse XML documnet", e);
		} catch (SAXException e) {
			throw new DocumentHandlerException("Cannot parse XML documnet", e);
		}

		return doc;
	}

	/**
	 * 创建Lucene文档
	 * @param contact
	 */
	public void populateDocument(Contact contact) {

		doc = new Document();
		doc.add(new Field("type", contact.getType(), Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field("name", contact.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field("address", contact.getAddress(), Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field("city", contact.getCity(), Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field("province", contact.getProvince(), Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field("postalcode", contact.getPostalcode(), Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field("country", contact.getCountry(), Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field("telephone", contact.getTelephone(), Field.Store.YES, Field.Index.NOT_ANALYZED));
	}

}
