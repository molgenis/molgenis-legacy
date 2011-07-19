/* Date:        March 21, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.batch;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.GenericPlugin;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class BatchViewPlugin extends GenericPlugin
{
    private static final long serialVersionUID = -3093693807976546141L;
    //private String action = "init";
    private BatchContainer batchContainer = null;
    private BatchService service;
    private BatchViewUi ui = new BatchViewUi();

    public BatchViewPlugin(String name, ScreenController<?> parent)
    {
		super(name, parent);
		service = new BatchService();
    }

    public void handleRequest(Database db, Tuple request)
    {
		try
		{
		    //this.action = request.getString("__action");
	
//		    if (action.equals("Refresh"))
//		    {
//		    	this.action = "init";
//		    }
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		}
    }

    @Override
    public void reload(Database db)
    {
		service.setDatabase(db, this.getLogin().getUserId());
	
		batchContainer = new BatchContainer(service, this.getLogin().getUserId());
		ui.updateBatchView(batchContainer, service);
    }

    /**
     * Render the html
     */
    public String render()
    {
    	return ui.getContainer().toHtml();
    }
}
