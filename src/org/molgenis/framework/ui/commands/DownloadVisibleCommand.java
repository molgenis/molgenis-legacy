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
import org.molgenis.framework.ui.FormController;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.util.CsvWriter;
import org.molgenis.util.SpreadsheetWriter;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

/**
 * This command downloads the records currently shown as csv.
 * @author Morris Swertz
 *
 * @param <E>
 */
public class DownloadVisibleCommand extends SimpleCommand
{
	private static final long serialVersionUID = -6279819301321361448L;
	public static final transient Logger logger = Logger.getLogger(DownloadVisibleCommand.class);

	public DownloadVisibleCommand(String name, ScreenController<?>  parentScreen)
	{
		super(name, parentScreen);
		this.setDownload(true);
		this.setLabel("Download visible (.csv)");
		this.setIcon("generated-res/img/download.png");
		this.setMenu("File");
	}

	@Override
	public ScreenModel.Show handleRequest(Database db, Tuple request, PrintWriter csvDownload) throws ParseException, DatabaseException,
			IOException
	{
		logger.debug(this.getName());

		FormModel<?> view = this.getFormScreen();
		List<? extends Entity> records = view.getRecords();
		
		List<String> fieldsToExport = ((FormController<?>)this.getController()).getVisibleColumnNames();
		
		SpreadsheetWriter writer = new CsvWriter(csvDownload, fieldsToExport);
		writer.writeHeader();
		
		for (Entity e : records)
		{
			for(String field: fieldsToExport)
				writer.writeValue(e.get(field));
			writer.writeEndOfLine();
		}
		
		return ScreenModel.Show.SHOW_MAIN;		
	}

	@Override
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