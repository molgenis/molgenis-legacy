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
    private String action = "init";
    private BatchContainer batchContainer = null;
    private BatchService service;
    //TODO: Danny: If unused, please remove
    //private static transient Logger logger = Logger.getLogger(BatchViewPlugin.class);
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
	    this.action = request.getString("__action");

	    if ( action.equals("Select") )
	    {
	    	//this.handleSelectRequest(request);
	    }
	    else if (action.equals("Add"))
	    {
	    	//this.handleAddRequest(request);
	    }
	    else if (action.equals("Remove"))
	    {
	    	//this.handleRemoveRequest(request);
	    }
	    else if (action.equals("Clear"))
	    {
	    	this.action = "init";
	    }
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	}
    }

    @Override
    public void reload(Database db)
    {
		service.setDatabase(db);
	
		if ("init".equals(this.action)) {
		    try {
				if (batchContainer == null) {
				    batchContainer = new BatchContainer(service, this.getLogin().getUserId());
				} else { //how can we do this better? Otherwise we are still calling the db everytime
				    batchContainer.updateBatches();
				}
				
				ui.updateBatchView(batchContainer, service);
		    } catch (Exception e) {
		    	e.printStackTrace();
		    }
		}
    }

    /**
     * Render the html
     */
    public String render()
    {
    	return ui.getContainer().toHtml();
    }
}
