package cc.pp.lucene.chap05.advance.searching;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import junit.framework.TestCase;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

@SuppressWarnings("rawtypes")
public class CategoeizerTest extends TestCase {

	private Map categoryMap;

	@Override
	protected void setUp() throws Exception {

		categoryMap = new TreeMap();
		buildCategoryVectors();
		//		dumpCategoryVectors();
	}

	public void testCategorization() {
		//		System.out.println(getCategory("extrame agile methodology"));
		assertEquals("technology/computers/programming", //
				getCategory("extrame agile methodology"));
		//		System.out.println(getCategory("montessori education philosophy"));
		assertEquals("education/pedagogy", getCategory("montessori education philosophy"));
	}

	public void dumpCategoryVectors() {

		Iterator categoryIterator = categoryMap.keySet().iterator();
		while (categoryIterator.hasNext()) {
			String category = (String) categoryIterator.next();
			System.out.println("Category " + category);

			Map vectorMap = (Map) categoryMap.get(category);
			Iterator vectorIterator = vectorMap.keySet().iterator();
			while (vectorIterator.hasNext()) {
				String term = (String) vectorIterator.next();
				System.out.println("    " + term + " = " + vectorMap.get(term));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void buildCategoryVectors() throws Exception {

		Directory dir = FSDirectory.open(new File("index/chap03index/"));
		IndexReader reader = DirectoryReader.open(dir);
		int maxDoc = reader.maxDoc();
		for (int i = 0; i < maxDoc; i++) {
			//			if (!reader.isDeleted(i)) {
			Document doc = reader.document(i);
			String category = doc.get("category");

			Map vectorMap = (Map) categoryMap.get(category);
			if (vectorMap == null) {
				vectorMap = new TreeMap();
				categoryMap.put(category, vectorMap);
			}

			Terms termFreqVector = reader.getTermVector(i, "subject");
			addTermFreqToMap(vectorMap, termFreqVector);
			//			}
		}
	}

	private void addTermFreqToMap(Map vectorMap, Terms termFreqVector) {

		//		String[] terms = termFreqVector.getTerms();
		//		int[] freqs = termFreqVector.getTermFrequencies();
		//
		//		for (int i = 0; i < terms.length; i++) {
		//			String term = terms[i];
		//			if (vectorMap.containsKey(term)) {
		//				Integer value = (Integer) vectorMap.get(term);
		//				vectorMap.put(term, new Integer(value.intValue() + freqs[i]));
		//			} else {
		//				vectorMap.put(term, new Integer(freqs[i]));
		//			}
		//		}
	}

	private String getCategory(String subject) {

		String[] words = subject.split(" ");

		Iterator categoryIterator = categoryMap.keySet().iterator();
		double bestAngle = Double.MAX_VALUE;
		String bestCategory = null;

		while (categoryIterator.hasNext()) {
			String category = (String) categoryIterator.next();
			//			System.out.println(category);
			double angle = computeAngle(words, category);
			//			System.out.println(" -> angle = " + angle + " (" + Math.toDegrees(angle) + ")");
			if (angle < bestAngle) {
				bestAngle = angle;
				bestCategory = category;
			}
		}

		return bestCategory;
	}

	private double computeAngle(String[] words, String category) {

		Map vectorMap = (Map) categoryMap.get(category);

		int dotProduct = 0;
		int sumOfSquares = 0;
		for (String word : words) {
			int categoryWordFreq = 0;
			if (vectorMap.containsKey(word)) {
				categoryWordFreq = ((Integer) vectorMap.get(word)).intValue();
			}
			dotProduct += categoryWordFreq;
			sumOfSquares += categoryWordFreq * categoryWordFreq;
		}

		double denominator;
		if (sumOfSquares == words.length) {
			denominator = sumOfSquares;
		} else {
			denominator = Math.sqrt(sumOfSquares) * Math.sqrt(words.length);
		}

		double ratio = dotProduct / denominator;

		return Math.acos(ratio);
	}

}
