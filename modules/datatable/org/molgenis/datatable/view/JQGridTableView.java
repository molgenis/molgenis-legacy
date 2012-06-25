package org.molgenis.datatable.view;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.molgenis.datatable.model.EntityTable;
import org.molgenis.datatable.model.FilterableTupleTable;
import org.molgenis.datatable.model.TableException;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryImp;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.html.HtmlWidget;
import org.molgenis.model.elements.Field;
import org.molgenis.util.Tuple;

import com.google.gson.Gson;

/**
 * This is an ajax based table view for any TupleTable using JqGrid.
 * 
 * It first renders the html representation.
 * 
 * It also registers to the controller to handle the AJAX requests.
 * 
 */
public class JQGridTableView extends HtmlWidget
{
	// plugin that will handle the ajax requests for us (also our handle to any
	// other state)

	private ScreenController<?> plugin;

	// table serving the data
	private FilterableTupleTable table;

	// jqgrid config
	private JQGridConfiguration config;

	/**
	 * Construct an Ajax html table for TupleTable
	 * 
	 * @param id
	 *            unique id of this html element
	 * @param plugin
	 *            the plugin that will host this plugin (and take care of
	 *            routing the AJAX requests back to this view
	 * @param table
	 *            the TupleTable being viewed
	 * @throws TableException
	 */
	public JQGridTableView(String id, ScreenController<?> plugin, EntityTable table) throws TableException
	{
		super(id);
		this.plugin = plugin;
		this.table = table;

		// configure
		config = new JQGridConfiguration();

		config.url = "molgenis.do?__target=" + plugin.getName() + "&__action=download_json_" + getId();

		for (Field f : table.getColumns())
		{
			config.colName.add(f.getLabel());

			ColModel m = new ColModel();
			m.name = f.getName();
			m.index = f.getName();
			config.colModel.add(m);
		}
	}

	@Override
	public String toHtml()
	{
		String result = "";

		// render the html table
		result += "<table id=\"" + getId() + "\"></table><div id=\"" + getId() + "_pager\"></div>";
		result += "<script>jQuery(\"#" + getId() + "\").jqGrid(" + new Gson().toJson(config) + ");";
		result += "jQuery(\"#" + getId() + "\").jqGrid('navGrid','#" + getId()
				+ "_pager',{edit:false,add:false,del:false});</script>";

		return result;
	}

	/**
	 * This method handles Ajax requests to the plugin where this plugin is
	 * hosted
	 */
	public void handleRequest(Database db, Tuple request, OutputStream out)
	{
		try
		{
			// this sucks, that we need to renew the table every time...
			//table.setDb(db);

			// Get the requested page. By default grid sets this to 1.
			Integer page = request.getInt("page");
			if (page == null || page < 1) page = 1;

			// get how many rows we want to have into the grid - rowNum
			// parameter in the grid
			Integer limit = request.getInt("rows");
			if (limit == null || limit < 0) limit = 10;

			// get index row - i.e. user click to sort. At first time sortname
			// parameter -
			// after that the index from colModel
			String sortIndex = request.getString("sidx");
			boolean sortAsc = "asc".equals(request.getString("sord")) ? true : false;

			// update the table with filters (TODO)

			// filtered count
			int recordCount = table.getRowCount();

			// calculate the total pages for the query
			int total_pages = 1;
			if (recordCount > 0 && limit > 0)
			{
				total_pages = (int) Math.ceil( (recordCount - 1) / limit) + 1;
			}

			// if for some reasons the requested page is greater than the total
			// set the requested page to total page
			if (page > total_pages) page = total_pages;

			// todo: implement filters
			Query q = new QueryImp();
			q.limit(limit);
			q.offset(page * limit - limit);

			// sorting
			if (!"".equals(sortIndex))
			{
				if (sortAsc) q.sortASC(sortIndex);
				else
					q.sortDESC(sortIndex);
			}

			// TODO we need to think about the interface here!
			table.getFilters().clear();
			table.getFilters().addAll(Arrays.asList(q.getRules()));

			// convert visible table to result
			JQGridResult result = new JQGridResult();
			
			result.page = page;
			result.records = recordCount;
			result.total = total_pages;

			int rowId = 1;
			for (Tuple tuple : table)
			{
				Row row = new Row();
				row.id = rowId++;

				for (String f : tuple.getFieldNames())
				{
					row.cell.add(tuple.getString(f));
				}

				result.rows.add(row);
			}

			out.write(new Gson().toJson(result).getBytes());
		}
		catch (Exception e)
		{
			// TODO what is the error mode of this???
			e.printStackTrace();
		}
	}

	/** Available JqGrid configuration settings */
	@SuppressWarnings("unused")
	private class JQGridConfiguration
	{
		public String url;
		public String datatype = "json";
		public List<String> colName = new ArrayList<String>();
		public List<ColModel> colModel = new ArrayList<ColModel>();
		public int rowNum = 10;
		public Integer[] rowList = new Integer[]
		{ 10, 20, 30 };
		public String pager = getId() + "_pager";
		public boolean viewrecords = true;
		public String sortorder = "desc";
		public String caption = getLabel();
	}

	/** JqGrid representation of fields */
	@SuppressWarnings("unused")
	private class ColModel
	{
		public String name;
		public String index;
		public int width = 100;
	}

	/** JqGrid representation of the AJAX response */
	@SuppressWarnings("unused")
	private class JQGridResult
	{
		// current page
		public int page;
		// total number of pages
		public int total;
		// currently visible records
		public int records;
		// records
		public List<Row> rows = new ArrayList<Row>();
	}

	/** JqGrid representation of a row */
	@SuppressWarnings("unused")
	private class Row
	{
		public int id;
		public List<String> cell = new ArrayList<String>();
	}
}
