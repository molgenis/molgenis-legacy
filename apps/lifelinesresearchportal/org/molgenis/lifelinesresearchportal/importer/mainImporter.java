package org.molgenis.lifelinesresearchportal.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.molgenis.Molgenis;
import org.molgenis.framework.db.Database;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReader;
import org.molgenis.util.Tuple;

import app.DatabaseFactory;

public class mainImporter {
	public static void main(String[] args) throws SQLException, Exception {

		// BasicConfigurator.configure();

		// empty db
		new Molgenis(
				"apps/lifelinesresearchportal/org/molgenis/lifelinesresearchportal/lifelinesresearchportal.properties")
				.updateDb(true);

		Logger.getRootLogger().setLevel(Level.ERROR);

		Database db = DatabaseFactory.create();
		try {

			db.beginTx();

			BufferedReader read = new BufferedReader(
					new FileReader(
							"/Users/pc_iverson/Desktop/Input/HL7Files/voorbeeld1_dataset.csv"));
			// BufferedReader read = new BufferedReader(new FileReader(
			// "/Users/Roan/Work/LifeLines/voorbeeld1_dataset.csv"));

			List<Measurement> listOfMeas = new ArrayList<Measurement>();
			List<Individual> listOfIndv = new ArrayList<Individual>();
			List<ObservedValue> listOfValues = new ArrayList<ObservedValue>();
			List<String> listOfFeatures = new ArrayList<String>();
			List<ProtocolApplication> paList = new ArrayList<ProtocolApplication>();
			Investigation i = new Investigation();
			i.setName("LifeLines");
			db.add(i);
			Protocol p = new Protocol();
			p.setName("TestProtocol");
			db.add(p);

			CsvReader reader = new CsvFileReader(
					new File(
							"/Users/pc_iverson/Desktop/Input/HL7Files/voorbeeld1_dataset.csv"));
			// CsvReader reader = new CsvFileReader(new
			// File("/Users/Roan/Work/LifeLines/voorbeeld1_dataset.csv"));

			// add measurements
			for (String name : reader.colnames()) {
				if (!"Pa_Id".equals(name)) {
					Measurement m = new Measurement();
					m.setInvestigation(i);
					m.setName(name);

					listOfFeatures.add(m.getName());
					listOfMeas.add(m);
				}
			}
			db.add(listOfMeas);

			// read the rows into protocolApp and values
			int count = 1;
			for (Tuple row : reader) {
				Individual indi = new Individual();
				indi.setName(row.getString("Pa_Id"));
				indi.setInvestigation(i);
				listOfIndv.add(indi);

				final ProtocolApplication pa = new ProtocolApplication();
				pa.setName("pa" + count++);
				pa.setProtocol(p);
				paList.add(pa);

				for (String column : reader.colnames()) {
					if (!"Pa_Id".equals(column)) {
						ObservedValue ob = new ObservedValue();
						ob.setFeature_Name(column);
						ob.setTarget_Name(indi.getName());
						ob.setValue(row.getString(column));
						ob.setProtocolApplication_Name(pa.getName());
						ob.setInvestigation(i);

						listOfValues.add(ob);
					}
				}

				// write if list too long
				if (listOfValues.size() > 100000) {
					System.out.println("Done");
					db.add(listOfIndv);
					db.add(paList);
					db.add(listOfValues);

					listOfIndv.clear();
					paList.clear();
					listOfValues.clear();
				}
			}

			// add remaining
			db.add(listOfIndv);
			db.add(paList);
			db.add(listOfValues);

			listOfIndv.clear();
			paList.clear();
			listOfValues.clear();
			System.out.println("Finished");
			db.commitTx();

			// Set measurementnames to protocol
		} catch (Exception e) {
			db.rollbackTx();
			e.printStackTrace();
		}
	}

}
