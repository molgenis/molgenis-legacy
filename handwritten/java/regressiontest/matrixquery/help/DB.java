package regressiontest.matrixquery.help;

import java.io.File;
import java.io.IOException;

import org.molgenis.core.OntologyTerm;
import org.molgenis.data.Data;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Panel;
import org.molgenis.pheno.Species;
import org.molgenis.util.TarGz;
import org.molgenis.xgap.Chromosome;
import org.molgenis.xgap.Marker;
import org.molgenis.xgap.Metabolite;

import plugins.archiveexportimport.ArchiveExportImportPlugin;
import plugins.archiveexportimport.XgapCsvImport;
import plugins.archiveexportimport.XgapExcelImport;
import app.JDBCDatabase;

public class DB
{
	static public boolean removeFuMetadata(JDBCDatabase db) throws DatabaseException, IOException
	{
		db.remove(db.find(Marker.class));
		db.remove(db.find(Individual.class));
		db.remove(db.find(Metabolite.class));
		db.remove(db.find(Data.class));
		db.remove(db.find(Panel.class));
		db.remove(db.find(Chromosome.class));
		db.remove(db.find(Species.class));
		db.remove(db.find(OntologyTerm.class));
		//db.remove(db.find(BibliographicReference.class));
		db.remove(db.find(Investigation.class));
		return true;
	}
	
	public boolean importFuData(JDBCDatabase db) throws Exception{
		File tarFu = new File(this.getClass().getResource("../../csv/tar/x1_3/Fu.tar.gz").getFile());
		File extractDir = TarGz.tarExtract(tarFu);
		
		if(ArchiveExportImportPlugin.isExcelFormatXGAPArchive(extractDir)){
			new XgapExcelImport(extractDir, db);
		}else{
			new XgapCsvImport(extractDir, db);
		}
		
		return true;
	}
}
