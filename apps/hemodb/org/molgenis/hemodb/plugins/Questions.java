
package org.molgenis.hemodb.plugins;


import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.hemodb.HemoSample;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.HandleRequestDelegationException;
import org.molgenis.util.Tuple;

/**
 * EditIndividualController takes care of all user requests and application logic.
 *
 * <li>Each user request is handled by its own method based action=methodName. 
 * <li> MOLGENIS takes care of db.commits and catches exceptions to show to the user
 * <li>AddIndividualModel holds application state and business logic on top of domain model. Get it via this.getModel()/setModel(..)
 * <li>AddIndividualView holds the template to show the layout. Get/set it via this.getView()/setView(..).
 */
public class Questions extends EasyPluginController<QuestionsModel>
{
	public Questions(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.setModel(new QuestionsModel(this)); //the default model
	}
	
	public ScreenView getView()
	{
		return new FreemarkerView("QuestionsView.ftl", getModel());
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
		
	}
	
	/**
	 * When action="updateDate": update model and/or view accordingly.
	 *
	 * Exceptions will be logged and shown to the user automatically.
	 * All db actions are within one transaction.
	 */
	
	public Show handleRequest(Database db, Tuple request, OutputStream out)
			throws HandleRequestDelegationException{	
		getModel().setAction(request.getAction());
		
		try {
		if(getModel().getAction().equals("verstuurJetty2")){
			System.out.println("We hebben Jetty");
		}
		String beestje = "sample1";

		
		
		List<HemoSample> x = db.find(HemoSample.class, new QueryRule(HemoSample.SAMPLEGROUP ,Operator.EQUALS, beestje));
		//(db.find(Investigation.class, new QueryRule(Investigation.NAME, Operator.EQUALS, investigationName)).size() == 0)
		//for(HemoSample h : x){
			
		//}
		}
		catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return Show.SHOW_MAIN;
	}
	


	
	
	
	
}