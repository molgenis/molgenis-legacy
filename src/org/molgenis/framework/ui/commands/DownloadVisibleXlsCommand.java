/**
 * 
 */
package org.molgenis.framework.ui.commands;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.jdbc.AbstractJDBCMapper;
import org.molgenis.framework.ui.FormController;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.util.CsvWriter;
import org.molgenis.util.Entity;
import org.molgenis.util.TupleWriter;
import org.molgenis.util.Tuple;
import org.molgenis.util.XlsWriter;

/**
 * This command downloads the records currently shown as csv.
 * @author Morris Swertz
 *
 * @param <E>
 */
public class DownloadVisibleXlsCommand extends SimpleCommand
{
	private static final long serialVersionUID = -6279819301321361448L;
	public static final transient Logger logger = Logger.getLogger(DownloadVisibleXlsCommand.class);

	public DownloadVisibleXlsCommand(String name, ScreenController<?>  parentScreen)
	{
		super(name, parentScreen);
		this.setDownload(true);
		this.setLabel("Download visible (.xls)");
		this.setIcon("generated-res/img/download.png");
		this.setMenu("File");
	}

	@Override
	public ScreenModel.Show handleRequest(Database db, Tuple request, OutputStream xlsDownload) throws ParseException, DatabaseException,
			Exception
	{
		FormModel<?> view = this.getFormScreen();
		List<String> fieldsToExport = ((FormController<?>)this.getController()).getVisibleColumnNames();
		AbstractJDBCMapper.find(view.getRecords(), new XlsWriter(xlsDownload), fieldsToExport);
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