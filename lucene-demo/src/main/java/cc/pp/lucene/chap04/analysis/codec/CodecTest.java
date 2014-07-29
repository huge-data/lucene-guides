package cc.pp.lucene.chap04.analysis.codec;

import junit.framework.TestCase;

import org.apache.commons.codec.language.Metaphone;

/**
 * Metaphone测试近音词
 * @author WG
 *
 */
public class CodecTest extends TestCase {

	public void testMetaphone() {

		Metaphone metaphoner = new Metaphone();
		System.out.println(metaphoner.encode("bear"));
		System.out.println(metaphoner.encode("bare"));
		assertEquals(metaphoner.encode("cute"), metaphoner.encode("cat"));
	}

}
