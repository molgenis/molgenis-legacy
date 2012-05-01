package org.molgenis.examples.ui;

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
import org.molgenis.framework.ui.html.IntInput;
import org.molgenis.framework.ui.html.JavaInput;
import org.molgenis.framework.ui.html.Label;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.framework.ui.html.MrefInput;
import org.molgenis.framework.ui.html.Paragraph;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.framework.ui.html.SelectMultipleInput;
import org.molgenis.framework.ui.html.StringInput;
import org.molgenis.framework.ui.html.VerticalLayout;
import org.molgenis.framework.ui.html.XrefInput;
import org.molgenis.util.Tuple;

public class SimpleInputsDemo extends EasyPluginController
{
	public SimpleInputsDemo(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public void reload(Database db) throws Exception
	{
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

		// string inputs
		view.add(new Paragraph("<h3>demos of inputs</h3>"));

		String themeSwitch = "<div id=\"switcher\"></div><script>$('#switcher').themeswitcher();</script>";
		view.add(new CustomHtml(themeSwitch).setLabel("changeJqueryTheme"));

		view.add(new StringInput("StringInput"));

		view.add(new StringInput("RequiredInput").setNillable(false));

		view.add(new IntInput("IntInput"));

		view.add(new DecimalInput("DecimalInput"));

		view.add(new DateInput("DateInput"));

		view.add(new DatetimeInput("DatetimeInput"));

		view.add(new BoolInput("BoolInput"));

		view.add(new ActionInput("submit"));
		view.add(new Paragraph(requestString));

		view.add(new JavaInput("CodeExample", "view.add(new StringInput(\"StringInput\"));"
				+ "\n\nview.add(new StringInput(\"RequiredInput\").setNillable(false));"
				+ "\n\nview.add(new IntInput(\"IntInput\"));" + "\n\nview.add(new DecimalInput(\"DecimalInput\"));"
				+ "\n\nview.add(new DateInput(\"DateInput\"));" + "\n\nview.add(new DatetimeInput(\"DatetimeInput\"));"
				+ "\n\nview.add(new BoolInput(\"BoolInput\"));").setLabel("<h3>Code example:</h3>"));

		return view;
	}

	private String requestString = "";

	public void submit(Database db, Tuple request)
	{
		this.requestString = request.toString();
	}
}