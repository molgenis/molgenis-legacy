package org.molgenis.ibdportal.plugins.importer;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.security.Login;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.pheno.Panel;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReaderListener;
import org.molgenis.util.Tuple;


public class Importer
{
	private Database db;
	private Logger logger;
	private String userName;
	private Investigation ibdInv;
	private String invName = "IBD";
	private String cohortName = "IBD cohort";
	private Panel ibdCohort;
	private List<String> seenPatients = new ArrayList<String>();
	private List<Individual> patientsToAdd = new ArrayList<Individual>();
	private List<Measurement> measurementsToAdd = new ArrayList<Measurement>();
	private List<ObservedValue> valuesToAdd = new ArrayList<ObservedValue>();
	private int rowCnt = 0;

	public Importer(Database db, Login login) throws Exception
	{
		this.db = db;
		logger = Logger.getLogger("Importer for IBD data");
		
		userName = login.getUserName();
		
		ibdInv = new Investigation();
		ibdInv.setName(invName);
		ibdInv.setOwns_Name(userName);
		this.db.add(ibdInv);
		
		ibdCohort = new Panel();
		ibdCohort.setName(cohortName);
		ibdCohort.setInvestigation_Name(invName);
		ibdCohort.setOwns_Name(userName);
		this.db.add(ibdCohort);
	}
	
	public void doImport(String filename) throws Exception
	{
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				int nrOfDiags = 0;
				int highestDiag = 4;
				String patName = null;
				
				for (String fieldName : tuple.getFields()) {
					
					String value = tuple.getString(fieldName);
					
					if (fieldName.equals("DATUMDB") || fieldName.equals("VERSIEDB")) {
						// These two measurements should be set on the cohort, once
						if (line_number == 1) {
							Measurement newMeas = new Measurement();
							newMeas.setName(fieldName);
							newMeas.setInvestigation_Name(invName);
							newMeas.setOwns_Name(userName);
							measurementsToAdd.add(newMeas);
							ObservedValue newVal = new ObservedValue();
							newVal.setTarget_Name(cohortName);
							newVal.setFeature_Name(fieldName);
							newVal.setValue(value);
							valuesToAdd.add(newVal);
						}
					} else if (fieldName.equals("PAID")) {
						// If not seen yet, create a new patient with name 'PAID'
						patName = value;
						if (patName == null) {
							// Skip line altogether if PAID is empty
							return;
						}
						if (!seenPatients.contains(patName)) {
							seenPatients.add(patName);
							Individual newPat = new Individual();
							newPat.setName(patName);
							newPat.setInvestigation_Name(invName);
							newPat.setOwns_Name(userName);
							patientsToAdd.add(newPat);
						}
					} else if (fieldName.startsWith("SCDIAG")) {
						// Diagnoses must be summarized to the most severe one
						// Hierarchy is from highest to lowest:
						// 2 (Crohn), 1 (Colitis), 3 (IBDU), 4 (IBDI)
						
						// First add measurement
						if (nrOfDiags == 0 && line_number == 1) {
							Measurement newMeas = new Measurement();
							newMeas.setName("SCDIAG_HIGHEST");
							newMeas.setInvestigation_Name(invName);
							newMeas.setOwns_Name(userName);
							measurementsToAdd.add(newMeas);
						}
						// Read in diagnosis for this column
						if (value != null) {
							int newDiag = Integer.parseInt(value);
							if ((highestDiag == 4 && newDiag < 4) ||
								(highestDiag == 3 && newDiag < 3) ||
								(highestDiag == 1 && newDiag == 2)) {
								highestDiag = newDiag;
							}
						}
						// When we've seen all 22 diagnosis columns, store highest
						if (nrOfDiags == 21) {
							ObservedValue newVal = new ObservedValue();
							newVal.setTarget_Name(patName);
							newVal.setFeature_Name("SCDIAG_HIGHEST");
							newVal.setValue(Integer.toString(highestDiag));
							valuesToAdd.add(newVal);
						} else {
							nrOfDiags++;
						}
					} else if (fieldName.startsWith("FKLID")) {
						// Skip!
					} else if (fieldName.startsWith("SBASAF")) {
						// Skip!
					} else {
						// All other columns result in a (new) Measurement + an ObservedValue
						if (line_number == 1) {
							Measurement newMeas = new Measurement();
							newMeas.setName(fieldName);
							newMeas.setInvestigation_Name(invName);
							newMeas.setOwns_Name(userName);
							measurementsToAdd.add(newMeas);
						}
						ObservedValue newVal = new ObservedValue();
						newVal.setTarget_Name(patName);
						newVal.setFeature_Name(fieldName);
						newVal.setValue(value);
						valuesToAdd.add(newVal);
					}
				}
				
				rowCnt++;
				if (rowCnt % 100 == 0) {
					writeToDb();
					System.out.println("Wrote data from lines " + (rowCnt - 100) + " through " + rowCnt + " to database");
				}
			}
		});
	}
	
	public void writeToDb() throws DatabaseException {
		// add patients to the db
		this.db.add(patientsToAdd);
		// add measurements to the db
		this.db.add(measurementsToAdd);
		// add all values to the db
		this.db.add(valuesToAdd);
		// clear all to save heap space
		patientsToAdd.clear();
		measurementsToAdd.clear();
		valuesToAdd.clear();
	}
	
	public void addPatientsToCohort() throws DatabaseException {
		ibdCohort.setIndividuals_Name(seenPatients);
		this.db.update(ibdCohort);
	}
	
}
