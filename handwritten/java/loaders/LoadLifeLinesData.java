package loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.protocol.ProtocolApplication;

import app.JDBCDatabase;

public class LoadLifeLinesData {
	public static void main(String[] args) throws Exception {
		int paIdx = 0; //name for protocal Application
		int investigationId = 1;
		
		JDBCDatabase db = new JDBCDatabase("/gcc/handwritten/apps/org/molgenis/biobank/bbmri.molgenis.properties/bbmri.molgenis.properties");
		
		Hashtable<String, Integer> fkMis = new Hashtable<String, Integer>();

		// Directory to the data dictionary
		//File dir = new File("/Users/jorislops/Downloads/LLFenotype");
		File dir = new File("/Users/despoina/Documents/LifelinesData/data_dictionary"); 

		try {
			db.beginTx();
			for (File file : dir.listFiles()) {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String headerLine = reader.readLine();
				String[] columns = headerLine.split(";");

				Measurement[] features = new Measurement[columns.length];

				String fileName = file.getName();
				String tableName = fileName.substring(0, 
						fileName.length() - 4);				
				List<Protocol> protocols = db.find(Protocol.class, new QueryRule("name",
						Operator.EQUALS, tableName));				
				
				for (int i = 0; i < columns.length; ++i) {
					List<Measurement> fList = db.find(
							Measurement.class, new QueryRule("name",
									Operator.EQUALS, tableName
											+ "."
											+ columns[i].toLowerCase()
													.trim()));
					if (fList.size() > 0) {
						features[i] = fList.get(0);
					}
				}

				while (reader.ready()) {
					String line = reader.readLine();
					String[] values = line.split(";");
					
					ObservationTarget target = null;
					for (int i = 0; i < values.length; ++i) {
						if(columns[i].equals("DNID")) {
							ObservationTarget example = new ObservationTarget();
							example.setName(values[i]);
							List<ObservationTarget> exampleTargets = db.findByExample(example);
							if(exampleTargets.size() > 0) {
								target = exampleTargets.get(0);
							}
							
							if(target == null) {
								target = new ObservationTarget();
								target.setInvestigation(investigationId);
								target.setName(values[i]);
								db.add(target);
							}
						} else if (values[i].trim() != "" && features[i] != null) {
							if(target != null) {
								ObservedValue ov = new ObservedValue();
								ov.setInvestigation(investigationId);
								ov.setFeature(features[i]);
								ov.setTime(new Date());
								
								Measurement feature = features[i];
								String type = feature.getDataType();
							
								String value = values[i].trim();
								if(value.equals("")) {
									ov.setValue("empty");
								} else if (type.equals("string")) {
									ov.setValue(value);
								} else if (type.equals("int") || type.equals("code")) {
									ov.setValue("incorrect");
									try {
										Integer.parseInt(value);
										ov.setValue(value);
									} catch (NumberFormatException nfe) {
										String errorMsg = "Invalid number: %s in File: %s For FieldName %s";
										System.err.println(String.format(errorMsg, value, fileName, columns[i]));
									}
								} else if (type.equals("datetime")) {
									ov.setValue("incorrect");

									try {
										SimpleDateFormat parse = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
										ov.setValue(new java.sql.Timestamp(parse.parse(value).getTime()).toString());
									} catch (ParseException pe0) {
										try {
											SimpleDateFormat parse = new SimpleDateFormat("MM-dd-yyyy");
											ov.setValue(new java.sql.Timestamp(parse.parse(value).getTime()).toString());
										} catch (ParseException pe1) {
											String errorMsg = "Invalid DateTime: %s in File: %s For FieldName %s";
											System.err.println(String.format(errorMsg, value, fileName, columns[i]));
										}
									}

								} else if (type.equals("decimal")) {		
									ov.setValue("incorrect");
									try {
									    Float.parseFloat(value);
										ov.setValue(value);
									} catch (NumberFormatException nfe) {
										String errorMsg = "Invalid Decimal: %s in File: %s For FieldName %s";
										System.err.println(String.format(errorMsg, value, fileName, columns[i]));
									}									
								} else if (type.equals("image")) {
									ov.setValue("image");
								} else if (type.equals("")) {
									ov.setValue("empty");
								} else {							
									String errorMessage = "DataFile: %s for field: %s has unkown type: %s";
									throw new IllegalArgumentException(String.format(errorMessage, tableName, columns[i], type));
								}
								
								ProtocolApplication pa = new ProtocolApplication();
								pa.setInvestigation(investigationId);
								pa.setName("" + paIdx++);
								
								pa.setProtocol(protocols.get(0));
								pa.setTime(new Date());
								
								db.add(pa);
								
								ov.setProtocolApplication(pa);
								ov.setTarget(target);
	
								db.add(ov);
							}
							//Thread.sleep(1000);
						} else {
							if(fkMis.get(tableName) == null) {
								fkMis.put(tableName, 1);
							} else {
								fkMis.put(tableName, fkMis.get(tableName)+1);
							}
								
						}
					}
				}
			}
			db.commitTx();
		} catch (Exception ex) {
			ex.printStackTrace();
			db.rollbackTx();
		}
		Enumeration<String> e = fkMis.keys(); 
		while(e.hasMoreElements()) {
			String el = e.nextElement();
			System.err.println(el + " " + fkMis.get(el));
		}		
		System.err.println("End!");
	}

}
