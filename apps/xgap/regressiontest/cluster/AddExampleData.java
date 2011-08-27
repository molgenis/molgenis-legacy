package regressiontest.cluster;

import java.io.File;
import java.io.InputStream;

import org.molgenis.framework.db.Database;
import org.molgenis.util.JarClass;
import org.molgenis.util.TarGz;

import plugins.archiveexportimport.ArchiveExportImportPlugin;
import plugins.archiveexportimport.XgapCsvImport;
import plugins.archiveexportimport.XgapExcelImport;
import xqtl.XqtlExampleData;

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
	}
}
