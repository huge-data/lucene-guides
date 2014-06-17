package cc.pp.chap01.lucene.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.lucene.LucenePackage;
import org.junit.Test;

public class LucenePackageInfosTest {

	@Test
	public void testLuceneInfos() {

		Package packageInfos = LucenePackage.get();
		Package luceneInfos = LucenePackage.class.getPackage();
		assertEquals("4.6.0", packageInfos.getSpecificationVersion());
		assertTrue(luceneInfos == packageInfos);
	}

}
