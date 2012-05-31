package plugins;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.EntityDataTable;
import org.molgenis.pheno.Measurement;

/**
 * DataTableTestController takes care of all user requests and application
 * logic.
 * 
 * <li>Each user request is handled by its own method based action=methodName.
 * <li>MOLGENIS takes care of db.commits and catches exceptions to show to the
 * user <li>DataTableTestModel holds application state and business logic on top
 * of domain model. Get it via this.getModel()/setModel(..) <li>
 * DataTableTestView holds the template to show the layout. Get/set it via
 * this.getView()/setView(..).
 */
public class DataTableTest extends EasyPluginController<DataTableTestModel>
{
	public DataTableTest(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.setModel(new DataTableTestModel(this)); // the default model
	}

	public ScreenView getView()
	{
		return new FreemarkerView("DataTableTestView.ftl", getModel());
	}
	
	EntityDataTable table;

	/**
	 * At each page view: reload data from database into model and/or change.
	 * 
	 * Exceptions will be caught, logged and shown to the user automatically via
	 * setMessages(). All db actions are within one transaction.
	 */
	@Override
	public void reload(Database db) throws Exception
	{
		table = new EntityDataTable("testtable", Measurement.class, db);
	}

	@Override
	public String render()
	{
		return table.render();
	}
}