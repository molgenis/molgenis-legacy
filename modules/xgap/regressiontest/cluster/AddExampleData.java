package regressiontest.cluster;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

import matrix.implementations.binary.BinaryDataMatrixInstance;

import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.db.jdbc.JDBCDatabase;
import org.molgenis.framework.db.jpa.JpaDatabase;
import org.molgenis.organization.Investigation;
import org.molgenis.util.JarClass;
import org.molgenis.util.TarGz;
import org.molgenis.xgap.InvestigationFile;

import plugins.archiveexportimport.ArchiveExportImportPlugin;
import plugins.archiveexportimport.XgapCsvImport;
import plugins.archiveexportimport.XgapExcelImport;
import xqtl.XqtlExampleData;
import filehandling.generic.PerformUpload;

public class AddExampleData
{
	public AddExampleData(Database db) throws Exception
	{
		File tarFu = new File("./publicdata/xqtl/xqtl_exampledata.tar.gz");
		
		//if using tomcat this doesn't work. To solve: I added publicdata to classpath
		//now I can load this data from the Jar file directly
		//@Joeri: you can consider to use this method allways given that ant copies this properly.
		if(!tarFu.exists())
			tarFu = new File(XqtlExampleData.class.getResource("xqtl_exampledata.tar.gz").getFile());
		
		File extractDir = null;
		if(tarFu.exists()){
			extractDir = TarGz.tarExtract(tarFu);
		}else{
			InputStream tfi = JarClass.getFileFromJARFile("Application.jar", "xqtl_exampledata.tar.gz");
			extractDir = TarGz.tarExtract(tfi);
		}

		

		if (ArchiveExportImportPlugin.isExcelFormatXGAPArchive(extractDir))
		{
			new XgapExcelImport(extractDir, db, true);
		}
		else
		{
			new XgapCsvImport(extractDir, db, true);
		}
		
		System.out.println("starting plink file import..");
		File plinkBin = new File("./publicdata/xqtl/plinkbin_exampleset.tar.gz");
		File extractDirPlink = TarGz.tarExtract(plinkBin);
		
		File bedFile = new File(extractDirPlink.getAbsolutePath() + File.separator + "hapmap1_bed.bed");
		File bimFile = new File(extractDirPlink.getAbsolutePath() + File.separator + "hapmap1_bim.bim");
		File famFile = new File(extractDirPlink.getAbsolutePath() + File.separator + "hapmap1_fam.fam");
		
		Investigation inv = db.find(Investigation.class, new QueryRule("name", Operator.EQUALS, "ClusterDemo")).get(0);
		
		boolean molgenisFilesAdded = false;
		
		InvestigationFile bimInvFile = null;
		InvestigationFile famInvFile = null;
		InvestigationFile bedInvFile = null;
		
		db.beginTx();
		
		try{
			String fileSetName = "hapmap1";
			
			bimInvFile = new InvestigationFile();
			bimInvFile.setName(fileSetName+"_bim");
			bimInvFile.setExtension("bim");
			bimInvFile.setInvestigation_Id(inv.getId());
			db.add(bimInvFile);
			
			famInvFile = new InvestigationFile();
			famInvFile.setName(fileSetName+"_fam");
			famInvFile.setExtension("fam");
			famInvFile.setInvestigation_Id(inv.getId());
			db.add(famInvFile);
			
			bedInvFile = new InvestigationFile();
			bedInvFile.setName(fileSetName+"_bed");
			bedInvFile.setExtension("bed");
			bedInvFile.setInvestigation_Id(inv.getId());
			db.add(bedInvFile);
			
			db.commitTx();
			
			molgenisFilesAdded = true;
		}
		catch(Exception e)
		{
			db.rollbackTx();
			throw new Exception(e.getMessage());
		}
		
		if(molgenisFilesAdded)
		{
			System.out.println("plink file records added, now uploading files..");
			PerformUpload.doUpload(db, bimInvFile, bimFile, true);
			PerformUpload.doUpload(db, famInvFile, famFile, true);
			PerformUpload.doUpload(db, bedInvFile, bedFile, true);
			System.out.println("uploading plink files done");
		}
		
		System.out.println("now working on adding binary plink phenotypes example");
		File importFile = new File(extractDirPlink.getAbsolutePath() + File.separator + "fake_metab_hapmap_example_plink_phenotypes.bin");
	
		Data data = new Data();
		data.setName("fake_metab_hapmap_example_plink_phenotypes");
		
		//update this 'Data' with the info from the binary file
		// but not name/investigationname
		// additionally, set storage to Binary :)
		BinaryDataMatrixInstance bmi = new BinaryDataMatrixInstance(importFile);
		data.setFeatureType(bmi.getData().getFeatureType());
		data.setTargetType(bmi.getData().getTargetType());
		data.setValueType(bmi.getData().getValueType());
		data.setInvestigation_Name(bmi.getData().getInvestigation_Name());
		data.setStorage("Binary");
		
		db.add(data);
		
		System.out.println("added to database: " + data.toString());
		
		// code from /molgenis_apps/modules/xgap/matrix/implementations/binary/BinaryDataMatrixWriter.java
		// upload as a MolgenisFile, type 'BinaryDataMatrix'
        HashMap<String, String> extraFields = new HashMap<String, String>();
        extraFields.put("data_" + Data.ID, data.getId().toString());
        extraFields.put("data_" + Data.NAME, data.getName());
		
        System.out.println("now uploading binary plink pheno matrix file");
		PerformUpload.doUpload(db, true, data.getName()+".bin", "BinaryDataMatrix", importFile, extraFields, true);
		 System.out.println("done uploading binary plink pheno matrix file");
	}
}
