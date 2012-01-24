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
	private int userId;
	private Investigation ibdInv;
	private String invName = "IBD";
	private String cohortName = "IBD cohort";
	private Panel ibdCohort;
	private List<String> seenPatients = new ArrayList<String>();
	private List<Individual> patientsToAdd = new ArrayList<Individual>();
	private List<Measurement> measurementsToAdd = new ArrayList<Measurement>();
	private List<ObservedValue> valuesToAdd = new ArrayList<ObservedValue>();

	public Importer(Database db, Login login) throws Exception
	{
		this.db = db;
		logger = Logger.getLogger("Importer for IBD data");
		userId = login.getUserId();
		
		ibdInv = new Investigation();
		ibdInv.setName(invName);
		
		ibdCohort = new Panel();
		ibdCohort.setName(cohortName);
		ibdCohort.setInvestigation_Name(invName);
	}
	
	public void doImport(String filename) throws Exception
	{
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				for (String fieldName : tuple.getFields()) {
					
					String value = tuple.getString(fieldName);
					String patName = null;
					int nrOfDiags = 0;
					int highestDiag = 4;
					
					if ((fieldName.equals("DATUMDB") || fieldName.equals("VERSIEDB")) && line_number == 2) {
						// These two measurements should be set on the cohort, once
						Measurement newMeas = new Measurement();
						newMeas.setName(fieldName);
						newMeas.setInvestigation_Name(invName);
						measurementsToAdd.add(newMeas);
						ObservedValue newVal = new ObservedValue();
						newVal.setTarget_Name(cohortName);
						newVal.setFeature_Name(fieldName);
						newVal.setValue(value);
						valuesToAdd.add(newVal);
					} else if (fieldName.equals("PAID")) {
						// If not seen yet, create a new patient with name 'PAID'
						patName = value;
						if (!seenPatients.contains(patName)) {
							seenPatients.add(patName);
							Individual newPat = new Individual();
							newPat.setName(patName);
							newPat.setInvestigation_Name(invName);
							patientsToAdd.add(newPat);
						}
					} else if (fieldName.startsWith("SCDIAG")) {
						// Diagnoses must be summarized to the most severe one
						// Hierarchy is from highest to lowest:
						// 2 (Crohn), 1 (Colitis), 3 (IBDU), 4 (IBDI)
						
						// First add measurement
						if (nrOfDiags == 0) {
							Measurement newMeas = new Measurement();
							newMeas.setName("SCDIAG_HIGHEST");
							newMeas.setInvestigation_Name(invName);
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
						if (nrOfDiags == 22) {
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
						// All other columns result in a new Measurement + an ObservedValue
						Measurement newMeas = new Measurement();
						newMeas.setName(fieldName);
						newMeas.setInvestigation_Name(invName);
						measurementsToAdd.add(newMeas);
						ObservedValue newVal = new ObservedValue();
						newVal.setTarget_Name(patName);
						newVal.setFeature_Name(fieldName);
						newVal.setValue(value);
						valuesToAdd.add(newVal);
					}
				}
			}
		});
	}
	
	public void writeToDb() throws DatabaseException {
		// add invesigation
		this.db.add(ibdInv);
		// add patients to the db
		this.db.add(patientsToAdd);
		// add measurements to the db
		this.db.add(measurementsToAdd);
		// add all values to the db
		this.db.add(valuesToAdd);
		// add all patients to the cohort
		ibdCohort.setIndividuals_Name(seenPatients);
		this.db.add(ibdCohort);
	}
	
}
