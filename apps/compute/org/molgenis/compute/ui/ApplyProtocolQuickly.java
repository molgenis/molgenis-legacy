/*
 * Date: February 16, 2011 Template: PluginScreenJavaTemplateGen.java.ftl
 * generator: org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.compute.ui;

import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.GenericPlugin;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.EntityInput;
import org.molgenis.framework.ui.html.TablePanel;
import org.molgenis.framework.ui.html.TextInput;
import org.molgenis.framework.ui.html.XrefInput;
import org.molgenis.pheno.Individual;
import org.molgenis.util.Tuple;


public class ApplyProtocolQuickly extends GenericPlugin
{
	// the one we say hello to
	Individual currentIndividual = null;

	public ApplyProtocolQuickly(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	public String render()
	{
		TablePanel table = new TablePanel("MyTablePanel", null);
		
		//say we want to render all inputs for individual
		////IndividualsForm f = new IndividualsForm();
		
//		HtmlForm myForm = f.getInputs(currentIndividual, false);
//		
//		//add to the panel
//		for(HtmlInput i: myForm.getInputs())
//		{
//			table.add(i);
//		}
//		
	

		EntityInput individualId = new XrefInput("individualId", currentIndividual);

		individualId.setXrefEntity(Individual.class);
		individualId.setXrefField(Individual.ID);
		individualId.setXrefLabel(Individual.NAME);

		table.add(individualId);

		TextInput description = new TextInput("description");
		description.setValue("Put your description here");
		
		if(currentIndividual != null)
		{
			description.setValue(currentIndividual.getDescription());
			
			individualId.setValue(currentIndividual.getId());
		}

		ActionInput doChangeDescription = new ActionInput("doChangeDescription");
		doChangeDescription.setLabel("Change description");

		//table.add(description);

		// see the table, and then the button
		return table.toHtml() + doChangeDescription.toHtml();

	}

	public void doChangeDescription(Database db, Tuple request)
	{
		try
		{
			db.beginTx();

			// we need to take description and add it to the selected
			// individual id.
			Integer indId = request.getInt("individualId");

			String description = request.getString("description");

			// now get individual from the databse
			List<Individual> indList;

			indList = db.query(Individual.class).eq(Individual.ID, indId)
					.find();

			currentIndividual = indList.get(0);
			
			//currentIndividual = db.findById(Individual.class, indId);

			currentIndividual.setDescription(description);

			db.update(currentIndividual);

			db.commitTx();

			this.getMessages().add(
					new ScreenMessage("succesfully updated description", true));

		}
		catch (Exception e)
		{
			try
			{
				db.rollbackTx();
			}
			catch (DatabaseException e1)
			{
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

	}

	@Override
	public void reload(Database db)
	{
		// try
		// {
		// Database db = this.getDatabase();
		// Query q = db.query(Experiment.class);
		// q.like("name", "test");
		// List<Experiment> recentExperiments = q.find();
		//			
		// //do something
		// }
		// catch(Exception e)
		// {
		// //...
		// }
	}

	@Override
	public boolean isVisible()
	{
		// you can use this to hide this plugin, e.g. based on user rights.
		// e.g.
		// if(!this.getLogin().hasEditPermission(myEntity)) return false;
		return true;
	}
}
