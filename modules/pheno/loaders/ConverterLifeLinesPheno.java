//package loaders;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//
//import jxl.Cell;
//import jxl.JXLException;
//import jxl.Sheet;
//import jxl.Workbook;
//
//
//import org.molgenis.framework.db.DatabaseException;
////import org.molgenis.util.cmdline.CmdLineException;
//
//import org.molgenis.pheno.Code_feature; //import pheno.feature.Code;
//import org.molgenis.pheno.ObservableFeature; //import pheno.feature.ObservableFeature;
//import org.molgenis.protocol.Protocol; //import pheno.protocol.Protocol;
//import org.molgenis.organization.Investigation; //import pheno.system.Investigation;
//
//
//import app.JpaDatabase;
//import java.util.HashMap;
//
//import javax.persistence.EntityManager;
//
///*
// * Datadictonary is used to make EAV structure in pheno model
// */
//public class ConverterLifeLinesPheno {
//
//	//private String fileName = "/Users/jorislops/Desktop/Archive/datadescription/Top10.nl.xls";
//	private String fileName = "/Users/despoina/Documents/LifelinesData/data_dictionary/datadescription/Top10.nl.xls";
//        private String dataSetName = "Top10";
//
//	public ConverterLifeLinesPheno() throws JXLException, IOException,
//			DatabaseException {
//                int databeginRow = 1;
//		JpaDatabase database = new JpaDatabase();
//		EntityManager em = database.getEntityManager();
//
//		try {
//			Investigation investigation = new Investigation();
//			investigation.setAccession("accession");
//			investigation.setDescription("Life Lines Study Dev");
//			investigation.setName("Life Lines Study Dev");
//			
//			//database.add(investigation);
//			em.getTransaction().begin();
//			em.persist(investigation);
//			em.getTransaction().commit();
//
//			Workbook workbook = Workbook.getWorkbook(new File(fileName));
//
//                        
//                        
//			HashMap<String, Code> codes = new HashMap<String, Code>();
//			// List<Code> codes = new ArrayList<Code>();
//
//
//			
//			for (Sheet sheet : workbook.getSheets()) {
//				if (sheet.getName().contains("DB INFO")) {
//					continue;
//				} else if(sheet.getName().contains("Voorblad")) {
//					continue;
//				}
//				
//				em.getTransaction().begin();
//				List<Integer> featureIds = new ArrayList<Integer>();
//				ObservableFeature feature = null;
//				for (int rowIdx = databeginRow; rowIdx < sheet.getRows(); ++rowIdx) {
//					Cell[] cells = sheet.getRow(rowIdx);
//					if (cells.length == 0) {
//						continue;
//					}
//
//					if (!cells[0].getContents().isEmpty()) {
//						String fieldName = cells[0].getContents();
//						String type = cells[1].getContents();
//						// String width = cells[2].getContents();
//						// String min = cells[3].getContents();
//						// String max = cells[4].getContents();
//						String description = cells[5].getContents();
//
//						String codeValue = null;
//						if (cells.length >= 7) {
//							codeValue = cells[6].getContents();
//						}
//						// String group = cells[8].getContents();
//						// String nr = cells[9].getContents();
//						// String pKey = cells[10].getContents();
//						// String rq = cells[11].getContents();
//						// String un = cells[12].getContents();
//						// String ids = cells[13].getContents();
//						//					
//						// String refField = cells[19].getContents();
//
//						// fill Observable Feature Object
//						feature = new ObservableFeature();
//
//                                                String datasetName = new File(fileName).getName().toLowerCase();
//                                                feature.setName(datasetName.split("\\.")[0] + "."
//								+ fieldName.toLowerCase());
//
////                                                feature.setName(sheet.getName().toLowerCase() + "."
////								+ fieldName.toLowerCase());
//
//
//						feature.setInvestigation(investigation);
//						description = description.replace("'", " ").replace(
//								'\\', ' ');
//
//                                                description = description.substring(0, description.length() >= 255 ? 244 : description.length());
//                                                feature.setDescription(description);
//
//						if (type.equals("varchar") || type.equals("nvarchar") || type.equals("text")) {
//							feature.setDataType("string");
//						} else if (type.equals("int")) {
//							feature.setDataType("int");
//						} else if (type.equals("datetime")) {
//							feature.setDataType("datetime");
//						} else if (type.equals("tinyint")) {
//							if (codeValue == null) {
//								feature.setDataType("int");
//							} else {
//								feature.setDataType("code");
//							}
//						} else if (type.equals("smallint")) {
//							feature.setDataType("int");
//						} else if (type.equals("numeric") || type.equals("decimal")) {		
//							feature.setDataType("decimal");
//						} else if (type.equals("image")) {
//							feature.setDataType("image"); 
//						} else if (type.equals("")) {
//							feature.setDataType("unkown");
//						} else {							
//							String sheetName = sheet.getName();
//							String errorMessage = "Sheet: %s for field: %s has unkown type: %s";
//							throw new IllegalArgumentException(String.format(errorMessage, sheetName, fieldName, type));
//						}
//						
//						em.persist(feature);
//
//						featureIds.add(feature.getId());
//
//						if (codeValue != null && !codeValue.trim().isEmpty()) {
//							String[] index = codeValue.split("=");
//							if (index.length >= 2) {
//								processCode(cells, feature, codes, em);
//							} else {
//								System.err.println("Empty Code!");
//							}
//						}
//						// features.add(feature);
//					} else if (cells.length == 7
//							&& !cells[6].getContents().trim().isEmpty()) {
//						// proces labels
//						processCode(cells, feature, codes, em);
//					}
//				}
//
//				Protocol protocol = new Protocol();
//                                protocol.setDescription(dataSetName);
//                                protocol.setName(dataSetName);
//
//				//protocol.setDescription(sheet.getName().toLowerCase());
//				protocol.setInvestigation(investigation);
//				//protocol.setName(sheet.getName().toLowerCase());
//
//				protocol.setObservableFeatures_Id(featureIds);
//				
//				em.persist(protocol);
//				
//				em.getTransaction().commit();
//				//database.add(protocol);
//			}
//
//                        //store codes
//			em.getTransaction().begin();
//			for (Code c : codes.values()) {
//                            em.persist(c);
//			}			
//			em.getTransaction().commit();
//			// CodeMapper codeMapper = new CodeMapper(database);
//		} catch (Exception e) {
//			System.err.println(e.getMessage());
//			e.printStackTrace();
//			database.rollbackTx();
//		}  finally {
//			database.close();
//		}
//		System.out.println("END");
//	}
//
//	public static void processCode(Cell[] cells, ObservableFeature feature,
//			HashMap<String, Code> codes, EntityManager em) {
//		String[] index = cells[6].getContents().split("=");
//		String value = index[1].trim();
//
//		Code code = codes.get(value);
//		if (code == null) {
//			code = new Code();
//		}
//
//		code.getFeature().add(feature);
////		code.getFeature().add(feature.getId());
//
//		// code.setFeature(feature);
//		code.setCode(value);
//		code.setDescription(cells[6].getContents().trim());
//		
//		if(codes.containsKey(value)) {
//			em.persist(code);
//		}
//		
//		codes.put(value, code);
//	}
//
//	/**
//	 * @param args
//	 * @throws CmdLineException
//	 * @throws DatabaseException
//	 */
//	public static void main(String[] args) throws DatabaseException {
//		try {
//			new ConverterLifeLinesPheno();
//		} catch (JXLException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		System.err.println("EIND!!!!");
//	}
//
//}
