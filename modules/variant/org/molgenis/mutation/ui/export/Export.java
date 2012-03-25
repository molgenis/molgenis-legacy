
package org.molgenis.mutation.ui.export;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.mutation.service.ExportService;

/**
 * ExportController takes care of all user requests and application logic.
 *
 * <li>Each user request is handled by its own method based action=methodName. 
 * <li> MOLGENIS takes care of db.commits and catches exceptions to show to the user
 * <li>ExportModel holds application state and business logic on top of domain model. Get it via this.getModel()/setModel(..)
 * <li>ExportView holds the template to show the layout. Get/set it via this.getView()/setView(..).
 */
public class Export extends EasyPluginController<ExportModel>
{
	private static final long serialVersionUID = 1L;

	public Export(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(new ExportModel(this));
		this.setView(new FreemarkerView("ExportView.ftl", getModel()));
	}

	@Override
	public void reload(Database db) throws Exception
	{
		ExportService exportService = new ExportService();
		exportService.setDatabase(db);
		this.getModel().setCsv(exportService.exportCsv(getModel().getSubmissionDate()));
	}
}