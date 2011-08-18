package org.molgenis.matrix.component.general;

import java.util.HashMap;


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
		ops.put("EQUALS", "==");
		ops.put("SORTASC", "sort asc");
		ops.put("SORTDESC", "sort desc");
		//ops.put("LIMIT", "sort desc");
		//ops.put("OFFSET", "sort desc");  ->  don't add here, just do by paging?
		return ops;
	}


	
}
