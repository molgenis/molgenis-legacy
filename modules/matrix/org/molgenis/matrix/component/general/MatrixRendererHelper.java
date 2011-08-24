package org.molgenis.matrix.component.general;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.molgenis.framework.db.QueryRule.Operator;


public class MatrixRendererHelper {
	
	public static final String MATRIX_COMPONENT_REQUEST_PREFIX = "matrix_component_request_prefix_";
	public static final int ROW_STOP_DEFAULT = 10;
	public static final int COL_STOP_DEFAULT = 5;
	
	public static final HashMap<String, String> operators() {
		HashMap<String, String> ops = new HashMap<String, String>();
		ops.put("GREATER", "&gt;");
		ops.put("GREATER_EQUAL", "&gt;=");
		ops.put("LESS", "&lt;");
		ops.put("LESS_EQUAL", "&lt;=");
		ops.put("EQUALS", "equals");
		ops.put("SORTASC", "sort asc");
		ops.put("SORTDESC", "sort desc");
		ops.put("NOT", "is not");
		//ops.put("LIMIT", "sort desc");
		//ops.put("OFFSET", "sort desc");  ->  don't add here, just do by paging?
		return ops;
	}

	public static List<Filter> copyFilterList(List<Filter> original){
		List<Filter> copy = new ArrayList<Filter>();
		for(Filter f : original){
			MatrixQueryRule q = new MatrixQueryRule(f.getQueryRule().getField(), f.getQueryRule().getOperator(), f.getQueryRule().getValue());
			Filter fCopy = new Filter(f.getFilterType(), q);
			copy.add(fCopy);
		}
		return copy;
	}
	
	public static Filter getFilterWhere(List<Filter> filters, Filter.Type type, String field, Operator operator){
		for (int filterIndex = 0; filterIndex < filters.size(); filterIndex++)
		{
			Filter f = filters.get(filterIndex);
			if(f.getFilterType().equals(type) && f.getQueryRule().getField().equals(field) && f.getQueryRule().getOperator().equals(operator))
			{
				return f;
			}
		}
		return null;
	}
	
}
