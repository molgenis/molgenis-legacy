package regressiontest.cluster;

import java.io.File;
import java.io.InputStream;

import org.molgenis.framework.db.Database;
import org.molgenis.util.JarClass;
import org.molgenis.util.TarGz;

import plugins.archiveexportimport.ArchiveExportImportPlugin;
import plugins.archiveexportimport.XgapCsvImport;
import plugins.archiveexportimport.XgapExcelImport;

public class AddExampleData
{
	public AddExampleData(Database db) throws Exception
	{
		File tarFu = new File("./publicdata/xqtl/xqtl_exampledata.tar.gz");
		
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
