package regressiontest.csv;

import java.io.File;

import org.molgenis.util.TarGz;

import plugins.archiveexportimport.XgapCsvExport;
import plugins.archiveexportimport.XgapCsvImport;
import plugins.emptydb.emptyDatabase;
import app.JDBCDatabase;

public class Case2
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

	public Case2() throws Exception
	{
		
		String fuInvestigationName = "MetaNetwork";
		String beamerInvestigationName = "Bone mineral density in F2 progeny from a C57BL/6J x CAST/EiJ intercross";
		
		JDBCDatabase db = new JDBCDatabase("xgap.properties");
		File path = new File(new File("").getAbsolutePath() + "/handwritten/java/regressiontest/csv/tar");
		
		// Clear database
		System.out.println("Step 1"); 
		new emptyDatabase(db, false);
		
		// Import Fu
		System.out.println("Step 2");
	//	File tarFu = new File(this.getClass().getResource("tar/Fu.tar.gz").getFile());
		File tarFu = new File(path + "/Fu.tar.gz");
		File extractFu = TarGz.tarExtract(tarFu);
		new XgapCsvImport(extractFu, db, false);
		
		// Import Beamer
		System.out.println("Step 3"); 
		//File tarBeamer = new File(this.getClass().getResource("tar/Beamer.tar.gz").getFile());
		File tarBeamer = new File(path + "/Beamer.tar.gz");
		File extractBeamer = TarGz.tarExtract(tarBeamer);
		new XgapCsvImport(extractBeamer, db, false);

		// Export both into one archive
		System.out.println("Step 4"); 
		File exportBoth = new File(System.getProperty("java.io.tmpdir") + File.separator + "fu_beamer_export");
		new XgapCsvExport(exportBoth, db);
		File repackBoth = TarGz.tarDir(exportBoth);
		
		//Clear database
		System.out.println("Step 5"); 
		new emptyDatabase(db, false);
		
		// Import both from one archive
		System.out.println("Step 6"); 
		File extractBoth = TarGz.tarExtract(repackBoth);
		new XgapCsvImport(extractBoth, db, false);
		
		// Export Fu
		System.out.println("Step 7"); 
		File exportFu = new File(System.getProperty("java.io.tmpdir") + File.separator + "fu_export");
		new XgapCsvExport(exportFu, db, fuInvestigationName);
		
		// Export Beamer
		System.out.println("Step 8"); 
		File exportBeamer = new File(System.getProperty("java.io.tmpdir") + File.separator + "beamer_export");
		new XgapCsvExport(exportBeamer, db, beamerInvestigationName);
		
		// Perform controls on file comparison and print results
		System.out.println("Step 9"); 
		boolean controlsAreCorrect = performControls();
		
		// Perform equality verification of both exported tar files
		System.out.println("Step 10"); 
		boolean fuDirsAreEqual = DirectoryCompare.compareDirs(extractFu, exportFu);
		boolean beamerDirsAreEqual = DirectoryCompare.compareDirs(extractBeamer, exportBeamer);
		
		System.out.println("Directories of Fu are equal: " + fuDirsAreEqual);
		System.out.println("Directories of Beamer are equal: " + beamerDirsAreEqual);
		
		if(controlsAreCorrect && fuDirsAreEqual && beamerDirsAreEqual){
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
