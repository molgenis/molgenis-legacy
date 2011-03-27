package plugins.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;


import commonservice.CommonService;

public class VWAReport4 extends AnimalDBReport
{
	private String[][] matrix;
	List<String> speciesList = new ArrayList<String>();
	private String type;

	public VWAReport4(Database db)
	{
		this.db = db;
		ct = CommonService.getInstance();
		ct.setDatabase(db);
		nrCol = 18;
		warningsList = new ArrayList<String>();
	}

	@Override
	public void makeReport(int year, String type)
	{
		try
		{
			this.year = year;
			this.type = type;

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
			String startOfYearString = year + "-01-01 00:00:00";
			Date startOfYear = sdf.parse(startOfYearString);
			String endOfYearString = (year + 1) + "-01-01 00:00:00";
			Date endOfYear = sdf.parse(endOfYearString);

			ArrayList<ArrayList<Integer>> rowList = new ArrayList<ArrayList<Integer>>();

			// Go through all animals
			List<Integer> targetIdList = ct.getAllObservationTargetIds("Individual", false);
			for (Integer targetid : targetIdList)
			{
				// Check AnimalType
				String animalType = "";
				int featid = ct.getMeasurementId("AnimalType");
				Query<ObservedValue> q = db.query(ObservedValue.class);
				q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, targetid));
				q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featid));
				List<ObservedValue> valueList = q.find();
				if (valueList.size() > 0)
				{
					animalType = valueList.get(0).getValue();
					// Ignore animals that are not of the correct type for this report
					if ((animalType.equals("A. Gewoon dier") && !type.equals("A")) ||
					    (animalType.equals("B. Transgeen dier") && !type.equals("B")) ||
					    (animalType.equals("C. Wildvang") && !type.equals("C")) ||
					    animalType.equals("D. Biotoop"))
					{
						continue;
					}
				}
				else
				{
					warningsList.add("Target " + targetid + " has no AnimalType");
					continue; // Ignore animals that have no type
				}

				// Get Active value and check (start)time + endtime
				boolean activeEndOfPrevYear = false;
				boolean activeEndOfThisYear = false;
				featid = ct.getMeasurementId("Active");
				q = db.query(ObservedValue.class);
				q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, targetid));
				q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featid));
				q.sortDESC(ObservedValue.TIME);
				valueList = q.find();
				if (valueList.size() > 0)
				{
					// get the info from the most recent Active value.
					Date activeStartDate = valueList.get(0).getTime();
					Date activeEndDate = valueList.get(0).getEndtime();
					// Remove animals that came in after the given year
					if (activeStartDate.after(endOfYear)) {
						continue;
					}
					if (activeStartDate.before(startOfYear))
					{
						activeEndOfPrevYear = true; // can be changed later if we find out this one is dead!
					}
					if (activeEndDate != null)
					{
						// Remove animals that died before the given year
						if (activeEndDate.before(startOfYear))
						{
							continue;
						}
						if (activeEndDate.after(endOfYear))
						{
							// Born before or in the year of interest and dead after: OK
							activeEndOfThisYear = true;
						}
					} else {
						// Born before or in the year of interest and not dead yet: OK
						activeEndOfThisYear = true;
					}
				}
				else
				{
					// Don't consider animals that have no  'Active' values
					warningsList.add("Target " + targetid + " has no 'Active' value(s)");
					continue;
				}

				// Get source and source type for animals that came in this year
				int inColumn = -1;
				if (activeEndOfPrevYear == false)
				{
					featid = ct.getMeasurementId("Source");
					q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, targetid));
					q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featid));
					valueList = q.find();
					if (valueList.size() > 0)
					{
						int sourceid = valueList.get(0).getRelation_Id();
						featid = ct.getMeasurementId("SourceType");
						q = db.query(ObservedValue.class);
						q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, sourceid));
						q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featid));
						valueList = q.find();
						if (valueList.size() > 0)
						{
							String sourcetype = valueList.get(0).getValue();
							if (sourcetype.equals("Eigen fok binnen uw organisatorische werkeenheid")) inColumn = 1;
							if (sourcetype.equals("Andere organisatorische werkeenheid vd instelling")) inColumn = 2;
							if (sourcetype.equals("Geregistreerde fok/aflevering in Nederland")) inColumn = 3;
							if (sourcetype.equals("Van EU-lid-staten")) inColumn = 4;
							if (sourcetype.equals("Niet-geregistreerde fok/afl in Nederland")) inColumn = 5;
							if (sourcetype.equals("Niet-geregistreerde fok/afl in andere EU-lid-staat")) inColumn = 6;
							if (sourcetype.equals("Andere herkomst")) inColumn = 7;
							// Since the sourcetype of animals from the wild is also
							// 'Niet-geregistreerde fok/afl in Nederland', we have
							// to find another way to distinguish them:
							if (animalType.equals("C. Wildvang")) {
								inColumn = 8;
							}
							// The sourcetype of animals in the wild ("Biotoop") is also
							// 'Niet-geregistreerde fok/afl in Nederland'
							if (animalType.equals("D. Biotoop")) {
								inColumn = -1;
							}
						}
					} else {
						warningsList.add("Target " + targetid + " has no Source");
					}
				}

				// Get animals that were removed this year
				int outColumn = -1;
				if (activeEndOfThisYear == false)
				{
					// Not in an experiment at all - ever?
					featid = ct.getMeasurementId("Experiment");
					q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, targetid));
					q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featid));
					if (q.count() == 0) {
						// List as 'dood voor de proef'
						outColumn = 9;
					} else {
						// Died while in or after experiment this year?
						featid = ct.getMeasurementId("Experiment");
						q = db.query(ObservedValue.class);
						q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, targetid));
						q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featid));
						q.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.GREATER_EQUAL, startOfYearString));
						q.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.LESS, endOfYearString));
						q.sortDESC(ObservedValue.ENDTIME); // make sure most recent experiment is on top
						List<ObservedValue> subprojectValueList = q.find();
						if (subprojectValueList.size() > 0) {
							// find 'FromExperiment' value for most recently ended experiment
							int experimentId = subprojectValueList.get(0).getRelation_Id();
							featid = ct.getMeasurementId("FromExperiment");
							q = db.query(ObservedValue.class);
							q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, targetid));
							q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featid));
							q.addRules(new QueryRule(ObservedValue.RELATION, Operator.EQUALS, experimentId));
							List<ObservedValue> fromSubprojectValueList = q.find(); // safe assumption: contains only one value
							int protocolApplicationId = fromSubprojectValueList.get(0).getProtocolApplication_Id();
							featid = ct.getMeasurementId("ActualAnimalEndStatus");
							q = db.query(ObservedValue.class);
							q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, targetid));
							q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featid));
							q.addRules(new QueryRule(ObservedValue.PROTOCOLAPPLICATION, Operator.EQUALS, protocolApplicationId));
							List<ObservedValue> endstatusValueList = q.find();
							if (endstatusValueList.size() == 1) {
								String endstatus = endstatusValueList.get(0).getValue();
								if (endstatus.equals("A. Dood in het kader van de proef"))
									outColumn = 10;
								if (endstatus.equals("B. Gedood na beeindiging van de proef"))
									outColumn = 11;
								// Animal died in given year and was in experiment, so we also have to count it in column 13
								if (endstatus.equals("C. Na einde proef in leven gelaten"))
									outColumn = 11;
							} else {
								warningsList.add("0 or more than 1 end statuses found for target " + targetid +
										" in experiment " + experimentId);
							}
						} else {
							// No experiments found in the current year, so list as 'dood voor de proef'
							outColumn = 9;
						}
					}

					// Handle 'afgevoerde' animals (col 14-17)
					featid = ct.getMeasurementId("Removal");
					q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, targetid));
					q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featid));
					q.addRules(new QueryRule(ObservedValue.TIME, Operator.GREATER_EQUAL, startOfYearString));
					q.addRules(new QueryRule(ObservedValue.TIME, Operator.LESS, endOfYearString));
					List<ObservedValue> removalValueList = q.find();
					if (removalValueList.size() == 1) {
						String removal = removalValueList.get(0).getValue();
						// If 'afgevoerd', columns pertaining to death don't apply anymore
						if (removal.equals("levend afgevoerd andere organisatorische eenheid RuG")) outColumn = 12;
						if (removal.equals("levend afgevoerd gereg. onderzoeksinstelling NL")) outColumn = 13;
						if (removal.equals("levend afgevoerd gereg. onderzoeksinstelling EU")) outColumn = 14;
						if (removal.equals("levend afgevoerd andere bestemming")) outColumn = 15;
					} else if (removalValueList.size() > 1) {
						warningsList.add("Animal " + targetid + " has multiple removal events.");
					}
				}

				// Get species and store values in the corresponding "bin"
				featid = ct.getMeasurementId("Species");
				q = db.query(ObservedValue.class);
				q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, targetid));
				q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featid));
				valueList = q.find();
				if (valueList.size() > 0)
				{
					// Get VWA species
					String vwaSpecies = "";
					int normalSpeciesId = valueList.get(0).getRelation_Id();
					int featureId = ct.getMeasurementId("VWASpecies");
					Query<ObservedValue> vwaSpeciesQuery = db.query(ObservedValue.class);
					vwaSpeciesQuery.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, normalSpeciesId));
					vwaSpeciesQuery.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featureId));
					List<ObservedValue> vwaSpeciesValueList = vwaSpeciesQuery.find();
					if (vwaSpeciesValueList.size() == 1) {
						vwaSpecies = vwaSpeciesValueList.get(0).getValue();
					}
					// Get scientific (Latin) species
					String latinSpecies = "";
					featureId = ct.getMeasurementId("LatinSpecies");
					Query<ObservedValue> latinSpeciesQuery = db.query(ObservedValue.class);
					latinSpeciesQuery.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, normalSpeciesId));
					latinSpeciesQuery.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featureId));
					List<ObservedValue> latinSpeciesValueList = latinSpeciesQuery.find();
					if (latinSpeciesValueList.size() == 1) {
						latinSpecies = latinSpeciesValueList.get(0).getValue();
					}
					if (!speciesList.contains(latinSpecies)) { // new species
						if (speciesList.contains(vwaSpecies)) { // already other entries for this VWA species
							int vwaIndex = speciesList.indexOf(vwaSpecies);
							speciesList.add(vwaIndex + 1, latinSpecies);
							// update VWA species row
							ArrayList<Integer> tmpRow = rowList.get(vwaIndex);
							Integer tmpValue;
							if (activeEndOfPrevYear)
							{
								tmpValue = tmpRow.get(0);
								tmpRow.set(0, tmpValue + 1);
							}
							for (int counter = 1; counter < 16; counter++) {
								tmpValue = tmpRow.get(counter);
								if (inColumn == counter || outColumn == counter) {
									tmpRow.set(counter, tmpValue + 1);
								}
							}
							if (activeEndOfThisYear)
							{
								tmpValue = tmpRow.get(16);
								tmpRow.set(16, tmpValue + 1);
							}
							rowList.set(vwaIndex, tmpRow);
							// Plus, store numbers on status etc. in Latin species row (that's below the VWA row)
							tmpRow = new ArrayList<Integer>();
							if (activeEndOfPrevYear) {
								tmpRow.add(1);
							} else {
								tmpRow.add(0);
							}
							for (int counter = 1; counter < 16; counter++) {
								if (inColumn == counter || outColumn == counter) {
									tmpRow.add(1);
								} else {
									tmpRow.add(0);
								}
							}
							if (activeEndOfThisYear) {
								tmpRow.add(1);
							} else {
								tmpRow.add(0);
							}
							tmpRow.add(0); // indicator that this is not an aggregation row
							rowList.add(vwaIndex + 1, tmpRow);
						} else { // VWA species and Latin species not in table yet
							speciesList.add(vwaSpecies);
							speciesList.add(latinSpecies);
							// fill VWA species row
							ArrayList<Integer> tmpRow = new ArrayList<Integer>();
							if (activeEndOfPrevYear) {
								tmpRow.add(1);
							} else {
								tmpRow.add(0);
							}
							for (int counter = 1; counter < 16; counter++) {
								if (inColumn == counter || outColumn == counter) {
									tmpRow.add(1);
								} else {
									tmpRow.add(0);
								}
							}
							if (activeEndOfThisYear) {
								tmpRow.add(1);
							} else {
								tmpRow.add(0);
							}
							tmpRow.add(1); // indicator that this is an aggregation row
							rowList.add(tmpRow);
							// Plus, store numbers on status etc. in Latin species row (that's below the VWA row)
							tmpRow = new ArrayList<Integer>();
							if (activeEndOfPrevYear) {
								tmpRow.add(1);
							} else {
								tmpRow.add(0);
							}
							for (int counter = 1; counter < 16; counter++) {
								if (inColumn == counter || outColumn == counter) {
									tmpRow.add(1);
								} else {
									tmpRow.add(0);
								}
							}
							if (activeEndOfThisYear) {
								tmpRow.add(1);
							} else {
								tmpRow.add(0);
							}
							tmpRow.add(0); // indicator that this is not an aggregation row
							rowList.add(tmpRow);
						}
					} else { // Latin species already in list (therefore, VWA species must also be in list)
						int latinIndex = speciesList.indexOf(latinSpecies);
						int vwaIndex = speciesList.indexOf(vwaSpecies);
						// Update Latin species row
						ArrayList<Integer> tmpRow = rowList.get(latinIndex);
						Integer tmpValue;
						if (activeEndOfPrevYear)
						{
							tmpValue = tmpRow.get(0);
							tmpRow.set(0, tmpValue + 1);
						}
						for (int counter = 1; counter < 16; counter++) {
							tmpValue = tmpRow.get(counter);
							if (inColumn == counter || outColumn == counter) {
								tmpRow.set(counter, tmpValue + 1);
							}
						}
						if (activeEndOfThisYear)
						{
							tmpValue = tmpRow.get(16);
							tmpRow.set(16, tmpValue + 1);
						}
						rowList.set(latinIndex, tmpRow);
						// Update VWA species row
						tmpRow = rowList.get(vwaIndex);
						if (activeEndOfPrevYear)
						{
							tmpValue = tmpRow.get(0);
							tmpRow.set(0, tmpValue + 1);
						}
						for (int counter = 1; counter < 16; counter++) {
							tmpValue = tmpRow.get(counter);
							if (inColumn == counter || outColumn == counter) {
								tmpRow.set(counter, tmpValue + 1);
							}
						}
						if (activeEndOfThisYear)
						{
							tmpValue = tmpRow.get(16);
							tmpRow.set(16, tmpValue + 1);
						}
						rowList.set(vwaIndex, tmpRow);
					}
				} else {
					warningsList.add("Target " + targetid + " has no Species");
				}
			} // end of loop through all animals

			// Fill matrix of nr. of species (rows) x 18 counts (columns)
			// + 1 column for the aggregation indicator
			matrix = new String[speciesList.size()][nrCol + 1];
			int idx = 0;
			for (String species : speciesList)
			{
				ArrayList<Integer> tmpRow = rowList.get(idx);
				
				if (tmpRow.get(17) == 0) {
					// Simple check on non-aggregate rows
					int numberIn = tmpRow.get(0) + tmpRow.get(1) + tmpRow.get(2) + tmpRow.get(3) +
						tmpRow.get(4) + tmpRow.get(5) + tmpRow.get(6) + tmpRow.get(7) + tmpRow.get(8);
					int numberOut = tmpRow.get(9) + tmpRow.get(10) + tmpRow.get(11) +
						tmpRow.get(12) + tmpRow.get(13) + tmpRow.get(14) + tmpRow.get(15);
					if (numberIn - numberOut != tmpRow.get(16)) {
						warningsList.add("Nr. at end of previous year minus nr. of removed not equal to nr. at end of current year for " + species);
					}
				}
				
				matrix[idx][0] = species;
				for (int counter = 1; counter < nrCol + 1; counter ++) {
					matrix[idx][counter] = tmpRow.get(counter - 1).toString();
				}
				idx++;
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public String toString()
	{
		// Header
		String output = "<br /><p><strong>JAARSTAAT AAN- EN AFVOER DIEREN registratiejaar " + year
				+ " - Registratieformulier 4" + type + "</strong></p><br />";
		
		// Table
		output += "<table border='1px' cellpadding='5px' cellspacing='5px'>";
		output += "<tr>";
		for (int col = 1; col <= nrCol; col++)
		{
			output += "<th";
			if (col == 2 || col == 10 || col == 17) output += " style='border-right-width:2px'";
			output += (">" + col + "</th>");
		}
		output += "</tr>";
		output += "<tr>";
		output += "<td style='padding:5px'>Codenummer/diersoort en/of naam</td>";
		output += "<td style='padding:5px; border-right-width:2px'>aanwezig op 1 jan. " + year + "</td>";
		output += "<td style='padding:5px'>eigen fok</td>";
		output += "<td style='padding:5px'>organisatorische eenheid RuG</td>";
		output += "<td style='padding:5px'>gereg. fok NL</td>";
		output += "<td style='padding:5px'>gereg. fok EU</td>";
		output += "<td style='padding:5px'>niet-gereg. fok NL</td>";
		output += "<td style='padding:5px'>niet-gereg. fok EU</td>";
		output += "<td style='padding:5px'>andere herkomst</td>";
		output += "<td style='padding:5px; border-right-width:2px'>wilde fauna</td>";
		output += "<td style='padding:5px'>dood of gedood<br />voor het begin van de proef</td>";
		output += "<td style='padding:5px'>dood of gedood<br />tijdens de proef</td>";
		output += "<td style='padding:5px'>dood of gedood<br />na afloop van de proef</td>";
		output += "<td style='padding:5px'>levend afgevoerd<br />andere organisatorische eenheid RuG</td>";
		output += "<td style='padding:5px'>levend afgevoerd<br />gereg. onderzoeksinstelling NL</td>";
		output += "<td style='padding:5px'>levend afgevoerd<br />gereg. onderzoeksinstelling EU</td>";
		output += "<td style='padding:5px; border-right-width:2px'>levend afgevoerd<br />andere bestemming</td>";
		output += "<td style='padding:5px'>aanwezig op 31 dec. " + year + "</td>";
		output += "</tr>";
		for (int idx = 0; idx < speciesList.size(); idx++)
		{
			output += "<tr>";
			String preMarkup = "";
			String postMarkup = "";
			if (matrix[idx][nrCol].equals("1")) {
				preMarkup = "<strong>";
				postMarkup = "<strong>";
			}
			for (int col = 1; col <= nrCol; col++)
			{
				output += "<td  style='padding:5px";
				if (col == 2 || col == 10 || col == 17) output += " ; border-right-width:2px";
				output += ("'>" + preMarkup + matrix[idx][col - 1] + postMarkup + "</td>");
			}
			output += "</tr>";
		}
		output += "</table>";
		
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
