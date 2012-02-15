package plugins.JavascriptTests;


import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
//import org.molgenis.framework.ui.html.JavascriptTests; DOES NOT EXIST!!!
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class JavascriptTestsPlugin extends PluginModel<Entity>{
	
	//A Test class for  javascript. 
	//JavascriptTests JsTest = new JavascriptTests("test");

	
	public JavascriptTestsPlugin(String name, ScreenController<?> parent) {
		super(name, parent);


		
		
	}
	
	@Override 
	public String getViewName() {
		return "plugins_JavascriptTests_JavascriptTestsPlugin";
	
	}
	
	@Override
	public String getViewTemplate()
	{
		return "plugins/JavascriptTests/JavascriptTestsPlugin.ftl";
		
	}
	
	@Override
	public void handleRequest(Database db, Tuple request){
		
	
	}
	
	@Override
	public void reload(Database db) {
	
	}


	@Override
	public boolean isVisible()
	{
		if (!this.getLogin().isAuthenticated()) {
			return false;
		}
		return true;
	}

	public String getJavascriptTest() {
		System.out.println("test from JavascripttestsPlugin");
		return "org.molgenis.framework.ui.html.JavascriptTests does not exist!";
		//return JsTest.toHtml();
	}
	
}
