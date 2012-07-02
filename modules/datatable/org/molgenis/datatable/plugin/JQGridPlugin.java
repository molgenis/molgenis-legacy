package org.molgenis.datatable.plugin;

import java.io.OutputStream;
import java.sql.Connection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.molgenis.MolgenisFieldTypes.FieldTypeEnum;
import org.molgenis.datatable.controller.Renderers.JQGridRenderer;
import org.molgenis.datatable.controller.Renderers.Renderer;
import org.molgenis.datatable.model.JoinQueryTable;
import org.molgenis.datatable.model.QueryTable;
import org.molgenis.datatable.model.TableException;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.datatable.view.JQGridView;
import org.molgenis.datatable.view.ViewFactory;
import org.molgenis.fieldtypes.DecimalField;
import org.molgenis.fieldtypes.StringField;
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
import com.mysema.query.types.expr.BooleanExpression;
import com.mysema.query.types.expr.ComparableExpressionBase;
import com.mysema.query.types.expr.NumberExpression;
import com.mysema.query.types.expr.SimpleExpression;
import com.mysema.query.types.expr.StringExpression;
import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.PathBuilder;
import com.mysema.query.types.path.StringPath;

/**
 * View data in a matrix.
 */
public class JQGridPlugin extends EasyPluginController<ScreenModel>
{
	public interface TupleTableBuilder
	{
		TupleTable create(Database db, Tuple request) throws TableException;
	}

	private static final long serialVersionUID = 8804579908239186037L;
	private Container container = new Container();
	private JQGridView gridView;

	private final TupleTableBuilder tupleTableBuilder;

	public JQGridPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
		// tupleTableBuilder = new TupleTableBuilder()
		// {
		// @Override
		// public TupleTable create(Database db) throws TableException
		// {
		// return new JdbcTable(db,
		// "SELECT Name, Continent, SurfaceArea, Population FROM Country",
		// Collections.<QueryRule>emptyList());
		// }
		// };

		tupleTableBuilder = new TupleTableBuilder()
		{
			@Override
			public TupleTable create(Database db, Tuple request) throws TableException
			{
				try
				{
					final Connection connection = db.getConnection();

					final SQLTemplates dialect = new MySQLTemplates();
					final SQLQueryImpl query = new SQLQueryImpl(connection, dialect);

					boolean joinTable = false;
					if(joinTable) {
						List<String> tableNames = new ArrayList<String>();
						final List<String> columnNames = new ArrayList<String>();
						getTableAndColumnNames(request, tableNames, columnNames);
						
						if(CollectionUtils.isEmpty(tableNames)) {
							tableNames = Arrays.asList("Country", "City");
						}
						
						final List<JoinQueryTable.Join> joins = Arrays.asList(new JoinQueryTable.Join("Country.Code", "City.CountryCode"));
						return new JoinQueryTable(query, tableNames, joins, db);						
					} 
					
					PathBuilder<RelationalPath> country = new PathBuilder<RelationalPath>(RelationalPath.class,
							"Country");
					PathBuilder<RelationalPath> city = new PathBuilder<RelationalPath>(RelationalPath.class, "City");
					query.from(country, city).where(country.get("code").eq(city.get("countrycode")));

					final NumberPath<Integer> countryPopulation = country.get(new NumberPath<Integer>(Integer.class,
							"Population"));
					final NumberPath<Integer> cityPopulation = city.get(new NumberPath<Integer>(Integer.class,
							"Population"));
					
					final NumberExpression<Double> cityPopulationRatio = cityPopulation.divide(countryPopulation);
					query.where(country.get("code").eq(city.get("countrycode")));
					//query.limit(10);
					query.orderBy(cityPopulationRatio.desc());

					// create select
					Field countryName = new Field("Country.Name");
					countryName.setType(new StringField());
					Field cityName = new Field("City.Name");
					cityName.setType(new StringField());
					Field ratio = new Field("ratio");
					ratio.setType(new DecimalField());
					
					LinkedHashMap<String, SimpleExpression<? extends Object>> selectMap = new LinkedHashMap<String, SimpleExpression<? extends Object>>();
					selectMap.put("Country.Name", country.get(new StringPath("name")));
					selectMap.put("City.Name", city.get(new StringPath("name")));
					selectMap.put("ratio", cityPopulationRatio);
					List<Field> columns = Arrays.asList(countryName, cityName, ratio);
					final QueryTable queryTable = new QueryTable(query, selectMap, columns);
					return queryTable;
				}
				catch (Exception ex)
				{
					throw new TableException(ex);
				}
			}

			private void getTableAndColumnNames(Tuple request, List<String> inTableNames, List<String> inColumnNames)
			{
				if(request != null) {
					@SuppressWarnings("unchecked")
					final List<String> columns = (List<String>) new Gson().fromJson((String) request.getObject("colNames"), Object.class);
					for(final String column : columns) {
						if(StringUtils.contains(column, ".")) {
							final String tableName = StringUtils.substringBefore(column, ".");
							final String columnName = StringUtils.substringAfter(column, ".");
							if(!inTableNames.contains(tableName)) {
								inTableNames.add(tableName);	
							}
							inColumnNames.add(columnName);
						} else {
							inColumnNames.add(column);
						}
					}
				}
			}
		};

		try
		{

		}
		catch (Exception ex)
		{
			throw new RuntimeException();
			// ex.printStackTrace();
		}

	}

	@Override
	public void reload(Database db)
	{
		try
		{
			final TupleTable tupleTable = tupleTableBuilder.create(db, null);
			// strange way to retrieve columns! Sould be in a ajax call when
			// grid is constructed!
			gridView = new JQGridView("myGrid", tupleTable);
			tupleTable.close();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * The options for export ranges:
	 * <ul>
	 * <li>GRID : Only the data currently displayed by the grid</li>
	 * <li>ALL : All data</li>
	 * <li>UNKNOWN : unknown.</li>
	 * </ul>
	 */

	private enum ExportRange
	{
		GRID, ALL, UNKOWN
	}

	/**
	 * Class wrapping the results of a jqGrid query. To be serialized by Gson,
	 * hence no accessors necessary for private datamembers.
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
	 * Handle a particular {@link MolgenisRequest}, and encode any resulting
	 * renderings/exports into a {@link MolgenisResponse}. Particulars handled:
	 * <ul>
	 * <li>Select the appropriate view towards which to export/render.</li>
	 * <li>Apply proper sorting and filter rules.</li>
	 * <li>Wrap the desired data source in the appropriate instantiation of
	 * {@link TupleTable}.</li>
	 * <li>Select and render the data.</li>
	 * </ul>
	 */
	@Override
	public Show handleRequest(Database db, Tuple request, OutputStream out) throws HandleRequestDelegationException
	{
		try
		{
			final ServletContext context = ((MolgenisRequest)request).getRequest().getSession().getServletContext();
			
			final TupleTable tupleTable = tupleTableBuilder.create(db, request);

			final int limit = request.getInt("rows");
//			final String sidx = request.getString("sidx");
//			final String sord = request.getString("sord");

			// add filter rules
			// final List<QueryRule> rules =
			// createQueryRulesFromJQGridRequest(request);
			// tupleTable.setQueryRules(rules);

			addFilters(request, (QueryTable) tupleTable);
			

			int rowCount = -1;
			rowCount = tupleTable.getRowCount();
			tupleTable.close(); // Not nice! We should fix this!
			int totalPages = 1;
			totalPages = (int) Math.ceil(rowCount / limit);
			int page = Math.min(request.getInt("page"), totalPages);
			int offset = Math.max(limit * page - limit, 0);

			// add query Rules
			// if (exportSelection != ExportRange.ALL)
			// {
			// rules.addAll(Arrays
			// .asList(new QueryRule(Operator.LIMIT, limit), new
			// QueryRule(Operator.OFFSET, offset)));
			// }
			// addSortRules(sidx, sord, rules);
			//
			// tupleTable.setQueryRules(rules);
			addSortOrderLimitOffset(request, (QueryTable) tupleTable, offset);
			

			renderData(context, ((MolgenisRequest) request).getRequest(), ((MolgenisRequest) request).getResponse(), page,
					totalPages, tupleTable);

			tupleTable.close();
		}
		catch (Exception e)
		{
			throw new HandleRequestDelegationException(e);
		}
		return null;
	}

	private void addSortOrderLimitOffset(Tuple request, QueryTable queryTable, int offset)
	{
		final ExportRange exportSelection = StringUtils.isNotEmpty(request.getString("exportSelection")) ? ExportRange
				.valueOf(request.getString("exportSelection")) : ExportRange.UNKOWN;
		
		final int limit = request.getInt("rows");
		final String sidx = request.getString("sidx");
		final String sord = request.getString("sord");
		
		final SQLQuery query = queryTable.getQuery();
		final LinkedHashMap<String, SimpleExpression<? extends Object>> selectMap = queryTable.getSelect();
		
		if(exportSelection != ExportRange.ALL) {
			query.limit(limit);
			query.offset(offset);
		}
		
		
		ComparableExpressionBase<?> sortColumn = ((ComparableExpressionBase<?>)selectMap.get(sidx));
		if(sord.equalsIgnoreCase("ASC")) {
			query.orderBy(sortColumn.asc());	
		} else {
			query.orderBy(sortColumn.desc());
		}		
	}

	/**
	 * Render a particular subset of data from a {@link TupleTable} to a
	 * particular {@link Renderer}.
	 * 
	 * @param request
	 *            The request encoding the particulars of the rendering to be
	 *            done.
	 * @param response
	 *            The response into which the view is rendered.
	 * @param page
	 *            The selected page (only relevant for {@link JQGridRenderer}
	 *            rendering)
	 * @param totalPages
	 *            The total number of pages (only relevant for
	 *            {@link JQGridRenderer} rendering)
	 * @param tupleTable
	 *            The table from which to render the data.
	 */
	private void renderData(ServletContext context, HttpServletRequest request, HttpServletResponse response, int page, int totalPages,
			final TupleTable tupleTable) throws TableException
	{
		String strViewType = (String) request.getParameter("viewType");
		if (StringUtils.isEmpty(strViewType))
		{ // strange that the grid doesn't submit it in first load!
			strViewType = "JQ_GRID";
		}
		try
		{
			final String viewFactoryClassName = request.getParameter("viewFactoryClassName");
			final ViewFactory viewFactory = (ViewFactory) Class.forName(viewFactoryClassName).newInstance();
			final Renderer view = viewFactory.createView(strViewType);
			view.export(context, response, request.getParameter("caption"), this, tupleTable, totalPages, page);
		}
		catch (Exception e)
		{
			throw new TableException(e);
		}
	}

	/**
	 * Function to build a datastructure filled with rows from a
	 * {@link TupleTable}, to be serialised by Gson and displayed from there by
	 * a jqGrid.
	 * 
	 * @param rowCount
	 *            The number of rows to select.
	 * @param totalPages
	 *            The total number of pages of data (ie. dependent on size of
	 *            dataset and nr. of rows per page)
	 * @param page
	 *            The selected page.
	 * @param table
	 *            The Tupletable from which to read the data.
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
		return result;
	}

	/**
	 * Extract the filter rules from the sent jquery request, and convert them
	 * into Molgenis Query rules.
	 * 
	 * @param request
	 *            A request containing filter rules
	 * @return A list of QueryRules that represent the filter rules from the
	 *         request.
	 */
	@SuppressWarnings("rawtypes")
	private static List<QueryRule> createQueryRulesFromJQGridRequest(Tuple request)
	{
		final String filtersParameter = request.getString("filters");
		final List<QueryRule> rules = new ArrayList<QueryRule>();
		if (StringUtils.isNotEmpty(filtersParameter))
		{
			final StringMap filters = (StringMap) new Gson().fromJson(filtersParameter, Object.class);
			final String groupOp = (String) filters.get("groupOp");
			@SuppressWarnings("unchecked")
			final ArrayList<StringMap<String>> jsonRules = (ArrayList<StringMap<String>>) filters.get("rules");
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

	@SuppressWarnings("unchecked")
	private static void addFilters(final Tuple request, final QueryTable queryTable) throws TableException
	{
		try {
			final SQLQuery query = queryTable.getQuery();
			final LinkedHashMap<String, SimpleExpression<? extends Object>> selectMap = queryTable.getSelect();
	
			final String filtersParameter = request.getString("filters");
			if (StringUtils.isNotEmpty(filtersParameter))
			{
				final StringMap filters = (StringMap) new Gson().fromJson(filtersParameter, Object.class);
				final String groupOp = (String) filters.get("groupOp");
				
				final ArrayList<StringMap<String>> jsonRules = (ArrayList<StringMap<String>>) filters.get("rules");

				BooleanExpression expr = null;
				for (StringMap<String> rule : jsonRules)
				{
					final String fieldName = rule.get("field");
					final String op = rule.get("op");
					final String value = rule.get("data");
					//final String index = rule.get("index");
	
					final SimpleExpression<? extends Object> selectExpr = selectMap.get(fieldName);
					final Field column = queryTable.getColumnByName(fieldName);
					final FieldTypeEnum type = column.getType().getEnumType();
					BooleanExpression rhs = getExpression(op, value, selectExpr, column, type);
					if (expr != null)
					{
						if(groupOp.equals("AND")) {
							expr = expr.and(rhs);
						} else if(groupOp.equals("OR")) {
							expr = expr.or(rhs);
						} else {
							throw new IllegalArgumentException(String.format("Unkown groupOp: %s", groupOp));
						}
					} else {
						expr = rhs;
					}
				}
				query.where(expr);
			}
		} catch (Exception ex) {
			throw new TableException(ex);
		}
	}

	private static BooleanExpression getExpression(final String op, final String value,
			final SimpleExpression<? extends Object> selectExpr, final Field column, final FieldTypeEnum type
			) throws ParseException
	{
		BooleanExpression expr = null;
		switch (type)
		{
			case DECIMAL:
			{
				final Double val = (Double) column.getType().getTypedValue(value);
				if (op.equals("eq"))
				{
					expr = ((NumberExpression<Double>) selectExpr).eq(val);
				}
				else if (op.equals("ne"))
				{
					expr = ((NumberExpression<Double>) selectExpr).ne(val);
				}
				else if (op.equals("le"))
				{
					expr = ((NumberExpression<Double>) selectExpr).lt(val);
				}
				else if (op.equals("gt"))
				{
					expr = ((NumberExpression<Double>) selectExpr).gt(val);
				} else {
					throw new UnsupportedOperationException(
							String.format("Operation: %s not implemented yet for type %s!", 
									op, type
					));
				}
			}
				break;
				
			case INT:
			{
				final Integer val = (Integer) column.getType().getTypedValue(value);
				if (op.equals("eq"))
				{
					expr = ((NumberExpression<Integer>) selectExpr).eq(val);
				}
				else if (op.equals("ne"))
				{
					expr = ((NumberExpression<Integer>) selectExpr).ne(val);
				}
				else if (op.equals("le"))
				{
					expr = ((NumberExpression<Integer>) selectExpr).lt(val);
				}
				else if (op.equals("gt"))
				{
					expr = ((NumberExpression<Integer>) selectExpr).gt(val);
				} else {
					throw new UnsupportedOperationException(
							String.format("Operation: %s not implemented yet for type %s!", 
									op, type
					));
				}
			}
				break;				
				
			case STRING:
			{
				final String val = (String) column.getType().getTypedValue(value);
				if (op.equals("eq"))
				{
					expr = ((StringExpression) selectExpr).eq(val);
				}
				else if (op.equals("ne"))
				{
					expr = ((StringExpression) selectExpr).ne(val);
				}
				else if (op.equals("bw") || op.equals("bn"))
				{
					expr = ((StringExpression) selectExpr).like(val +"%");
					if(op.equals("bn")) {
						expr = expr.not();
					}
				} else {
					throw new UnsupportedOperationException(
							String.format("Operation: %s not implemented yet for type %s!", 
									op, type
					));
				}
			}
			break;
			default:
				throw new UnsupportedOperationException(
						String.format("Operation: %s not implemented yet for type %s!", 
								op, type
				));
		}
		return expr;
	}

	/**
	 * Create a {@link QueryRule} based on a jquery operator string, from the
	 * filter popup/dropdown in the {@link JQGridRenderer} UI. Example:
	 * Supplying the arguments 'name', 'ne', 'Asia' creates a QueryRule that
	 * filters for rows where the 'name' column does not equal 'Asia'.
	 * 
	 * @param field
	 *            The field to which to apply the operator
	 * @param op
	 *            The operator string (jquery syntax)
	 * @param value
	 *            The value (if any) for the right-hand side of the operator
	 *            expression.
	 * @return A new QueryRule that represents the supplied jquery expression.
	 */
	private static QueryRule convertOperator(final String field, final String op, final String value)
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
		}
		else
		{
			throw new IllegalArgumentException(String.format("Unkown Operator: %s", op));
		}
		return rule;
	}

	/**
	 * Get a string that represents the type of a {@link Field} in jquery
	 * syntax.
	 * 
	 * @param f
	 *            The field to convert
	 * @return A string representing the field's type in jquery syntax.
	 */
	public String getJQGirdColumnType(Field f)
	{
		final FieldTypeEnum fieldType = f.getType().getEnumType();
		switch (fieldType)
		{
			case DATE:
				return ",date: true";
			case DATE_TIME:
				return ",date: true, time: true";
			case DECIMAL:
				return ",number: 'true'";
				// case ENUM: return "";
			case INT:
				return ",integer: 'true'";
			case LONG:
				return ",integer: 'true'";
			default:
				return ""; // handle as text
		}
	}

	/**
	 * Add a 'NOT' operator to a particular rule.
	 * 
	 * @param rule
	 *            The rule to negate.
	 * @return A new {@link QueryRule} which is the negation of the supplied
	 *         rule.
	 */
	private static QueryRule toNotRule(QueryRule rule)
	{
		return new QueryRule(Operator.NOT, rule);
	}

	/**
	 * Add sorting rules to the rendered data.
	 * 
	 * @param sidx
	 *            The column index by which to sort
	 * @param sord
	 *            The order in which to sort (ascending/descending)
	 * @param rules
	 *            The already-applied rules, to which the new sorting rule will
	 *            be added.
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

	public String render()
	{
		return gridView.render();
	}
}
