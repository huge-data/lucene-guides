package cc.pp.lucene.test;

import java.io.FileNotFoundException;
import java.io.IOException;

public class LocalFileTest {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {

		//		Properties props = new Properties();
		//		File file = new File("data/chap03data/health/ltm.properties");
		//		props.load(new FileInputStream(file));
		//		System.out.println(props.getProperty("author"));
		//		System.out.println(System.getProperty("index.dir"));
		String str = "\u9053\u5FB7\u7D93";
		String decodedstr = new String(str.getBytes("UTF-8"));
		System.out.println(str);
		System.out.println(decodedstr);

	}

}
