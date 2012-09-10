package updateMatrixImporter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.molgenis.datatable.model.CsvTable;
import org.molgenis.datatable.model.MemoryTable;
import org.molgenis.datatable.view.JQGridView;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.model.elements.Field;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.Entity;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;
import org.molgenis.util.ValueLabel;

import com.google.gson.Gson;

public class UpdateMatrixImporter extends PluginModel<Entity> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4743753566046137438L;
	private String report = null;
	private CsvTable csvTable = null;
	private JQGridView tableView = null;
	private String STATUS = "UploadFile";
	private String investigationName = "LifeLines";
	private String tempalteFilePath = null;
	private String jsonForMapping = null;

	private List<String> listOfProtocols = new ArrayList<String>();
	private List<String> colHeaders = new ArrayList<String>();
	private List<String> newFeatures = new ArrayList<String>();
	private List<String> rowHeaders = new ArrayList<String>();
	private List<String> newTargets = new ArrayList<String>();

	public UpdateMatrixImporter(String name, ScreenController<?> parent) {
		super(name, parent);
	}

	@Override
	public String getViewName() {
		return "UpdateMatrixImporter";
	}

	@Override
	public String getViewTemplate() {
		return "UpdateMatrixImporter/UpdateMatrixImporter.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request) throws Exception {

		if (request.getAction().equals("uploadFile")) {

			colHeaders.clear();
			newFeatures.clear();
			rowHeaders.clear();
			newTargets.clear();
			listOfProtocols.clear();
			jsonForMapping = null;

			String fileName = request.getString("uploadFileName");

			File tmpDir = new File(System.getProperty("java.io.tmpdir"));

			File templateMapping = new File(tmpDir.getAbsolutePath()
					+ "/tempalteMapping.xls");

			if (!templateMapping.exists()) {

			}

			tempalteFilePath = templateMapping.getAbsolutePath();

			checkHeaders(db, request, fileName);

			STATUS = "CheckFile";

		} else if (request.getAction().equals("uploadNewFile")) {

			STATUS = "UploadFile";

		} else if (request.getAction().equals("previewFileAction")) {

			STATUS = "previewFile";

			if (csvTable != null) {

				// If there are no new records and columns at all, show all the
				// table
				if (newFeatures.size() == 0 && newTargets.size() == 0) {

					tableView = new JQGridView("Preview", this, csvTable);

				} else if (newTargets.size() > 0) {// If there are new records,
													// show new records only
					String targetString = csvTable.getAllColumns().get(0)
							.getName();

					List<Tuple> newRecords = new ArrayList<Tuple>();

					for (Tuple tuple : csvTable.getRows()) {
						String targetName = tuple.getString(targetString);
						if (newTargets.contains(targetName)) {
							newRecords.add(tuple);
						}
					}

					MemoryTable table = new MemoryTable(newRecords);
					tableView = new JQGridView("Preview", this, table);
				}
			}
		} else if (request.getAction().equals("previousStepSummary")) {

			STATUS = "CheckFile";

		} else if (request.getAction().equals("importUploadFile")) {

			importUploadFile(db, request);

			STATUS = "UploadFile";

		} else if (request.getAction().equals("uploadMapping")) {

			// List<String> listOfMappings = new ArrayList<String>();
			String mappingFileName = request.getString("mappingForColumns");

			CsvTable mappingTable = new CsvTable(new File(mappingFileName));

			mappingClass allMappings = new mappingClass();

			for (Tuple tuple : mappingTable.getRows()) {

				String variableName = tuple.getString("variable");
				String dataType = tuple.getString("datatype");
				String category = tuple.getString("category");
				String table = tuple.getString("table");
				allMappings.addMapping(variableName, dataType, table, category);

			}

			jsonForMapping = new Gson().toJson(allMappings.getMapping());

			// for (String newFeature : newFeatures) {
			//
			// String variableName = newFeature.replaceAll(" ", "_");
			// String dataType = request.getString(variableName + "_dataType");
			// String protocolTable = request.getString(variableName
			// + "_protocolTable");
			// mappingClass newMappingClass = mappingClass.createMappingClass(
			// variableName, dataType, protocolTable);
			//
			// listOfMappings.add(new Gson().toJson(newMappingClass));
			// }

			System.out.println();

		} else if (request.getAction().equals("downloadTemplate")) {

			WorkbookSettings ws = new WorkbookSettings();

			ws.setLocale(new Locale("en", "EN"));

			File tmpDir = new File(System.getProperty("java.io.tmpdir"));

			File mappingResult = new File(tmpDir + File.separator
					+ "template.xls");

			WritableWorkbook workbook = Workbook.createWorkbook(mappingResult,
					ws);

			WritableSheet outputExcel = workbook.createSheet("Sheet1", 0);

			outputExcel.addCell(new Label(0, 0, "Table"));

			outputExcel.addCell(new Label(1, 0, "variable"));

			outputExcel.addCell(new Label(2, 0, "data type"));

			workbook.write();
			workbook.close();

			HttpServletRequestTuple rt = (HttpServletRequestTuple) request;
			HttpServletResponse httpResponse = rt.getResponse();
			// System.out.println(">>> " + this.getParent().getName()+
			// "or >>>  "+ this.getSelected().getLabel());
			// String redirectURL = httpRequest.getRequestURL() + "?__target=" +
			// this.getParent().getName() + "&select=MeasurementsDownloadForm";

			String redirectURL = "tmpfile/template.xls";

			httpResponse.sendRedirect(redirectURL);

		} else if (request.getAction().equals("showNewRecordsOnly")) {

			STATUS = "previewFile";

			String targetString = csvTable.getAllColumns().get(0).getName();

			List<Tuple> newRecords = new ArrayList<Tuple>();

			for (Tuple tuple : csvTable.getRows()) {
				String targetName = tuple.getString(targetString);
				if (newTargets.contains(targetName)) {
					newRecords.add(tuple);
				}
			}

			MemoryTable table = new MemoryTable(newRecords);
			tableView = new JQGridView("Preview", this, table);

		} else {

			if (STATUS.equals("previewFile")) {
				tableView.handleRequest(db, request, null);
			}
		}

	}

	private void importUploadFile(Database db, Tuple request)
			throws DatabaseException {

		try {
			db.beginTx();

			List<Individual> listOfTargets = new ArrayList<Individual>();
			List<Measurement> listOfFeatures = new ArrayList<Measurement>();
			List<ObservedValue> listOfValues = new ArrayList<ObservedValue>();
			List<ProtocolApplication> listOfPA = new ArrayList<ProtocolApplication>();

			List<Field> allColumns = csvTable.getAllColumns();

			String targetString = allColumns.get(0).getName();

			// new rows are added
			if (newTargets.size() > 0) {

				for (String targetName : newTargets) {
					Individual inv = new Individual();
					inv.setName(targetName);
					inv.setInvestigation_Name(investigationName);
					listOfTargets.add(inv);
				}
			}

			// new columns are added.
			if (newFeatures.size() > 0) {
				for (String feature : newFeatures) {
					Measurement m = new Measurement();
					m.setName(feature);
					m.setInvestigation_Name(investigationName);
					listOfFeatures.add(m);
				}
			}

			for (Tuple row : csvTable.getRows()) {

				String targetName = row.getString(targetString);
				// Only add the new records to the database
				String paName = null;

				if (newTargets.contains(targetName)) {
					ProtocolApplication pa = new ProtocolApplication();
					pa.setProtocol_Name("TestProtocol");
					pa.setName("pa_" + row.getString(targetString));
					pa.setInvestigation_Name(investigationName);
					paName = pa.getName();
					listOfPA.add(pa);
				}

				for (Field field : allColumns) {

					String eachColumn = field.getName();

					if (newFeatures.contains(eachColumn) && paName == null
							|| paName != null
							&& !eachColumn.equalsIgnoreCase(targetString)) {

						if (paName == null) {
							if (db.find(
									ObservedValue.class,
									new QueryRule(ObservedValue.TARGET_NAME,
											Operator.EQUALS, targetName))
									.size() > 0) {
								paName = db
										.find(ObservedValue.class,
												new QueryRule(
														ObservedValue.TARGET_NAME,
														Operator.EQUALS,
														targetName)).get(0)
										.getProtocolApplication_Name();
							} else {
								ProtocolApplication pa = new ProtocolApplication();
								pa.setProtocol_Name("TestProtocol");
								pa.setName("pa_" + row.getString(targetString));
								pa.setInvestigation_Name(investigationName);
								paName = pa.getName();
								listOfPA.add(pa);
							}
						}
						ObservedValue ov = new ObservedValue();
						ov.setTarget_Name(row.getString(targetString));
						ov.setFeature_Name(eachColumn);
						ov.setValue(row.getString(eachColumn));
						ov.setInvestigation_Name(investigationName);
						ov.setProtocolApplication_Name(paName);
						listOfValues.add(ov);

					}
				}
			}

			db.add(listOfTargets);
			db.add(listOfFeatures);
			db.add(listOfPA);
			db.add(listOfValues);

			if (newFeatures.size() > 0
					&& db.find(
							Protocol.class,
							new QueryRule(Protocol.NAME, Operator.EQUALS,
									"NotClassified")).size() > 0) {
				Protocol p = db.find(
						Protocol.class,
						new QueryRule(Protocol.NAME, Operator.EQUALS,
								"NotClassified")).get(0);
				p.getFeatures_Name().addAll(newFeatures);

				db.update(p);
			}

			db.commitTx();

		} catch (DatabaseException e) {

			e.printStackTrace();
			db.rollbackTx();
		}
	}

	// public void download_json_test(Database db, Tuple request, OutputStream
	// out)
	// throws HandleRequestDelegationException {
	// // handle requests for the table named 'test'
	//
	// tableView.handleRequest(db, request, out);
	//
	// }

	@Override
	public void reload(Database db) {
		// TODO Auto-generated method stub
		System.out.println("-----------RELOAD!");
	}

	public void checkHeaders(Database db, Tuple request, String filePath) {

		File file = new File(filePath);

		try {

			csvTable = new CsvTable(file);

			String targetString = csvTable.getAllColumns().get(0).getName();

			// check the existing variables in measurements
			for (Field field : csvTable.getAllColumns()) {
				colHeaders.add(field.getName());
				newFeatures.add(field.getName());
			}

			colHeaders.remove(0);
			newFeatures.remove(0);

			List<Measurement> colHeadersMeasurement = db.find(
					Measurement.class, new QueryRule(Measurement.NAME,
							Operator.IN, colHeaders));

			for (Measurement m : colHeadersMeasurement) {
				if (colHeaders.contains(m.getName())) {
					newFeatures.remove(m.getName());
				}
			}

			// check the existing records

			for (Tuple tuple : csvTable.getRows()) {
				rowHeaders.add(tuple.getString(targetString));
				newTargets.add(tuple.getString(targetString));
			}

			List<ObservationTarget> rowHeadersTarget = db.find(
					ObservationTarget.class, new QueryRule(
							ObservationTarget.NAME, Operator.IN, rowHeaders));

			for (ObservationTarget ot : rowHeadersTarget) {
				if (rowHeaders.contains(ot.getName())) {
					newTargets.remove(ot.getName());
				}
			}
			colHeaders.removeAll(newFeatures);
			rowHeaders.removeAll(newTargets);

			report = new Gson().toJson(ReportUploadStatus
					.createReportUploadStatus(colHeaders, newFeatures,
							rowHeaders, newTargets));

			for (Protocol p : db.find(Protocol.class, new QueryRule(
					Protocol.INVESTIGATION_NAME, Operator.EQUALS,
					investigationName))) {
				listOfProtocols.add(p.getName());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public class mappingClass {

		HashMap<String, eachMapping> allMappings = new HashMap<String, eachMapping>();

		public void addMapping(String variableName, String dataType,
				String table, String category) {

			if (allMappings.containsKey(variableName)) {
				allMappings.get(variableName).addCategory(category);
			} else {
				eachMapping newMapping = new eachMapping(variableName,
						dataType, table, category);
				allMappings.put(variableName, newMapping);
			}
		}

		public List<eachMapping> getMapping() {
			return new ArrayList<eachMapping>(allMappings.values());
		}

		private class eachMapping {

			private String variableName;
			private String dataType;
			private String table;
			private List<String> listOfCategories = new ArrayList<String>();

			private eachMapping(String variableName, String dataType,
					String table, String category) {

				this.variableName = variableName;
				this.dataType = dataType;
				this.table = table;
				this.listOfCategories.add(category);
			}

			private void addCategory(String category) {
				this.listOfCategories.add(category);
			}
		}
	}

	public static class ReportUploadStatus {

		List<String> colHeaders;
		List<String> newFeatures;
		List<String> rowHeaders;
		List<String> newTargets;

		public static ReportUploadStatus createReportUploadStatus(
				List<String> colHeaders, List<String> newFeatures,
				List<String> rowHeaders, List<String> newTargets) {
			ReportUploadStatus instance = new ReportUploadStatus();
			instance.colHeaders = colHeaders;
			instance.newFeatures = newFeatures;
			instance.rowHeaders = rowHeaders;
			instance.newTargets = newTargets;
			return instance;
		}
	}

	public String getReport() {
		return report;
	}

	public String getSTATUS() {
		return STATUS;
	}

	public String getTableView() {
		return tableView.getHtml();
	}

	public List<String> getFeatureDataTypes() throws Exception {

		List<String> dataTypes = new ArrayList<String>();
		for (ValueLabel label : Measurement.class.newInstance()
				.getDataTypeOptions()) {
			dataTypes.add(label.getLabel());
		}

		return dataTypes;
	}

	public List<String> getProtocolTables() throws Exception {

		return listOfProtocols;
	}

	public String getTempalteFilePath() {
		return tempalteFilePath;
	}

	public String getUrl() {
		return "molgenis.do?__target=" + this.getName();
	}

	public String getJsonForMapping() {
		return jsonForMapping;
	}

}