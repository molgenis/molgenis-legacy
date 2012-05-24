package org.molgenis.datatable.controller;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.molgenis.datatable.model.JdbcTable;
import org.molgenis.datatable.model.TableException;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.datatable.view.CsvExporter;
import org.molgenis.datatable.view.ExcelExporter;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.server.MolgenisContext;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.server.MolgenisResponse;
import org.molgenis.framework.server.MolgenisService;
import org.molgenis.util.Tuple;

import com.google.gson.Gson;
import com.google.gson.internal.StringMap;
import com.google.gson.reflect.TypeToken;

public class JQGridController implements MolgenisService
{
	private static final String EXCEL_VIEW = "excelView";
	private static final String CSV_VIEW = "csvView";
	private static final String JQ_GRID_VIEW = "jqGridView";

	class JQGridResult
	{
		int page;
		int total;
		int records;

		private ArrayList<LinkedHashMap<String, String>> rows = new ArrayList<LinkedHashMap<String, String>>();

		public JQGridResult(int page, int total, int records)
		{
			this.page = page;
			this.total = total;
			this.records = records;
		}
	}

	// private final MolgenisContext context;

	public JQGridController(MolgenisContext context)
	{
		// this.context = context;
	}

	private final static String SQL_START = "SELECT %1$s FROM %2$s ";

	@Override
	public void handleRequest(MolgenisRequest request, MolgenisResponse response) throws ParseException,
			DatabaseException, IOException
	{
		try
		{
			final String viewType = request.getString("viewType");

			final int limit = request.getInt("rows");
			final String sidx = request.getString("sidx");
			final String sord = request.getString("sord");

			final Map<String, String> dataSource = JsonToMap(request, "dataSource");
			final List<QueryRule> rules = addFilterRules(request);

			final String[] columnNames = new Gson().fromJson(request.getString("colNames"), String[].class);
			final String dataSourceType = dataSource.get("type");
			final String fromExpression = dataSource.get("fromExpression");
			final String sqlCount = String.format(SQL_START, "COUNT(*)", fromExpression);
			final String sqlSelect = String.format(SQL_START, StringUtils.join(columnNames, ","), fromExpression);

			addSortRules(sidx, sord, rules);
			
			final Database db = request.getDatabase();
			final int rowCount = getRowCount(sqlCount, rules, db);
			final int totalPages = (int) Math.ceil(rowCount / limit);
			final int page = Math.min(request.getInt("page"), totalPages);
			final int offset = Math.max(limit * page - limit, 0);

			rules.addAll(Arrays.asList(new QueryRule(Operator.LIMIT, limit), new QueryRule(Operator.OFFSET, offset)));

			final TupleTable jdbcTable = new JdbcTable(db, sqlSelect, rules);
			
			if(viewType.equals(JQ_GRID_VIEW)) {
				final JQGridResult result = buildJQGridResults(rowCount, totalPages, page, jdbcTable);
				response.getResponse().getWriter().print(new Gson().toJson(result));
			} else if(viewType.equals(CSV_VIEW)) {
				final CsvExporter csvExport = new CsvExporter(jdbcTable);
				csvExport.export(response.getResponse().getOutputStream());
			} else if(viewType.equals(EXCEL_VIEW)) {
				final ExcelExporter excelExport = new ExcelExporter(jdbcTable);
				excelExport.export(response.getResponse().getOutputStream());
			}
		}
		catch (Exception e)
		{
			throw new DatabaseException(e);
		}
	}

	private JQGridResult buildJQGridResults(final int rowCount, final int totalPages, final int page,
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

	@SuppressWarnings("rawtypes")
	private List<QueryRule> addFilterRules(MolgenisRequest request)
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

	private QueryRule toNotRule(QueryRule rule)
	{
		return new QueryRule(Operator.NOT, rule);
	}

	private Map<String, String> JsonToMap(MolgenisRequest request, String fieldName)
	{
		return new Gson().fromJson(request.getString(fieldName), new TypeToken<Map<String, String>>()
		{
		}.getType());
	}

	private int getRowCount(final String sqlCount, final List<QueryRule> rules, final Database db)
			throws DatabaseException, SQLException
	{
		final ResultSet countSet = db.executeQuery(sqlCount, rules.toArray(new QueryRule[0]));
		int rowCount = 0;
		if (countSet.next())
		{
			final Number count = (Number) countSet.getObject(1);
			rowCount = count.intValue();
		}
		countSet.close();
		return rowCount;
	}

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
}
