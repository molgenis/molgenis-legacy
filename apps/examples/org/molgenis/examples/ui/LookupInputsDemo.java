package org.molgenis.examples.ui;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.CustomHtml;
import org.molgenis.framework.ui.html.JavaInput;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.framework.ui.html.MrefInput;
import org.molgenis.framework.ui.html.Paragraph;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.framework.ui.html.SelectMultipleInput;
import org.molgenis.framework.ui.html.StringInput;
import org.molgenis.framework.ui.html.VerticalLayout;
import org.molgenis.framework.ui.html.XrefInput;
import org.molgenis.organization.Investigation;
import org.molgenis.util.Tuple;

public class LookupInputsDemo extends EasyPluginController
{
	Investigation Investigation = null;

	public LookupInputsDemo(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public void reload(Database db) throws Exception
	{
		if (db.count(Investigation.class) > 0) this.Investigation = db.query(Investigation.class).limit(1).find()
				.get(0);
	}

	public String getCustomHtmlHeaders()
	{
		return new ActionInput().getCustomHtmlHeaders()
				+ new SelectInput().getCustomHtmlHeaders()
				+ new StringInput().getCustomHtmlHeaders()
				+ "<link rel=\"stylesheet\" style=\"text/css\" type=\"text/css\" href=\"generated-res/css/molgenis_jquery_icons.css\">"
				+ "<script type=\"text/javascript\" src=\"http://jqueryui.com/themeroller/themeswitchertool/\"></script>";
	}

	@Override
	public ScreenView getView()
	{
		MolgenisForm view = new MolgenisForm(this, new VerticalLayout());

		// select boxes
		view.add(new Paragraph("<h3>demos of selects</h3>"));

		String themeSwitch = "<div id=\"switcher\"></div><script>$('#switcher').themeswitcher();</script>";
		view.add(new CustomHtml(themeSwitch).setLabel("changeJqueryTheme"));

		if (Investigation != null) view.add(new XrefInput("XrefInputDefault", this.Investigation));
		else
			view.add(new XrefInput("XrefInput", Investigation.class));

		view.add(new MrefInput("MrefInput", Investigation.class));

		SelectInput select = new SelectInput("SelectInput");
		select.addOption("EU", "Europe");
		select.addOption("AS", "Asia");
		select.addOption("AF", "Africa");
		select.addOption("NA", "North America");
		select.addOption("SA", "South America");
		select.addOption("AU", "Australia");
		select.addOption("AN", "Antartica");

		view.add(select);

		SelectMultipleInput mselect = new SelectMultipleInput("SelectMultipleInput");
		mselect.addOption("EU", "Europe");
		mselect.addOption("AS", "Asia");
		mselect.addOption("AF", "Africa");
		mselect.addOption("NA", "North America");
		mselect.addOption("SA", "South America");
		mselect.addOption("AU", "Australia");
		mselect.addOption("AN", "Antartica");

		view.add(mselect);
		
		view.add(new ActionInput("submit"));
		view.add(new Paragraph(requestString));

		view.add(new JavaInput("CodeExample", codeString).setLabel("<h3>CodeExample</h3>"));

		return view;
	}
	
	public void submit(Database db, Tuple request)
	{
		this.requestString = request.toString();
	}
	
	private String requestString = "";

	private String codeString = "" + "\n//initialise 'ajax' entity lookup from entity object or class"
			+ "\nif(Investigation != null) view.add(new XrefInput(\"XrefInputDefault\", this.Investigation));"
			+ "\nelse view.add(new XrefInput(\"XrefInput\", Investigation.class));"
			+ "\n\n//initialize mref lookup from entity class"
			+ "\nview.add(new MrefInput(\"MrefInput\", Investigation.class));" + "\n"
			+ "\nSelectInput select = new SelectInput(\"SelectInput\");" + "\nselect.addOption(\"EU\", \"Europe\");"
			+ "\nselect.addOption(\"AS\", \"Asia\");" + "\nselect.addOption(\"AF\", \"Africa\");"
			+ "\nselect.addOption(\"NA\", \"North America\");" + "\nselect.addOption(\"SA\", \"South America\");"
			+ "\nselect.addOption(\"AU\", \"Australia\");" + "\nselect.addOption(\"AN\", \"Antartica\");"
			+ "\nview.add(select);" + "\n"
			+ "\nSelectMultipleInput mselect = new SelectMultipleInput(\"SelectMultipleInput\");"
			+ "\nmselect.addOption(\"EU\", \"Europe\");" + "\nmselect.addOption(\"AS\", \"Asia\");"
			+ "\nmselect.addOption(\"AF\", \"Africa\");" + "\nmselect.addOption(\"NA\", \"North America\");"
			+ "\nmselect.addOption(\"SA\", \"South America\");" + "\nmselect.addOption(\"AU\", \"Australia\");"
			+ "\nmselect.addOption(\"AN\", \"Antartica\");" + "\nview.add(mselect);";
}