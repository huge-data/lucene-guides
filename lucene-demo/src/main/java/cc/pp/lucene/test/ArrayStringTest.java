package cc.pp.lucene.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrayStringTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String[] arr = new String[] { "1", "2" };
		List<String> list1 = Arrays.asList(arr);
		System.out.println(list1);

		List<String> list2 = new ArrayList<String>();
		list2.add("a1");
		list2.add("a2");
		//		String[] toBeStored = list2.toArray(new String[list2.size()]);
		String[] toBeStored = list2.toArray(new String[0]);
		for (String s : toBeStored) {
			System.out.println(s);
		}

		String[] result = new String[0];
		System.out.println(result.length);
	}

}
