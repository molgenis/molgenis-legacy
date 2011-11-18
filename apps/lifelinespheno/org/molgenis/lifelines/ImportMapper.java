package org.molgenis.lifelines;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.molgenis.framework.db.Database;
import org.molgenis.lifelines.listeners.ImportTupleListener;
import org.molgenis.lifelines.listeners.VwDictListener;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReader;

import app.DatabaseFactory;

/**
 * This class is responsible for mapping tuples from a source schema to entities
 * in MOLGENIS. The tuples can come from a CSV file or from SQL queries on some
 * database. The target schema is molgenis.pheno
 */
public class ImportMapper {

	//for each table / csv file there will be one entry
	static Map<String, ImportTupleListener> mappings = new LinkedHashMap<String, ImportTupleListener>();
	
	/** for testing only!
	 * @throws Exception */
	public static void main(String[] args) throws Exception
	{
		//path to directory with csv files
		String path = "/Users/jorislops/";
		
		//target for output, either CsvWriter or Database
		Database db = DatabaseFactory.create();
		
		//create a mapping in right order of import
		mappings.put("VW_DICT", new VwDictListener("VW_DICT", db)); //will import measurements
		mappings.put("BEZOEK", new LifeLinesStandardListener("BEZOEK",db)); //will import values
		
		//iterate through the map assuming CSV files
		for(String csvFileName: mappings.keySet())
		{
			//create CsvReader
			CsvReader reader = new CsvFileReader(new File(path + csvFileName +".csv"));
			
			reader.parse(mappings.get(csvFileName));
			
			mappings.get(csvFileName).commit();
			
		}
		
		
		
	}
	
}
