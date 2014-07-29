package cc.pp.chap08and09.tools;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.spell.LevensteinDistance;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class SpellCheckerExample {

	/**
	 * 测试函数

	 * @throws IOException 	 */
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {

		//		if (args.length != 2) {
		//			System.err.println("Usage: SpellCheckerTest SpellckerIndexDir" //
		//					+ " wordToRespell");
		//			System.exit(-1);
		//		}
		//		String spellCheckDir = args[0];
		//		String wordToRespell = args[1];

		String spellCheckDir = "index/chap08index/spellcheckerindex/";
		String wordToRespell = "lucene";

		Directory dir = FSDirectory.open(new File(spellCheckDir));
		if (!IndexReader.indexExists(dir)) {
			System.out.println("\nErroe: No spellchecker index at path \"" + //
					spellCheckDir + "\"; please run CreateSpellCheckIndex first\n");
			System.exit(-1);
		}

		SpellChecker spell = new SpellChecker(dir);
		spell.setStringDistance(new LevensteinDistance());
		//		spell.setStringDistance(new JaroWinklerDistance());

		String[] suggestions = spell.suggestSimilar(wordToRespell, 5);
		System.out.println(suggestions.length + " suggestions for '" + //
				wordToRespell + "':");
		for (String suggestion : suggestions) {
			System.out.println(" " + suggestion);
		}

	}

}
