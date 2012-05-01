package org.molgenis.examples.ui;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.FlowLayout;
import org.molgenis.framework.ui.html.JavaInput;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.framework.ui.html.Paragraph;
import org.molgenis.framework.ui.html.StringInput;
import org.molgenis.util.Tuple;

public class HelloWorldDemo extends EasyPluginController
{
	private static final long serialVersionUID = 7794050660074280454L;

	public HelloWorldDemo(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public void reload(Database db) throws Exception
	{
		// this method is called on every page reload
	}

	@Override
	public ScreenView getView()
	{
		//return new FreemarkerView("HelloWorld.ftl", this.getModel());
		
		MolgenisForm f = new MolgenisForm(this, new FlowLayout());
		
		f.add(new Paragraph("<h3>This is a code example of a complete plugin.</h3>"));
		
		f.add(new Paragraph("Hello "+ name+"!<br/><br/>Type your name below:"));
		f.add(new StringInput("name"));
		//action below maps to public void sayHello()
		f.add(new ActionInput("sayHello"));
		
		f.add(new Paragraph("<h3>Code example:</h3>"));
		f.add(new Paragraph("<div style=\"background: lightyellow\">To get it to work you need <ul>" +
				"<li>Add to ui.xml <plugin name=\"HelloWorld\" type=\"package.of.your.HelloWorldDemo\"</li>" +
				"<li>Put the code below as a java class file</li></ul></div>"));
		f.add(new JavaInput("CodeExample", theCode).setHeight(50));
		
		return f;
	}

	// here we handle the action named 'sayHello'
	private String name = "UNKNOWN";

	public void sayHello(Database db, Tuple request)
	{
		if (request.isNull("name")) name = "UNKNOWN";
		else
			name = request.getString("name");
	}

	private String theCode = "\n  package org.molgenis.examples.ui;" + "\n"
			+ "\n  import org.molgenis.framework.db.Database;"
			+ "\n  import org.molgenis.framework.ui.EasyPluginController;"
			+ "\n  import org.molgenis.framework.ui.ScreenController;"
			+ "\n  import org.molgenis.framework.ui.ScreenView;"
			+ "\n  import org.molgenis.framework.ui.html.ActionInput;"
			+ "\n  import org.molgenis.framework.ui.html.FlowLayout;"
			+ "\n  import org.molgenis.framework.ui.html.MolgenisForm;"
			+ "\n  import org.molgenis.framework.ui.html.Paragraph;"
			+ "\n  import org.molgenis.framework.ui.html.StringInput;" + "\n  import org.molgenis.util.Tuple;" + "\n"
			+ "\n  //include to this plugin in your molgenis_ui.xml via "
			+ "\n  //<plugin name=\"myname\" type=\"org.molgenis.example.ui.HelloWorldDemo\"/>"
			+ "\n  public class HelloWorldDemo extends EasyPluginController" + "\n  {"
			+ "\n  	private static final long serialVersionUID = 7794050660074280454L;"
			+ "\n  	public HelloWorldDemo(String name, ScreenController<?> parent)" + "\n  	{"
			+ "\n  		super(name, parent);" + "\n     }" + "\n" + "\n  	@Override\n  	public ScreenView getView()"
			+ "\n  	{" 
			+ "\n  		MolgenisForm f = new MolgenisForm(this, new FlowLayout());" 
			+ "\n"
			+ "\n       f.add(new Paragraph(\"<h3>This is a code example of a complete plugin.</h3>\"));"
			+ "\n  		f.add(new Paragraph(\"Hello \"+ name+\"!<br/><br/>Type your name below:\"));"
			+ "\n  		f.add(new StringInput(\"name\"));" + "\n  		//action below maps to public void sayHello()"
			+ "\n  		f.add(new ActionInput(\"sayHello\"));" + "\n  		" + "\n  		return f;" + "\n  	}" + "\n"
			+ "\n  	//here we handle the action named 'sayHello'" + "\n  	private String name = \"UNKNOWN\";"
			+ "\n  	public void sayHello(Database db, Tuple request)" + "\n  	{"
			+ "\n  		if(request.isNull(\"name\")) name = \"UNKNOWN\";"
			+ "\n  		else name = request.getString(\"name\");" + "\n  	}" + "\n  	" + "\n  	@Override"
			+ "\n  	public void reload(Database db) throws Exception" + "\n  	{"
			+ "\n  		//this method is called on every page reload" + "\n  	}" + "\n}";
}
