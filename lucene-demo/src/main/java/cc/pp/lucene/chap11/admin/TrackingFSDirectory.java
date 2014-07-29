package cc.pp.lucene.chap11.admin;
//package cc.pp.lucene.chap11.admin;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.HashSet;
//import java.util.Set;
//
//import org.apache.lucene.store.BufferedIndexInput;
//import org.apache.lucene.store.IndexInput;
//import org.apache.lucene.store.IndexOutput;
//import org.apache.lucene.store.SimpleFSDirectory;
//
//public class TrackingFSDirectory extends SimpleFSDirectory {
//
//	/**
//	 * 跟踪关闭
//	 */
//	protected class TrackingFSIndexInput extends SimpleFSIndexInput {
//
//		String name;
//
//		boolean cloned = false;
//
//		public TrackingFSIndexInput(String name, int bufferSize) throws IOException {
//			super(new File(getFile(), name), bufferSize, getReadChunkSize());
//			this.name = name;
//		}
//
//		@Override
//		public Object clone() {
//			TrackingFSIndexInput clone = (TrackingFSIndexInput) super.clone();
//			clone.cloned = true;
//			return clone;
//		}
//
//		@Override
//		public void close() throws IOException {
//			super.close();
//			if (!cloned) {
//				synchronized(TrackingFSDirectory.this) {
//					openInputs.remove(name);
//				}
//			}
//			report("Close Input: " + name);
//		}
//	}
//
//	/**
//	 * 跟踪关闭
//	 */
//	protected class TrackingFSIndexOutput extends SimpleFSIndexOutput {
//
//		String name;
//
//		public TrackingFSIndexOutput(String name) throws IOException {
//			super(new File(getFile(), name));
//			this.name = name;
//		}
//
//		@Override
//		public void close() throws IOException {
//			super.close();
//			synchronized(TrackingFSDirectory.this) {
//				openOutputs.remove(name);
//			}
//			report("Close Output: " + name);
//		}
//	}
//
//	/**
//	 *  存储所有打开的文件名
//	 */
//	private final Set<String> openOutputs = new HashSet<>();
//
//	private final Set<String> openInputs = new HashSet<>();
//
//	public TrackingFSDirectory(File path) throws IOException {
//		super(path);
//	}
//
//	@Override
//	synchronized public IndexOutput createOutput(String name) throws IOException {
//		openOutputs.add(name);
//		report("Open Output: " + name);
//		File file = new File(getFile(), name);
//		if (file.exists() && !file.delete()) {
//			throw new IOException("Cannot overwrite: " + file);
//		}
//		return new TrackingFSIndexOutput(name);
//	}
//
//	/**
//	 * 返回所有打开的数量
//	 */
//	synchronized public int getFileDescriptorCount() {
//		return openOutputs.size() + openInputs.size();
//	}
//
//	/**
//	 * 打开跟踪输入
//	 */
//	@Override
//	synchronized public IndexInput openInput(String name) throws IOException {
//		return openInput(name, BufferedIndexInput.BUFFER_SIZE);
//	}
//
//	/**
//	 * 打开跟踪输出
//	 */
//	@Override
//	synchronized public IndexInput openInput(String name, //
//			int bufferSize) throws IOException {
//		openInputs.add(name);
//		report("Open Input: " + name);
//		return new TrackingFSIndexInput(name, bufferSize);
//	}
//
//	synchronized private void report(String message) {
//		System.out.println(System.currentTimeMillis() + ": " + //
//				message + "; total " + getFileDescriptorCount());
//	}
//
//}
