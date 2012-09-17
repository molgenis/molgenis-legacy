
package org.molgenis.sandbox.ui;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.BoolInput;
import org.molgenis.framework.ui.html.CustomHtml;
import org.molgenis.framework.ui.html.DateInput;
import org.molgenis.framework.ui.html.DatetimeInput;
import org.molgenis.framework.ui.html.DecimalInput;
import org.molgenis.framework.ui.html.FlowLayout;
import org.molgenis.framework.ui.html.HtmlElement.UiToolkit;
import org.molgenis.framework.ui.html.HtmlSettings;
import org.molgenis.framework.ui.html.IntInput;
import org.molgenis.framework.ui.html.Label;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.framework.ui.html.MrefInput;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.framework.ui.html.SelectMultipleInput;
import org.molgenis.framework.ui.html.StringInput;
import org.molgenis.framework.ui.html.VerticalLayout;
import org.molgenis.framework.ui.html.XrefInput;
import org.molgenis.organization.Investigation;
import org.molgenis.util.Tuple;


/**
 * InputsTestController takes care of all user requests and application logic.
 *
 * <li>Each user request is handled by its own method based action=methodName. 
 * <li> MOLGENIS takes care of db.commits and catches exceptions to show to the user
 * <li>InputsTestModel holds application state and business logic on top of domain model. Get it via this.getModel()/setModel(..)
 * <li>InputsTestView holds the template to show the layout. Get/set it via this.getView()/setView(..).
 */
public class InputsTest extends EasyPluginController<InputsTestModel>
{
	public InputsTest(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.setModel(new InputsTestModel(this)); //the default model
		//this.setView(new FreemarkerView("InputsTestView.ftl", getModel())); //<plugin flavor="freemarker"
	}
	
	/**
	 * At each page view: reload data from database into model and/or change.
	 *
	 * Exceptions will be caught, logged and shown to the user automatically via setMessages().
	 * All db actions are within one transaction.
	 */ 
	@Override
	public void reload(Database db) throws Exception
	{	
//		//example: update model with data from the database
//		Query q = db.query(Investigation.class);
//		q.like("name", "molgenis");
//		getModel().investigations = q.find();
	}
	
	public String getCustomHtmlHeaders()
	{
		return new ActionInput().getCustomHtmlHeaders() 
		+ new SelectInput().getCustomHtmlHeaders()
		+ new StringInput().getCustomHtmlHeaders()+
		"<link rel=\"stylesheet\" style=\"text/css\" type=\"text/css\" href=\"generated-res/css/molgenis_jquery_icons.css\">"+
		"<script type=\"text/javascript\" src=\"http://jqueryui.com/themeroller/themeswitchertool/\"></script>";
	}
	
	public void changelibrary(Database db, Tuple request)
	{
		logger.info("changelibrary: " + request);
		String lib = request.getString("library");
		if("JQUERY".equals(lib)) HtmlSettings.uiToolkit = UiToolkit.JQUERY;
		if("DEFAULT".equals(lib)) HtmlSettings.uiToolkit = UiToolkit.ORIGINAL;
		
	}
	
	public ScreenView getView()
	{
		MolgenisForm view = new MolgenisForm(this, new FlowLayout());
		
		view.add(new Label("select demo (and to change library used)"));
		
		VerticalLayout libraryPanel = new VerticalLayout();
		
		SelectInput select = new SelectInput("library", HtmlSettings.uiToolkit.toString());
		select.addOption("JQUERY", "Jquery toolkit");
		select.addOption("DEFAULT","MOLGENIS original");
		//select.addOption("DOJO","Dojo Toolkit (doesn't work, requires all to be DOJO :-()");
		
		libraryPanel.add(select);
		libraryPanel.add(new ActionInput("changelibrary"));
		
		String themeSwitch = "<div id=\"switcher\"></div><script>$('#switcher').themeswitcher();</script>";
		libraryPanel.add(new CustomHtml(themeSwitch));

		
		view.add(libraryPanel);
		
		view.add(new Label("demos of buttons"));
	
		FlowLayout buttonDemo = new FlowLayout();
		
		buttonDemo.add(new ActionInput("hello world"));
		
		ActionInput button2 = new ActionInput("with icon");
		//icons go via css so you only need to name here
		button2.setIcon("save");
		buttonDemo.add(button2);
		
		ActionInput button3 = new ActionInput("icon only");
		button3.setIcon("save");
		button3.setShowLabel(false);
		
		buttonDemo.add(button3);
		
		view.add(buttonDemo);
		
		//select boxes
		view.add(new Label("demos of selects"));
		view.add(new XrefInput("XrefInput", Investigation.class));
		try
		{
			new XrefInput<Investigation>("XrefInputDefault", 
					Investigation.class,
					this.getDatabase().findById(Investigation.class,1));
		}
		catch (DatabaseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		view.add(new MrefInput("MrefInput", Investigation.class));
		
		SelectMultipleInput mselect = new SelectMultipleInput("MultipleSelect");
		mselect.addOption("EU", "Europe");
		mselect.addOption("AS","Asia");
		mselect.addOption("AF","Africa");
		mselect.addOption("NA","North America");
		mselect.addOption("SA","South America");
		mselect.addOption("AU","Australia");
		mselect.addOption("AN","Antartica");
		
		view.add(mselect);

		
		//string inputs
		view.add(new Label("demos of inputs"));
		
		view.add(new StringInput("stringInput"));
		
		//multicol select using html, TODO
//		SelectInput multicol = new SelectInput("MultiCol");
//		multicol.addOption("option1","<span style=\"width: 100px; border: solid thin red;\"> <b>col11</b> </span> <span style=\"width: 100px; border: solid thin red;\"> col12 </span>".replace("<", "&lt;").replace(">", "&gt;"));
//		multicol.addOption("option2","<span style=\"width: 100px; border: solid thin red;\"> <b>col21</b> </span> <span style=\"width: 100px; border: solid thin red;\"> col22 </span>".replace("<", "&lt;").replace(">", "&gt;"));
//		main.add(multicol);
		
		
		//required
		StringInput req = new StringInput("requiredInput");
		req.setNillable(false);
		view.add(req);
		
		view.add(new IntInput("IntInput"));
		
		view.add(new DecimalInput("DecimalInput"));
		
		view.add(new DateInput("DateInput"));
		
		view.add(new DatetimeInput("DatetimeInput"));
		
		view.add(new BoolInput("BoolInput"));
		
		return view;
	}
}