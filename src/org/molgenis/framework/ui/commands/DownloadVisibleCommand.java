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
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.util.CsvWriter;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

/**
 * This command downloads the records currently shown as csv.
 * @author Morris Swertz
 *
 * @param <E>
 */
public class DownloadVisibleCommand<E extends Entity> extends SimpleCommand<E>
{
	private static final long serialVersionUID = -6279819301321361448L;
	public static final transient Logger logger = Logger.getLogger(DownloadVisibleCommand.class);

	public DownloadVisibleCommand(String name, SimpleModel<E> parentScreen)
	{
		super(name, parentScreen);
		this.setDownload(true);
		this.setLabel("Download visible");
		this.setIcon("generated-res/img/download.png");
		this.setMenu("File");
	}

	@Override
	public ScreenModel.Show handleRequest(Database db, Tuple request, PrintWriter csvDownload) throws ParseException, DatabaseException,
			IOException
	{
		logger.debug(this.getName());

		FormModel<E> view = this.getFormScreen();
		List<E> records = view.getRecords();
		CsvWriter writer = new CsvWriter(csvDownload, view.create().getFields());
		writer.writeHeader();
		for (Entity e : records)
			writer.writeRow(e);
		
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