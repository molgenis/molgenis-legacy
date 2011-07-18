package plugins.LuceneIndex;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

//import org.apache.cxf.transport.jms.ServerConfig;

public class LuceneConfiguration
{
	public String LUCENE_INDEX_DIRECTORY;
	public String LUCENE_ONTOINDEX_DIRECTORY;
	public String ONTOLOGIES_DIRECTORY;
	public String USE_ONTOLOGIES; 
	public String NUM_OF_FIELDS;
	public String INDX;
	public String msWin;

	public String getINDX() {
		return INDX;
	}
	public  String GetLuceneConfiguration(String param) {
		
		
		File argh = new File(this.getClass().getResource("LuceneIndexConfiguration.bbmri.properties").getFile().replace("%20", " "));
		System.out.println(argh.getAbsolutePath());
		INDX = argh.getAbsolutePath();
		
		System.out.println(argh.exists());
		
		/*
		 * System.out.println(getClass().getClassLoader());
		 
		System.out.println((new File("LuceneIndexConfiguration.properties").getAbsolutePath()));
		//ServerConfig.class.getCanonicalPath();
		
		 File dir1 = new File(".");
	        File dir2 = new File("../../../");
	        try {
	            System.out.println("Current dir : " + dir1.getCanonicalPath());
	            System.out.println("Parent  dir : " + dir2.getCanonicalPath());
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		*/
		
		Properties configFile = new Properties();

		try {
			configFile.load(new FileInputStream(INDX));
			
		} catch (IOException e) {
				e.printStackTrace();
		}
		if (param=="NUM_OF_FIELDS") return NUM_OF_FIELDS=configFile.getProperty("numberOfFields");
		if (param=="USE_ONTOLOGIES") return USE_ONTOLOGIES = configFile.getProperty("useOntologiesInQueryExpansion");
		if (param=="LUCENE_INDEX_DIRECTORY") return LUCENE_INDEX_DIRECTORY = configFile.getProperty("lucene_index_directory");
		if (param=="LUCENE_ONTOINDEX_DIRECTORY") return LUCENE_ONTOINDEX_DIRECTORY = configFile.getProperty("lucene_ontocat_index_directory");
		if (param=="ONTOLOGIES_DIRECTORY") return ONTOLOGIES_DIRECTORY  = configFile.getProperty("ontologies_directory");
		if (param=="msWin") return msWin = configFile.getProperty("msWin");
		
		return param;
		
	}

	
}
