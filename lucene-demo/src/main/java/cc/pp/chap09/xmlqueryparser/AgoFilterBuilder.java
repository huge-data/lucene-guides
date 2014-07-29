package cc.pp.chap09.xmlqueryparser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import org.apache.lucene.search.Filter;
import org.apache.lucene.search.NumericRangeFilter;
import org.apache.lucene.xmlparser.DOMUtils;
import org.apache.lucene.xmlparser.FilterBuilder;
import org.apache.lucene.xmlparser.ParserException;
import org.w3c.dom.Element;

public class AgoFilterBuilder implements FilterBuilder {

	static HashMap<String, Integer> timeUnits = new HashMap<>();

	static {
		timeUnits.put("days", Calendar.DAY_OF_YEAR);
		timeUnits.put("months", Calendar.MONTH);
		timeUnits.put("years", Calendar.YEAR);
	}

	@Override
	public Filter getFilter(Element element) throws ParserException {

		// 提取域、时间单位、from和to信息
		String fieldName = DOMUtils.getAttributeWithInheritanceOrFail(element, "fieldName");
		String timeUnit = DOMUtils.getAttribute(element, "timeUnit", "days");
		Integer calUnit = timeUnits.get(timeUnit);
		if (calUnit == null) {
			throw new ParserException("Illegal time unit:" + timeUnit //
					+ " - must be days, months or years");
		}
		int agoStart = DOMUtils.getAttribute(element, "from", 0);
		int agoEnd = DOMUtils.getAttribute(element, "to", 0);
		if (agoStart < agoEnd) {
			int oldAgoStart = agoStart;
			agoStart = agoEnd;
			agoEnd = oldAgoStart;
		}
		
		// 解析时间
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		Calendar start = Calendar.getInstance();
		start.add(calUnit, agoStart * -1);

		Calendar end = Calendar.getInstance();
		end.add(calUnit, agoEnd * -1);

		// 创建NumericRangeFilter
		return NumericRangeFilter.newIntRange(fieldName, //
				Integer.valueOf(sdf.format(start.getTime())), //
				Integer.valueOf(sdf.format(end.getTime())), //
				true, true);
	}
	
}
