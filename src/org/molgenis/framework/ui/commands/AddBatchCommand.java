package org.molgenis.framework.ui.commands;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.CsvToDatabase;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Database.DatabaseAction;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.framework.ui.html.TextInput;
import org.molgenis.util.CsvStringReader;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

/**
 * The command to add in batch/upload csv
 */
public class AddBatchCommand<E extends Entity> extends SimpleCommand
{
	private static final long serialVersionUID = -4067952586340535730L;
	private static final Logger logger = Logger.getLogger(AddBatchCommand.class);

	public AddBatchCommand(String name, ScreenController<?> owner)
	{
		super(name, owner);
		this.setLabel("Add in batch/upload CSV");
		this.setIcon("generated-res/img/upload.png");
		this.setDialog(true);
		this.setMenu("File");
	}

	@Override
	public List<ActionInput> getActions()
	{
		List<ActionInput> inputs = new ArrayList<ActionInput>();

		ActionInput submit = new ActionInput("Add", ActionInput.Type.SAVE);
		submit.setValue("upload_csv");
		submit.setIcon("generated-res/img/save.png");
		submit.setDescription("Store the data");
		inputs.add(submit);

		ActionInput cancel = new ActionInput("Cancel", ActionInput.Type.CLOSE);
		cancel.setIcon("generated-res/img/cancel.png");
		cancel.setDescription("Cancel adding data");
		inputs.add(cancel);

		return inputs;
	}

	@Override
	public List<HtmlInput<?>> getInputs() throws DatabaseException
	{
		// delegate to the formscreen
		List<HtmlInput<?>> inputs = this.getFormScreen().getNewRecordForm().getInputs();

		// remove not-null constraints
		for (HtmlInput<?> i : inputs)
			i.setNillable(true);

		// add the textarea for csv
		TextInput csvInput = new TextInput("__csvdata", "put here your data in comma-separated format.");
		csvInput.setLabel("CSV data");
		csvInput.setTooltip("put here your data in comma-separated format.");
		csvInput.setDescription("Put your CSV data here.");
		inputs.add(csvInput);

		return inputs;
	}

	@Override
	public ScreenModel.Show handleRequest(Database db, Tuple request, OutputStream downloadStream) throws Exception
	{
		logger.debug(this.getName());

		// check if in dialog
		if (request.getString(FormModel.INPUT_SHOW) == null)
		{
			ScreenMessage msg = null;
			try
			{
				CsvToDatabase<? extends Entity> csvReader = this.getFormScreen().getCsvReader();

				int updatedRows = csvReader.importCsv(db, new CsvStringReader(request.getString("__csvdata")), request,
						DatabaseAction.ADD);
				// for (E entity : entities)
				// logger.debug("parsed: " + entity);
				// view.getDatabase().add(entities);
				msg = new ScreenMessage("CSV UPLOAD SUCCESS: added " + updatedRows + " rows", null, true);
				logger.debug("CSV UPLOAD SUCCESS: added " + updatedRows + " rows");
				getFormScreen().getPager().resetFilters();
				getFormScreen().getPager().last(db);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				msg = new ScreenMessage("CSV UPLOAD FAILED: " + e.getMessage(), null, false);
				logger.error("CSV UPLOAD FAILED: " + e.getMessage());
			}
			getFormScreen().getMessages().add(msg);
		}

		// show result in the main screen
		return ScreenModel.Show.SHOW_MAIN;
	}

	@Override
	public boolean isVisible()
	{
		// hide add button if the screen is readonly
		return !this.getFormScreen().isReadonly();
	}

}