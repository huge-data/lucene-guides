package cc.pp.lucene.chap02.core;

import org.apache.lucene.LucenePackage;

public class LucenePackageInfos {

	public static void main(String[] args) {

		Package packageInfos = LucenePackage.get();
		Package luceneInfos = LucenePackage.class.getPackage();
		System.out.println(packageInfos.toString());
		System.out.println(luceneInfos.toString());

	}

}
