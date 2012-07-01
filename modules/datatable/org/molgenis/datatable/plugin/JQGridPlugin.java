package org.molgenis.datatable.plugin;



import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.molgenis.MolgenisFieldTypes.FieldTypeEnum;
import org.molgenis.datatable.controller.Renderers.JQGridRenderer;
import org.molgenis.datatable.controller.Renderers.Renderer;
import org.molgenis.datatable.model.QueryDSLTable;
import org.molgenis.datatable.model.TableException;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.datatable.view.JQGridView;
import org.molgenis.datatable.view.ViewFactory;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.server.MolgenisResponse;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.model.elements.Field;
import org.molgenis.util.HandleRequestDelegationException;
import org.molgenis.util.Tuple;

import com.google.gson.Gson;
import com.google.gson.internal.StringMap;
import com.mysema.query.sql.MySQLTemplates;
import com.mysema.query.sql.RelationalPath;
import com.mysema.query.sql.SQLQuery;
import com.mysema.query.sql.SQLQueryImpl;
import com.mysema.query.sql.SQLTemplates;
import com.mysema.query.types.Expression;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.path.PathBuilder;


/**
 * View data in a matrix.
 */
public class JQGridPlugin extends EasyPluginController<ScreenModel>
{
	private static final long serialVersionUID = 8804579908239186037L;
	private Container container = new Container();
	private JQGridView gridView;
	
	private TupleTable tupleTable;
	
	public JQGridPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	@Override
	public void reload(Database db)
	{
		try
		{
	        final Connection conn = db.getConnection();
	        final SQLTemplates dialect = new MySQLTemplates(); // SQL-dialect
	        final SQLQuery query = new SQLQueryImpl(conn, dialect);
	        
	        PathBuilder<RelationalPath> country = new PathBuilder<RelationalPath>(RelationalPath.class, "Country");
	        PathBuilder<RelationalPath> city = new PathBuilder<RelationalPath>(RelationalPath.class, "City");
	        
	        List<Expression> select = Arrays.<Expression>asList(country.get("name"), city.get("name"));
	        List<Expression> from = Arrays.<Expression>asList(country, city);
	        List<Predicate> where = Arrays.<Predicate>asList(country.get("code").eq(city.get("countrycode")));
			
			tupleTable = new QueryDSLTable(query, select, from, where);
			
			gridView = new JQGridView("myGrid", tupleTable.getColumns());
			container.add(gridView);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * The options for export ranges:
	 *<ul>
	 * <li>GRID		: Only the data currently displayed by the grid</li>
	 * <li>ALL		: All data</li>
	 * <li>UNKNOWN	: unknown.</li>
	 * </ul>
	 */

	private enum ExportRange {
		GRID, 
		ALL,
		UNKOWN
	}
	

	/**
	 * Class wrapping the results of a jqGrid query. To be serialized by Gson, hence no accessors necessary for private datamembers.
	 */
	public static class JQGridResult
	{
		@SuppressWarnings("unused")
		private final int page;
		@SuppressWarnings("unused")
		private final int total;
		@SuppressWarnings("unused")
		private final int records;

		private ArrayList<LinkedHashMap<String, String>> rows = new ArrayList<LinkedHashMap<String, String>>();

		public JQGridResult(int page, int total, int records)
		{
			this.page = page;
			this.total = total;
			this.records = records;
		}
	}
	
	/**
	 * Handle a particular {@link MolgenisRequest}, and encode any resulting renderings/exports into a {@link MolgenisResponse}.
	 * Particulars handled:
	 * <ul>
	 *  <li>Select the appropriate view towards which to export/render.</li>
	 *  <li>Apply proper sorting and filter rules.</li>
	 *  <li>Wrap the desired data source in the appropriate instantiation of {@link TupleTable}.</li>
	 *  <li>Select and render the data.</li>
	 * </ul>
	 */
	
	
	@Override
	public void handleRequest(Database db, Tuple request) throws HandleRequestDelegationException {

	
//	@Override
//	public void handleRequest(MolgenisRequest request, MolgenisResponse response) throws ParseException,
//			DatabaseException, IOException
//	{
		try
		{	
			final ExportRange exportSelection = 
				StringUtils.isNotEmpty(request.getString("exportSelection")) ?			
						ExportRange.valueOf(request.getString("exportSelection")) : 
						ExportRange.UNKOWN;
			
			final int limit = 	request.getInt("rows");
			final String sidx = request.getString("sidx");
			final String sord = request.getString("sord");			
			
			//add filter rules
			final List<QueryRule> rules = addFilterRules(request);
			
			int rowCount = -1;
			rowCount = tupleTable.getRowCount();				
			int totalPages = 1;
			totalPages = (int) Math.ceil(rowCount / limit);
			int page = Math.min(request.getInt("page"), totalPages);
			int offset = Math.max(limit * page - limit, 0);			
			
			//add query Rules
			if(exportSelection != ExportRange.ALL) {
				rules.addAll(Arrays.asList(new QueryRule(Operator.LIMIT, limit), new QueryRule(Operator.OFFSET, offset)));
			}
			addSortRules(sidx, sord, rules);			
						
			renderData(((MolgenisRequest)request).getRequest(), ((MolgenisRequest)request).getResponse(), page, totalPages, tupleTable);

			tupleTable.close();
		}
		catch (Exception e)
		{
			throw new HandleRequestDelegationException();
		}
	}

	/**
	 * Render a particular subset of data from a {@link TupleTable} to a particular {@link Renderer}. 
	 * @param request		The request encoding the particulars of the rendering to be done.
	 * @param response		The response into which the view is rendered.
	 * @param page			The selected page (only relevant for {@link JQGridRenderer} rendering)
	 * @param totalPages	The total number of pages (only relevant for {@link JQGridRenderer} rendering)
	 * @param tupleTable	The table from which to render the data.
	 */
	private void renderData(HttpServletRequest request, HttpServletResponse response, int page, int totalPages,
			final TupleTable tupleTable) throws TableException
	{
		
		String strViewType = (String)request.getAttribute("viewType");
		if(StringUtils.isEmpty(strViewType)) { //strange that the grid doesn't submit it in first load!
			strViewType = "JQ_GRID";
		}
		try {
			final String viewFactoryClassName = (String)request.getAttribute("viewFactoryClassName");
			final ViewFactory viewFactory = (ViewFactory) Class.forName(viewFactoryClassName).newInstance();
			final Renderer view = viewFactory.createView(strViewType);
			view.export(response, (String)request.getAttribute("caption"), this, tupleTable, totalPages, page);
		} catch (Exception e) {
			throw new TableException(e);
		}
	}

	/**
	 * Function to build a datastructure filled with rows from a {@link TupleTable}, to be 
	 * serialised by Gson and displayed from there by a jqGrid.
	 * @param rowCount The number of rows to select.
	 * @param totalPages The total number of pages of data (ie. dependent on size of dataset and nr. of rows per page)
	 * @param page The selected page.
	 * @param table The Tupletable from which to read the data.
	 * @return
	 */
	public static JQGridResult buildJQGridResults(final int rowCount, final int totalPages, final int page,
			final TupleTable table) throws TableException
	{
		final JQGridResult result = new JQGridResult(page, totalPages, rowCount);
		for (final Tuple row : table)
		{
			final LinkedHashMap<String, String> rowMap = new LinkedHashMap<String, String>();

			final List<String> fieldNames = row.getFieldNames();
			for (final String fieldName : fieldNames)
			{
				final String rowValue = !row.isNull(fieldName) ? row.getString(fieldName) : "null";
				rowMap.put(fieldName, rowValue); // TODO encode to HTML
			}
			result.rows.add(rowMap);
		}
		table.close();
		return result;
	}

	/**
	 * Extract the filter rules from the sent jquery request, and convert them into Molgenis Query rules.
	 * @param request A request containing filter rules
	 * @return A list of QueryRules that represent the filter rules from the request.
	 */
	@SuppressWarnings("rawtypes")
	private List<QueryRule> addFilterRules(Tuple request)
	{
		final String filtersParameter = request.getString("filters");		
		final List<QueryRule> rules = new ArrayList<QueryRule>();
		if (StringUtils.isNotEmpty(filtersParameter))
		{
			final StringMap filters = (StringMap) new Gson().fromJson(filtersParameter, Object.class);
			final String groupOp = (String) filters.get("groupOp");
			@SuppressWarnings("unchecked")
			final ArrayList<StringMap<String>> jsonRules = (ArrayList) filters.get("rules");
			int ruleIdx = 0;
			for (StringMap<String> rule : jsonRules)
			{
				final String field = rule.get("field");
				final String op = rule.get("op");
				final String value = rule.get("data");


				final QueryRule queryRule = convertOperator(field, op, value);
				rules.add(queryRule);

				final boolean notLast = jsonRules.size() - 1 != ruleIdx++;				
				if (groupOp.equals("OR") && notLast)
				{
					rules.add(new QueryRule(Operator.OR));
				}
			}
		}
		return rules;
	}
	
	/**
	 * Create a {@link QueryRule} based on a jquery operator string, from the filter popup/dropdown in the {@link JQGridRenderer} UI.
	 * Example: Supplying the arguments 'name', 'ne', 'Asia' creates a QueryRule that filters for rows where 
	 * the 'name' column does not equal 'Asia'.
	 * @param field The field to which to apply the operator
	 * @param op The operator string (jquery syntax)
	 * @param value The value (if any) for the right-hand side of the operator expression.
	 * @return A new QueryRule that represents the supplied jquery expression.
	 */
	private QueryRule convertOperator(final String field, final String op, final String value)
	{
		// ['eq','ne','lt','le','gt','ge','bw','bn','in','ni','ew','en','cn','nc']
		QueryRule rule = new QueryRule(field, Operator.EQUALS, value);
		if (op.equals("eq"))
		{
			rule.setOperator(Operator.EQUALS);
		}
		else if (op.equals("ne"))
		{
			// NOT
			rule.setOperator(Operator.EQUALS);			
			rule = toNotRule(rule);
		}
		else if (op.equals("lt"))
		{
			rule.setOperator(Operator.LESS);			
		}
		else if (op.equals("le"))
		{
			rule.setOperator(Operator.LESS_EQUAL);
		}
		else if (op.equals("gt"))
		{
			rule.setOperator(Operator.GREATER);
		}
		else if (op.equals("ge"))
		{
			rule.setOperator(Operator.GREATER_EQUAL);
		}
		else if (op.equals("bw"))
		{			
			rule.setValue(value + "%");
			rule.setOperator(Operator.LIKE);
		}
		else if (op.equals("bn"))
		{
			// NOT			
			rule.setValue(value + "%");
			rule.setOperator(Operator.LIKE);
			rule = toNotRule(rule);
		}
		else if (op.equals("in"))
		{
			rule.setOperator(Operator.IN);
		}
		else if (op.equals("ni"))
		{
			// NOT
			rule.setOperator(Operator.IN);
			rule = toNotRule(rule);
		}
		else if (op.equals("ew"))
		{			
			rule.setValue("%" + value);
			rule.setOperator(Operator.LIKE);
		}
		else if (op.equals("en"))
		{
			// NOT			
			rule.setValue("%" + value);
			rule.setOperator(Operator.LIKE);
			rule = toNotRule(rule);
		}
		else if (op.equals("cn"))
		{			
			rule.setValue("%" + value + "%");
			rule.setOperator(Operator.LIKE);
		}
		else if (op.equals("nc"))
		{
			// NOT
			rule.setValue("%" + value + "%");
			rule.setOperator(Operator.LIKE);
			rule = toNotRule(rule);
		} else {
			throw new IllegalArgumentException(String.format("Unkown Operator: %s", op));
		}
		return rule;
	}
	
	/**
	 * Get a string that represents the type of a {@link Field} in jquery syntax.
	 * @param f The field to convert
	 * @return A string representing the field's type in jquery syntax.
	 */
	public String getJQGirdColumnType(Field f) {
		final FieldTypeEnum fieldType = f.getType().getEnumType();
		switch(fieldType) {
			case DATE: return ",date: true";
			case DATE_TIME: return ",date: true, time: true";
			case DECIMAL: return ",number: 'true'";
			//case ENUM: return "";
			case INT: return ",integer: 'true'";
			case LONG: return ",integer: 'true'";
			default:
				return ""; //handle as text
		}
	}

	/**
	 * Add a 'NOT' operator to a particular rule.
	 * @param rule The rule to negate.
	 * @return A new {@link QueryRule} which is the negation of the supplied rule.
	 */
	private QueryRule toNotRule(QueryRule rule)
	{
		return new QueryRule(Operator.NOT, rule);
	}

	/**
	 * Add sorting rules to the rendered data.
	 * @param sidx The column index by which to sort
	 * @param sord The order in which to sort (ascending/descending)
	 * @param rules The already-applied rules, to which the new sorting rule will be added.
	 */
	private void addSortRules(final String sidx, final String sord, final List<QueryRule> rules)
	{
		if (StringUtils.isNotEmpty(sidx))
		{
			final QueryRule sort = new QueryRule();
			sort.setValue(sidx);
			sort.setOperator(StringUtils.equals(sord, "asc") ? Operator.SORTASC : Operator.SORTDESC);
			rules.add(sort);
		}
	}

	public ScreenView getView()
    {
    	return container;
    }

	public String render() {
		return gridView.render();
	}
}
