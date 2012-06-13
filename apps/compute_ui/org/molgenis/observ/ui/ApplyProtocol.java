package org.molgenis.observ.ui;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.framework.ui.html.XrefInput;
import org.molgenis.observ.Protocol;
import org.molgenis.util.Tuple;

/**
 * Screen 1: Choose protocol []; This will create a table of all protocol
 * applications for this protocol, one row per protocol application.
 * 
 * Apply protocol [choose target]. Will create another row at the bottom.
 */
public class ApplyProtocol extends EasyPluginController<ApplyProtocolModel>
{
	Protocol protocol;
	
	public ApplyProtocol(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(new ApplyProtocolModel(this)); // the default model
		this.setView(new FreemarkerView("ApplyProtocolView.ftl", getModel())); // <plugin
																				// flavor="freemarker"
	}

	/**
	 * At each page view: reload data from database into model and/or change.
	 * 
	 * Exceptions will be caught, logged and shown to the user automatically via
	 * setMessages(). All db actions are within one transaction.
	 */
	@Override
	public void reload(Database db) throws Exception
	{
		// //example: update model with data from the database
		// Query q = db.query(Investigation.class);
		// q.like("name", "molgenis");
		// getModel().investigations = q.find();
	}

	public void changeProtocol(Database db, Tuple request)
	{
		
	}
	
	public String render()
	{
		MolgenisForm f = new MolgenisForm(this);

		f.add(new XrefInput("protocol", Protocol.class));
		f.add(new ActionInput("changeProtocol", "Change protocol"));

		return f.render();

	}
}