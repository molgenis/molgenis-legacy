package org.molgenis.datatable.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.molgenis.datatable.model.CsvTable;
import org.molgenis.datatable.model.JdbcTable;
import org.molgenis.datatable.model.TableException;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.datatable.view.CsvExporter;
import org.molgenis.datatable.view.ExcelExporter;
import org.molgenis.datatable.view.SPSSExporter;
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
	private enum ViewType {
		JQ_GRID {
			@Override
			void export(JQGridController controller, TupleTable tupleTable, int totalPages, int page, OutputStream out) throws TableException {
				JQGridResult result = JQGridController.buildJQGridResults(tupleTable.getRowCount(), totalPages, page, tupleTable);
				PrintWriter pout = new PrintWriter(out);
				pout.print(new Gson().toJson(result));
				pout.close();
			}
		},
		EXCEL {
			@Override
			void export(JQGridController controller, TupleTable tupleTable, int totalPages, int page, OutputStream out) throws TableException {
				final ExcelExporter excelExport = new ExcelExporter(tupleTable);
				excelExport.export(out);		
			}
		},
		CSV {
			@Override
			void export(JQGridController controller, TupleTable tupleTable, int totalPages, int page, OutputStream out) throws TableException {
				final CsvExporter csvExport = new CsvExporter(tupleTable);
				csvExport.export(out);
			}
		},
		SPSS {
			@Override
			void export(JQGridController controller, TupleTable tupleTable, int totalPages, int page, OutputStream out) throws TableException {				
				try {
					File tempDir = (File)controller.context.getServletContext().getAttribute( "javax.servlet.context.tempdir" );
					// create a temporary file in that directory
					File spssFile = File.createTempFile( "spssExport", ".sps", tempDir );
					File spssCsvFile = File.createTempFile( "csvSpssExport", ".csv", tempDir );
					
					FileOutputStream spssFileStream = new FileOutputStream(spssFile);
					FileOutputStream spssCsvFileStream = new FileOutputStream(spssCsvFile);
					final SPSSExporter spssExporter = new SPSSExporter(tupleTable);
					spssExporter.export(spssCsvFileStream, spssFileStream, spssCsvFile.getName());
					spssCsvFileStream.close();
					spssFileStream.close();
					
					System.out.println(spssFile.getAbsolutePath());
					System.out.println(spssCsvFile.getAbsolutePath());
				} catch (Exception e) {
					throw new TableException(e);
				}
			}
		};
		
		abstract void export(JQGridController controller, TupleTable tupleTable, int totalPages, int page, OutputStream out) throws TableException;
	}	

	private enum ExportRange {
		GRID, 
		ALL,
		UNKOWN
	}
	
	static class JQGridResult
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

	private final MolgenisContext context;

	public JQGridController(MolgenisContext context)
	{
		this.context = context;
		
	}

	private final static String SQL_START = "SELECT %1$s FROM %2$s ";

	@Override
	public void handleRequest(MolgenisRequest request, MolgenisResponse response) throws ParseException,
			DatabaseException, IOException
	{
		try
		{
			TupleTable tupleTable = null;
			
			String strViewType = request.getString("viewType");
			ViewType viewType = ViewType.JQ_GRID;
			if(StringUtils.isNotEmpty(strViewType)) {
				viewType = ViewType.valueOf(strViewType);
			}

			final ExportRange exportSelection = 
				StringUtils.isNotEmpty(request.getString("exportSelection")) ?			
						ExportRange.valueOf(request.getString("exportSelection")) : 
						ExportRange.UNKOWN;
			
			final int limit = 	request.getInt("rows");
			final String sidx = request.getString("sidx");
			final String sord = request.getString("sord");

			final Map<String, String> dataSource = JsonToMap(request, "dataSource");
			final String dataSourceType = dataSource.get("type");
			
			
			//add filter rules
			final List<QueryRule> rules = addFilterRules(request);
			
			int page = 0;
			int offset = 0;
			int rowCount = -1;
			int totalPages = 1;
			
			final String[] columnNames = new Gson().fromJson(request.getString("colNames"), String[].class);
			if(dataSourceType.equals("jdbc")) {
				final String fromExpression = dataSource.get("fromExpression");
				final String sqlSelect = String.format(SQL_START, StringUtils.join(columnNames, ","), fromExpression);

				tupleTable = new JdbcTable(request.getDatabase(), sqlSelect, rules);
				rowCount = tupleTable.getRowCount();	
			} else if (dataSourceType.equals("csv")) {
				tupleTable = new CsvTable("/Users/jorislops/Desktop/country.csv");
				rowCount = tupleTable.getRowCount();
			}

			totalPages = (int) Math.ceil(rowCount / limit);
			page = Math.min(request.getInt("page"), totalPages);
			offset = Math.max(limit * page - limit, 0);			
			
			//add query Rules
			if(exportSelection != ExportRange.ALL) {
				rules.addAll(Arrays.asList(new QueryRule(Operator.LIMIT, limit), new QueryRule(Operator.OFFSET, offset)));
			}
			addSortRules(sidx, sord, rules);			
			
			viewType.export(this, tupleTable, totalPages, page, response.getResponse().getOutputStream());

			tupleTable.close();
		}
		catch (Exception e)
		{
			throw new DatabaseException(e);
		}
	}

	private static JQGridResult buildJQGridResults(final int rowCount, final int totalPages, final int page,
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
