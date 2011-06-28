package org.molgenis.framework.ui.commands;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.FormController;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.commands.AddCommand;
import org.molgenis.framework.ui.html.EntityForm;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

/**
 * The command to add a new record
 */
public class AddXrefCommand<E extends Entity> extends AddCommand<E>
{
	private static final long serialVersionUID = 1512493344265778285L;
	private Entity xrefEntity;
	private EntityForm<?> xrefForm;

	public AddXrefCommand(ScreenController<?>  parent)
	{
		this("", parent, null, null);
	}

	public AddXrefCommand(String name, ScreenController<?> parent, Entity xrefEntity, EntityForm<?> xrefForm)
	{
		super(name, parent);
		this.xrefEntity = xrefEntity;
		this.xrefForm   = xrefForm;
		this.setLabel("Add " + xrefEntity.getClass().getSimpleName());
		this.setIcon("generated-res/img/new.png");
		this.setDialog(true);
		this.setMenu("Edit");
		this.setToolbar(false);
	}

	@Override
	public List<HtmlInput> getInputs() throws DatabaseException
	{
		return this.xrefForm.getInputs();
	}

	@Override
	public boolean isVisible()
	{
		return false; 
	}

	@Override
	public ScreenModel.Show handleRequest(Database db, Tuple request, PrintWriter downloadStream) throws ParseException, DatabaseException, IOException
	{
		if (request.getString(FormModel.INPUT_SHOW) == null)
		{
			ScreenMessage msg = null;
			try
			{
				db.beginTx();
				xrefEntity.set(request);
				int updatedRows = db.add(xrefEntity);
				db.commitTx();
				((FormController<E>) this.getController()).getModel().getCurrent().set(this.getName(), this.xrefEntity.getIdValue());
				msg = new ScreenMessage("ADD SUCCESS: affected " + updatedRows,	null, true);
			}
			catch (Exception e)
			{
				db.rollbackTx();
				msg = new ScreenMessage("ADD FAILED: " + e.getMessage(), null, false);
			}
			((FormController<E>) this.getController()).getModel().getMessages().add(msg);
		}
		return ScreenModel.Show.SHOW_MAIN;
	}
}