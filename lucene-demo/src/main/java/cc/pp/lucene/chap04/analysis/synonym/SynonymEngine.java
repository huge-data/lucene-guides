package cc.pp.lucene.chap04.analysis.synonym;

import java.io.IOException;

public interface SynonymEngine {
	
	String[] getSynonyms(String s) throws IOException;

}
