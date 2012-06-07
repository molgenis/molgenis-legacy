package org.molgenis.datatable.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.molgenis.MolgenisFieldTypes.FieldTypeEnum;
import org.molgenis.datatable.DataSourceFactory.DataSourceFactory;
import org.molgenis.datatable.model.TableException;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.datatable.view.ExcelExporter;
import org.molgenis.datatable.view.SPSSExporter;
import org.molgenis.datatable.view.ViewFactory;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.server.MolgenisContext;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.server.MolgenisResponse;
import org.molgenis.framework.server.MolgenisService;
import org.molgenis.model.elements.Field;
import org.molgenis.util.Tuple;
import org.molgenis.util.ZipUtils;
import org.molgenis.util.ZipUtils.DirectoryStructure;

import com.google.gson.Gson;
import com.google.gson.internal.StringMap;

public class JQGridController implements MolgenisService
{
	public static abstract class DataSourceDescription {
		private final String type;
		private final List<Field> columns;
		
		protected DataSourceDescription(String type, List<Field> columns) {
			this.type = type;
			this.columns = columns;
		}
		
		public String toJson() {
			return new Gson().toJson(this);
		}

		public String getType()
		{
			return type;
		}

		public List<Field> getColumns()
		{
			return columns;
		}
	}
	
	public static class JDBCDataSourceDescription extends DataSourceDescription {
		private final String fromExpression;
		
		public JDBCDataSourceDescription(String fromExpression, List<Field> columns) {
			super("jdbc", columns);
			this.fromExpression = fromExpression;
		}		
		
		public String getFromExpression() {
			return fromExpression;
		}
	}
	
	public static class CSVDataSourceDescription extends DataSourceDescription {
		private final String csvUri;

		public CSVDataSourceDescription(String csvUri) {
			super("csv", Collections.<Field>emptyList());
			this.csvUri = csvUri;
		}

		public String getCsvUri()
		{
			return csvUri;
		}				
	}	
	
	public interface View {
		public void export(HttpServletResponse response, String fileName, JQGridController controller, TupleTable tupleTable, int totalPages, int currentPage) throws TableException, IOException;
	} 
	
	public static class ViewHelper {
		public static void setHeader(HttpServletResponse response, String contentType, String fileName) {
			response.setContentType(contentType);
			response.addHeader("Content-Disposition", "attachment; filename="+ fileName);
		}
	}
	
	//should be placed in own package or class
	public static class JQGridView implements View {
		@Override
		public void export(HttpServletResponse response, String fileName, JQGridController controller, TupleTable tupleTable, int totalPages, int currentPage) throws TableException, IOException {
			final JQGridResult result = JQGridController.buildJQGridResults(tupleTable.getRowCount(), totalPages, currentPage, tupleTable);
			final PrintWriter pout = new PrintWriter(response.getOutputStream());
			pout.print(new Gson().toJson(result));
			pout.close();
		}		
	}

	public static class ExcelView implements View {
		@Override
		public void export(HttpServletResponse response, String fileName, JQGridController controller, TupleTable tupleTable, int totalPages, int currentPage) throws TableException, IOException {
			ViewHelper.setHeader(response, "application/ms-excel", fileName + ".xlsx");
			final ExcelExporter excelExport = new ExcelExporter(tupleTable);
			excelExport.export(response.getOutputStream());		
		}
	}
	
	public static class CSVView implements View {
		@Override
		public void export(HttpServletResponse response, String fileName, JQGridController controller, TupleTable tupleTable, int totalPages, int currentPage) throws TableException, IOException {
			ViewHelper.setHeader(response, "application/ms-excel", fileName + ".csv");
			final ExcelExporter excelExport = new ExcelExporter(tupleTable);
			excelExport.export(response.getOutputStream());	
		}
	}	

	public static class SPSSView implements View {
		@Override
		public void export(HttpServletResponse response, String fileName, JQGridController controller, TupleTable tupleTable, int totalPages, int currentPage) throws TableException, IOException {

			try {
				final File tempDir = (File)controller.context.getServletContext().getAttribute( "javax.servlet.context.tempdir" );
				// create a temporary file in that directory
				final File spssFile = File.createTempFile( "spssExport", ".sps", tempDir );
				final File spssCsvFile = File.createTempFile( "csvSpssExport", ".csv", tempDir );
				final File zipExport = File.createTempFile( "spssExport", ".zip", tempDir );
				
				final FileOutputStream spssFileStream = new FileOutputStream(spssFile);
				final FileOutputStream spssCsvFileStream = new FileOutputStream(spssCsvFile);
				final SPSSExporter spssExporter = new SPSSExporter(tupleTable);
				spssExporter.export(spssCsvFileStream, spssFileStream, spssCsvFile.getName());
				
				spssCsvFileStream.close();
				spssFileStream.close();
				ZipUtils.compress(Arrays.asList(spssFile, spssCsvFile), zipExport, DirectoryStructure.EXCLUDE_DIR);
				ViewHelper.setHeader(response, "application/octet-stream", fileName + ".zip");
				exportFile(zipExport, response);
			} catch (Exception e) {
				throw new TableException(e);
			}		
		}
		
		private void exportFile(File file, HttpServletResponse response) throws IOException {
			FileInputStream fileIn = new FileInputStream(file);
			ServletOutputStream out = response.getOutputStream();
			 
			byte[] outputByte = new byte[4096];
			//copy binary contect to output stream
			while(fileIn.read(outputByte, 0, 4096) != -1)
			{
				out.write(outputByte, 0, 4096);
			}
			fileIn.close();
			out.flush();
			out.close();			
		}
	}		

	private enum ExportRange {
		GRID, 
		ALL,
		UNKOWN
	}
	
	private static class JQGridResult
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

	private final MolgenisContext context;

	public JQGridController(MolgenisContext context)
	{
		this.context = context;
	}
	
	@Override
	public void handleRequest(MolgenisRequest request, MolgenisResponse response) throws ParseException,
			DatabaseException, IOException
	{
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
			
			@SuppressWarnings("unchecked")
			final StringMap<String> dataSource = (StringMap<String>)new Gson().fromJson(request.getString("dataSource"), Object.class);
			final DataSourceFactory dsFactory = (DataSourceFactory) Class.forName(request.getString("dataSourceFactoryClassName")).newInstance();
			final TupleTable tupleTable = dsFactory.createDataSource(dataSource, request.getDatabase(), rules);
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
						
			renderData(request, response, page, totalPages, tupleTable);

			tupleTable.close();
		}
		catch (Exception e)
		{
			throw new DatabaseException(e);
		}
	}

	private void renderData(MolgenisRequest request, MolgenisResponse response, int page, int totalPages,
			final TupleTable tupleTable) throws InstantiationException, IllegalAccessException, ClassNotFoundException,
			TableException, IOException
	{
		String strViewType = request.getString("viewType");
		if(StringUtils.isEmpty(strViewType)) { //strange that the grid doesn't submit it in first load!
			strViewType = "JQ_GRID";
		}
		final String viewFactoryClassName = request.getString("viewFactoryClassName");
		final ViewFactory viewFactory = (ViewFactory) Class.forName(viewFactoryClassName).newInstance();
		final View view = viewFactory.createView(strViewType);
		view.export(response.getResponse(), request.getString("caption"), this, tupleTable, totalPages, page);
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

	private QueryRule toNotRule(QueryRule rule)
	{
		return new QueryRule(Operator.NOT, rule);
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
