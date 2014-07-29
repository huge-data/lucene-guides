package cc.pp.lucene.chap04.analysis.synonym;

import java.io.IOException;
import java.util.HashMap;

public class TestSynonymEngine implements SynonymEngine {

	private static HashMap<String, String[]> map = new HashMap<String, String[]>();

	static {
		map.put("quick", new String[] { "fast", "speedy" });
		map.put("jumps", new String[] { "leaps", "hops" });
		map.put("over", new String[] { "above" });
		map.put("lazy", new String[] { "apathetic", "sluggish" });
		map.put("dog", new String[] { "canine", "pooch" });
	}

	@Override
	public String[] getSynonyms(String s) throws IOException {
		return map.get(s);
	}

}
