package plugins.snpannotator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import org.apache.commons.dbcp.BasicDataSource;
import org.molgenis.framework.db.Database;
import org.molgenis.util.Tuple;

import app.DatabaseFactory;

import com.opera.core.systems.scope.exceptions.FatalException;


public class machielAnnotator {

	// SNP data
	private int location = -1;
	private String consequence_type = "";
	private String allele = "";
	private int strand = 0;
	private String chromosome = "";
	
	// Gene data
	private int start = -1;
	private int end = -1;
	private String description = "";
	private String biotype = "";
		
	// ID's
	private int variation_id = -1;
	private int seq_region_id = -1;
	private int gene_id;
	
	// Databases
	public static String homo_sapiens_variation = "homo_sapiens_variation_52_36n";
	public static String homo_sapiens_core = "homo_sapiens_core_52_36n";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String x = "rs11900053;rs300789;rs300780;rs300773;rs10519439;rs300711;rs408209;rs409572;rs10495480;rs300692";
		String[] y = x.split(";");
		
		for(String z : y)
		{
			System.out.println("SNP: " + z);
			new machielAnnotator(z);
		}
	}
	
	public machielAnnotator(String SNPID) {
		/**
		 * 0) goto SNP database
		 * 1) get variation ID
		 * 2) get all info from variation_feature
		 * 2.5) get chromosome
		 * 3) goto gene database
		 * 4) check for genes in this regen
		 */
		
		Database db;
		
		// connect to SNP database
		/** 0 */ db = setDatabaseConnection(homo_sapiens_variation);		
		/** 1 */ variation_id = getVariationIDfromName(db, SNPID);
		/** 2 */ setVariationInfo(db, variation_id);
		/** 2.5 */ setChromosome(db);
		/** 3 */ db = setDatabaseConnection(homo_sapiens_core);
		/** 4 */ setGeneInfo(db);
		
		
		try{
			// Create file 
			FileWriter fstream = new FileWriter("SNPscraper.txt",true);
			BufferedWriter out = new BufferedWriter(fstream);
			//out.write("Hello Java");

			out.write("SNP: " + SNPID + "\n");
			out.write("Location: " + location + "\n");
			out.write("Chromosoom: " + chromosome + "\n");
			out.write("Consequence type: " + consequence_type + "\n");
			out.write("Strand: " + strand + "\n");
			out.write("Allele: " + allele + "\n");
			
			out.write("In gene: " + gene_id + "\n");
			out.write("Gene description: " + description + "\n");
			out.write("Gene biotype: " + biotype + "\n");
			out.write("Gene location: " + start + "-" + end + "\n");
			out.write("\n");
			
			
			out.close();
		} catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		
		
		
	}
	
	private int getVariationIDfromName(Database db, String Name)
	{
		int ID = -1;
		
		// variables
		List<Tuple> result;
				
		try {
			result = db.sql("select variation_id from variation where name='" + Name + "';");
			for (Tuple t : result) {
				ID = t.getInt(0);				
				break;
			}		
		} catch (Exception e) {
			System.out.println("DB error: Variation ID query");
		}
		
		return ID;
	}
	
	private void setVariationInfo(Database db, int variation_id)
	{		
		// variables
		List<Tuple> result;
				
		try {
			result = db.sql("select seq_region_start, seq_region_strand, allele_string, consequence_type, seq_region_id from variation_feature where variation_id=" + variation_id + ";");
			for (Tuple t : result) {
				location = t.getInt(0);
				strand = t.getInt(1);
				allele = t.getString(2);
				consequence_type = t.getString(3);
				seq_region_id = t.getInt(4); 
				break;
			}		
		} catch (Exception e) {
			System.out.println("DB error: variation feature query");
		}
	}
	
	private void setChromosome(Database db)
	{		
		// variables
		List<Tuple> result;
				
		try {
			result = db.sql("select name from seq_region where seq_region_id=" + seq_region_id + ";");
			for (Tuple t : result) {
				chromosome = t.getString(0); 
				break;
			}		
		} catch (Exception e) {
			System.out.println("DB error: variation feature query");
		}
	}
	
	private void setGeneInfo(Database db)
	{		
		// variables
		List<Tuple> result;
				
		try {
			result = db.sql(" select gene_id, biotype, description, seq_region_start, seq_region_end from gene where seq_region_id=" + seq_region_id + " and seq_region_start <=" + location + " and seq_region_end >= " + location + ";");
			for (Tuple t : result) {
				gene_id = t.getInt(0);
				biotype = t.getString(1);
				description = t.getString(2);
				start = t.getInt(3);
				end = t.getInt(4); 
				break;
			}
			if (result.size() > 1)
			{
				System.out.println("Multiple genes detected, only showing first one.");
			}
		} catch (Exception e) {
			System.out.println("DB error: variation feature query");
		}
	}
	
	/**
	 * Creates a database connection to mysql
	 * 
	 * @param inputName the file containing the information needed to make the connection
	 * @return JDBCDatabase connection
	 * @throws FatalException 
	 */
	private Database setDatabaseConnection(String dbName) {
		BasicDataSource data_src = new BasicDataSource();
		data_src.setDriverClassName("com.mysql.jdbc.Driver");
		data_src.setUsername("anonymous");
		data_src.setUrl("jdbc:mysql://ensembldb.ensembl.org:5306/" + dbName);
		Database db = null;
		
		try {
			db = DatabaseFactory.create(data_src, new File(""));
		} catch (Exception e) {
			System.out.println("DB error: " + dbName);
		}
		
		return (Database) db;
	}
	

}
