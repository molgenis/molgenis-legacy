package org.molgenis.animaldb.plugins.administration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.molgenis.animaldb.commonservice.CommonService;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;


public class VWAReport5 extends AnimalDBReport {
	
	private ArrayList<ArrayList<String>> matrix = new ArrayList<ArrayList<String>>();
	private List<Integer> nrOfAnimalList = new ArrayList<Integer>();
	private String userName;
	
	public VWAReport5(Database db, String userName) {
		this.userName = userName;
		this.db = db;
		ct = CommonService.getInstance();
		ct.setDatabase(db);
		nrCol = 17;
		warningsList = new ArrayList<String>();
	}
	
	@Override
	public void makeReport(int year, String type) {
		try {
			this.year = year;
			SimpleDateFormat fullFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
			SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
			String startOfYearString = year + "-01-01 00:00:00";
			Date startOfYear = fullFormat.parse(startOfYearString);
			String endOfYearString = (year + 1) + "-01-01 00:00:00";
			Date endOfYear = fullFormat.parse(endOfYearString);
			
			List<String> investigationNames = ct.getOwnUserInvestigationNames(userName);
			List<ObservationTarget> decappList = ct.getAllMarkedPanels("DecApplication", investigationNames);
			for (ObservationTarget d : decappList) {
				String decName = d.getName();
				// Check if the DEC application was (partly) in this year
				Date startOfDec = null;
				String startOfDecString = ct.getMostRecentValueAsString(decName, "StartDate");
				if (startOfDecString != null && !startOfDecString.equals("")) {
					startOfDec = dbFormat.parse(startOfDecString);
					if (startOfDec.after(endOfYear)) {
						continue;
					}
				} else {
					continue;
				}
				Date endOfDec = null;
				String endOfDecString = ct.getMostRecentValueAsString(decName, "EndDate");
				if (endOfDecString != null && !endOfDecString.equals("")) {
					endOfDec = dbFormat.parse(endOfDecString);
					if (endOfDec.before(startOfYear)) {
						continue;
					}
				}
				// Get DEC number
				String decNr = ct.getMostRecentValueAsString(decName, "DecNr");
				// Find the experiments belonging to this DEC
				List<Integer> experimentIdList = new ArrayList<Integer>();
				Query<ObservedValue> q = db.query(ObservedValue.class);
				q.addRules(new QueryRule(ObservedValue.RELATION_NAME, Operator.EQUALS, decName));
				q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "DecApplication"));
				List<ObservedValue> valueList = q.find();
				// Make sure we have a list of unique experiments!
				for (ObservedValue v : valueList) {
					if (!experimentIdList.contains(v.getTarget_Id())) {
						experimentIdList.add(v.getTarget_Id());
					}
				}
				for (int expid : experimentIdList) {
					String expName = ct.getObservationTargetLabel(expid);
					// Get the experiment subcode
					String expCode = ct.getMostRecentValueAsString(expName, "ExperimentNr");
					// Doel vd proef (experiment's Goal)
					String goal = ct.getMostRecentValueAsString(expName, "Goal");
					// Belang van de proef (experiment's Concern)
					String concern = ct.getMostRecentValueAsString(expName, "Concern");
					// Wettelijke bepalingen (experiment's LawDef)
					String lawDef = ct.getMostRecentValueAsString(expName, "LawDef");
					// Toxicologisch / veiligheidsonderzoek	(experiment's ToxRes)
					String toxRes = ct.getMostRecentValueAsString(expName, "ToxRes");
					// Technieken byzondere	(experiment's SpecialTechn)
					String specialTechn = ct.getMostRecentValueAsString(expName, "SpecialTechn");
					
					// Get the animals that were in the experiment this year
					q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.RELATION_NAME, Operator.EQUALS, expName));
					q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Experiment"));
					q.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.LESS_EQUAL, endOfYearString));
					q.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.NOT, null));
					valueList = q.find();
					for (ObservedValue animalInExpValue : valueList) {
						// Get the corresponding protocol application
						int protocolApplicationId = animalInExpValue.getProtocolApplication_Id();
						// Get animal ID
						String animalName = animalInExpValue.getTarget_Name();
						// Get dates
						Date entryDate = animalInExpValue.getTime();
						Date exitDate = animalInExpValue.getEndtime();
						// Check dates
						if (entryDate.after(endOfYear)) {
							continue;
						}
						if (exitDate == null) { // should not be possible
							continue;
						} else if (exitDate.before(startOfYear)) {
							continue;
						}
						
						// Get the data about the animal in the experiment
						// Bijzonderheid dier (animal's AnimalType)
						String animalType = ct.getMostRecentValueAsString(animalName, "AnimalType");
						// Diersoort (animal's Species -> VWASpecies and LatinSpecies)
						String vwaSpecies = "";
						String latinSpecies = "";
						String normalSpecies = ct.getMostRecentValueAsXrefName(animalName, "Species");
						// Get VWA species
						Query<ObservedValue> vwaSpeciesQuery = db.query(ObservedValue.class);
						vwaSpeciesQuery.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, normalSpecies));
						vwaSpeciesQuery.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "VWASpecies"));
						List<ObservedValue> vwaSpeciesValueList = vwaSpeciesQuery.find();
						if (vwaSpeciesValueList.size() == 1) {
							vwaSpecies = vwaSpeciesValueList.get(0).getValue();
						}
						// Get scientific (Latin) species
						Query<ObservedValue> latinSpeciesQuery = db.query(ObservedValue.class);
						latinSpeciesQuery.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, normalSpecies));
						latinSpeciesQuery.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "LatinSpecies"));
						List<ObservedValue> latinSpeciesValueList = latinSpeciesQuery.find();
						if (latinSpeciesValueList.size() == 1) {
							latinSpecies = latinSpeciesValueList.get(0).getValue();
						}
						
						// Herkomst en hergebruik (animal's SourceTypeSubproject, which includes reuse)
						String sourceType = "";
						q = db.query(ObservedValue.class);
						q.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, animalName));
						q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "SourceTypeSubproject"));
						q.addRules(new QueryRule(ObservedValue.PROTOCOLAPPLICATION, Operator.EQUALS, protocolApplicationId));
						valueList = q.find();
						if (valueList.size() > 0) {
							sourceType = valueList.get(0).getValue();
						}
						// Aantal dieren (count later on!)
						
						// Anesthesie (animal's Anaesthesia)
						String anaesthesia = "";
						q = db.query(ObservedValue.class);
						q.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, animalName));
						q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Anaesthesia"));
						q.addRules(new QueryRule(ObservedValue.PROTOCOLAPPLICATION, Operator.EQUALS, protocolApplicationId));
						valueList = q.find();
						if (valueList.size() > 0) {
							anaesthesia = valueList.get(0).getValue();
						}
						
						// Pijnbestrijding, postoperatief (animal's PainManagement)
						String painManagement = "";
						q = db.query(ObservedValue.class);
						q.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, animalName));
						q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "PainManagement"));
						q.addRules(new QueryRule(ObservedValue.PROTOCOLAPPLICATION, Operator.EQUALS, protocolApplicationId));
						valueList = q.find();
						if (valueList.size() > 0) {
							painManagement = valueList.get(0).getValue();
						}
						
						String actualDiscomfort = "";
						String actualAnimalEndStatus = "";	
						// Find protocol application ID for the removing of this animal from this DEC subproject
						q = db.query(ObservedValue.class);
						q.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, animalName));
						q.addRules(new QueryRule(ObservedValue.RELATION_NAME, Operator.EQUALS, expName));
						q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "FromExperiment"));
						valueList = q.find();
						if (valueList.size() > 0) {
							int removalProtocolApplicationId = valueList.get(0).getProtocolApplication_Id();
							
							// Ongerief (animal's ActualDiscomfort)
							q = db.query(ObservedValue.class);
							q.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, animalName));
							q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "ActualDiscomfort"));
							q.addRules(new QueryRule(ObservedValue.PROTOCOLAPPLICATION, Operator.EQUALS, removalProtocolApplicationId));
							valueList = q.find();
							if (valueList.size() > 0) {
								actualDiscomfort = valueList.get(0).getValue();
							} else {
								// Something's wrong here...
								warningsList.add("No 'ActualDiscomfort' value found for Animal " + animalName + 
										" in DEC subproject " + expName);
							}
							
							// Toestand dier na beeindiging proef (animal's most recent ActualAnimalEndStatus)
							q = db.query(ObservedValue.class);
							q.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, animalName));
							q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "ActualAnimalEndStatus"));
							q.sortDESC(ObservedValue.TIME);
							valueList = q.find();
							if (valueList.size() > 0) {
								// Check if most recent end status was in the PA we're now looking at
								if (valueList.get(0).getProtocolApplication_Id().equals(removalProtocolApplicationId)) {
									actualAnimalEndStatus = valueList.get(0).getValue();
									// If most recent end status was 'in leven gelaten' but animal died in given year,
									// change to 'dood ihkv de proef' because that's how the law wants it...
									if (actualAnimalEndStatus.equals("C. Na einde proef in leven gelaten")) {
										q = db.query(ObservedValue.class);
										q.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, animalName));
										q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Active"));
										q.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.GREATER_EQUAL, startOfYearString));
										q.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.LESS_EQUAL, endOfYearString));
										q.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.NOT, null));
										valueList = q.find();
										if (valueList.size() > 0) {
											// So animal did indeed die in the given year
											actualAnimalEndStatus = "B. Gedood na beeindiging van de proef";
										}
									}
								} else {
									// Find the end status value in the PA we're looking at
									for (ObservedValue endStatusValue : valueList) {
										if (endStatusValue.getProtocolApplication_Id().equals(removalProtocolApplicationId)) {
											actualAnimalEndStatus = endStatusValue.getValue();
										}
									}
								}
							} else {
								// Something's wrong here...
								warningsList.add("No 'ActualAnimalEndStatus' value(s) found for Animal " + animalName + 
										" in DEC subproject " + expName);
							}
						} else {
							// Something's wrong here...
							warningsList.add("No 'FromExperiment' value found for Animal " + animalName + 
									" in DEC subproject " + expName);
						}
						
						ArrayList<String> newRow = new ArrayList<String>();
						newRow.add(decNr + expCode + " - " + d.getName());
						if (endOfDec != null) {
							newRow.add(dbFormat.format(endOfDec));
						} else {
							newRow.add("");
						}
						newRow.add(animalType);
						newRow.add(vwaSpecies);
						newRow.add(sourceType);
						newRow.add("");
						newRow.add(goal);
						newRow.add(concern);
						newRow.add(lawDef);
						newRow.add(toxRes);
						newRow.add(specialTechn);
						newRow.add(anaesthesia);
						newRow.add(painManagement);
						newRow.add(actualDiscomfort);
						newRow.add(actualAnimalEndStatus);
						newRow.add(decNr + expCode);
						newRow.add(latinSpecies);
						
						if (matrix.contains(newRow)) {
							// If the above values are exactly the same as an earlier row, aggregate them
							int rowIndex = matrix.indexOf(newRow);
							int tmpNr = nrOfAnimalList.get(rowIndex);
							nrOfAnimalList.set(rowIndex, tmpNr + 1);
						} else {
							// Else, make a new row
							matrix.add(newRow);
							nrOfAnimalList.add(1);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		String output = "<br /><p><strong>JAARSTAAT DIERPROEVEN registratiejaar " + year + " - Registratieformulier 5</strong></p>";
		output += "<br /><div id='reporttablediv'><table border='1' cellpadding='5' cellspacing='5'>";
		output += "<tr>";
		output += "<th></th>";
		output += "<th></th>";
		for (int col = 1; col < 15; col++) {
			output += ("<th>" + col + "</th>");
		}
		output += "<th></th>";
		output += "</tr>";
		output += "<tr>";
		output += "<td style='padding:5px'>DEC-nr.</td>";
		output += "<td style='padding:5px'>DEC verlopen op</td>";
		output += "<td style='padding:5px'>Bijzonderheid dier</td>";
		output += "<td style='padding:5px'>Diersoort</td>";
		output += "<td style='padding:5px'>Herkomst en hergebruik</td>";
		output += "<td style='padding:5px'>Aantal dieren</td>";
		output += "<td style='padding:5px'>Doel vd proef</td>";
		output += "<td style='padding:5px'>Belang van de proef</td>";
		output += "<td style='padding:5px'>Wettelijke bepalingen</td>";
		output += "<td style='padding:5px'>Toxicologisch / veiligheidsonderzoek</td>";
		output += "<td style='padding:5px'>Bijzondere technieken</td>";
		output += "<td style='padding:5px'>Anesthesie</td>";
		output += "<td style='padding:5px'>Pijnbestrijding, postoperatief</td>";
		output += "<td style='padding:5px'>Ongerief</td>";
		output += "<td style='padding:5px'>Toestand dier na beeindiging proef</td>";
		output += "<td style='padding:5px'>DEC-nummer / Onderzoeksprotocol</td>";
		output += "<td style='padding:5px'>Naam (wetenschappelijke naam)</td>";
		output += "</tr>";
		int counter = 0;
		for (ArrayList<String> currentRow : matrix) {
			output += "<tr>";
			for (int col = 0; col < nrCol; col++) {
				if (col == 5) {
					output += ("<td style='padding:5px'>" + nrOfAnimalList.get(counter) + "</td>");
				} else {
					output += ("<td style='padding:5px'>" + currentRow.get(col) + "</td>");
				}
			}
			output += "</tr>";
			counter++;
		}
		output += "</table></div>";
		
		// Warnings
		if (warningsList.size() > 0) {
			output += "<p><strong>Warnings</strong><br />";
			for (String warning : warningsList) {
				output += (warning + "<br />");
			}
			output += "</p>";
		}
		
		return output;
	}
}
