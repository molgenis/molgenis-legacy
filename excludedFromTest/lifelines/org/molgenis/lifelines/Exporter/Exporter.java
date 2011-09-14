package org.molgenis.lifelines.Exporter;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.model.MolgenisModelException;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;

import app.CsvExport;
import app.DatabaseFactory;

public class Exporter {
	public static void main(String[] args) throws Exception {
		try {
		Database db = DatabaseFactory.create();
//		File dir = File.createTempFile("molgenis","");		
//		dir.delete(); //delete the file, need dir
		File dir1 = new File("/Users/jorislops/Desktop/export/");
				
		List<ObservationTarget> measurements = db.getEntityManager().createQuery("SELECT m from ObservationTarget m", ObservationTarget.class)
			.getResultList();
		
		File f = new File(dir1 + "/investigation.txt");
		System.out.println(f.getAbsolutePath());
		System.out.println(f.getCanonicalPath());
		
		new CsvExport().exportObservationTarget(measurements, f);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		//new CsvExport().exportAll(dir1, db);
	}
}
