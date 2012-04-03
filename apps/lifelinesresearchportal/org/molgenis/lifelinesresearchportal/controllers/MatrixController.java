package org.molgenis.lifelinesresearchportal.controllers;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.lifelinesresearchportal.models.MatrixModel;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.PhenoMatrix;
import org.molgenis.matrix.Utils.CsvExporter;
import org.molgenis.matrix.Utils.ExcelExporter;
import org.molgenis.matrix.Utils.Exporter;
import org.molgenis.matrix.Utils.SPSSExporter;
import org.molgenis.matrix.component.Column;
import org.molgenis.matrix.component.Column.ColumnType;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.HandleException;

import com.mindbright.jca.security.UnsupportedOperationException;

import app.DatabaseFactory;
import app.ExcelExport;

/**
 * Servlet implementation class jqGrid
 */
public class MatrixController extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	private PhenoMatrix<ObservationTarget, Measurement, ObservedValue> matrix;

	private EntityManager em;

	private Investigation investigation;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MatrixController()
	{
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		Database db = null;

		final String operation = request.getParameter("op");
		final String selectedColumnsPar = request.getParameter("selectedCols");
		final String exportType = request.getParameter("exportType");

		try
		{
			db = DatabaseFactory.create();
			em = db.getEntityManager();

			investigation = (Investigation) em.createQuery("from " + Investigation.class.getName()).getResultList()
					.get(0);

			LinkedHashMap<Protocol, List<Measurement>> selectedByTree = getSelectedFromTree(selectedColumnsPar);
			matrix = new MatrixModel<ObservationTarget, Measurement, ObservedValue>(em, investigation, selectedByTree);

			if (StringUtils.equals(operation, "jsTreeJson"))
			{
				// Tree update
				LinkedHashMap<Protocol, List<Measurement>> measurementByProtocol = getMeasurementByProtocol(em,
						investigation, Arrays.asList("PA_ID"));
				jsTreeJson(response, measurementByProtocol);
			}
			else
			{
				if (StringUtils.equals(operation, "getColModel"))
				{
					response.setContentType("text/javascript");
					getColModel(response.getWriter(), matrix.getMeasurementsByProtocol());
				}
				else if (StringUtils.equals(operation, "getColumnNames"))
				{
					getColumnNames(response.getWriter(), matrix.getMeasurementsByProtocol());
				}
				else
				{
					renderMatrix(request, response, exportType);
				}
			}
		}
		catch (Exception ex)
		{
			Logger.getLogger(MatrixController.class.getName()).log(Level.SEVERE, null, ex);
		}
		finally
		{
			try
			{
				db.close();
			}
			catch (DatabaseException dbEx)
			{
				Logger.getLogger(MatrixController.class.getName()).log(Level.SEVERE, null, dbEx);
			}
		}
	}

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private LinkedHashMap<Protocol, List<Measurement>> getSelectedFromTree(String selectedColumnsPar)
	{
		final LinkedHashMap<Protocol, List<Measurement>> selectedMeasurementByProtocol = getSelectedMeasurements(em,
				selectedColumnsPar);
		if (selectedMeasurementByProtocol.isEmpty())
		{
			final Protocol patientProtocol = em.createQuery("SELECT p FROM Protocol p WHERE p.name = :name AND p.investigation = :investigation", Protocol.class)
					.setParameter("name", "PATIENT")
					.setParameter("investigation", investigation)
					.getSingleResult();
			
			final List<ObservableFeature> features = em.createQuery("SELECT m FROM Protocol p JOIN p.features m WHERE p = :protocol AND m.name <> 'PA_ID'", ObservableFeature.class)
					.setParameter("protocol", patientProtocol)
					.getResultList();
			
			final List<Measurement> measurements = (List<Measurement>) (List) features;
			selectedMeasurementByProtocol.put(patientProtocol, measurements);
		}
		return selectedMeasurementByProtocol;
	}

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private LinkedHashMap<Protocol, List<Measurement>> getMeasurementByProtocol(EntityManager em,
			Investigation investigation, List<String> excludeMeasurementNames)
	{
		String ql = "SELECT p FROM Protocol p JOIN FETCH p.features f WHERE p.investigation = :investigation AND f.name NOT IN (:excludeList)";
		List<Protocol> protocols = em.createQuery(ql, Protocol.class).setParameter("investigation", investigation)
				.setParameter("excludeList", excludeMeasurementNames).getResultList();
		LinkedHashMap<Protocol, List<Measurement>> measurementsByProtocol = new LinkedHashMap<Protocol, List<Measurement>>();
		for (Protocol protocol : protocols)
		{
			measurementsByProtocol.put(protocol, (List<Measurement>) (List) protocol.getFeatures());
		}
		return measurementsByProtocol;
	}

	private LinkedHashMap<Protocol, List<Measurement>> getSelectedMeasurements(EntityManager em,
			String selectedColumnsPar)
	{
		LinkedHashMap<Protocol, List<Measurement>> selectedMeasurementByProtocol = new LinkedHashMap<Protocol, List<Measurement>>();
		if (StringUtils.isNotEmpty(selectedColumnsPar) && !selectedColumnsPar.equals("null")
				&& !selectedColumnsPar.equals("[]"))
		{
			// Selected columns formatted as ["50", "50.12", ...]; here split
			// into procotol.measurement combinations
			String[] cols = selectedColumnsPar.substring(2, selectedColumnsPar.length() - 2).replaceAll("\"", "")
					.split(",");
			for (String c : cols)
			{
				addSelectedMeasurement(selectedMeasurementByProtocol, c);
			}
		}
		return selectedMeasurementByProtocol;
	}

	private void addSelectedMeasurement(LinkedHashMap<Protocol, List<Measurement>> selectedMeasurementByProtocol,
			String columnDescr)
	{
		// Split column: protocol.measurement
		String[] parts = columnDescr.split("\\.");
		int protocolId = Integer.parseInt(parts[0]);

		Protocol protocol = em.find(Protocol.class, protocolId);

		if (parts.length > 1)
		{ // if measurement selected
			int measurementId = Integer.parseInt(parts[1]);
			Measurement m = em.find(Measurement.class, measurementId);

			boolean exists = false;
			for (Protocol p : selectedMeasurementByProtocol.keySet())
			{
				if (p.getId().equals(protocol.getId()))
				{
					exists = true;
					break;
				}
			}

			if (exists)
			{
				if (!selectedMeasurementByProtocol.get(protocol).contains(m))
				{
					selectedMeasurementByProtocol.get(protocol).add(m);
				}
			}
			else
			{
				List<Measurement> measurements = new ArrayList<Measurement>();
				measurements.add(m);
				selectedMeasurementByProtocol.put(protocol, measurements);
			}
		}
	}

	public void renderMatrix(HttpServletRequest request, HttpServletResponse response, String exportType)
	{
		String pageParam = request.getParameter("page");
		String limitParam = request.getParameter("rows");
		String sidx = request.getParameter("sidx");
		String sord = request.getParameter("sord");

		int page = 1;
		if (StringUtils.isNotEmpty(pageParam))
		{
			page = Integer.parseInt(pageParam);
		}

		int limit = 10;
		if (StringUtils.isNotEmpty(limitParam))
		{
			limit = Integer.parseInt(limitParam);
		}

		if (StringUtils.isNotEmpty(sidx))
		{
			String[] parts = sidx.split("\\.");
			Protocol p = em.find(Protocol.class, Integer.parseInt(parts[0]));
			Measurement m = em.find(Measurement.class, Integer.parseInt(parts[1]));
			if (StringUtils.isEmpty(sord))
			{
				sord = "ASC";
			}
			matrix.setSort(p, m, sord);
		}
		// put constraints in matrix (offset, limit, filters)

		matrix.setRowOffset(page);
		matrix.setRowLimit(limit);

		// {"groupOp":"AND","rules":[{"field":"GEWICHT","op":"eq","data":"136"},{"field":"LENGTE","op":"eq","data":"1212"}]}
		String filters = request.getParameter("filters");
		applyFiltersToMatrix(matrix, filters);

		try
		{
			if (StringUtils.isNotEmpty(exportType) && StringUtils.isNotEmpty(exportType))
			{
				String exportSelection = request.getParameter("exportSelection");
				if (exportSelection.equals("All"))
				{
					matrix.setRowLimit(0);
					matrix.setRowLimit(matrix.getRowCount());
				}
				boolean exportVisable = !exportSelection.equals("All");

				Exporter<ObservationTarget, Measurement, ObservedValue> exporter = null;
				if (exportType.equals("Excel"))
				{
					exporter = new ExcelExporter<ObservationTarget, Measurement, ObservedValue>(matrix,
							response.getOutputStream());
				}
				else if (exportType.equals("Spss"))
				{
					exporter = new SPSSExporter<ObservationTarget, Measurement, ObservedValue>(matrix,
							response.getOutputStream());
				}
				else if (exportType.equals("Csv"))
				{
					exporter = new CsvExporter<ObservationTarget, Measurement, ObservedValue>(matrix,
							response.getOutputStream());
				}

				download(request, response, exporter, exportVisable);
			}
			else
			{
				renderJsonTable(matrix, response.getWriter());
			}
		}
		catch (Exception e)
		{
			HandleException.handle(e);
		}
	}

	public void renderJsonTable(PhenoMatrix<ObservationTarget, Measurement, ObservedValue> matrix, PrintWriter outWriter)
			throws Exception
	{
		List<Object[]> records = matrix.getTypedValues();

		StringBuilder out = new StringBuilder();

		out.append("<?xml version='1.0' encoding='utf-8'?>");
		out.append("<rows>");

		int rowLimit = matrix.getRowLimit();
		int rowCount = matrix.getRowCount();
		int rowOffset = matrix.getRowOffset();

		int totalPages = (int) Math.ceil((float) rowCount / (float) rowLimit);

		out.append(String.format("<page>%s</page>", rowOffset));
		out.append(String.format("<total>%s</total>", totalPages));
		out.append(String.format("<records>%s</records>", rowCount));

		for (final Object[] rec : records)
		{
			final String rowId = rec[0].toString();
			out.append(String.format("<row id=\"%s\">", rowId));
			for (final Object cell : rec)
			{
				if (cell != null && StringUtils.isNotEmpty(cell.toString()))
				{
					out.append(String.format("<cell>%s</cell>", cell.toString()));
				}
				else
				{
					out.append(String.format("<cell>%s</cell>", ""));
				}
			}
			out.append("</row>");
		}
		out.append("</rows>");

		outWriter.append(out.toString());
		outWriter.flush();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException
	{
		doGet(request, response);
	}

	private void getColModel(PrintWriter out, LinkedHashMap<Protocol, List<Measurement>> measurementByProtocol)
	{
		final String col = "{\"index\":\"%s\",\"search\":true,\"searchoptions\":{\"sopt\":%s%s},\"stype\":\"text\",\"width\":100,\"name\":\"%s\",\"resizable\":true}";
		;
		final StringBuilder json = new StringBuilder();

		json.append("[");
		int idx = 0;
		for (Map.Entry<Protocol, List<Measurement>> entry : measurementByProtocol.entrySet())
		{
			if (idx > 0)
			{
				json.append(",");
			}
			++idx;

			final List<Measurement> value = entry.getValue();

			for (int i = 0; i < value.size(); ++i)
			{
				if (i != 0)
				{
					json.append(",");
				}

				Measurement m = value.get(i);

				String colName = m.getName();
				ColumnType columnType = Column.getColumnType(m.getDataType());

				final String dateSearchOptions = "[\"eq\",\"ne\",\"lt\",\"le\",\"gt\",\"ge\"]";
				final String stringSearchOptions = "[\"eq\",\"ne\"]"; // add
																		// like!
				final String numberSearchOptions = "[\"eq\",\"ne\",\"lt\",\"le\",\"gt\",\"ge\"]";
				final String codeSearchOptions = "[\"eq\",\"ne\"]";

				String dataInit = "";
				String searchOptions = "";
				if (columnType == ColumnType.Date || columnType == ColumnType.Datetime)
				{
					searchOptions = dateSearchOptions;
					dataInit = ",\"dataInit\": function(element) { $(element).datepicker({dateFormat: 'yy-mm-dd'}); }";
				}
				else if (columnType == ColumnType.Integer)
				{
					searchOptions = numberSearchOptions;
				}
				else if (columnType == ColumnType.Decimal)
				{
					searchOptions = numberSearchOptions;
				}
				else if (columnType == ColumnType.Code)
				{
					searchOptions = codeSearchOptions;
				}
				else if (columnType == ColumnType.String)
				{
					searchOptions = stringSearchOptions;
				}

				String index = String.format("%s.%s", entry.getKey().getId(), value.get(i).getId());
				String column = String.format(col, index, searchOptions, dataInit, colName);
				json.append(column);
			}
		}
		json.append("]");

		System.err.println(json.toString());

		out.append(json.toString());
		out.flush();
	}

	private void getColumnNames(PrintWriter out, LinkedHashMap<Protocol, List<Measurement>> measurementByProtocol)
	{
		List<String> values = new ArrayList<String>();

		for (Map.Entry<Protocol, List<Measurement>> entry : measurementByProtocol.entrySet())
		{
			for (Measurement measurement : entry.getValue())
			{
				values.add(measurement.getName());
			}
		}
		JSONArray data = new JSONArray(values);
		out.append(data.toString());
		out.flush();
	}

	private static void applyFiltersToMatrix(PhenoMatrix<ObservationTarget, Measurement, ObservedValue> matrix,
			String filters)
	{
		if (StringUtils.isNotEmpty(filters))
		{
			JSONObject jFilter = null;
			try
			{
				jFilter = new JSONObject(filters);

				String groupOp = (String) jFilter.get("groupOp");
				JSONArray rules = jFilter.getJSONArray("rules");

				for (int i = 0; i < rules.length(); ++i)
				{
					JSONObject searchRule = (JSONObject) rules.get(i);

					String field = (String) searchRule.get("field");

					String[] parts = field.split("\\.");
					int protocolId = Integer.parseInt(parts[0]);
					int measurementId = Integer.parseInt(parts[1]);

					String op = (String) searchRule.get("op");
					String value = (String) searchRule.get("data");
					
					
					
					Operator operator = getOperator(op);
					matrix.addCondition(protocolId, measurementId, op, operator, value);
				}
				System.out.println(groupOp);
				System.out.println(rules);
			}
			catch (Exception ex)
			{
				Logger.getLogger(MatrixController.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	private static Operator getOperator(String operator)
	{
		operator = operator.toLowerCase();
		if(operator.equals("eq")) {
			return Operator.EQUALS;
		} else if(operator.equals("ne")) {
			throw new UnsupportedOperationException("operator not equals is not implemented!");
		} else if(operator.equals("lt")) {
			return Operator.LESS;
		} else if(operator.equals("le")) {
			return Operator.LESS_EQUAL;
		} else if(operator.equals("gt")) {
			return Operator.GREATER;
		} else if(operator.equals("ge")) {
			return Operator.GREATER_EQUAL;
		}
		throw new IllegalArgumentException(String.format("unkown operator: %s", operator));
	}

	private static void jsTreeJson(HttpServletResponse response,
			LinkedHashMap<Protocol, List<Measurement>> mesurementsByProtocol) throws IOException
	{
		try
		{
			List<JSONObject> tableNodes = new ArrayList<JSONObject>();

			for (Map.Entry<Protocol, List<Measurement>> entry : mesurementsByProtocol.entrySet())
			{
				String tableName = entry.getKey().getName();
				Integer protocolId = entry.getKey().getId();

				JSONObject tableNode = new JSONObject();
				tableNode.put("data", tableName);
				tableNode.put("attr", new JSONObject().put("id", protocolId));
				tableNode.put("metadata", new JSONObject().put("id", protocolId));

				List<JSONObject> children = new ArrayList<JSONObject>();
				for (Measurement measurement : entry.getValue())
				{
					JSONObject columnNode = new JSONObject();
					columnNode.put("data", measurement.getName());
					columnNode.put("attr", new JSONObject().put("id", protocolId + "." + measurement.getId()));
					columnNode.put("metadata", new JSONObject().put("id", protocolId + "." + measurement.getId()));
					children.add(columnNode);
				}
				tableNode.put("children", children);
				tableNodes.add(tableNode);
			}
			JSONArray data = new JSONArray(tableNodes);

			PrintWriter out = response.getWriter();
			System.out.println(data.toString());
			out.println(data.toString());
			out.flush();
		}
		catch (Exception e)
		{
			HandleException.handle(e);
		}
	}

	private void download(HttpServletRequest req, HttpServletResponse resp, 
			Exporter<ObservationTarget, Measurement, ObservedValue> exporter, 
			boolean exportVisable)
			throws IOException, MatrixException
	{
		String filename = "matrix" + exporter.getFileExtension();
		
		ServletOutputStream op = resp.getOutputStream();
		ServletContext context = getServletConfig().getServletContext();
		String mimetype = context.getMimeType(filename);
		if(StringUtils.isEmpty(mimetype)) {
			exporter.getMimeType();
		}
		
		resp.setContentType((mimetype != null) ? mimetype : "application/octet-stream");
		//resp.setContentLength((int) f.length());
		resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

		if (exportVisable)
		{
			exporter.exportVisible();
		}
		else
		{
			exporter.exportAll();
		}

		op.flush();
		op.close();
	}
}
