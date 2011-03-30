/* Date:        February 24, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.protocol;

import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.GenericPlugin;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class ApplyProtocolPlugin extends GenericPlugin
{
    private static final long serialVersionUID = -5500131586262572567L;
    private ProtocolPluginService service;
    private ApplyProtocolPluginModel model;
    private ApplyProtocolHandler handler;
    private ApplyProtocolUI ui;

    public ApplyProtocolPlugin(String name, ScreenModel<Entity> parent)
    {
		super(name, parent);
	
		service = new ProtocolPluginService();
		model = new ApplyProtocolPluginModel();
		ui = new ApplyProtocolUI(model, service);
		handler = new ApplyProtocolHandler(model, ui, service);
    }

    @Override
    public void handleRequest(Database db, Tuple request)
    {
    	ScreenMessage message = null;
	    String action = request.getString("__action");

	    if( action.equals("Select") )
	    {
	    	message = handler.handleSelect(request);
	    }
	    if( action.equals("Clear") )
	    {
	    	ui.initScreen();
	    }
	    if( action.equals("Apply") )
	    {
	    	message = handler.handleApply(request);
	    }
	    if( action.equals("ApplyAllDefaults") )
	    {
	    	message = handler.handleApplyAllDefaults(request);
	    }
	    if( action.contains("ApplyDefault_"))
	    {
	    	message = handler.handleApplyDefaults(request, Integer.parseInt(action.substring(13)));
	    }
	    if( action.contains("ApplyStartTime_"))
	    {
	    	message = handler.handleApplyStartTime(request, Integer.parseInt(action.substring(15)));
	    }
	    if( action.contains("ApplyEndTime_"))
	    {
	    	message = handler.handleApplyEndTime(request, Integer.parseInt(action.substring(13)));
	    }

	    if (message != null) {
	    	this.setMessages(message);
	    }
    }

  
    @Override
    public void reload(Database db)
    {
		service.setDatabase(db);
	
		try {
		    List<Integer> allTargetIdList = service.getAllObservationTargetIds();			
		    model.setTargetMap(service.getObservationTargetNames(allTargetIdList));
		} catch (Exception e) {
		    // Something went wrong, targetMap will remain null but getTargetName() can handle this
		}
	
		// Only first time:
		if (ui.getProtocolApplicationContainer() == null) {
		    ui.initScreen();
		}
    }

    @Override
    public boolean isVisible()
    {
		if (this.getLogin().isAuthenticated()){
		    return true;
		} else {
		    return false;
		}
    }

    public String render()
    {
    	return ui.getProtocolApplicationContainer().toHtml();
    }

}
