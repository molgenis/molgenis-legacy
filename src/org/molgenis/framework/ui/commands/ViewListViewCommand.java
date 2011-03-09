/**
 * 
 */
package org.molgenis.framework.ui.commands;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.SimpleModel;
import org.molgenis.framework.ui.FormModel.Mode;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class ViewListViewCommand<E extends Entity> extends SimpleCommand<E>
{
	private static final long serialVersionUID = -5921987163907171740L;
	public static final transient Logger logger = Logger.getLogger(ViewListViewCommand.class);

	public ViewListViewCommand(String name, SimpleModel<E> parentScreen)
	{
		super(name, parentScreen);
		this.setLabel("View List of Records");
		this.setIcon("generated-res/img/listview.png");
		this.setMenu("View");
		this.setToolbar(true);
	}

	@Override
	public ScreenModel.Show handleRequest(Database db, Tuple request, PrintWriter downloadStream) throws DatabaseException, ParseException,
			IOException
	{
		logger.debug(this.getName());

		getFormScreen().setMode(Mode.LIST_VIEW);
		Integer offset = request.getInt(FormModel.INPUT_OFFSET);
		if (offset != null)
		{
			getFormScreen().getPager().setOffset(offset - 1);
		}
		else
		{
			getFormScreen().getPager().setOffset(getFormScreen().getOffset());
		}
		return ScreenModel.Show.SHOW_MAIN;
	}

	@Override
	public boolean isVisible()
	{
		// hide when already in list view
		return !getFormScreen().getMode().equals(Mode.LIST_VIEW);
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