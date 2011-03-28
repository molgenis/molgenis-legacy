package regressiontest.cluster;

import java.io.File;

import org.molgenis.framework.db.Database;
import org.molgenis.util.TarGz;

import plugins.archiveexportimport.ArchiveExportImportPlugin;
import plugins.archiveexportimport.XgapCsvImport;
import plugins.archiveexportimport.XgapExcelImport;

public class AddExampleData
{
	public AddExampleData(Database db) throws Exception
	{
		File tarFu = new File(this.getClass().getResource("../csv/tar/gcc_xqtl.tar.gz").getFile());
		File extractDir = TarGz.tarExtract(tarFu);

		if (ArchiveExportImportPlugin.isExcelFormatXGAPArchive(extractDir))
		{
			new XgapExcelImport(extractDir, db);
		}
		else
		{
			new XgapCsvImport(extractDir, db);
		}
	}
}
