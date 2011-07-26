package org.molgenis.framework.ui.commands;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.ui.FormController;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.ScreenModel.Show;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.model.MolgenisModelException;
import org.molgenis.util.CsvWriter;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;
import org.molgenis.util.XlsWriter;

public class DownloadXLS <E extends Entity> extends SimpleCommand
{
	private static final long serialVersionUID = -2682113764135477871L;
	public static final transient Logger logger = Logger.getLogger(DownloadAllCommand.class);

	//public DownloadXLS(String name, FormController<?> parentScreen)
	public DownloadXLS(String name, FormController<E> parentScreen)
	{
		super(name, parentScreen);
		this.setLabel("Download .xls");
		this.setIcon("generated-res/img/download.png");
		this.setDownload(true);
		this.setMenu("File");
	}

	
	//BIG TODO : actually implement XLS download NOT csv, or include Joeri's function?
	@Override
//	public Show handleRequest(Database db, Tuple request,PrintWriter downloadStream) throws ParseException, DatabaseException, IOException
	public ScreenModel.Show handleRequest(Database db, Tuple request, PrintWriter csvDownload) throws DatabaseException
	{
		logger.debug(this.getName());

		FormModel<? extends Entity> model = this.getFormScreen();
		FormController<?> controller = ((FormController<?>)this.getController());
		
		List<String> fieldsToExport = controller.getVisibleColumnNames();
		
		QueryRule[] rules;
		try
		{
			rules = controller.rewriteAllRules(db, Arrays.asList(model.getRulesExclLimitOffset()));
		}
		catch (MolgenisModelException e)
		{
			e.printStackTrace();
			throw new DatabaseException(e);
		}
		
		//TODO : the actual xls headers/formatting 
		//TODO : this needs different call or TODO just an extra if in abstractMolgenisServlet for the different suffix (.xls) ?
		//db.find(model.getController().getEntityClass(), new CsvWriter(csvDownload), fieldsToExport, rules);
		db.find(model.getController().getEntityClass(), new XlsWriter(csvDownload), fieldsToExport, rules);


		return ScreenModel.Show.SHOW_MAIN;
	}

	public List<ActionInput> getActions()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<HtmlInput<?>> getInputs() throws DatabaseException
	{
		// TODO Auto-generated method stub
		return null;
	}


}
