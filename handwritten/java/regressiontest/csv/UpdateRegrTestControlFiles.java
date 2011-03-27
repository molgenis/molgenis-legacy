package regressiontest.csv;

import java.io.File;

import org.molgenis.util.TarGz;

import plugins.archiveexportimport.XgapCsvExport;
import plugins.archiveexportimport.XgapCsvImport;
import plugins.emptydb.emptyDatabase;
import app.JDBCDatabase;

/**
 * Do not use unless you've analysed the regression test results. Be sure to
 * update only changes you've verified.
 * 
 * @author joerivandervelde
 * 
 */
public class UpdateRegrTestControlFiles
{

	public UpdateRegrTestControlFiles() throws Exception
	{	

		
		String baileyInvestigationName = "Identification of QTL for locomotor activation and anxiety using related inbred strains B6 and C58/J";
		String lyonsInvestigationName = "QTL analysis: Plasma lipids and susceptibility to gallstones in F2 progeny of DBA/2J x CAST/EiJ on a high-fat diet";
		String fuInvestigationName = "MetaNetwork";
		String beamerInvestigationName = "Bone mineral density in F2 progeny from a C57BL/6J x CAST/EiJ intercross";

		JDBCDatabase db = new JDBCDatabase("xgap.properties");
		File path = new File(new File("").getAbsolutePath() + "/handwritten/java/regressiontest/csv/tar");

		System.out.println("New db");
		new emptyDatabase(db, false);

		System.out.println("Import Bailey"); //$NON-NLS-1$
		File tarBaileyOld = new File(path + "/Bailey_old.tar.gz"); //$NON-NLS-1$
		File extractBailey = TarGz.tarExtract(tarBaileyOld);
		new XgapCsvImport(extractBailey, db);

		System.out.println("Import Lyons");
		File tarLyonsOld = new File(path + "/Lyons_old.tar.gz");
		File extractLyons = TarGz.tarExtract(tarLyonsOld);
		new XgapCsvImport(extractLyons, db);

		System.out.println("Import Fu");
		File tarFuOld = new File(path + "/Fu_old.tar.gz");
		File extractFu = TarGz.tarExtract(tarFuOld);
		new XgapCsvImport(extractFu, db);

		System.out.println("Import Beamer");
		File tarBeamerOld = new File(path + "/Beamer_old.tar.gz");
		File extractBeamer = TarGz.tarExtract(tarBeamerOld);
		new XgapCsvImport(extractBeamer, db);
		

		System.out.println("Export Bailey");
		File exportBailey = new File(System.getProperty("java.io.tmpdir") + File.separator + "bailey_export");
		new XgapCsvExport(exportBailey, db, baileyInvestigationName);
		File repackBailey = TarGz.tarDir(exportBailey);
		repackBailey.renameTo(new File(tarBaileyOld.getAbsolutePath().replace("_old", "")));
	
		System.out.println("Export Lyons");
		File exportLyons = new File(System.getProperty("java.io.tmpdir") + File.separator + "lyons_export");
		new XgapCsvExport(exportLyons, db, lyonsInvestigationName);
		File repackLyons = TarGz.tarDir(exportLyons);
		repackLyons.renameTo(new File(tarLyonsOld.getAbsolutePath().replace("_old", "")));
	
		System.out.println("Export Fu");
		File exportFu = new File(System.getProperty("java.io.tmpdir") + File.separator + "fu_export");
		new XgapCsvExport(exportFu, db, fuInvestigationName);
		File repackFu = TarGz.tarDir(exportFu);
		repackFu.renameTo(new File(tarFuOld.getAbsolutePath().replace("_old", "")));
		
		System.out.println("Export Beamer");
		File exportBeamer = new File(System.getProperty("java.io.tmpdir") + File.separator + "beamer_export");
		new XgapCsvExport(exportBeamer, db, beamerInvestigationName);
		File repackBeamer = TarGz.tarDir(exportBeamer);
		repackBeamer.renameTo(new File(tarBeamerOld.getAbsolutePath().replace("_old", "")));
	
	}

	public static void main(String[] args) throws Exception
	{
		new UpdateRegrTestControlFiles();

	}

}
