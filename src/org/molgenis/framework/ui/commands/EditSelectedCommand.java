package org.molgenis.framework.ui.commands;

import java.io.PrintWriter;
import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.FormModel.Mode;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.generators.db.JpaMapperGen;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

/**
 * This command shows a dialog to edit in batch It therefor uses a custom
 * template
 */
public class EditSelectedCommand extends SimpleCommand
{
	public static final transient Logger logger = Logger.getLogger(EditSelectedCommand.class);

	public EditSelectedCommand(String name, FormModel parentScreen)
	{
		super(name, parentScreen);
		this.setLabel("Update selected");
		this.setIcon("generated-res/img/update.gif");
		this.setDialog(true);
		this.setMenu("Edit");
	}

	@Override
	public String getViewName()
	{
		return "form_massupdate";
	}

	@Override
	public boolean isVisible()
	{
		FormModel view = this.getFormScreen();
		return !view.isReadonly() && view.getMode().equals(Mode.LIST_VIEW);
	}

	@Override
	public ScreenModel.Show handleRequest(Database db, Tuple request, PrintWriter out) throws DatabaseException
	{
		logger.debug(this.getName());

		// check whether in the popup
		if (request.getString(FormModel.INPUT_SHOW) == null)
		{
			FormModel view = this.getFormScreen();
			List<Object> idList = request.getList(FormModel.INPUT_SELECTED);
			for (Object id : idList)
			{
				logger.info("mass updating id: " + id);
			}

			ScreenMessage msg = null;

			int row = 0;
			try
			{
				Query<Entity> q = db.query(view.getEntityClass()).in(view.create().getIdField(), idList);
				List<Entity> entities = q.find();

				db.beginTx();
				for (Entity e : entities)
				{
					row++;
					e.set(request, false);
					db.update(e);
				}
				db.commitTx();
				msg = new ScreenMessage("MASS UPDATE SUCCESS: updated " + entities.size() + " rows", null, true);
			}

			catch (Exception e)
			{
				try
				{
					db.rollbackTx();
				}
				catch (DatabaseException e1)
				{
					logger.error("doMassUpdate() Should never happen: " + e1);
					e1.printStackTrace();
				}
				msg = new ScreenMessage("MASS UPDATE FAILED on item '" + row + "': " + e, null, false);
			}

			view.getMessages().add(msg);
		}
		return ScreenModel.Show.SHOW_MAIN;
	}

	@Override
	public List<HtmlInput> getActions()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<HtmlInput> getInputs() throws DatabaseException
	{
		// TODO Auto-generated method stub
		return null;
	}
}