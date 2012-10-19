package org.molgenis.framework.tupletable.view.JQGridJSObjects;

import org.molgenis.util.Tuple;

import com.google.gson.Gson;

/**
 * This is the request that we receive from JQGrid via JSON. Also, this can be
 * used to preload a 'saved' filters/sort/paging setting.
 */
public class JQGridPostData
{
	public JQGridPostData(Tuple request)
	{
		rows = request.getInt("rows");
		page = request.getInt("page");
		sidx = request.getString("sidx");
		sord = request.getString("sord");
		colPage = request.getInt("colPage");

		this.filters = new Gson().fromJson(request.getString("filters"), JQGridFilter.class);

		if (filters == null)
		{
			// Check simple, single search
			String searchString = request.getString("searchString");
			String searchField = request.getString("searchField");
			String searchOper = request.getString("searchOper");

			if ((searchString != null) && (searchField != null) && (searchOper != null))
			{
				JQGridRule.JQGridOp op = JQGridRule.JQGridOp.valueOf(searchOper);
				filters = new JQGridFilter();
				filters.rules.add(new JQGridRule(searchField, op, searchString));
			}
		}
	}

	public JQGridPostData()
	{
	}

	public JQGridFilter filters = new JQGridFilter();
	/** sort order */
	public String sord = null;
	/** sort index field name */
	public String sidx = null;
	/** number of rows to show */
	public int rows = 0;
	/** page offset, each page being rows long */
	public int page = 0;
	public int colPage = 1;
}
