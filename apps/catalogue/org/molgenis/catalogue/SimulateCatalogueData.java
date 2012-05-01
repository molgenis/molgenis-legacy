package org.molgenis.catalogue;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.pheno.Measurement;

import app.DatabaseFactory;

public class SimulateCatalogueData {

	public static void main(String[] args) throws DatabaseException {
		BasicConfigurator.configure();

		Database db = DatabaseFactory.create();

		for (int j = 1; j < 100; j++) {
			List<Measurement> measurements = new ArrayList<Measurement>();
			for (int i = 1; i < 1000; i++) {
				Measurement m = new Measurement();
				m.setName("meas" + i + "_" + System.currentTimeMillis());
				measurements.add(m);
			}
			db.add(measurements);
		}

	}
}
