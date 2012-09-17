//package org.molgenis.datatable.view;
//
//import java.io.OutputStream;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import org.apache.commons.lang.StringUtils;
//import org.molgenis.datatable.controller.Renderers.JQGridRenderer;
//import org.molgenis.datatable.model.FilterableTupleTable;
//import org.molgenis.datatable.model.TableException;
//import org.molgenis.datatable.model.TupleTable;
//import org.molgenis.framework.db.Database;
//import org.molgenis.framework.db.Query;
//import org.molgenis.framework.db.QueryImp;
//import org.molgenis.framework.db.QueryRule;
//import org.molgenis.framework.db.QueryRule.Operator;
//import org.molgenis.framework.ui.ScreenController;
//import org.molgenis.framework.ui.html.HtmlWidget;
//import org.molgenis.model.elements.Field;
//import org.molgenis.util.Tuple;
//
//import com.google.gson.Gson;
//import com.google.gson.internal.StringMap;
//
///**
// * This is an ajax based table view for any TupleTable using JqGrid.
// * 
// * It first renders the html representation.
// * 
// * It also registers to the controller to handle the AJAX requests.
// * 
// */
//public class JQGridTableView extends HtmlWidget
//{
//	// plugin that will handle the ajax requests for us (also our handle to any
//	// other state)
//
//	private ScreenController<?> plugin;
//
//	// table serving the data
//	private TupleTable table;
//
//	// jqgrid config
//	private JQGridConfiguration config;
//
//	// jqgrid navGrid config
//	private JQGridToolbar toolbar;
//
//	/**
//	 * Construct an Ajax html table for EntityTable
//	 * 
//	 * @param id
//	 *            unique id of this html element
//	 * @param plugin
//	 *            the plugin that will host this plugin (and take care of
//	 *            routing the AJAX requests back to this view
//	 * @param table
//	 *            the EntityTable being viewed
//	 * @throws TableException
//	 */
//	public JQGridTableView(String id, ScreenController<?> plugin, TupleTable table) throws TableException
//	{
//		super(id);
//		this.plugin = plugin;
//		this.table = table;
//	}
//
//	@Override
//	public String toHtml()
//	{
//		// configure
//		config = new JQGridConfiguration();
//		toolbar = new JQGridToolbar();
//
//		// url
//		config.url = "molgenis.do?__target=" + plugin.getName() + "&__action=download_json_" + getId();
//
//		// enable sorting and filtering?
//		if (table instanceof FilterableTupleTable)
//		{
//			config.sortable = true;
//			//config.search = true;
//			toolbar.search = true;
//		}
//
//		try
//		{
//			for (Field f : table.getColumns())
//			{
//				config.colName.add(f.getLabel());
//
//				ColModel m = new ColModel();
//				m.name = f.getName();
//				m.index = f.getName();
//				if(table instanceof FilterableTupleTable) m.sortable = true;
//				config.colModel.add(m);
//			}
//		}
//		catch (TableException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		String result = "";
//		Gson gson = new Gson();
//
//		// render the html table
//		result += "<table id=\"" + getId() + "\"></table><div id=\"" + getId() + "_pager\"></div>";
//		result += "<script>jQuery(\"#" + getId() + "\").jqGrid(" + gson.toJson(config) + ");";
//		result += "jQuery(\"#" + getId() + "\").jqGrid('navGrid','#" + getId() + "_pager'," + gson.toJson(toolbar)
//				+ ",{},{},{},{multipleSearch:true});";
//		result +="</script>";
//
//		return result;
//	}
//
//	/**
//	 * This method handles Ajax requests to the plugin where this plugin is
//	 * hosted
//	 */
//	public void handleRequest(Database db, Tuple request, OutputStream out)
//	{
//		try
//		{
//			// this sucks, that we need to renew the table every time...
//			// table.setDb(db);
//
//			// Get the requested page. By default grid sets this to 1.
//			Integer page = request.getInt("page");
//			if (page == null || page < 1) page = 1;
//
//			// get how many rows we want to have into the grid - rowNum
//			// parameter in the grid
//			Integer limit = request.getInt("rows");
//			if (limit == null || limit < 0) limit = 10;
//
//			// get index row - i.e. user click to sort. At first time sortname
//			// parameter -
//			// after that the index from colModel
//			String sortIndex = request.getString("sidx");
//			boolean sortAsc = "asc".equals(request.getString("sord")) ? true : false;
//
//			// update the table with filters (TODO)
//
//			// filtered count
//			int recordCount = table.getCount();
//
//			// calculate the total pages for the query
//			int total_pages = 1;
//			if (recordCount > 0 && limit > 0)
//			{
//				total_pages = (int) Math.ceil((recordCount - 1) / limit) + 1;
//			}
//
//			// if for some reasons the requested page is greater than the total
//			// set the requested page to total page
//			if (page > total_pages) page = total_pages;
//
//			// sorting & filtering, if available
//			if (table instanceof FilterableTupleTable)
//			{
//				FilterableTupleTable fTable = (FilterableTupleTable) table;
//				fTable.getFilters().clear();
//
//				// would be nice if table implemented this api!
//				Query q = new QueryImp();
//
//				// sort
//				if (!"".equals(sortIndex))
//				{
//					if (sortAsc) q.sortASC(sortIndex);
//					else
//						q.sortDESC(sortIndex);
//				}
//
//				fTable.getFilters().addAll(Arrays.asList(q.getRules()));
//
//				// filters
//				fTable.getFilters().addAll(this.convertJqFilters(request));
//			}
//
//			// set limit offset
//			table.setLimitOffset(limit, page * limit - limit);
//
//			// TODO we need to think about the interface here!
//
//			// table.getFilters().addAll(Arrays.asList(q.getRules()));
//
//			// convert visible table to result
//			JQGridResult result = new JQGridResult();
//
//			result.page = page;
//			result.records = recordCount;
//			result.total = total_pages;
//
//			int rowId = 1;
//			for (Tuple tuple : table)
//			{
//				Row row = new Row();
//				row.id = rowId++;
//
//				for (String f : tuple.getFieldNames())
//				{
//					row.cell.add(tuple.getString(f));
//				}
//
//				result.rows.add(row);
//			}
//
//			out.write(new Gson().toJson(result).getBytes());
//		}
//		catch (Exception e)
//		{
//			// TODO what is the error mode of this???
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * Extract the filter rules from the sent jquery request, and convert them
//	 * into Molgenis Query rules.
//	 * 
//	 * @param request
//	 *            A request containing filter rules
//	 * @return A list of QueryRules that represent the filter rules from the
//	 *         request.
//	 */
//	@SuppressWarnings("rawtypes")
//	private List<QueryRule> convertJqFilters(Tuple request)
//	{
//		final String filtersParameter = request.getString("filters");
//		final List<QueryRule> rules = new ArrayList<QueryRule>();
//		if (StringUtils.isNotEmpty(filtersParameter))
//		{
//			final StringMap filters = (StringMap) new Gson().fromJson(filtersParameter, Object.class);
//			final String groupOp = (String) filters.get("groupOp");
//			@SuppressWarnings("unchecked")
//			final ArrayList<StringMap<String>> jsonRules = (ArrayList) filters.get("rules");
//			int ruleIdx = 0;
//			for (StringMap<String> rule : jsonRules)
//			{
//				final String field = rule.get("field");
//				final String op = rule.get("op");
//				final String value = rule.get("data");
//
//				final QueryRule queryRule = convertOperator(field, op, value);
//				rules.add(queryRule);
//
//				final boolean notLast = jsonRules.size() - 1 != ruleIdx++;
//				if (groupOp.equals("OR") && notLast)
//				{
//					rules.add(new QueryRule(Operator.OR));
//				}
//			}
//		}
//		return rules;
//	}
//
//	/**
//	 * Create a {@link QueryRule} based on a jquery operator string, from the
//	 * filter popup/dropdown in the {@link JQGridRenderer} UI. Example:
//	 * Supplying the arguments 'name', 'ne', 'Asia' creates a QueryRule that
//	 * filters for rows where the 'name' column does not equal 'Asia'.
//	 * 
//	 * @param field
//	 *            The field to which to apply the operator
//	 * @param op
//	 *            The operator string (jquery syntax)
//	 * @param value
//	 *            The value (if any) for the right-hand side of the operator
//	 *            expression.
//	 * @return A new QueryRule that represents the supplied jquery expression.
//	 */
//	private QueryRule convertOperator(final String field, final String op, final String value)
//	{
//		// ['eq','ne','lt','le','gt','ge','bw','bn','in','ni','ew','en','cn','nc']
//		QueryRule rule = new QueryRule(field, Operator.EQUALS, value);
//		if (op.equals("eq"))
//		{
//			rule.setOperator(Operator.EQUALS);
//		}
//		else if (op.equals("ne"))
//		{
//			// NOT
//			rule.setOperator(Operator.EQUALS);
//			rule = toNotRule(rule);
//		}
//		else if (op.equals("lt"))
//		{
//			rule.setOperator(Operator.LESS);
//		}
//		else if (op.equals("le"))
//		{
//			rule.setOperator(Operator.LESS_EQUAL);
//		}
//		else if (op.equals("gt"))
//		{
//			rule.setOperator(Operator.GREATER);
//		}
//		else if (op.equals("ge"))
//		{
//			rule.setOperator(Operator.GREATER_EQUAL);
//		}
//		else if (op.equals("bw"))
//		{
//			rule.setValue(value + "%");
//			rule.setOperator(Operator.LIKE);
//		}
//		else if (op.equals("bn"))
//		{
//			// NOT
//			rule.setValue(value + "%");
//			rule.setOperator(Operator.LIKE);
//			rule = toNotRule(rule);
//		}
//		else if (op.equals("in"))
//		{
//			rule.setOperator(Operator.IN);
//		}
//		else if (op.equals("ni"))
//		{
//			// NOT
//			rule.setOperator(Operator.IN);
//			rule = toNotRule(rule);
//		}
//		else if (op.equals("ew"))
//		{
//			rule.setValue("%" + value);
//			rule.setOperator(Operator.LIKE);
//		}
//		else if (op.equals("en"))
//		{
//			// NOT
//			rule.setValue("%" + value);
//			rule.setOperator(Operator.LIKE);
//			rule = toNotRule(rule);
//		}
//		else if (op.equals("cn"))
//		{
//			rule.setValue("%" + value + "%");
//			rule.setOperator(Operator.LIKE);
//		}
//		else if (op.equals("nc"))
//		{
//			// NOT
//			rule.setValue("%" + value + "%");
//			rule.setOperator(Operator.LIKE);
//			rule = toNotRule(rule);
//		}
//		else
//		{
//			throw new IllegalArgumentException(String.format("Unkown Operator: %s", op));
//		}
//		return rule;
//	}
//
//	/**
//	 * Add a 'NOT' operator to a particular rule.
//	 * 
//	 * @param rule
//	 *            The rule to negate.
//	 * @return A new {@link QueryRule} which is the negation of the supplied
//	 *         rule.
//	 */
//	private QueryRule toNotRule(QueryRule rule)
//	{
//		return new QueryRule(Operator.NOT, rule);
//	}
//
//	/** Available JqGrid configuration settings */
//	@SuppressWarnings("unused")
//	private class JQGridConfiguration
//	{
//		public String url;
//		public String datatype = "json";
//		public List<String> colName = new ArrayList<String>();
//		public List<ColModel> colModel = new ArrayList<ColModel>();
//		public int rowNum = 10;
//		public Integer[] rowList = new Integer[]
//		{ 10, 20, 30 };
//		public String pager = getId() + "_pager";
//		public boolean viewrecords = true;
//		public String sortorder = "desc";
//		public String caption = getLabel();
//		public boolean autowidth = true;
//		public boolean sortable = false;
//		//public boolean search = false;
//	}
//
//	/** JqGrid representation of toolbar */
//	@SuppressWarnings("unused")
//	private class ColModel
//	{
//		public String name;
//		public String index;
//		public int width = 100;
//		public boolean sortable = false;
//	}
//
//	/** JqGrid representation of fields */
//	@SuppressWarnings("unused")
//	private class JQGridToolbar
//	{
//		public boolean del = false;
//		public boolean add = false;
//		public boolean edit = false;
//		public boolean search = false;
//	}
//
//	/** JqGrid representation of the AJAX response */
//	@SuppressWarnings("unused")
//	private class JQGridResult
//	{
//		// current page
//		public int page;
//		// total number of pages
//		public int total;
//		// currently visible records
//		public int records;
//		// records
//		public List<Row> rows = new ArrayList<Row>();
//	}
//
//	/** JqGrid representation of a row */
//	@SuppressWarnings("unused")
//	private class Row
//	{
//		public int id;
//		public List<String> cell = new ArrayList<String>();
//	}
//}
