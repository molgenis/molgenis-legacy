///* Date:        February 7, 2011
// * Template:	PluginScreenJavaTemplateGen.java.ftl
// * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
// *
// * Despoina Antonakaki antonakd@gmail.com
// */
//
//package plugins.LifelinesData;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//import jxl.Cell;
//import jxl.Sheet;
//import jxl.Workbook;
//import jxl.read.biff.BiffException;
//
//import org.molgenis.framework.db.Database;
//import org.molgenis.framework.db.DatabaseException;
//import org.molgenis.framework.ui.PluginModel;
//import org.molgenis.framework.ui.ScreenModel;
//import org.molgenis.organization.Investigation;
//import org.molgenis.pheno.Code;
//import org.molgenis.pheno.Measurement;
//import org.molgenis.protocol.Protocol;
//import org.molgenis.util.Tuple;
//
//public class LifelinesDataConvertToPhenoModel extends PluginModel
//{
//	//TODO : REMOVE HARDCODED !!!!!!!
//
//	//private String fileName = "/Users/jorislops/Desktop/Archive/datadescription/Top10.nl.xls";
//	private String fileName = "/Users/despoina/Documents/LifelinesData/data_dictionary/datadescription/Top10.nl.xls";
//    private String dataSetName = "Top10";
//        
//	public LifelinesDataConvertToPhenoModel(String name, ScreenModel parent)
//	{
//		super(name, parent);
//	}
//
//	@Override
//	public String getViewName()
//	{
//		return "plugins_LifelinesData_LifelinesDataConvertToPhenoModel";
//	}
//
//	@Override
//	public String getViewTemplate()
//	{
//		return "plugins/LifelinesData/LifelinesDataConvertToPhenoModel.ftl";
//	}
//
//	@Override
//	public void handleRequest(Database db, Tuple request)
//	{
//
//		if ("ConvertLifelinesDataToPhenoModel".equals(request.getAction())) {
//			
//			try {
//					int databeginRow = 1;
//
//					db.beginTx();
//					
//					Investigation investigation  =  new Investigation();
//					investigation.setAccession("accession");
//					investigation.setDescription("Life Lines Study Dev1");
//					investigation.setName("Life Lines Study Dev1");
//									
//					db.add(investigation);
//					//db.commitTx();
//
//					Workbook workbook = Workbook.getWorkbook(new File(fileName));
//					
//					HashMap<String, Code> codes = new HashMap<String, Code>();
//					// List<Code> codes = new ArrayList<Code>();
//					
//					for (Sheet sheet : workbook.getSheets()) {
//						if (sheet.getName().contains("DB INFO")) {
//							continue;
//						} else if(sheet.getName().contains("Voorblad")) {
//							continue;
//						}
//						
//						List<Integer> featureIds = new ArrayList<Integer>();
//						Measurement measurement = null;
//						for (int rowIdx = databeginRow; rowIdx < sheet.getRows(); ++rowIdx) {
//							Cell[] cells = sheet.getRow(rowIdx);
//
//							if (cells.length == 0) {
//								continue;
//							}
//							
//							if (!cells[0].getContents().isEmpty()) {
//								String fieldName = cells[0].getContents();
//								String type = cells[1].getContents();
//								// String width = cells[2].getContents();
//								// String min = cells[3].getContents();
//								// String max = cells[4].getContents();
//								String description = cells[5].getContents();
//
//								String codeValue = null;
//								if (cells.length >= 7) {
//									codeValue = cells[6].getContents();
//								}
//								// String group = cells[8].getContents();
//								// String nr = cells[9].getContents();
//								// String pKey = cells[10].getContents();
//								// String rq = cells[11].getContents();
//								// String un = cells[12].getContents();
//								// String ids = cells[13].getContents();
//								//					
//								// String refField = cells[19].getContents();
//
//								// fill Observable Feature Object		
//								measurement = new Measurement();
//
//                                String datasetName = new File(fileName).getName().toLowerCase();
//                                measurement.setName(datasetName.split("\\.")[0] + "."  + fieldName.toLowerCase());
//						
//						//                                feature.setName(sheet.getName().toLowerCase() + "."
//						//				+ fieldName.toLowerCase());
//						
//						
//								measurement.setInvestigation(investigation);
//								description = description.replace("'", " ").replace(
//										'\\', ' ');
//						
//						        description = description.substring(0, description.length() >= 255 ? 244 : description.length());
//						        measurement.setDescription(description);
//						
//								if (type.equals("varchar") || type.equals("nvarchar") || type.equals("text")) {
//									measurement.setDataType("string");
//								} else if (type.equals("int")) {
//									measurement.setDataType("int");
//								} else if (type.equals("datetime")) {
//									measurement.setDataType("datetime");
//								} else if (type.equals("tinyint")) {
//									if (codeValue == null) {
//										measurement.setDataType("int");
//									} else {
//										measurement.setDataType("code");
//									}
//								} else if (type.equals("smallint")) {
//									measurement.setDataType("int");
//								} else if (type.equals("numeric") || type.equals("decimal")) {		
//									measurement.setDataType("decimal");
//								} else if (type.equals("image")) {
//									measurement.setDataType("image"); 
//								} else if (type.equals("")) {
//									measurement.setDataType("unkown");
//								} else {							
//									String sheetName = sheet.getName();
//									String errorMessage = "Sheet: %s for field: %s has unkown type: %s";
//									throw new IllegalArgumentException(String.format(errorMessage, sheetName, fieldName, type));
//								}
//								
//								db.add(measurement);
//								//db.commitTx();
//
//								
//								featureIds.add(measurement.getId());
//								
//								
//								if (codeValue != null && !codeValue.trim().isEmpty()) {
//									String[] index = codeValue.split("=");
//									if (index.length >= 2) {
//										processCode(db, cells, measurement, codes);
//									} else {
//										System.err.println("Empty Code!");
//									}
//								}
//								// features.add(feature);
//							} else if (cells.length == 7
//									&& !cells[6].getContents().trim().isEmpty()) {
//								// proces labels
//								processCode(db, cells, measurement, codes);
//							}
//						}
//
//						Protocol protocol = new Protocol();
//		                protocol.setDescription(dataSetName);
//		                protocol.setName(dataSetName);
//
//						//protocol.setDescription(sheet.getName().toLowerCase());
//						protocol.setInvestigation(investigation);
//						//protocol.setName(sheet.getName().toLowerCase());
//
//						protocol.setFeatures_Id(featureIds);
//						
//						db.add(protocol);
//						//db.commitTx();
//
//						//protocol.setObservableFeatures_Id(featureIds);
//						//database.add(protocol);
//					}
//		            //store codes
//					for (Code c : codes.values()) {
//						db.add(c);
//						//db.commitTx();
//					}
//					// CodeMapper codeMapper = new CodeMapper(database);					
//					db.commitTx();
//	
//			} catch (DatabaseException e) {
//				e.printStackTrace();
//			}catch (IOException e) {
//				e.printStackTrace();
//			} catch (BiffException e) {
//				e.printStackTrace();
//			}			
//			System.out.println("END");
//	
//		}
//	}
//	
//	
//
//	public static void processCode(Database db, Cell[] cells, Measurement measurement, HashMap<String, Code> codes) {
//		String[] index = cells[6].getContents().split("=");
//		String value = index[1].trim();
//
//		Code code = codes.get(value);
//		if (code == null) {
//			code = new Code();
//		}
//
//		code.getFeature().add(measurement);
////		code.getFeature().add(feature.getId());
//
//		// code.setFeature(feature);
//		code.setCode(value);
//		code.setDescription(cells[6].getContents().trim());
//		
//		if(codes.containsKey(value)) {
//			try {
//				db.add(code);
//			} catch (DatabaseException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			//em.persist(code);
//		}
//		
//		codes.put(value, code);
//	}
//
//
//	@Override
//	public void reload(Database db)
//	{
////		try
////		{
////			Database db = this.getDatabase();
////			Query q = db.query(Experiment.class);
////			q.like("name", "test");
////			List<Experiment> recentExperiments = q.find();
////			
////			//do something
////		}
////		catch(Exception e)
////		{
////			//...
////		}
//	}
//	
//	@Override
//	public boolean isVisible()
//	{
//		//you can use this to hide this plugin, e.g. based on user rights.
//		//e.g.
//		//if(!this.getLogin().hasEditPermission(myEntity)) return false;
//		return true;
//	}
//}
