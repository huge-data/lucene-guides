package cc.pp.chap08and09.tools;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * 三个目录还没弄清楚**
 * @author wanggang
 *
 */
public class CreateSpellCheckerIndex {

	/**
	 * 测试函数
	 * @param args
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {

		//		if (args.length != 3) {
		//			System.err.println("Usage: SpellCheckerTest SpellSpeckerIndexDir " //
		//					+ "IndexDir IndexField");
		//			System.exit(-1);
		//		}
		//		String spellCheckDir = args[0];
		//		String indexDir = args[1];
		//		String indexField = args[2];
		String spellCheckDir = "index/chap08index/spellcheckerindex/";
		String indexDir = "index/chap03index/";
		String indexField = "data/chap08data/voc.txt"; // 词典

		System.out.println("Now build SpellChecker index..");

		Directory dir1 = FSDirectory.open(new File(spellCheckDir));
		// 建立SpellChecker
		SpellChecker spell = new SpellChecker(dir1);
		long startTime = System.currentTimeMillis();

		// 打开IndexReader
		Directory dir2 = FSDirectory.open(new File(indexDir));
		IndexReader reader = IndexReader.open(dir2);
		try {
			// 加入所有单词
			spell.indexDictionary(new LuceneDictionary(reader, indexField));
		} finally {
			reader.clone();
		}

		dir1.close();
		dir2.close();

		long endTime = System.currentTimeMillis();
		System.out.println(" took " + (endTime - startTime) + " ms");

	}

}
