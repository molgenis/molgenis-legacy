/**
 * @author Jessica Lundberg
 * @date 27-10-2010
 * 
 * Class to pre-load the NGS database with essential basic information.
 */
package plugins.fillngsdb;



import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jxl.common.Logger;

import org.molgenis.core.OntologyTerm;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.organization.Investigation;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.protocol.Workflow;
import org.molgenis.protocol.WorkflowElement;

import commonservice.CommonService;

public class FillNgsDatabase {

	private CommonService ct;
	private static final Logger logger = Logger.getLogger(FillNgsDatabase.class);
	private static final long serialVersionUID = -4185805410343262548L;
	private Database db;

	public FillNgsDatabase(Database db) {
		ct = CommonService.getInstance();
		ct.setDatabase(db);
		this.db = db;
		
	}

	/**
	 * Populate the database with ngs information
	 * 
	 * @throws IOException
	 * @throws DatabaseException
	 * @throws ParseException 
	 * 
	 */
	public void populateDatabase() throws IOException, ParseException {
	    	try {
	    	db.beginTx();
		addMeasurements();
		logger.info("Successfully added Measurements (Observable Features)");
		addProtocols();
		logger.info("Successfully added protocols to the database");
		addLabWorkers();
		logger.info("Successfully added Lab Workers");
		addWorkflows();
		logger.info("Successfully added Workflows");
		addWorkflowElements();
		logger.info("Successfully added Workflow Elements");
		addOntologyTerms();
		
		db.commitTx();
		logger.info("The database was successfully filled");
	    	} catch (DatabaseException d) {
	    	    logger.error("The database was not successfully filled", d);
	    	    try { db.rollbackTx(); } catch (Exception e) { logger.warn("Rollback failed"); }
	    	}
	    	
	    	
	}

	/**Adds an ontology term to be used by Project, which extends Panel, which is
	 * where the term is required.
	 * 
	 * @throws DatabaseException
	 * @throws IOException
	 */
	private void addOntologyTerms() throws DatabaseException, IOException {
	    OntologyTerm ontTerm = new OntologyTerm();
	    ontTerm.set__Type("OntologyTerm");
	    ontTerm.setName("Project");
	    ontTerm.setDefinition("Project");
	    db.add(ontTerm);
	    
	    
	}

	/**Adds workflows
	 * 
	 * @throws DatabaseException
	 * @throws IOException
	 * @throws ParseException
	 */
	private void addWorkflows() throws DatabaseException, IOException, ParseException {
		Workflow workflow1 = new Workflow();
		workflow1.setName("DNA_Illumina");
		db.add(workflow1);
		
		Workflow workflow2 = new Workflow();
		workflow2.setName("RNA_Illumina_Nano_Experion");
		db.add(workflow2);
	}
	
	/**
	 * 
	 * @throws DatabaseException
	 * @throws ParseException
	 * @throws IOException
	 */
	public void addWorkflowElements() throws DatabaseException, ParseException, IOException {
	    
	    	
	
		Workflow dna_illumina = ct.getWorkflow("DNA_Illumina");
		Workflow rna_illumina = ct.getWorkflow("RNA_Illumina_Nano_Experion");	
		
		List<Integer> workflows = new ArrayList<Integer>();
		workflows.add(dna_illumina.getId());
		workflows.add(rna_illumina.getId());
		
		/* Begin Workflow */
		WorkflowElement element1 = new WorkflowElement();
		element1.setName("Begin Workflow");
		element1.setProtocol(ct.getProtocol("initial"));
		element1.setWorkflow_Id(workflows);
		
		db.add(element1);
		
		
		//Gel workflow element
		WorkflowElement element2 = new WorkflowElement();
		element2.setName("Gel electrophoresis");
		element2.setProtocol(ct.getProtocol("gel quality control"));
		element2.setWorkflow_Id(dna_illumina.getId());
		
		List<Integer> prevSteps1 = new ArrayList<Integer>();
		prevSteps1.add(ct.getWorkflowElement("Begin Workflow").getId());
		element2.setPreviousSteps_Id(prevSteps1);
		
		db.add(element2);
		
		//nanodrop
		WorkflowElement element3 = new WorkflowElement();
		element3.setName("Nanodrop");
		element3.setProtocol(ct.getProtocol("nanodrop quality control"));
		element3.setWorkflow_Id(workflows);
		
		List<Integer> prevSteps2 = new ArrayList<Integer>();
		prevSteps2.add(ct.getWorkflowElement("Begin Workflow").getId());
		element3.setPreviousSteps_Id(prevSteps2);
		db.add(element3);
		
		//experion
		WorkflowElement element4 = new WorkflowElement();
		element4.setName("Experion");
		element4.setProtocol(ct.getProtocol("experion quality control"));
		element4.setWorkflow_Id(workflows);
		
		List<Integer> prevSteps3 = new ArrayList<Integer>();
		prevSteps3.add(ct.getWorkflowElement("Begin Workflow").getId());
		prevSteps3.add(ct.getWorkflowElement("Nanodrop").getId());
		prevSteps3.add(ct.getWorkflowElement("Gel electrophoresis").getId());
		element4.setPreviousSteps_Id(prevSteps3);
		db.add(element4);
		
		//end-repair
		WorkflowElement element5 = new WorkflowElement();
		element5.setName("End-repair");
		element5.setProtocol(ct.getProtocol("end repair sequencing prep"));
		element5.setWorkflow_Id(workflows);
		
		List<Integer> prevSteps4 = new ArrayList<Integer>();
		prevSteps4.add(ct.getWorkflowElement("Experion").getId());
		prevSteps4.add(ct.getWorkflowElement("Nanodrop").getId());
		prevSteps4.add(ct.getWorkflowElement("Gel electrophoresis").getId());
		element5.setPreviousSteps_Id(prevSteps4);
		db.add(element5);
		
		//dA-tailing
		WorkflowElement element6 = new WorkflowElement();
		element6.setName("dA-tailing");
		element6.setProtocol(ct.getProtocol("dA tailing sequencing prep"));
		element6.setWorkflow_Id(workflows);
		
		List<Integer> prevSteps5 = new ArrayList<Integer>();
		prevSteps5.add(ct.getWorkflowElement("End-repair").getId());
		element6.setPreviousSteps_Id(prevSteps5);
		db.add(element6);
		
		//ligase
		WorkflowElement element7 = new WorkflowElement();
		element7.setName("Ligase");
		element7.setProtocol(ct.getProtocol("ligase sequencing prep"));
		element7.setWorkflow_Id(workflows);
		
		List<Integer> prevSteps6 = new ArrayList<Integer>();
		prevSteps6.add(ct.getWorkflowElement("dA-tailing").getId());
		element7.setPreviousSteps_Id(prevSteps6);
		db.add(element7);
		
		//fragmentase
		WorkflowElement element8 = new WorkflowElement();
		element8.setName("Fragmentase");
		element8.setProtocol(ct.getProtocol("framentase"));
		element8.setWorkflow_Id(workflows);
		
		List<Integer> prevSteps7 = new ArrayList<Integer>();
		prevSteps7.add(ct.getWorkflowElement("Ligase").getId());
		element8.setPreviousSteps_Id(prevSteps7);
		db.add(element8);
		
		/* Nebulization */
		WorkflowElement element9 = new WorkflowElement();
		element9.setName("Nebulization");
		element9.setProtocol(ct.getProtocol("nebulization"));
		element9.setWorkflow_Id(workflows);
		
		List<Integer> prevSteps8 = new ArrayList<Integer>();
		prevSteps8.add(ct.getWorkflowElement("Ligase").getId());
		element9.setPreviousSteps_Id(prevSteps8);
		db.add(element9);
		
		//pcr amplification
		WorkflowElement element10 = new WorkflowElement();
		element10.setName("Amplification");
		element10.setProtocol(ct.getProtocol("pcr amplification"));
		element10.setWorkflow_Id(workflows);
		
		List<Integer> prevSteps9 = new ArrayList<Integer>();
		prevSteps9.add(ct.getWorkflowElement("Nebulization").getId());
		prevSteps9.add(ct.getWorkflowElement("Fragmentase").getId());
		element10.setPreviousSteps_Id(prevSteps9);
		db.add(element10);
		
		//size selection
		WorkflowElement element11 = new WorkflowElement();
		element11.setName("Size selection");
		element11.setProtocol(ct.getProtocol("size selection"));
		element11.setWorkflow_Id(workflows);
		
		List<Integer> prevSteps10 = new ArrayList<Integer>();
		prevSteps10.add(ct.getWorkflowElement("Amplification").getId());
		element11.setPreviousSteps_Id(prevSteps10);
		db.add(element11);
		
		//ga run
		WorkflowElement element12 = new WorkflowElement();
		element12.setName("Genome Analyzer II Run");
		element12.setProtocol(ct.getProtocol("genome analyzer run"));
		element12.setWorkflow_Id(workflows);
		
		List<Integer> prevSteps11 = new ArrayList<Integer>();
		prevSteps11.add(ct.getWorkflowElement("Size selection").getId());
		element12.setPreviousSteps_Id(prevSteps11);
		db.add(element12);
	}

	/**
	 * Add all basic protocols for NGS sequencing to the database
	 * 
	 * @throws IOException
	 * @throws DatabaseException
	 * 
	 */
	public void addProtocols() throws DatabaseException, IOException {
		try { // this is very naughty to surround everything with one try, but i
				// cba to make 20 trys
			ct.addProtocol("initial", "Before starting workflow",
					new ArrayList<String>());

			List<String> gel = new ArrayList<String>();
			gel.add("gel jpeg");
			ct.addProtocol("gel quality control",
					"A quality check is done using gel electrophoreses", gel);

			List<String> nanodrop = new ArrayList<String>();
			nanodrop.add("nanodrop jpeg");
			nanodrop.add("nanodrop graph");
			ct.addProtocol("nanodrop quality control",
					"A quality check is done using nanodrop ", nanodrop);

			List<String> experion = new ArrayList<String>();
			experion.add("experion concentration ng nl");
			experion.add("thinned raw");
			experion.add("experion graph1");
			experion.add("experion graph2");
			experion.add("experion table1");
			experion.add("experion table2");
			experion.add("experion results");

			ct.addProtocol("experion quality control",
					"A quality check is done using experion", experion);

			List<String> endRepair = new ArrayList<String>();
			endRepair.add("end repair time");
			endRepair.add("end repair temp");
			ct.addProtocol(
					"end repair sequencing prep",
					"End-repair (repairing the ends of the sample after fragmentation)",
					endRepair);

			List<String> dATailing = new ArrayList<String>();
			dATailing.add("dA tailing time");
			dATailing.add("dA tailing temp");
			ct.addProtocol(
					"dA tailing sequencing prep",
					"dA-tailing (incorporate a non-templated dAMP on the 3« end of a blunt DNA fragment)",
					dATailing);

			List<String> ligase = new ArrayList<String>();
			ligase.add("ligase time");
			ligase.add("ligase temp");
			ligase.add("ligase adaptor1");
			ligase.add("ligase adaptor2");
			ct.addProtocol(
					"ligase sequencing prep",
					"ligation (Catalyzes the formation of a phosphodiester bond between juxtaposed 5' phosphate and 3' hydroxyl termini in duplex DNA or RNA)",
					ligase);

			List<String> nebulization = new ArrayList<String>();
			nebulization.add("fragmentation time");
			nebulization.add("remaining nl after fragmentation");
			nebulization.add("fragmentation jpeg");
			ct.addProtocol("nebulization", "fragmentation using nebulization",
					nebulization);

			List<String> fragmentase = new ArrayList<String>();
			fragmentase.add("fragmentation time");
			fragmentase.add("remaining nl after fragmentation");
			fragmentase.add("fragmentation jpeg");
			ct.addProtocol("framentase", "fragmentation using fragmentase",
					fragmentase);

			List<String> amplification = new ArrayList<String>();
			amplification.add("amplification cycles");
			amplification.add("amplification primer1");
			amplification.add("amplification primer2");
			ct.addProtocol("pcr amplification",
					"Amplifying the sample through PCR", amplification);
			
			ct.addProtocol("size selection", "Choose best-sized pieces for ga run",
					new ArrayList<String>());

			List<String> ga = new ArrayList<String>();
			ct.addProtocol("genome analyzer run",
					"Running the samples through the Genome Analyzer II", ga);
		} catch (Exception e) {
			logger.error(
					"Something went horribly awry while adding protocols to the database",
					e);
		}
	}

	/**
	 * Add all observable features for NGS sequencing to the database
	 * 
	 * @throws IOException
	 * @throws DatabaseException
	 * 
	 */
	public void addMeasurements() throws DatabaseException, IOException {

		// Quality Control
		ct.makeMeasurement(
				"gel jpeg",
				"output from a gel electrophoresis quality control, in the form of a jpeg",
				"image");
		ct.makeMeasurement(
				"nanodrop jpeg",
				"output from a nanodrop quality control test, as a jpeg (concentrations of dna listed)",
				"image");
		ct.makeMeasurement("nanodrop graph",
				"graph results of nanodrop quality control test", "image");
		ct.makeMeasurement("experion concentration ng nl",
				"the concentration of the solution used for experion (ng/nl)",
				"decimal");
		ct.makeMeasurement("thinned raw", "if the dna is thinned or raw",
				"string"); // TODO: should be an Enum
		ct.makeMeasurement("experion graph1",
				"Graph depicting experion results", "image");
		ct.makeMeasurement("experion graph2",
				"Graph depicting experion results", "image");
		ct.makeMeasurement("experion table1",
				"Table depicting experion results (concentrations)", "image");
		ct.makeMeasurement("experion table2",
				"Table depicting experion results (concentrations)", "image");
		ct.makeMeasurement(
				"experion results",
				"The result of an experion quality control, saying whether the dna is good to go (i.e. positive or negative)",
				"string"); // TODO: should be a boolean

		// Fragmentation
		ct.makeMeasurement("fragmentation time",
				"Time in minutes that the sample is left to fragment", "int"); 
																				// int??
		ct.makeMeasurement("remaining nl after fragmentation",
				"the remaining solution amount after fragmentation (in nl)",
				"decimal");
		ct.makeMeasurement("fragmentation jpeg",
				"image showing results of fragmentation", "image");

		// Preparation
		ct.makeMeasurement("end repair time",
				"time in minutes spent completing end-repair", "int");
																		// int??
		ct.makeMeasurement("end repair temp",
				"temperature in Celsius that the end-repair was completed at",
				"decimal");
		ct.makeMeasurement("dA tailing time",
				"time in minutes spent completing dA-tailing", "int");
																		// int??
		ct.makeMeasurement("dA tailing temp",
				"temperature in Celsius that dA-tailing was completed at",
				"decimal");
		ct.makeMeasurement("ligase time",
				"time in minutes spent completing ligase", "int"); 
																	// int??
		ct.makeMeasurement("ligase temp",
				"temperature in Celsius that ligase was completed at",
				"decimal");
		ct.makeMeasurement("ligase adaptor1", "adaptor used for ligation",
				"string");
		ct.makeMeasurement("ligase adaptor2", "adaptor used for ligation",
				"string");

		// Amplification
		ct.makeMeasurement("amplification cycles",
				"Number of cycles used during amplification", "int");
		ct.makeMeasurement("amplification primer1",
				"name of primer used for amplification", "string");
		ct.makeMeasurement("amplification primer2",
				"name of primer used for amplification", "string");

	}

	/**
	 * Add all labworkers to the database
	 * 
	 * @throws IOException
	 * @throws DatabaseException
	 * 
	 */
	public void addLabWorkers() throws DatabaseException, IOException {
		ct.addLabWorker("Jelkje", "Bergsma", "j.j.bergsma@medgen.umcg.nl");
	}
}
