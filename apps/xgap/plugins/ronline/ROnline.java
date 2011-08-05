/* Date:        February 2, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.ronline;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class ROnline<E extends Entity> extends PluginModel<E> {
	private static final long serialVersionUID = -4016852670578378111L;
	private ROnlineModel model = new ROnlineModel(this);
	long timeOut = 60;

	public ROnlineModel getModel() {
		return model;
	}

	public ROnline(String name, ScreenModel parent) {
		super(name, parent.getController());
	}

	@Override
	public String getCustomHtmlHeaders() {
		return "<script type=\"text/javascript\"><!--window.onload = document.onload(){id = document.getElementById('inputBox');id.focus() = TRUE;document.onkeydown(){if(window.event.keyCode==13){document.forms.${screen.name}.__action.value = 'execute';}}// --> </script>";
	}
	
	@Override
	public String getCustomHtmlBodyOnLoad(){
		return "document.getElementById('inputBox').focus();";
	}

	@Override
	public String getViewName() {
		return "ROnline";
	}

	@Override
	public String getViewTemplate() {
		return "plugins/ronline/ROnline.ftl";
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	public void handleRequest(Database db, Tuple request) {
			if (request.getString("__action") != null) {

			System.out.println("*** handleRequest __action: "
					+ request.getString("__action"));

			try {

				if (request.getString("__action").equals("execute")) {
					
					String executeThis = request.getString("executeThis");
					List<String> allRes = new ArrayList<String>();
					allRes.addAll(this.model.getResults());
					allRes.add("> " + executeThis);
					if(!this.model.getRp().isRunning()){
						newRProcess(timeOut);
					}
					List<String> newRes = this.model.getRp().execute(executeThis);
					allRes.addAll(newRes);
					this.model.setResults(allRes);
					
				}else if(request.getString("__action").equals("executeMulti")){
					
				}

				this.setMessages();
			} catch (Exception e) {
				e.printStackTrace();
				this.setMessages(new ScreenMessage(e.getMessage() != null ? e
						.getMessage() : "null", false));
			}
		}
	}

	public void clearMessage() {
		this.setMessages();
	}
	
	private void newRProcess(long timeOut) throws Exception{
		RProcess rp =  new RProcess(timeOut);
		new Thread(rp).start();
		this.model.setRp(rp);
		this.model.setResults(this.model.getRp().getStartupMessage());
	}

	@Override
	public void reload(Database db) {

		
		
		try {

			if(this.model.getRp() == null || !this.model.getRp().isRunning()){
				newRProcess(timeOut);
			}
			

		} catch (Exception e) {
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage() != null ? e
					.getMessage() : "null", false));
		}

	}

}
