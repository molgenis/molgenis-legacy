package regressiontest.matrixquery.help;

import java.io.File;
import java.util.List;

import org.molgenis.core.MolgenisFile;
import org.molgenis.core.OntologyTerm;
import org.molgenis.data.Data;
import org.molgenis.data.DecimalDataElement;
import org.molgenis.data.TextDataElement;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Panel;
import org.molgenis.util.TarGz;
import org.molgenis.xgap.Chromosome;
import org.molgenis.xgap.DerivedTrait;
import org.molgenis.xgap.Marker;
import org.molgenis.xgap.Metabolite;

import plugins.archiveexportimport.ArchiveExportImportPlugin;
import plugins.archiveexportimport.XgapCsvImport;
import plugins.archiveexportimport.XgapExcelImport;
import app.JDBCDatabase;

public class DB
{
	static public boolean removeData(JDBCDatabase db) throws Exception
	{
	
		List<MolgenisFile> mfList = db.find(MolgenisFile.class);
		System.out.print("Number of files: " + mfList.size() + "\n");
		db.remove(mfList);
		mfList = db.find(MolgenisFile.class);
		System.out.print("Number of after delete: " + mfList.size() + "\n");		
		
		db.remove(db.find(TextDataElement.class));
		db.remove(db.find(DecimalDataElement.class));
		db.remove(db.find(Marker.class));
		db.remove(db.find(Metabolite.class));
		db.remove(db.find(Chromosome.class));
		db.remove(db.find(Individual.class));
		db.remove(db.find(Data.class));
		db.remove(db.find(DerivedTrait.class));
		db.remove(db.find(Panel.class));
		db.remove(db.find(Investigation.class));
		db.remove(db.find(OntologyTerm.class));
		
		//new emptyDatabase(db, false);
		
		return true;
	}
	
	public boolean importExampleData(JDBCDatabase db) throws Exception{
		File tarFu = new File(this.getClass().getResource("../../csv/tar/gcc_xqtl.tar.gz").getFile());
		File extractDir = TarGz.tarExtract(tarFu);
		
		if(ArchiveExportImportPlugin.isExcelFormatXGAPArchive(extractDir)){
			new XgapExcelImport(extractDir, db);
		}else{
			new XgapCsvImport(extractDir, db);
		}
		
		return true;
	}
}
