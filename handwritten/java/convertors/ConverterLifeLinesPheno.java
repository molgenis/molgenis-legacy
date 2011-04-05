package convertors;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import jxl.Cell;
import jxl.JXLException;
import jxl.Sheet;
import jxl.Workbook;

import org.molgenis.MolgenisOptions;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Code;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.cmdline.CmdLineException;

import app.JDBCDatabase;

public class ConverterLifeLinesPheno {

	private String fileName = "/Users/jorislops/Desktop/LifeLinesData/2006004 - Datadictionary - 1.30.45.xls";

	public ConverterLifeLinesPheno() throws JXLException, IOException,
			CmdLineException, DatabaseException {

		JDBCDatabase database = new JDBCDatabase(new MolgenisOptions(
				"molgenis.properties"));
		int investigationId = -1;

		try {
			database.beginTx();

			Investigation investigation = new Investigation();
			investigation.setAccession("accession");
			investigation.setDescription("Life Lines Study Dev");
			investigation.setName("Life Lines Study Dev");

			database.add(investigation);
			investigationId = investigation.getId();

			Workbook workbook = Workbook.getWorkbook(new File(fileName));

			Hashtable<String, Code> codes = new Hashtable<String, Code>();
			// List<Code> codes = new ArrayList<Code>();

			for (Sheet sheet : workbook.getSheets()) {
				if (sheet.getName().contains("DB INFO")) {
					continue;
				} else if(sheet.getName().contains("Voorblad")) {
					continue;
				}

				List<Integer> featureIds = new ArrayList<Integer>();
				Measurement feature = null;
				for (int rowIdx = 11; rowIdx < sheet.getRows(); ++rowIdx) {
					Cell[] cells = sheet.getRow(rowIdx);
					if (cells.length == 0) {
						continue;
					}

					if (cells[0].getContents() != "") {
						String fieldName = cells[0].getContents();
						String type = cells[1].getContents();
						// String width = cells[2].getContents();
						// String min = cells[3].getContents();
						// String max = cells[4].getContents();
						String description = cells[5].getContents();

						String codeValue = null;
						if (cells.length >= 7) {
							codeValue = cells[6].getContents();
						}
						// String group = cells[8].getContents();
						// String nr = cells[9].getContents();
						// String pKey = cells[10].getContents();
						// String rq = cells[11].getContents();
						// String un = cells[12].getContents();
						// String ids = cells[13].getContents();
						//					
						// String refField = cells[19].getContents();

						// fill Observable Feature Object
						feature = new Measurement();
						feature.setName(sheet.getName().toLowerCase() + "."
								+ fieldName.toLowerCase());
						feature.setInvestigation(investigationId);
						description = description.replace("'", " ").replace(
								'\\', ' ');
						feature.setDescription(description);

						if (type.equals("varchar") || type.equals("nvarchar") || type.equals("text")) {
							feature.setDataType("string");
						} else if (type.equals("int")) {
							feature.setDataType("int");
						} else if (type.equals("datetime")) {
							feature.setDataType("datetime");
						} else if (type.equals("tinyint")) {
							if (codeValue == null) {
								feature.setDataType("int");
							} else {
								feature.setDataType("code");
							}
						} else if (type.equals("smallint")) {
							feature.setDataType("int");
						} else if (type.equals("numeric") || type.equals("decimal")) {		
							feature.setDataType("decimal");
						} else if (type.equals("image")) {
							feature.setDataType("image"); 
						} else if (type.equals("")) {
							feature.setDataType("unkown");
						} else {							
							String sheetName = sheet.getName();
							String errorMessage = "Sheet: %s for field: %s has unkown type: %s";
							throw new IllegalArgumentException(String.format(errorMessage, sheetName, fieldName, type));
						}

						database.add(feature);
						featureIds.add(feature.getId());

						if (codeValue != null && codeValue.trim() != "") {
							String[] index = codeValue.split("=");
							if (index.length >= 2) {
								processCode(cells, feature, codes);
							} else {
								System.err.println("Empty Code!");
							}
						}
						// features.add(feature);
					} else if (cells.length == 7
							&& cells[6].getContents().trim() != "") {
						// proces labels
						processCode(cells, feature, codes);
					}
				}

				Protocol protocol = new Protocol();
				protocol.setDescription(sheet.getName().toLowerCase());
				protocol.setInvestigation(investigationId);
				protocol.setName(sheet.getName().toLowerCase());

				protocol.setFeatures_Id(featureIds);
				database.add(protocol);
			}

			Collection<Code> codeList = codes.values();
			List<Code> persist = new ArrayList<Code>();
			for (Code c : codeList) {
				persist.add(c);
			}
			database.add(persist);
			database.commitTx();
			// CodeMapper codeMapper = new CodeMapper(database);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			database.rollbackTx();
		}  finally {
			database.close();
		}
	}

	public static void processCode(Cell[] cells, ObservableFeature feature,
			Hashtable<String, Code> codes) {
		String[] index = cells[6].getContents().split("=");
		String value = index[1].trim();

		Code code = codes.get(value);
		if (code == null) {
			code = new Code();
		}
		//getFeature()==List<Integer> or List<Feature> :-(((
		//getFeature_Id() == List<Integer>
		code.getFeature().add(feature.getId());

		// code.setFeature(feature);
		code.setCode_String(value);
		code.setDescription(cells[6].getContents().trim());
		codes.put(value, code);
	}

	/**
	 * @param args
	 * @throws CmdLineException
	 * @throws DatabaseException
	 */
	public static void main(String[] args) throws CmdLineException,
			DatabaseException {
		try {
			new ConverterLifeLinesPheno();
		} catch (JXLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.err.println("EIND!!!!");
	}

}
