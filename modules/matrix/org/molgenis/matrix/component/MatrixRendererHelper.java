package org.molgenis.matrix.component;

import java.util.HashMap;


public class MatrixRendererHelper {
	
	static final public String MATRIX_COMPONENT_REQUEST_PREFIX = "matrix_component_request_prefix_";
	static final int ROW_STOP_DEFAULT = 10;
	static final int COL_STOP_DEFAULT = 5;
	
	static HashMap<String, String> operators() {
		HashMap<String, String> ops = new HashMap<String, String>();
		ops.put("GREATER", "&gt;");
		ops.put("GREATER_EQUAL", "&gt;=");
		ops.put("LESS", "&lt;");
		ops.put("LESS_EQUAL", "&lt;=");
		ops.put("EQUALS", "==");
		ops.put("SORTASC", "sort asc");
		ops.put("SORTDESC", "sort desc");
		return ops;
	}


	
}
