/**
 * 
 */
package org.molgenis.framework.ui.commands;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.FormController;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.util.CsvWriter;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

/**
 * This command returns all records currently selected as CSV download.
 *
 * @param <E>
 */
public class DownloadAllCommand<E extends Entity> extends SimpleCommand
{
	private static final long serialVersionUID = -2682113764135477871L;
	public static final transient Logger logger = Logger.getLogger(DownloadAllCommand.class);

	public DownloadAllCommand(String name, ScreenController<?>  parentScreen)
	{
		super(name, parentScreen);
		this.setLabel("Download all");
		this.setIcon("generated-res/img/download.png");
		this.setDownload(true);
		this.setMenu("File");
	}

	@Override
	public ScreenModel.Show handleRequest(Database db, Tuple request, PrintWriter csvDownload) throws DatabaseException
	{
		logger.debug(this.getName());

		FormModel<? extends Entity> model = this.getFormScreen();
		
		List<String> fieldsToExport = ((FormController<?>)this.getController()).getVisibleColumnNames();
		
		//TODO : remove entity name, capitals to small , and remove all _name fields
		db.find(model.getController().getEntityClass(), new CsvWriter(csvDownload), fieldsToExport, model.getRulesExclLimitOffset());

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