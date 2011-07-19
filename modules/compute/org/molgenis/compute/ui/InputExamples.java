/*
 * Date: February 15, 2011 Template: PluginScreenJavaTemplateGen.java.ftl
 * generator: org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.compute.ui;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.GenericPlugin;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.DateInput;
import org.molgenis.framework.ui.html.DatetimeInput;
import org.molgenis.framework.ui.html.EntityInput;
import org.molgenis.framework.ui.html.TablePanel;
import org.molgenis.framework.ui.html.TextInput;
import org.molgenis.framework.ui.html.XrefInput;
import org.molgenis.pheno.Individual;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

public class InputExamples extends GenericPlugin
{
	TablePanel view = new TablePanel("myTablePanel", null);
	Tuple previousRequest = new SimpleTuple();

	public InputExamples(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String render()
	{
		TextInput previous = new TextInput("previousRequest");
		previous.setReadonly(true);
		previous.setValue(this.previousRequest.toString());

		view.add(previous);

		DateInput date = new DateInput("date");

		view.add(date);

		DatetimeInput datetime = new DatetimeInput("datetime", null);

		view.add(datetime);

		try {
			EntityInput xref;
			xref = new XrefInput("xref", Individual.class);
			xref.setXrefEntity(Individual.class);
			view.add(xref);
		} catch (Exception e) {
			logger.error("Not able to add xref input for 'Individual'");
			e.printStackTrace();
		}

		ActionInput doUpdate = new ActionInput("doUpdate");

		return view.toHtml() + doUpdate.toHtml();
	}

	public void doUpdate(Database db, Tuple request)
	{
		// primitive error handling
		if (request.isNull("datetime")) this.getMessages().add(
				new ScreenMessage("date time is null", false));
		else
		{
			this.getMessages().clear();
			this.getMessages().add(
					new ScreenMessage("all is well", true));
		}

		this.previousRequest = new SimpleTuple(request);
	}
}
