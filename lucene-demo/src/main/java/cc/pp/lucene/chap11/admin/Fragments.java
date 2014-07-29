package cc.pp.lucene.chap11.admin;

import java.util.Collection;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexDeletionPolicy;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.KeepOnlyLastCommitDeletionPolicy;
import org.apache.lucene.index.SnapshotDeletionPolicy;
import org.apache.lucene.store.Directory;

import cc.pp.lucene.common.LuceneConstant;

public class Fragments {

	@SuppressWarnings({ "unused", "resource" })
	public static void main(String[] args) throws Exception {

		Directory dir = null;
		Analyzer analyzer = null;
		// start
		IndexDeletionPolicy policy = new KeepOnlyLastCommitDeletionPolicy();
		SnapshotDeletionPolicy snapshotter = new SnapshotDeletionPolicy(policy);
		IndexWriterConfig conf = new IndexWriterConfig(LuceneConstant.LUCENE_VERSION, analyzer);
		conf.setIndexDeletionPolicy(snapshotter);
		IndexWriter writer = new IndexWriter(dir, conf);
		// end

		IndexCommit commit = snapshotter.snapshot();
		try {
			Collection<String> fileNames = commit.getFileNames();
		} finally {
			snapshotter.release(commit);
		}
	}

}
