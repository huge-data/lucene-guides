package cc.pp.lucene.utils;

import java.io.File;

public class PathUtils {

	public static void deletePath(String path) {

		File file = new File(path);
		if (file.isDirectory()) {
			String[] files = file.list();
			for (String f : files) {
				deletePath(file.getAbsolutePath() + "/" + f);
			}
		}
		file.delete();
	}

}
