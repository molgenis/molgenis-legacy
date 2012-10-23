package org.molgenis.framework.tupletable.view.JQGridJSObjects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.tupletable.DatabaseTupleTable;
import org.molgenis.framework.tupletable.EditableTupleTable;
import org.molgenis.framework.tupletable.FilterableTupleTable;
import org.molgenis.framework.tupletable.TableException;
import org.molgenis.framework.tupletable.TupleTable;
import org.molgenis.model.elements.Field;

import com.google.gson.Gson;

public class JQGridConfiguration
{
	public String id;

	/** ajax url */
	public final String url;

	public final String editurl;

	/** formatting of the ajax service data */
	public final String datatype = "json";

	/** id of pager diff (== toolbar at bottom) */
	public final String pager;

	/** labels of the columns */
	public List<String> colNames = new ArrayList<String>();

	/** definitions of the columns */
	public List<JQGridColModel> colModel = new ArrayList<JQGridColModel>();

	/** current limit = number of rows to show */
	public int rowNum = 10;

	/** choices of alternative rowNum values */
	public Integer[] rowList = new Integer[]
	{ 10, 20, 30 };

	/** indicates whether we want to show total records from query in page bar */
	public boolean viewrecords = true;

	/** the caption of this table */
	public String caption = "jqGrid View";

	public boolean autowidth = true;

	/** whether this grid is sortable */
	public String sortname = "";

	/** default sorting order */
	public String sortorder = "desc";

	/** default height */
	public String height = "232px";

	/** the total column count **/
	public int totalColumnCount;

	/** the current column offset **/
	public int colOffset;

	/** the current coliumn limit, so the nr of visible columns **/
	public int colLimit;

	/** list of hidden column names **/
	public List<String> hiddenColumns;

	/** Wether the first column is 'fixed', must always be visible **/
	public boolean firstColumnFixed;

	/** virtual scrolling */
	// public int scroll = 1;

	/** preload filter settings */
	// note: this is a string value
	// public String postData =
	// "{filters : '{\"groupOp\":\"AND\",\"rules\":[{\"field\":\"Country.Code\",\"op\":\"eq\",\"data\":\"AGO\"}]}'}";
	public JQGridPostData postData = new JQGridPostData();

	/** ???? */
	public HashMap<String, Object> jsonReader = new HashMap<String, Object>();

	// public String postData = "viewType : JQ_GRID";

	public JQGridSettings settings = new JQGridSettings();

	public JQGridSearchOptions searchOptions = new JQGridSearchOptions();

	@SuppressWarnings("unchecked")
	public Object[] toolbar = Arrays.asList(true, "top").toArray();

	public JQGridConfiguration(Database db, String id, String idField, String url, String caption, TupleTable tupleTable)
			throws TableException
	{
		if(tupleTable instanceof DatabaseTupleTable)
		{
			((DatabaseTupleTable) tupleTable).setDb(db);
		}
		
		this.id = id;
		this.pager = "#" + id + "_pager";
		this.url = url;
		this.editurl = url;
		this.caption = caption;
		this.totalColumnCount = tupleTable.getColCount();
		this.colOffset = tupleTable.getColOffset();
		this.colLimit = tupleTable.getColLimit();
		this.firstColumnFixed = tupleTable.isFirstColumnFixed();

		// "{repeatitems: false, id: \"Code\"}"
		jsonReader.put("repeatitems", false);
		jsonReader.put("id", idField);

		this.hiddenColumns = tupleTable.getHiddenColumnNames();

		if (tupleTable instanceof FilterableTupleTable)
		{
			// sortable = true;
			settings.search = true;
		}

		if (tupleTable instanceof EditableTupleTable)
		{
			settings.add = true;
			settings.edit = true;
			settings.del = true;
		}

		//set col names
		for (final Field f : tupleTable.getColumns())
		{
			colModel.add(new JQGridColModel(f));
		}


		//System.out.println(new Gson().toJson(settings));
	}

	public JQGridConfiguration(String id, String url, String caption)
	{
		this.id = id;
		pager = "#" + id + "Pager";
		this.url = url;
		this.editurl = url;
		this.caption = caption;
	}
}