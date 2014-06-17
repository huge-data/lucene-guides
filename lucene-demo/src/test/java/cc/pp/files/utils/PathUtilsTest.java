package cc.pp.files.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import cc.pp.lucene.utils.PathUtils;

public class PathUtilsTest {

	@Test
	public void testDeletePath() {

		File file = new File("testdata");
		file.mkdir();
		assertTrue(file.exists());
		file = new File("testdata/data1");
		file.mkdir();
		assertTrue(file.exists());
		file = new File("testdata/data2");
		file.mkdir();
		assertTrue(file.exists());

		PathUtils.deletePath("testdata");
		assertFalse(new File("testdata").exists());
	}

}
