package cc.pp.lucene.chap05.advance.searching;

import java.io.File;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * 查找同类书籍
 * @author Administrator
 *
 */
public class BooksLikeThis {

	private final IndexReader reader;
	private final IndexSearcher searcher;

	public BooksLikeThis(IndexReader reader) {
		this.reader = reader;
		searcher = new IndexSearcher(reader);
	}

	/**
	 * 主函数
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		Directory dir = FSDirectory.open(new File("index/chap03index/"));
		IndexReader reader = DirectoryReader.open(dir);
		int numDocs = reader.maxDoc();

		BooksLikeThis blt = new BooksLikeThis(reader);
		for (int i = 0; i < numDocs; i++) { // 遍历每本书

			System.out.println();

			Document doc = reader.document(i);
			System.out.println(doc.get("title"));

			Document[] docs = blt.docsLike(i, 10); // 查找与这本书相似的书籍
			if (docs.length == 0) {
				System.out.println(" None like this");
			}
			for (Document likeThisDoc : docs) {
				System.out.println(" -> " + likeThisDoc.get("title"));
			}
		}

		reader.close();
		dir.close();
	}

	public Document[] docsLike(int id, int max) throws Exception {

		Document doc = reader.document(id);

		// 添加“author”搜索项，并加权
		String[] authors = doc.getValues("author");
		BooleanQuery authorQuery = new BooleanQuery();
		for (String author : authors) {
			authorQuery.add(new TermQuery(new Term("author", author)), BooleanClause.Occur.SHOULD);
		}
		authorQuery.setBoost(2.0f);

		// 从“subject”项向量中添加搜索项
		BooleanQuery subjectQuery = new BooleanQuery();
		Terms terms = reader.getTermVector(id, "subject");
		for (int i = 0; i < terms.size(); i++) {
			// 这里需要修改
			//			subjectQuery.add(new TermQuery(new Term("subject", vector.)), BooleanClause.Occur.SHOULD);
		}

		// 创建联合索引
		BooleanQuery likeThisQuery = new BooleanQuery();
		likeThisQuery.add(authorQuery, BooleanClause.Occur.SHOULD);
		likeThisQuery.add(subjectQuery, BooleanClause.Occur.SHOULD);

		// 出去当前的书号，以防止被搜索到
		likeThisQuery.add(new TermQuery(new Term("isbn", //
				doc.get("isbn"))), BooleanClause.Occur.MUST_NOT);

		//		System.out.println(" Query: " + likeThisQuery.toString("contents"));

		TopDocs hits = searcher.search(likeThisQuery, 10);
		int size = max;
		if (max > hits.scoreDocs.length) {
			size = hits.scoreDocs.length;
		}

		Document[] docs = new Document[size];
		for (int i = 0; i < size; i++) {
			docs[i] = reader.document(hits.scoreDocs[i].doc);
		}

		return docs;
	}

}
