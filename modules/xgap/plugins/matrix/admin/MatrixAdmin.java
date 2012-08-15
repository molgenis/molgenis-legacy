/* Date:        February 2, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.matrix.admin;

import matrix.general.DataMatrixHandler;

import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.FormController;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.util.Tuple;

public class MatrixAdmin extends PluginModel
{

	private MatrixAdminModel model = new MatrixAdminModel();
	private DataMatrixHandler dmh = null;

	public MatrixAdminModel getMyModel()
	{
		return model;
	}

	public MatrixAdmin(String name, ScreenController<?> parent)
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
		return "MatrixAdmin";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/matrix/admin/MatrixAdmin.ftl";
	}

	public void handleRequest(Database db, Tuple request)
	{
		if (request.getString("__action") != null)
		{

			try
			{

				if (request.getString("__action").equals("deleteBackend"))
				{
					dmh.deleteDataMatrixSource(this.model.getSelectedData(), db);
					this.model.setSelectedData(null);
				}

			}
			catch (Exception e)
			{
				e.printStackTrace();
				this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
			}
		}
	}

	@Override
	public void reload(Database db)
	{

		ScreenController<?> parentController = (ScreenController<?>) this.getParent().getParent();
		FormModel<Data> parentForm = (FormModel<Data>) ((FormController)parentController).getModel();
		Data data = parentForm.getRecords().get(0);
		
		if(dmh == null){
			dmh = new DataMatrixHandler(db);
		}

		try
		{
			//ASSUMING newOrOtherData");

			this.model.setSelectedData(data);
			this.model.setHasBackend(dmh.isDataStoredIn(data, data.getStorage(), db));
			
			logger.info("hasBackend: " + this.model.isHasBackend());

		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
		}

	}

}
