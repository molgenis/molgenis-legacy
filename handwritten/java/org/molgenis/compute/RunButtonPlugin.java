/* Date:        January 26, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.compute;


import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.protocol.WorkflowElement;
import org.molgenis.util.Tuple;

import java.util.ArrayList;
import java.util.List;

public class RunButtonPlugin extends PluginModel
{
	String stuffToShow = "";
	WorkflowElement startingWorkflowElement = null;
	String nameOfCurrentJob = "";
	
	public RunButtonPlugin(String name, ScreenModel parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_compute_RunButtonPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/compute/RunButtonPlugin.ftl";
	}

	/*
	//Non recursive
	private void compute(AnalysisElement ae, Database db, List<String> listOfParameters, List<String> listOfCommands) {
		//Get the operation of this AnalysisElement
		Operation currentOperation = null;
		try {
			currentOperation = db.findById(Operation.class, ae.getProtocol());
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Get the input Parameters for this Operation
		List<Integer> currentInputParametersIDs = currentOperation.getInputParameters_Id();
		for (Integer currentInputParameterID : currentInputParametersIDs) {
			Parameter currentInputParameter = null;
			try {
				currentInputParameter = db.findById(Parameter.class, currentInputParameterID);
			} catch (DatabaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String nameOfCurrentInputParameter = currentInputParameter.getName();
			if (!listOfParameters.contains(nameOfCurrentInputParameter)) {
				listOfParameters.add(nameOfCurrentInputParameter);
			}
		}

		//Get the output Parameters for this Operation
		List<Integer> currentOutputParametersIDs = currentOperation.getOutputParameters_Id();
		for (Integer currentOutputParameterID : currentOutputParametersIDs) {
			Parameter currentOutputParameter = null;
			try {
				currentOutputParameter = db.findById(Parameter.class, currentOutputParameterID);
			} catch (DatabaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String nameOfCurrentOutputParameter = currentOutputParameter.getName();
			if (!listOfParameters.contains(nameOfCurrentOutputParameter)) {
				listOfParameters.add(nameOfCurrentOutputParameter);
			}
		}

		//Add the template of the command to be executed. This is the correct order!
		listOfCommands.add(currentOperation.getCommandTemplate());
	}
	*/
	
	/*
	private void computeRecursive(AnalysisElement ae, List<AnalysisElement> currentAnalysisElements, boolean computed[], Database db, List<String> listOfParameters, List<String> listOfCommands) {
		//Does this Analysis Elements has previous steps?
		List<String> currentPreviousSteps = ae.getPreviousSteps2_name();
		
		//Find all the previousSteps that are not computed and compute them
		for (String currentPreviousStep : currentPreviousSteps) {
			int currentAnalysisElementsIndex = 0;
			for (AnalysisElement currentAnalysisElement : currentAnalysisElements) {
				if (currentAnalysisElement.getName().equals(currentPreviousStep)) {
					//Is this computed?
					if (!computed[currentAnalysisElementsIndex]) {
						//No it is not computed. Compute it! (recursion)
						//this.stuffToShow += "From computeRecursive: about to recurse: " + currentAnalysisElement.getName() + " index: " + currentAnalysisElementsIndex + " <br>";
						computeRecursive(currentAnalysisElement, currentAnalysisElements, computed, db, listOfParameters, listOfCommands);
						computed[currentAnalysisElementsIndex] = true;
						//this.stuffToShow += "Set Computed from computeRecursive: " + currentAnalysisElementsIndex + "<br>";
					}
				}
				currentAnalysisElementsIndex++; 
			}
		}
		
		//All previous steps (if existed) have been computed. Time to compute this ae
		//this.stuffToShow += "About to compute NO RECURSIVE: " + ae.getName() + "<br>";
		compute(ae, db, listOfParameters, listOfCommands);
	}
	*/
	
	/*
	private void executeComputeProtocol(ComputeProtocol cw, Database db, List <Integer> executedComputeProtocols, List<Integer> insertedComputeFeatures, List<String> scriptTemplates) {
		//Take the input variables
		List <Integer> currentInputParameters = cw.getInputs_Id();
		for (Integer currentInputParameter : currentInputParameters) {
			if (!insertedComputeFeatures.contains(currentInputParameter)) {
				insertedComputeFeatures.add(currentInputParameter);
			}
		}
		
		//Take the output variables
		List <Integer> currentOutputParameters = cw.getOutputs_Id();
		for (Integer currentOutputParameter : currentOutputParameters) {
			if (!insertedComputeFeatures.contains(currentOutputParameter)) {
				insertedComputeFeatures.add(currentOutputParameter);
			}
		}
		
		scriptTemplates.add(cw.getScriptTemplate());
	}
	*/
	
	/*
	private void executeWorkflowElement(WorkflowElement we, Database db, List<Integer> executedWorkflowElements, List<Integer> insertedComputeFeatures, List<String> scriptTemplates) throws DatabaseException {
		//Does this WorkflowElement has previousSteps?
		
		List <Integer> currentWEPreviousStepsIDs = we.getPreviousSteps_Id();
		//Run recursively all the previous steps
		for(Integer currentWEPreviousStepsID : currentWEPreviousStepsIDs) {
			WorkflowElement currentWEPreviousStep = db.findById(WorkflowElement.class, currentWEPreviousStepsID.intValue());
			if (!executedWorkflowElements.contains(currentWEPreviousStep.getId())) {
				//This hasn't been executed. EXECUTE IT!
				executeWorkflowElement(currentWEPreviousStep, db, executedWorkflowElements, insertedComputeFeatures, scriptTemplates);
			}
		}
		
		//All the previous steps have been executed execute this Workflow
		ComputeProtocol currentComputeProtocol = db.findById(ComputeProtocol.class, we.getProtocol_Id());
		executeComputeProtocol(currentComputeProtocol, db, executedWorkflowElements, insertedComputeFeatures, scriptTemplates);
		executedWorkflowElements.add(we.getId());
	}
	*/
	
	@Override
	public void handleRequest(Database db, Tuple request)
	{
		
		if(request.getAction().equals("run2")) {
			nameOfCurrentJob = request.getString("nameOfCurrentJob");
			this.stuffToShow += "Enter parameters for: <b>" + nameOfCurrentJob + "</b></br>";
		}
		
		if(request.getAction().equals("run")) {
			
			this.stuffToShow = "";
			
			//Get the WorkflowElement
			FormModel<WorkflowElement> parentForm = (FormModel<WorkflowElement>)this.getParent();
			WorkflowElement currentWorkflowElement = parentForm.getRecords().get(0);
			startingWorkflowElement = currentWorkflowElement;
			this.stuffToShow += "</br>You are about to run the WorkflowElement: <b>" + currentWorkflowElement.getName() + "</b> </br>";
			
			this.stuffToShow += "<form></br>";
			this.stuffToShow += "Enter a name for t his run:";
			this.stuffToShow += "<input type=\"text\" name=\"nameOfCurrentJob\"/></br>";
			this.stuffToShow += "<input type=\"submit\" value=\"submit\" onclick=\"__action.value='run2';return true;\"/></br>";
			this.stuffToShow += "</form></br>";
		}
		
		if(request.getAction().equals("runold2")) {
			
			List<Integer> executedWorkflowElements = new ArrayList<Integer>();
			List<Integer> insertedComputeFeatures = new ArrayList<Integer>();
			List<String>  scriptTemplates = new ArrayList<String>();
			
			//Get the WorkflowElement
			FormModel<WorkflowElement> parentForm = (FormModel<WorkflowElement>)this.getParent();
			WorkflowElement currentWorkflowElement = parentForm.getRecords().get(0);
			this.stuffToShow += "</br>Running: " + currentWorkflowElement.getName() + " </br>";
			
			//Execute it
//			try {
//				executeWorkflowElement(currentWorkflowElement, db, executedWorkflowElements, insertedComputeFeatures, scriptTemplates);
//			} catch (DatabaseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			this.stuffToShow += "<form> </br>";
			this.stuffToShow += "<input type=\"text\" name=\"\" /></br>";
//		<input type="text" name="InputToken" value="<#if screen.getInputToken()?exists>${screen.getInputToken()} </#if>"/			
//			for (Integer insertedComputeFeature : insertedComputeFeatures) {
//				ComputeFeature currentComputeFeature = null;
//				try {
//					currentComputeFeature = db.findById(ComputeFeature.class, insertedComputeFeature.intValue());
//				} catch (DatabaseException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				this.stuffToShow += "<input type=\"text\" name=\"" + currentComputeFeature.getName() + "\" /></br>" ;
//			}
			this.stuffToShow += "<input type=\"submit\" value=\"Submit\" /></br>";
			//<input type="submit" value="Build Index" onclick="__action.value='CreateLuceneIndex';return true;"/>
			this.stuffToShow += "</form> </br>";
		}
		
		if(request.getAction().equals("runold"))
		{
			/*
						
			//this.stuffToShow = "Monitor Execution.." + "<br>";
			
			//Get The job
			FormModel<Job> parentForm = (FormModel<Job>)this.getParent();
			Job currentJob = parentForm.getRecords().get(0);
			
			String currentJobName = currentJob.getName();
			//this.stuffToShow += "Executing workflow: " + currentJobName + "<br>";
		
			//Get the AnalysisWorkflow
			AnalysisWorkflow currentAnalysisWorkflow = null;
			try {
				currentAnalysisWorkflow = db.findById(AnalysisWorkflow.class, currentJob.getAnalysisWorkflow());
			} catch (DatabaseException e) {
				e.printStackTrace();
			}
			
			String currentAnalysisWorkflowName = currentAnalysisWorkflow.getName();
			//this.stuffToShow += "Analysis Workflow: " + currentAnalysisWorkflowName + "<br>";
			
			//Get all the AnalysisElements that are assigned with this AnalysisWorkflow
			//db.findById(AnalysisElement.class, id)
			
			List<AnalysisElement> currentAnalysisElements = new ArrayList<AnalysisElement>();
			
			//this.stuffToShow += "Analysis Elements: " + "<br>";
			List<AnalysisElement> allAnalysisElements = null;
			
			//Get All AnalysisElements
			try {
				//QueryRule qr = new QueryRule();
				allAnalysisElements = db.find(AnalysisElement.class);
			} catch(DatabaseException e) {
				e.printStackTrace();
			}
			
			//For every AnalysisElement in database..
			for (AnalysisElement allAnalysisElement : allAnalysisElements) {
				List<String> allAEAnalysisWorkflowNames = allAnalysisElement.getAnalysisWorkflow_name();
				
				boolean found = false;
				for (String allAEAnalysisWorkflowName : allAEAnalysisWorkflowNames) {
					if (allAEAnalysisWorkflowName.equals(currentAnalysisWorkflowName)) {
						currentAnalysisElements.add(allAnalysisElement);
						String currentAnalysisElementName = allAnalysisElement.getName();
						//this.stuffToShow += currentAnalysisElementName + "<br>";
						found = true;
						break;
					}
					
				}
			}
			
			//Get the Operations
			List<Operation> currentOperations = new ArrayList<Operation>();
			//this.stuffToShow += "Operations: <br>";
			for (AnalysisElement currentAnalysisElement: currentAnalysisElements) {
				Operation currentOperation = null;
				
				try {
					currentOperation = db.findById(Operation.class, currentAnalysisElement.getProtocol());
					currentOperations.add(currentOperation);
				//	this.stuffToShow += currentOperation.getName() + "<br>";
				} catch (DatabaseException e) {
					e.printStackTrace();
				}
			}
			
			
			//Pipeline resolver
			//Initially all AnalysisElements are not computed
			int sizeOfAnalysisElements = currentAnalysisElements.size();
			boolean computed[] = new boolean[sizeOfAnalysisElements];
			for (int i=0; i<sizeOfAnalysisElements; i++) computed[i] = false;

			//The list Of Pipeline's parameters
			List<String> listOfParameters = new ArrayList<String>();
			//The list of Pipeline's commands
			List<String> listOfCommands = new ArrayList<String>();
			
			boolean finish = false;
			while (!finish) {
				//Take all AnalysisElements
				//Find one that is not computed
				finish = true;
				for (int i=0; i<sizeOfAnalysisElements; i++) {
					if (!computed[i]) {
						//This one is not computed
						finish = false;
						
						AnalysisElement currentAnalysisElement = currentAnalysisElements.get(i);
						//Compute it!
						//this.stuffToShow += "About to computeRecursive: " + currentAnalysisElement.getName() + " Index: " + i + "<br>";
						computeRecursive(currentAnalysisElement, currentAnalysisElements, computed, db, listOfParameters, listOfCommands);
						computed[i] = true;
						//this.stuffToShow += "set Computed: " + i + "<br>";
					}
				}
			}
			
			this.stuffToShow += "<p>" + "Operations: " + "<br>";
			this.stuffToShow += "<ol>";
			for (String anOperation : listOfCommands) {
				this.stuffToShow += "<li>" + anOperation + "</li>";
			}
			this.stuffToShow += "</ol>";

			this.stuffToShow += "<form action=\"http://www.example.com\" method=\"post\">";
			for (String aParameter : listOfParameters) {
				this.stuffToShow += aParameter + ": <input type=\"text\" name=\"" + aParameter + "\" /> <br>";
			}
			this.stuffToShow += "<input type=\"submit\" value=\"Submit\" />";
			this.stuffToShow += "</form>";
			
//			this.stuffToShow += "<p>" + "Parameters: " + "</p>";
//			this.stuffToShow += "<ul>";
//			for (String aParameter : listOfParameters) {
//				this.stuffToShow += "<li>" + aParameter + "</li>";
//			}
//			this.stuffToShow += "</ul>";
		
			*/
		}
		
		//replace example below with yours
//		try
//		{
//		Database db = this.getDatabase();
//		String action = request.getString("__action");
//		
//		if( action.equals("do_add") )
//		{
//			Experiment e = new Experiment();
//			e.set(request);
//			db.add(e);
//		}
//		} catch(Exception e)
//		{
//			//e.g. show a message in your form
//		}
	}

	@Override
	public void reload(Database db)
	{
//		try
//		{
//			Database db = this.getDatabase();
//			Query q = db.query(Experiment.class);
//			q.like("name", "test");
//			List<Experiment> recentExperiments = q.find();
//			
//			//do something
//		}
//		catch(Exception e)
//		{
//			//...
//		}
	}
	
	@Override
	public boolean isVisible()
	{
		//you can use this to hide this plugin, e.g. based on user rights.
		//e.g.
		//if(!this.getLogin().hasEditPermission(myEntity)) return false;
		return true;
	}

	public String getStuffToShow() {
		return stuffToShow;
	}

	public void setStuffToShow(String stuffToShow) {
		this.stuffToShow = stuffToShow;
	}
	
 
}
