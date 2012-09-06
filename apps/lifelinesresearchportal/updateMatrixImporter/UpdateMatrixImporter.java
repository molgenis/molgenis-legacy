package updateMatrixImporter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

			String fileName = request.getString("uploadFileName");

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

		} catch (Exception e) {
			e.printStackTrace();
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
}