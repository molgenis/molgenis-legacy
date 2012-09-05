package plugins;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jxl.Cell;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;

import app.DatabaseFactory;

public class OpalIndividualDataExporter {

	public Database db = null;

	public String investigationName = null;

	public static void main(String args[]) throws Exception {

		new OpalIndividualDataExporter();
	}

	public OpalIndividualDataExporter() throws Exception {

		db = DatabaseFactory.create();

		investigationName = "LifeLines";

		List<ObservationTarget> listOfTargets = db.find(
				ObservationTarget.class, new QueryRule(
						ObservationTarget.INVESTIGATION_NAME, Operator.EQUALS,
						investigationName));

		List<String> listOfTargetNames = new ArrayList<String>();

		for (ObservationTarget target : listOfTargets) {
			listOfTargetNames.add(target.getName());
		}

		for (Protocol p : db.find(Protocol.class,
				new QueryRule(Protocol.INVESTIGATION_NAME, Operator.EQUALS,
						investigationName))) {

			if (p.getFeatures_Name().size() > 0) {

				File mappingResult = new File(
						"/Users/pc_iverson/Desktop/importData/" + p.getName()
								+ ".csv");

				File temp = new File(
						"/Users/pc_iverson/Desktop/importData/temp.xls");

				OutputStream os = (OutputStream) new FileOutputStream(
						mappingResult);
				String encoding = "UTF8";

				OutputStreamWriter osw = new OutputStreamWriter(os, encoding);

				BufferedWriter bw = new BufferedWriter(osw);

				WorkbookSettings ws = new WorkbookSettings();

				ws.setLocale(new Locale("en", "EN"));

				WritableWorkbook workbook = Workbook.createWorkbook(temp, ws);

				WritableSheet dataSheet = workbook.createSheet("importData", 0);

				dataSheet.addCell(new Label(0, 0, "Participant"));

				for (String eachFeature : p.getFeatures_Name()) {
					int colIndex = p.getFeatures_Name().indexOf(eachFeature) + 1;
					dataSheet.addCell(new Label(colIndex, 0, eachFeature));
				}

				List<ObservedValue> allValues = new ArrayList<ObservedValue>();

				for (int i = 0; i < listOfTargetNames.size(); i = i + 1000) {

					for (int j = 0; j < p.getFeatures_Name().size(); j = j + 1000) {

						System.out.println("next round!");

						Query<ObservedValue> q = db.query(ObservedValue.class);
						int targetIndex = i + 1000;
						if (i + 1000 > listOfTargetNames.size()) {
							targetIndex = listOfTargetNames.size();
						}
						int featureIndex = j + 1000;
						if (j + 1000 > p.getFeatures_Name().size()) {
							featureIndex = p.getFeatures_Name().size();
						}
						q.addRules(new QueryRule(ObservedValue.TARGET_NAME,
								Operator.IN, listOfTargetNames.subList(i,
										targetIndex)));
						q.addRules(new QueryRule(ObservedValue.FEATURE_NAME,
								Operator.IN, p.getFeatures_Name().subList(j,
										featureIndex)));
						allValues.addAll(q.find());
					}
				}

				if (allValues.size() > 0) {

					for (ObservedValue ov : allValues) {

						int rowIndex = listOfTargetNames.indexOf(ov
								.getTarget_Name()) + 1;
						int colIndex = p.getFeatures_Name().indexOf(
								ov.getFeature_Name()) + 1;

						dataSheet.addCell(new Label(0, rowIndex, ov
								.getTarget_Name()));

						dataSheet.addCell(new Label(colIndex, rowIndex, ov
								.getValue()));

					}

					Cell[] row = null;

					// Gets the cells from sheet
					for (int i = 0; i < dataSheet.getRows(); i++) {
						row = dataSheet.getRow(i);

						if (row.length > 0) {
							bw.write(row[0].getContents());
							for (int j = 1; j < row.length; j++) {
								bw.write(',');
								bw.write(row[j].getContents());
							}
						}
						bw.newLine();
					}
				}

				bw.flush();
				bw.close();
				temp.delete();
			}

			System.out.println("finished");
		}
	}
}
