package org.molgenis.compute.ui;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.FormModel.Mode;
import org.molgenis.framework.ui.commands.SimpleCommand;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.framework.ui.html.StringInput;
import org.molgenis.framework.ui.html.TextInput;
import org.molgenis.ngs.Worksheet;
import org.molgenis.protocol.Workflow;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import app.ui.ComputeWorkflowsFormController;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA. User: georgebyelas Date: 28/07/2011 Time: 09:13 To
 * change this template use File | Settings | File Templates.
 */
public class WorksheetBatchCommand extends SimpleCommand
{
	String currentRequest = "";

	private ActionInput runButton;
	private ActionInput runButton2;

	private boolean isFirstClick = true;
	private Vector<Integer> numbers = null;

	private NGSProcessing processing = new NGSProcessing();

	public WorksheetBatchCommand(ScreenController<?> parentController)
	{
		super("runbatch", parentController);
		this.setLabel("Run analysis on selected");
		this.setMenu("Edit");
		this.setDialog(true);
		this.setToolbar(true);
		this.setIcon("generated-res/img/run.png");

		// create a button for each workflow
		// True goal:
		// user selects pipeline,
		// show only data that can enter this pipeline
		runButton = new ActionInput("demultiplex", ActionInput.Type.CUSTOM);
		runButton.setButtonValue("Demultiplex");
		runButton.setLabel("Demultiplex the files that contain the selected samples!");

		// String workflowname =

		runButton2 = new ActionInput("findVariants", ActionInput.Type.CUSTOM);
		runButton2.setButtonValue("Find variants");
		runButton2.setLabel("Find variants in selected samples!");
	}

	@Override
	public List<HtmlInput<?>> getInputs() throws DatabaseException
	{

		List<HtmlInput<?>> inputs = new ArrayList<HtmlInput<?>>();

		TextInput request = new TextInput("request");
		request.setValue(currentRequest);
		request.setDescription("");
		inputs.add(request);

		return inputs;
	}

	@Override
	public List<ActionInput> getActions()
	{
		List<ActionInput> inputs = new ArrayList<ActionInput>();
		inputs.add(runButton);
		inputs.add(runButton2);
		return inputs;
	}

	@Override
	public boolean isVisible()
	{
		FormModel<? extends Entity> view = this.getFormScreen();
		return view.getMode().equals(Mode.LIST_VIEW);
	}

	public List<Worksheet> getWorksheetList(Database db, List<Integer> ids)
	{
		return null;
	}

	public ScreenModel.Show handleRequest(Database db, Tuple request, OutputStream downloadStream) throws Exception
	{
		System.out.println(">> In handleRequest!");
		logger.debug("worksheet batch command button clicked: " + request.toString());

		String action = request.getString("__action");
		String command = request.getString("__command");

		String lines = request.getString("massUpdate");
		System.out.println(">> Selected lines: " + lines);

		System.out.println(">> Action: " + action);
		System.out.println(">> request==" + request.toString());

		if (isFirstClick)
		{
			System.out.println("first click");
			numbers = new Vector<Integer>();
			if (lines != null)
			{
				// there is >= 1 line selected

				if (lines.indexOf(',') == -1)
				{
					// only one line was selected
					numbers.addElement(Integer.parseInt(lines.trim()));

				}
				else
				{
					// multiple lines were selected

					// strip off the starting '[' and ending ']'
					lines = lines.substring(1, lines.length() - 1);
					String tmp = null;

					// iteratively get all ids
					int i = lines.indexOf(',');
					while (-1 < i)
					{
						tmp = lines.substring(0, i);
						numbers.addElement(Integer.parseInt(tmp.trim()));
						lines = lines.substring(i + 2);
						i = lines.indexOf(',');
					}

					// get last id
					numbers.addElement(Integer.parseInt(lines.trim()));

				}
			}
			isFirstClick = false;
			return ScreenModel.Show.SHOW_DIALOG;
		}
		else
		{
			System.out.println("second click");
			print("NUmbers are: " + numbers);
			Iterator it = numbers.iterator();
			while (it.hasNext())
			{
				Integer worksheetID = (Integer) it.next();
				try
				{
					Worksheet w = db.findById(Worksheet.class, worksheetID);
					print("You chose to do: " + request.getAction());
					print("Starting analysis for worksheetID " + worksheetID);
					// start the chosen pipeline
					Boolean pipelineFound = false;
					List<Workflow> workflowlist = db.query(Workflow.class).find();
					for (Workflow wf : workflowlist)
					{
						if (wf.getName().equalsIgnoreCase(request.getAction()))
						{
							print("Starting workflow: " + wf.getName());
							processing.processSingleWorksheet(db, request, w, wf);
							pipelineFound = true;
						}
					}
					if (!pipelineFound) {
						print("Pipeline not found!");
					}
				}
				catch (DatabaseException e)
				{
					e.printStackTrace();
				}
			}

			isFirstClick = true;
			return ScreenModel.Show.SHOW_CLOSE; // ScreenModel.Show.SHOW_DIALOG;

		}
	}

	private void print(String string)
	{
		System.out.println(">> " + string);
	}
}
