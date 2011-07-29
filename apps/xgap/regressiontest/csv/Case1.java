package regressiontest.csv;

import java.io.File;

import org.molgenis.framework.db.Database;
import org.molgenis.util.TarGz;

import plugins.archiveexportimport.XgapCsvExport;
import plugins.archiveexportimport.XgapCsvImport;
import plugins.emptydb.emptyDatabase;
import app.DatabaseFactory;

public class Case1
{

	boolean result;
	
	public boolean isResult()
	{
		return result;
	}

	public void setResult(boolean result)
	{
		this.result = result;
	}

	public Case1() throws Exception
	{
		
		String baileyInvestigationName = "Identification of QTL for locomotor activation and anxiety using related inbred strains B6 and C58/J";
		String lyonsInvestigationName = "QTL analysis: Plasma lipids and susceptibility to gallstones in F2 progeny of DBA/2J x CAST/EiJ on a high-fat diet";
		String keurentjesName = "MetaNetwork";
		String beamerName = "Bone mineral density in F2 progeny from a C57BL/6J x CAST/EiJ intercross";
		
		Database db = DatabaseFactory.create("xgap.properties");
		File path = new File(new File("").getAbsolutePath() + "/handwritten/java/regressiontest/csv/tar");
		
		// Clear database
		System.out.println("Step 1"); 
		new emptyDatabase(db, false);
		
		// Import Bailey
		System.out.println("Step 2");
	//	File tarBailey = new File(this.getClass().getResource("tar/Bailey.tar.gz").getFile());
		File tarBailey = new File(path + "/Bailey.tar.gz");
		File extractBailey = TarGz.tarExtract(tarBailey);
		new XgapCsvImport(extractBailey, db, false);
		
		// Import Lyons
		System.out.println("Step 3"); 
	//	File tarLyons = new File(this.getClass().getResource("tar/Lyons.tar.gz").getFile());
		File tarLyons = new File(path + "/Lyons.tar.gz");
		File extractLyons = TarGz.tarExtract(tarLyons);
		new XgapCsvImport(extractLyons, db, false);

		// Export both into one archive
		System.out.println("Step 4"); 
		File exportBoth = new File(System.getProperty("java.io.tmpdir") + File.separator + "bailey_lyons_export");
		new XgapCsvExport(exportBoth, db);
		File repackBoth = TarGz.tarDir(exportBoth);
		
		//Clear database
		System.out.println("Step 5"); 
		new emptyDatabase(db, false);
		
		// Import both from one archive
		System.out.println("Step 6"); 
		File extractBoth = TarGz.tarExtract(repackBoth);
		new XgapCsvImport(extractBoth, db, false);
		
		// Export Bailey
		System.out.println("Step 7"); 
		File exportBailey = new File(System.getProperty("java.io.tmpdir") + File.separator + "bailey_export");
		new XgapCsvExport(exportBailey, db, baileyInvestigationName);
		
		// Export Lyons
		System.out.println("Step 8"); 
		File exportLyons = new File(System.getProperty("java.io.tmpdir") + File.separator + "lyons_export");
		new XgapCsvExport(exportLyons, db, lyonsInvestigationName);
		
		// Perform controls on file comparison and print results
		System.out.println("Step 9"); 
		boolean controlsAreCorrect = performControls();
		
		// Perform equality verification of both exported tar files
		System.out.println("Step 10"); 
		boolean baileyDirsAreEqual = DirectoryCompare.compareDirs(extractBailey, exportBailey);
		boolean lyonsDirsAreEqual = DirectoryCompare.compareDirs(extractLyons, exportLyons);
		
		System.out.println("Directories of Bailey are equal: " + baileyDirsAreEqual);
		System.out.println("Directories of Lyons are equal: " + lyonsDirsAreEqual);
		
		if(controlsAreCorrect && baileyDirsAreEqual && lyonsDirsAreEqual){
			this.setResult(true);
		}else{
			this.setResult(false);
		}

	}

	private boolean performControls() throws Exception{
		File controlA = new File(this.getClass().getResource("control/containsA.txt").getFile());
		File controlB = new File(this.getClass().getResource("control/containsB.txt").getFile());
		
		boolean fileComparePositiveControl1 = DirectoryCompare.compareFileContent(controlA, controlA);
		boolean fileComparePositiveControl2 = DirectoryCompare.compareFileContent(controlB, controlB);
		boolean fileCompareNegativeControl1 = DirectoryCompare.compareFileContent(controlA, controlB);
		boolean fileCompareNegativeControl2 = DirectoryCompare.compareFileContent(controlB, controlA);
		
		System.out.println("Positive 'file content compare' control 1 (true): " + fileComparePositiveControl1);
		System.out.println("Positive 'file content compare' control 2 (true): " + fileComparePositiveControl2);
		System.out.println("Negative 'file content compare' control 1 (false): " + fileCompareNegativeControl1);
		System.out.println("Negative 'file content compare' control 2 (false): " + fileCompareNegativeControl2);
		
		if(fileComparePositiveControl1 == true && fileComparePositiveControl2 == true && fileCompareNegativeControl1 == false && fileCompareNegativeControl2 == false){
			return true;
		}else{
			return false;
		}
	
	}

}
