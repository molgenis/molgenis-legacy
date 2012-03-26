package org.molgenis.ibdportal.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DataFormatException;

public class ReplaceGenoIds
{

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception
	{
		if(args.length != 3) {
			throw new Exception("You must supply 3 arguments: FAM file to be read from, CSV file for linking pheno to geno IDs, FAM file to be created");
		}
		
		//get args
		String famFileName = args[0];
		String csvFileName = args[1];
		String outputFileName = args[2];
		
		//print args
		System.out.println("ReplaceGenoIds called with arguments:");
		System.out.println("FAM file = " + famFileName);
		System.out.println("CSV file = " + csvFileName);
		System.out.println("Output FAM file = " + outputFileName);
		
		//check if source files exist
		File famFile = new File(famFileName);
		if (famFile == null || !famFile.exists()){
			throw new Exception("FAM file '" + famFile + "' not found at location '" + famFile.getAbsolutePath() + "'");
		}
		if(!famFile.getName().endsWith(".fam")){
			throw new Exception("FAM file name '" + famFileName + "' does not end with '.fam', are you sure it is a FAM file?");
		}
		File csvFile = new File(csvFileName);
		if (csvFile == null || !csvFile.exists()){
			throw new Exception("CSV file '" + csvFile + "' not found at location '" + csvFile.getAbsolutePath() + "'");
		}
		if(!csvFile.getName().endsWith(".csv")){
			throw new Exception("CSV file name '" + csvFileName + "' does not end with '.csv', are you sure it is a CSV file?");
		}
		
		// Read CSV file, create hashmap
		Map<String, String> idMap = new HashMap<String, String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile)));
		String line = null;
		while ((line = reader.readLine()) != null) {
			 String[] lineParts = line.split(";");
			 idMap.put(lineParts[1], lineParts[0]);
			 //System.out.println("Added key = " + lineParts[1] + " value = " + lineParts[0] + " to ID hashmap");
		}
		reader.close();
		
		// Open output file for writing
		FileWriter output = new FileWriter(outputFileName);
		
		// Read FAM file, replace geno with pheno ID and write to new file
		int lineNr = 1;
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(famFile)));
		while ((line = reader.readLine()) != null) {
		    String[] lineParts = line.split("\\s"); // FAM file contains 6 items, 2nd is ID
		    String genoId = lineParts[1];
		    String phenoId = idMap.get(genoId);
		    if (phenoId != null) {
		    	//System.out.println("Replacing " + genoId + " with " + phenoId + " on line " + lineNr);
		    	output.write(line.replace(genoId, phenoId) + "\n");
		    } else {
		    	System.out.println( "Keeping geno ID '" + genoId + "' on line " + lineNr + " because it's not listed in your CSV file");
		    	output.write(line + "\n");
		    }
		    lineNr++;
		}
		reader.close();
		output.flush();
		output.close();
	}

}
