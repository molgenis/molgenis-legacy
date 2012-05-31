package org.molgenis.ngs.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

import org.molgenis.compute.ComputeParameter;
import org.molgenis.compute.ComputeProtocol;
import org.molgenis.compute.ComputeWorkflow;
import org.molgenis.compute.ComputeWorkflowStep;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Database.DatabaseAction;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.DivPanel;
import org.molgenis.framework.ui.html.EntityTable;
import org.molgenis.framework.ui.html.FileInput;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.framework.ui.html.Paragraph;
import org.molgenis.framework.ui.html.StringInput;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.Tuple;

public class ImportCompute extends EasyPluginController<ImportCompute>
{
	private static final long serialVersionUID = -7403318653011743829L;

	enum State
	{
		UPLOAD, REVIEW
	};

	private State state = State.UPLOAD;

	List<ComputeParameter> parameters = new ArrayList<ComputeParameter>();
	List<ComputeProtocol> protocols = new ArrayList<ComputeProtocol>();
	List<ComputeWorkflow> workflows = new ArrayList<ComputeWorkflow>();
	List<ComputeWorkflowStep> steps = new ArrayList<ComputeWorkflowStep>();

	public ImportCompute(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.setModel(this); // the default model is itself
	}

	@Override
	public void reload(Database db) throws Exception
	{

	}

	public void uploadWorkflow(Database db, Tuple request) throws IOException, DataFormatException, Exception
	{
		workflows = new ArrayList<ComputeWorkflow>();
		parameters = new ArrayList<ComputeParameter>();
		steps = new ArrayList<ComputeWorkflowStep>();
		protocols = new ArrayList<ComputeProtocol>();

		// parse parameters
		String parametersFile = request.getString("filefor_parametersFile");

		if (parametersFile != null) for (Tuple t : new CsvFileReader(new File(parametersFile)))
		{
			ComputeParameter p = new ComputeParameter();
			p.set(t, false);
			parameters.add(p);
		}

		// create workflow
		String workflowName = request.getString("workflowName");
		if (workflowName != null)
		{
			ComputeWorkflow wf = new ComputeWorkflow();
			wf.setIdentifier(workflowName);
			wf.setDescription("not provided");
			workflows.add(wf);
		}

		// parse workflow elements
		String workflowFile = request.getString("filefor_workflowFile");

		if (workflowFile != null)
		{
			if (workflowName == null) throw new Exception(
					"when uploading a workflowFile you need to provide workflowName");
			for (Tuple t : new CsvFileReader(new File(workflowFile)))
			{
				ComputeWorkflowStep p = new ComputeWorkflowStep();
				p.set(t, false);
				p.setComputeWorkflow_Identifier(workflowName);
				steps.add(p);
			}
		}

		// load protocols
		String protocolsDir = request.getString("pathToProtocols");

		if (protocolsDir != null)
		{
			File dir = new File(protocolsDir);
			for (String f : dir.list())
			{
				if (f.endsWith(".ftl"))
				{
					ComputeProtocol p = new ComputeProtocol();
					p.setIdentifier(f.replace(".ftl", ""));

					File file = new File(dir.getAbsolutePath() + "/" + f);
					p.setScriptTemplate(readFileAsString(file));
					p.setTargetType("BIG TODO");

					protocols.add(p);
				}
			}
		}

		state = State.REVIEW;
	}

	public void saveUpload(Database db, Tuple request) throws DatabaseException
	{
		int count = 0;

		// because of self relation we need to load in two batches
		Map<String, List> hasOnes = new LinkedHashMap<String, List>();
		for (ComputeParameter p : parameters)
		{
			hasOnes.put(p.getIdentifier(), p.getHasOne_Identifier());
			p.setHasOne_Identifier(new ArrayList());
		}
		db.update(new ArrayList<ComputeParameter>(parameters), DatabaseAction.ADD_UPDATE_EXISTING, "identifier");
		for (ComputeParameter p : parameters)
		{
			p.setHasOne_Identifier(hasOnes.get(p.getIdentifier()));
		}
		count += db.update(new ArrayList<ComputeParameter>(parameters), DatabaseAction.ADD_UPDATE_EXISTING,
				"identifier");

		count += db.update(new ArrayList<ComputeProtocol>(protocols), DatabaseAction.ADD_UPDATE_EXISTING, "identifier");

		count += db.update(new ArrayList<ComputeWorkflow>(workflows), DatabaseAction.ADD_UPDATE_EXISTING, "identifier");
		
		// because of self relation we need to load in two batches
		Map<String, List> prevSteps = new LinkedHashMap<String, List>();
		for (ComputeWorkflowStep p : steps)
		{
			prevSteps.put(p.getIdentifier(), p.getPrevSteps_Identifier());
			p.setPrevSteps_Identifier(new ArrayList());
		}
		db.update(new ArrayList<ComputeWorkflowStep>(steps), DatabaseAction.ADD_UPDATE_EXISTING, "computeWorkflow_identifier","identifier");
		for (ComputeWorkflowStep p : steps)
		{
			p.setPrevSteps_Identifier(prevSteps.get(p.getIdentifier()));
		}
		count += db.update(new ArrayList<ComputeWorkflowStep>(steps), DatabaseAction.ADD_UPDATE_EXISTING, "computeWorkflow_identifier","identifier");

		state = State.UPLOAD;

		this.setSuccess("Added or updated " + count + " records succesfully");
	}

	public void resetUpload(Database db, Tuple request)
	{
		state = State.UPLOAD;
	}

	@Override
	public ScreenView getView()
	{
		MolgenisForm f = new MolgenisForm(this);

		if (state == State.UPLOAD)
		{
			DivPanel p1 = new DivPanel();
			p1.setLabel("<h3>Workflow:</h3>");
			p1.add(new StringInput("workflowName"));
			p1.add(new FileInput("workflowFile"));

			DivPanel p2 = new DivPanel();
			p2.setLabel("<h3>Protocols:</h3>");
			p2.add(new FileInput("parametersFile"));
			p2.add(new StringInput("pathToProtocols", "pathToProtocols",
					"/Users/mswertz/Documents/OmicsConnectWorkspace/molgenis_apps/apps/ngs/protocols", true, false));
			
			f.add(p1);
			f.add(p2);
			
			f.add(new ActionInput("uploadWorkflow"));
		}

		if (state == State.REVIEW)
		{
			f.add(new Paragraph("<h2>Please review uploaded data:</h2>"));
			f.add(new ActionInput("resetUpload", "reset"));
			f.add(new ActionInput("saveUpload", "save"));

			if (workflows.size() > 0)
			{
				EntityTable s = new EntityTable("workflows", workflows);
				s.setLabel("<h3>Workflows:</h3>");
				f.add(s);
			}

			if (steps.size() > 0)
			{
				EntityTable p = new EntityTable("workflowELements", steps, false, "identifier",
						"computeWorkflow_identifier", "computeProtocol_identifier", "prevSteps_identifier");
				p.setLabel("<h3>Workflow steps:</h3>");
				f.add(p);
			}

			if (parameters.size() > 0)
			{
				EntityTable s = new EntityTable("parameters", parameters, false, "identifier", "dataType",
						"defaultValue", "description", "hasOne_identifier");
				s.setLabel("<h3>Parameters:</h3>");
				f.add(s);
			}

			if (protocols.size() > 0)
			{
				EntityTable s = new EntityTable("protocols", protocols, false, "identifier", "scriptInterpreter",
						"walltime", "mem", "cores", "nodes", "scriptTemplate");
				s.setLabel("<h3>Protocols:</h3>");
				f.add(s);
			}

		}

		return f;
	}

	private static String readFileAsString(File filePath) throws java.io.IOException
	{
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1)
		{
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}

}