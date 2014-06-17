package cc.pp.lucene.driver;

import cc.pp.lucene.demo.IndexFiles;

public class Driver {

	public static void main(String[] args) {

		if (args.length < 1) {
			System.err.println("Usage: <className>");
			System.exit(-1);
		}

		switch (args[0]) {
		case "indexFiles":
			String[] input = new String[args.length - 1];
			for (int i = 1; i < args.length - 1; i++) {
				input[i - 1] = args[i];
			}
			IndexFiles.main(args);
			break;
		default:
			return;
		}

	}

}
