package org.molgenis.compute;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.ngs.LibraryLane;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.protocol.WorkflowElement;
import org.molgenis.util.CsvFileWriter;
import org.molgenis.util.CsvWriter;
import org.molgenis.util.Tuple;

import app.DatabaseFactory;

/**
 * This class can import/export data out of compute into/from
 * <ul>
 * <li>Freemarker templates for each protocol + annotation header
 * <li>parameters.txt for all parameters
 * <li>worksheet.txt for all targets 'platgeslagen' / 'flattenend'
 * <li>workflowelement.txt for all elements
 * <ul>
 * 
 * @author mswertz
 * 
 */
public class ExportImportToTemplates
{
	Logger logger = Logger.getLogger(ExportImportToTemplates.class);

	public static void main(String[] args) throws Exception
	{
		// here evil hardcoding
		File dir = new File("/tmp/compute_test");
		Database db = DatabaseFactory.create();

		// initialize logger
		BasicConfigurator.configure();

		// export
		dir.mkdirs(); // make the dir if not exist
		new ExportImportToTemplates().export(dir, db);

	}

	/**
	 * Export to directory: +dir ++templates/protocol1.ftl, etc ++parameters.csv
	 * ++workflowelements.csv ++worksheet.csv
	 * 
	 * @param dir
	 * @param db
	 * @throws Exception
	 */
	public void export(File dir, Database db) throws Exception
	{
		// retrieve the data
		List<ComputeProtocol> protocols = db.find(ComputeProtocol.class);
		List<ComputeParameter> parameters = db.find(ComputeParameter.class);
		List<WorkflowElement> elements = db
				.find(WorkflowElement.class);
		List<Tuple> worksheet = this.generateWorksheet(db, LibraryLane.class);

		// write out parameters and workflowelements just as before to
		// ComputeParameter.txt and WorkflowElements.txt
		// todo: rewrite to filter fields?

		// export parameters
		this.exportParameters(dir, parameters);
		
		// export the workflow elements
		this.exportWorkflowElements(dir, elements);

		// export protocols
		this.exportProtocols(dir, protocols);
	}

	private void exportProtocols(File dir, List<ComputeProtocol> protocols) throws FileNotFoundException
	{
		// TODO Auto-generated method stub
		for (ComputeProtocol p : protocols)
		{
			File target = new File(dir.getAbsoluteFile() + File.separator + "protocols" + File.separator
					+ p.getName() + ".ftl");
			
			//make sure parent folder exists
			target.getParentFile().mkdirs();
			
			PrintWriter writer = new PrintWriter(target);
			
			//create header string
			String header = "#";
			if(p.getWalltime() != null) header += "walltime="+p.getWalltime()+" ";
			if(p.getCores() != null) header += "cores="+p.getCores()+" ";
			if(p.getMemoryReq() != null) header += "memory="+p.getMemoryReq()+" "; 
			
			//write header and script template
			writer.println(header);
			writer.println();
			writer.println(p.getScriptTemplate());
			writer.close();
		}
	}

	/** Export parameters while only keeping a subset of the headers (for now) 
	 * @throws IOException */
	private void exportParameters(File dir, List<ComputeParameter> parameters) throws IOException
	{
		File paramFile = new File(dir.getAbsoluteFile() + File.separator
				+ "computeparameter.txt");
		List<String> paramFields = Arrays.asList(new String[]
		{ "name", "description", "defaultValue" });

		CsvWriter writer = new CsvFileWriter(paramFile, paramFields);
		writer.writeHeader();
		for (ComputeParameter p : parameters)
			writer.writeRow(p);
		writer.close();
	}
	
	private void exportWorkflowElements(File dir, List<WorkflowElement> elements) throws IOException
	{
		File paramFile = new File(dir.getAbsoluteFile() + File.separator
				+ "workflowelement.txt");
		List<String> paramFields = Arrays.asList(new String[]
		{"name","protocol_name","PreviousSteps_name"});

		CsvWriter writer = new CsvFileWriter(paramFile, paramFields);
		writer.writeHeader();
		for (WorkflowElement e : elements)
			writer.writeRow(e);
		writer.close();
	}
	
	

	/**
	 * Generate a worksheet from the database using one level of target as the
	 * 'main' level. E.g. in case of multiplexing one lane will have multiple
	 * samples.
	 * 
	 * @param db
	 * @return
	 */
	public List<Tuple> generateWorksheet(Database db,
			Class<? extends ObservationElement> target)
	{
		// this will query first the selected target and load those into tuples
		// meanwhile it will also follow xrefs and mrefs on the target to find
		// associated ObservatioElement.
		// in first version hardcode:
		// Sample <- Lane

		return null;
	}
}
