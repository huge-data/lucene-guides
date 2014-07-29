package cc.pp.lucene.chap02.index;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

import cc.pp.lucene.common.TestUtil;

public class lockTest extends TestCase {

	private Directory dir;
	private File indexDir;

	@Override
	protected void setUp() throws IOException {

		indexDir = new File(System.getProperty("java.io.tmpdir", "tmp") + //
				System.getProperty("file.separator") + "index");
		dir = FSDirectory.open(indexDir);
	}

	public void testWriteLock() throws IOException {

		Analyzer analyzer = new SimpleAnalyzer(Version.LUCENE_46);
		IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_46, analyzer);
		IndexWriter writer1 = new IndexWriter(dir, conf);
		IndexWriter writer2 = null;
		try {
			writer2 = new IndexWriter(dir, conf);
			fail("We should never reach this point");
		} catch (LockObtainFailedException e) {
			//			e.printStackTrace();
		} finally {
			writer1.close();
			assertNull(writer2);
			TestUtil.rmDir(indexDir);
		}
	}

}
