/*
 * Date: February 16, 2011 Template: PluginScreenJavaTemplateGen.java.ftl
 * generator: org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.compute;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.TextInput;
import org.molgenis.framework.ui.html.XrefInput;
import org.molgenis.pheno.Individual;
import org.molgenis.util.Tuple;

public class ApplyProtocolQuicklyOld extends PluginModel
{
	// the one we say hello to
	String helloTo = "world";
	Individual currentIndividual = null;

	public ApplyProtocolQuicklyOld(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_compute_ApplyProtocolQuickly";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/compute/ApplyProtocolQuickly.ftl";
	}

	public String getHelloTo()
	{
		return this.helloTo;
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try
		{
			this.getMessages().clear();
			db.beginTx();
					

			if ("ChangeDescription".equals(request.getAction()))
			{
				doChangeDescription(db,request);
			}
			else if ("do_change_name".equals(request.getAction()))
			{
				doChangeName(db,request);
				
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();

			try
			{
				db.rollbackTx();
			}
			catch (DatabaseException e1)
			{
				e1.printStackTrace();
			}

			this.getMessages().add(new ScreenMessage(e.getMessage(), false));
		}
	}

	private void doChangeName(Database db, Tuple request) throws DatabaseException, IOException
	{
		this.helloTo = request.getString("name");

		// side effect add to database
		Individual ind = new Individual();
		ind.setName(this.helloTo);

		db.add(ind);

		db.commitTx();
		
		this.getMessages().add(new ScreenMessage("succesfully said hello to ...", true));
		
	}

	private void doChangeDescription(Database db, Tuple request) throws DatabaseException, ParseException, IOException
	{
		// we need to take description and add it to the selected
		// individual id.
		Integer indId = request.getInt("individualId");

		String description = request.getString("description");

		// now get individual from the databse
		List<Individual> indList = db.query(Individual.class).eq(Individual.ID, indId)
				.find();

		Individual ind = indList.get(0);

		ind.setDescription(description);

		db.update(ind);
		
		db.commitTx();
		
		this.getMessages().add(new ScreenMessage("succesfully updated description", true));
		
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

	public ActionInput getChangeButton()
	{
		return new ActionInput("ChangeDescription");
	}

	public XrefInput getChooseIndividual()
	{
		XrefInput individualId = new XrefInput("individualId", currentIndividual);

		individualId.setXrefEntity(Individual.class);
		individualId.setXrefField(Individual.ID);
		individualId.setXrefLabel(Individual.NAME);

		return individualId;
	}

	public TextInput getTextarea()
	{
		TextInput description = new TextInput("description");
		description.setValue("Put your description here");

		return description;
	}
}
