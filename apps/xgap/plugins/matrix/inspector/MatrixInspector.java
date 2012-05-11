/* Date:        February 2, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.matrix.inspector;

import matrix.general.DataMatrixHandler;

import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.FormController;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.util.Tuple;

import plugins.matrix.manager.Browser;
import plugins.matrix.manager.CreateBrowserInstance;

public class MatrixInspector extends PluginModel
{

	private MatrixInspectorModel model = new MatrixInspectorModel();
	private DataMatrixHandler dmh = null;

	public MatrixInspectorModel getMyModel()
	{
		return model;
	}

	public MatrixInspector(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

// moved overlib to molgenis core
//	@Override
//	public String getCustomHtmlHeaders()
//	{
//		return "<script src=\"res/scripts/overlib.js\" language=\"javascript\"></script>";
//
//	}

	@Override
	public String getViewName()
	{
		return "MatrixInspector";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/matrix/inspector/MatrixInspector.ftl";
	}

	public void handleRequest(Database db, Tuple request)
	{
	
	}

	@Override
	public void reload(Database db)
	{

		if(dmh == null){
			dmh = new DataMatrixHandler(db);
		}
		
		ScreenController<?> parentController = (ScreenController<?>) this.getParent().getParent();
		FormModel<Data> parentForm = (FormModel<Data>) ((FormController)parentController).getModel();
		Data data = parentForm.getRecords().get(0);

		try
		{

			//ASSUMING newOrOtherData");

			this.model.setSelectedData(data);
			this.model.setHasBackend(dmh.isDataStoredIn(data, data.getStorage(), db));
			
			logger.info("hasBackend: " + this.model.isHasBackend());
			
			if (this.model.isHasBackend())
			{
				logger.info("*** creating browser instance");
				Browser br = new CreateBrowserInstance(db, data, this.getApplicationController()).getBrowser();
				this.model.setWarningsAndErrors(new WarningsAndErrors(data, db, br.getModel().getInstance()));

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
		}

	}

}
