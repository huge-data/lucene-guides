package cc.pp.chap06.extsearch.filters;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.Filter;
import org.apache.lucene.util.OpenBitSet;

public class SpecialsFilter extends Filter {

	private static final long serialVersionUID = 1L;

	private final SpecialsAccessor accessor;

	public SpecialsFilter(SpecialsAccessor accessor) {
		this.accessor = accessor;
	}

	@Override
	public DocIdSet getDocIdSet(IndexReader reader) throws IOException {

		OpenBitSet bits = new OpenBitSet(reader.maxDoc());
		String[] isbns = accessor.isbns();

		int[] docs = new int[1];
		int[] freqs = new int[1];

		for (String isbn : isbns) {
			if (isbn != null) {
				// 搜索与isbn匹配的文档
				TermDocs termDocs = reader.termDocs(new Term("isbn", isbn));
				int count = termDocs.read(docs, freqs);
				if (count == 1) {
					bits.set(docs[0]); // 记录每个匹配的文档
				}
			}
		}

		return bits;
	}

	@Override
	public String toString() {
		return "SpecialsFilter";
	}

}
