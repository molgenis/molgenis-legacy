package plugins.archiveexportimport;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.db.jdbc.JDBCDatabase;
import org.molgenis.framework.db.jpa.JpaDatabase;
import org.molgenis.organization.Investigation;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.Tuple;

import xgap.importexport.DataElementImportByFile;
import decorators.NameConvention;
import filehandling.generic.PerformUpload;

public class XgapCommonImport {

	/**
	 * Generic importer of all types of matrices. FIXME: has a few strange assumptions, have to recheck this..
	 * @param investigationNames
	 * @param db
	 * @param dataDir
	 * @throws Exception
	 */
	public static void importMatrices(List<String> investigationNames, Database db, boolean useTx, File dataDir, boolean skipWhenDestExists)
			throws Exception {

		//FIXME: this apparently assumes a new database because all investigations/matrices are queried?
		//FIXME: why not just get all DataMatrix objects from database instead?
		List<Investigation> investigationList = db.find(Investigation.class,
				new QueryRule("name", Operator.IN, investigationNames));

		Integer[] investigationIds = new Integer[investigationList.size()];

		for (int i = 0; i < investigationList.size(); i++) {
			investigationIds[i] = investigationList.get(i).getId();
		}

		// get the Data objects from the database
		List<Data> matricesInDb = db.find(Data.class, new QueryRule(
				"investigation", Operator.IN, investigationIds));

		for (Data data : matricesInDb) {
			
			String dataFileName = NameConvention.escapeFileName(data.getName()) + getExtension(data);
			
			File dataFile = null; //used for all imports
			
			if(data.getStorage().equals("Database")){
				//import file to the db
				//FIXME: assumes the data files have the same (escaped) names as the matrices? dangerous?
				dataFile = new File(dataDir + File.separator + dataFileName);
				DataElementImportByFile di = new DataElementImportByFile(db);
				di.ImportByFile(dataFile, data, useTx, false, false, false);
			}else{		
				String type = data.getStorage() + "DataMatrix";
				File content = new File(dataDir + File.separator + dataFileName);
				HashMap<String, String> extraFields = new HashMap<String, String>();
				extraFields.put("data_" + Data.ID, data.getId().toString());
				extraFields.put("data_" + Data.NAME, data.getName());
				PerformUpload.doUpload(db, useTx, dataFileName, type, content, extraFields, skipWhenDestExists);
			}
		}
	}
	
	//FIXME: bad..
	private static String getExtension(Data data) throws Exception{
		if(data.getStorage().equals("Binary")){
			return ".bin";
		}else if(data.getStorage().equals("CSV")){
			return ".txt";
		}else if(data.getStorage().equals("Database")){
			return ".txt";
		}else{
			throw new Exception(data.getStorage() + " is not a reckognized storage option to create an extension for (eg bin, txt)");
		}
	}
	
	/**
	 * Helper function. FIXME: needed??
	 * @param investigationFile
	 * @return
	 * @throws Exception
	 */
	public static List<String> getInvestigationNameFromFile(File investigationFile) throws Exception
	{
		final List<String> names = new ArrayList<String>();
		CsvFileReader cfr = new CsvFileReader(investigationFile);

		for(Tuple tuple: cfr)
		{
			names.add(tuple.getString("name"));
		}

		return names;
	}

}
