<#include "GeneratorHelper.ftl">
<#--#####################################################################-->
<#--                                                                   ##-->
<#--         START OF THE OUTPUT                                       ##-->
<#--                                                                   ##-->
<#--#####################################################################-->

package ${package};

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.util.Tuple;

public class ${clazzName} extends EasyPluginController<${clazzName}>
{
	public ${clazzName}(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(this); //you can create a seperate class as 'model'.
	}
	
	//what is shown to the user
	public ScreenView getView()
	{
		//uncomment next line if you want to use template
		//return new FreemarkerView("${clazzName}View.ftl", getModel()); 
		
		MolgenisForm view = new MolgenisForm();
		
		view.add(new StringInput("helloName"));
		view.add(new ActionInput("sayHello"));
		
		return view;
	}
	
	private String helloName = "UNKNOWN";
	
	//matches ActionInput("sayHello")
	public void sayHello(Database db, Tuple request)
	{
		if(!request.isNull("helloName"))
		{
			this.helloName = request.getString("helloName");
		}
	}
	
	@Override
	public void reload(Database db) throws Exception
	{	
//		//example: update model with data from the database
//		Query q = db.query(Person.class);
//		q.like("name", "john");
//		getModel().investigations = q.find();
	}
}