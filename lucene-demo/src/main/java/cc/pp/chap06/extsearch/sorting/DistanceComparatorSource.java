package cc.pp.chap06.extsearch.sorting;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldComparatorSource;
import org.apache.lucene.search.SortField;

/**
 * 排序架构
 * @author wanggang
 *
 */
public class DistanceComparatorSource extends FieldComparatorSource {

	/**
	 * 对匹配文档进行排序
	 * @author wanggang
	 *
	 */
	@SuppressWarnings("unused")
	private class DistanceScoreDocLookupComparator extends FieldComparator {

		private int[] xDoc, yDoc;
		private final float[] values;
		private float bottom;
		String fieldName;

		public DistanceScoreDocLookupComparator(String fieldName, int numHits) {
			this.values = new float[numHits];
			this.fieldName = fieldName;
		}

		@Override
		public int compare(int slot1, int slot2) {
			if (values[slot1] < values[slot2])
				return -1;
			if (values[slot1] > values[slot2])
				return 1;
			return 0;
		}

		@Override
		public int compareBottom(int doc) throws IOException {
			float docDistance = getDistance(doc);
			if (bottom < docDistance)
				return -1;
			if (bottom > docDistance)
				return 1;
			return 0;
		}

		@Override
		public void copy(int slot, int doc) throws IOException {
			values[slot] = getDistance(doc);
		}

		@Override
		public void setBottom(int slot) {
			bottom = values[slot];
		}

		@Override
		public void setNextReader(IndexReader reader, int docBase) throws IOException {
			xDoc = FieldCache.DEFAULT.getInts(reader, "x"); // 从域缓存获取x、y的值
			yDoc = FieldCache.DEFAULT.getInts(reader, "y");
		}

		public int sortType() {
			return SortField.CUSTOM;
		}

		@SuppressWarnings("rawtypes")
		@Override
		public Comparable value(int slot) { // 检索计算的距离，用于排序
			return new Float(values[slot]);
		}

		private float getDistance(int doc) {
			int deltax = xDoc[doc] - x;
			int deltay = yDoc[doc] - y;
			return (float) Math.sqrt(deltax * deltax + deltay * deltay);
		}

	}

	private static final long serialVersionUID = 1L;

	/**
	 * 初始化坐标
	 */
	private final int x;
	private final int y;

	public DistanceComparatorSource(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public FieldComparator newComparator(String fieldName, int numHits, int sorPos, //
			boolean reversed) throws IOException {
		return new DistanceScoreDocLookupComparator(fieldName, numHits);
	}

}
