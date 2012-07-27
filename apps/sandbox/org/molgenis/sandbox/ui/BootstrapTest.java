package org.molgenis.sandbox.ui;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.ui.Button;
import org.molgenis.ui.Form;
import org.molgenis.ui.Form.FormType;
import org.molgenis.ui.Icon;
import org.molgenis.ui.Label;
import org.molgenis.ui.SelectInput;
import org.molgenis.ui.StringInput;
import org.molgenis.ui.theme.bootstrap.BootstrapTheme;
import org.molgenis.util.Tuple;
import org.molgenis.util.ValueLabel;

public class BootstrapTest extends EasyPluginController<BootstrapTest>
{
	public BootstrapTest(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.setModel(this); //you can create a seperate class as 'model'.
	}
	
	//what is shown to the user
	public ScreenView getView()
	{
		//default form is vertical
		Form f = new Form();
		f.add(new Label("Your name:"));
		f.add(new StringInput("name"));
		f.add(new Label("Click to say hello world..."));
		f.add(new Button("helloWorld").setLabel("Say hello").setIcon(Icon.SEARCH));
		
		Form f2 = new Form(FormType.INLINE);
		f2.add(new Label("Your name:"));
		f2.add(new StringInput("name"));
		f2.add(new Label("Click to say hello world..."));
		f2.add(new Button("helloWorld").setLabel("Say hello").setIcon(Icon.SEARCH));
		
		Form f3 = new Form(FormType.HORIZONTAL).setLegend("Test form <small>(doesn't do anything)</small>");
		f3.add(new StringInput("first").label("First Name:"));
		f3.add(new StringInput("last").label("Last Name:"));
		f3.add(new SelectInput("country", "NL").label("Country:").options("UK","IT"));
		f3.add(new Button("helloWorld").setLabel("Say hello").setIcon(Icon.SEARCH));
		
		return new BootstrapTheme(f3);
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
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String getCustomHtmlHeaders()
	{
		return new BootstrapTheme().getCustomHtmlHeaders();
	}
}