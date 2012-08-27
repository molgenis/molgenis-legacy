package org.molgenis.datatable.view.JQGridJSObjects;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Class wrapping the results of a jqGrid query. To be serialized by Gson,
 * hence no accessors necessary for private datamembers.
 */
public class JQGridResult {
	public final int page;
	public final int total;
	public final int records;
	public final ArrayList<LinkedHashMap<String, String>> rows = new ArrayList<LinkedHashMap<String, String>>();

	public JQGridResult(int page, int total, int records) {
		this.page = page;
		this.total = total;
		this.records = records;
	}
}