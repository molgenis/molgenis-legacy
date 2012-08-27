//package converters.opal;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.log4j.BasicConfigurator;
//import org.apache.log4j.Logger;
//import org.molgenis.core.OntologyTerm;
//import org.molgenis.data.Data;
//import org.molgenis.data.TextDataElement;
//import org.molgenis.organization.Investigation;
//import org.molgenis.pheno.Code;
//import org.molgenis.pheno.Individual;
//import org.molgenis.pheno.Measurement;
//import org.molgenis.pheno.Observation;
//import org.molgenis.pheno.ObservedValue;
//import org.molgenis.util.CsvReaderListener;
//import org.molgenis.util.ExcelReader;
//import org.molgenis.util.Tuple;
//
//import app.JDBCDatabase;
//
//public class ConvertOpalExcelToPheno
//{
//	final Logger logger = Logger.getLogger(ConvertOpalExcelToPheno.class);
//
//	// maps to Opal.Table
//	final Map<String, Investigation> investigations = new LinkedHashMap<String, Investigation>();
//	// each study has one data table (not entirely true!)
//	final List<Data> datas = new ArrayList<Data>();
//
//	// maps to Opal.Variables
//	final List<Measurement> measurements = new ArrayList<Measurement>();
//
//	// maps to Opal.Categories
//	final List<Code> codes = new ArrayList<Code>();
//
//	// maps to Opal.Participant
//	final List<Individual> individuals = new ArrayList<Individual>();
//
//	// list of unit
//	final Map<String, OntologyTerm> units = new LinkedHashMap<String, OntologyTerm>();
//
//	// maps to Opal.Table.Cell
//	final List<Observation> values = new ArrayList<Observation>();
//
//	// indicator to use pheno or xgap like values
//	boolean xgap = true;
//
//	public static void main(String[] args) throws Exception
//	{
//		new ConvertOpalExcelToPheno();
//	}
//
//	public ConvertOpalExcelToPheno() throws Exception
//	{
//		// start logger
//		BasicConfigurator.configure();
//
//		File opalExcel = new File("/Users/mswertz/Downloads/view-export.xls");
//
//		// read file
//		loadVariables(opalExcel);
//		loadCategories(opalExcel);
//		loadDataTabel(opalExcel, "healthy-obese");
//
//		// store in database
//		JDBCDatabase db = new JDBCDatabase(
//				"modules/xgap/org/molgenis/xgap/xqtlworkbench/xqtl.properties");
//		try
//		{
//			db.beginTx();
//			db.add(new ArrayList<Investigation>(investigations.values()));
//			db.add(individuals);
//			db.add(new ArrayList<OntologyTerm>(units.values()));
//			for (Measurement m : measurements)
//			{
//				logger.debug("adding: " + m);
//				db.add(m);
//			}
//			db.add(codes);
//			if(xgap) db.add(datas);
//			db.add(values);
//			db.commitTx();
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//			db.rollbackTx();
//		}
//	}
//
//	private void loadVariables(File opalExcel) throws Exception
//	{
//		ExcelReader reader = new ExcelReader(opalExcel, "Variables");
//		logger.info("loading variables");
//
//		reader.parse(new CsvReaderListener()
//		{
//
//			@Override
//			public void handleLine(int lineNumber, Tuple tuple)
//					throws Exception
//			{
//				// mapping Opal.Variable -> pheno.Measurement
//				Measurement m = new Measurement();
//
//				// table -> study
//				if (investigations.get(tuple.getString("table")) == null)
//				{
//					Investigation investigation = new Investigation();
//					investigation.setName(tuple.getString("table"));
//					investigations.put(investigation.getName(), investigation);
//				}
//				m.setInvestigation_Name(tuple.getString("table"));
//
//				// name -> name
//				m.setName(tuple.getString("name"));
//
//				// valueType -> dataType
//				String dataType = tuple.getString("valueType");
//				if (dataType.equals("integer")) dataType = "int";
//				m.setDataType(dataType);
//
//				// mimeType -> IGNORED
//				// unit -> unit
//				if (!tuple.isNull("unit"))
//				{
//					if (units.get(tuple.getString("unit")) == null)
//
//					{
//						OntologyTerm unit = new OntologyTerm();
//						unit.setName(tuple.getString("unit"));
//						units.put(unit.getName(), unit);
//					}
//					m.setUnit_Name(tuple.getString("unit"));
//				}
//
//				// repeatable -> temporal
//				m.setTemporal(tuple.getBool("repeatable") ? true : false);
//
//				// occurrenceGroup -> IGNORED
//				// label:en -> description
//				m.setDescription(tuple.getString("label:en"));
//
//				// script -> IGNORED, todo
//				// variableUri -> IGNORED, should become DbXref
//				// dataschemaUri -> IGNORED, should become DbXref
//				// projectUri -> IGNORED, should become DbXref
//
//				logger.debug(m);
//				measurements.add(m);
//
//			}
//		});
//	}
//
//	/**
//	 * Maps Opal.Category -> Pheno.Code
//	 * <ul>
//	 * <li>table -> IGNORED?
//	 * <li>variable -> feature
//	 * <li>code -> code
//	 * <li>missing -> missing (new)
//	 * <li>label:en -> name
//	 * </ul>
//	 * 
//	 * @param opalExcel
//	 * @throws Exception
//	 */
//	private void loadCategories(File opalExcel) throws Exception
//	{
//		ExcelReader reader = new ExcelReader(opalExcel, "Categories");
//		logger.info("loading categories");
//
//		reader.parse(new CsvReaderListener()
//		{
//
//			@Override
//			public void handleLine(int lineNumber, Tuple tuple)
//					throws Exception
//			{
//				// map Opal.Category -> Pheno.Code
//				Code c = new Code();
//
//				// table -> investigation
//				// c.setInvestigation(tuple.getString("table"));
//
//				// variable -> feature
//				c.setFeature_Name(tuple.getString("variable"));
//
//				// name -> codeString
//				c.setCodeString(tuple.getString("name"));
//
//				// missing -> missing (new)
//				c.setMissing(tuple.getBool("missing"));
//
//				// label:en -> name
//				c.setDescription(tuple.getString("label:en"));
//
//				logger.debug(c);
//				codes.add(c);
//			}
//		});
//	}
//
//	private void loadDataTabel(File opalExcel, final String sheetName)
//			throws Exception
//	{
//		// matrix with ObservedValue(
//		// investigation_name=sheetName,
//		// target_name=row.'Entity ID',
//		// feature_name=row.<colname>,
//		// value=row.get(<colname>)
//		// )
//
//		ExcelReader reader = new ExcelReader(opalExcel, sheetName);
//		logger.info("loading values for data table: " + sheetName);
//
//		final Data data = new Data();
//		if (xgap)
//		{
//			data.setName(sheetName);
//			data.setInvestigation_Name(sheetName);
//			data.setFeatureType("Measurement");
//			data.setTargetType("Individual");
//			data.setValueType("Text");
//			data.setStorage("Database");
//			datas.add(data);
//
//		}
//
//		reader.parse(new CsvReaderListener()
//		{
//
//			@Override
//			public void handleLine(int lineNumber, Tuple tuple)
//					throws Exception
//			{
//				int featureIndex = 0;
//				for (String featureName : tuple.getFields())
//				{
//					if ("Entity ID".equals(featureName))
//					{
//						Individual i = new Individual();
//						i.setName(tuple.getString(featureName));
//						i.setInvestigation_Name(sheetName);
//						logger.debug(i);
//						individuals.add(i);
//					}
//					else
//					{
//						if (xgap)
//						{
//							TextDataElement v = new TextDataElement();
//							v.setData_Name(data.getName());
//							v.setInvestigation_Name(sheetName);
//							v.setFeature_Name(featureName);
//							v.setFeatureIndex(featureIndex++);
//							v.setTarget_Name(tuple.getString("Entity ID"));
//							v.setTargetIndex(lineNumber-1);
//							v.setValue(tuple.getString(featureName));
//							logger.debug(v);
//							values.add(v);
//						}
//						else
//						{
//							ObservedValue v = new ObservedValue();
//							v.setInvestigation_Name(sheetName);
//							v.setFeature_Name(featureName);
//							v.setTarget_Name(tuple.getString("Entity ID"));
//							v.setValue(tuple.getString(featureName));
//							logger.debug(v);
//							values.add(v);
//						}
//					}
//				}
//			}
//		});
//	}
//}
