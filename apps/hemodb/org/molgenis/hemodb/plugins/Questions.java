package org.molgenis.hemodb.plugins;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.hemodb.HemoSample;
import org.molgenis.hemodb.HemoSampleGroup;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.HandleRequestDelegationException;
import org.molgenis.util.Tuple;

/**
 * EditIndividualController takes care of all user requests and application
 * logic.
 * 
 * <li>Each user request is handled by its own method based action=methodName.
 * <li>MOLGENIS takes care of db.commits and catches exceptions to show to the
 * user <li>AddIndividualModel holds application state and business logic on top
 * of domain model. Get it via this.getModel()/setModel(..) <li>
 * AddIndividualView holds the template to show the layout. Get/set it via
 * this.getView()/setView(..).
 */
public class Questions extends EasyPluginController<QuestionsModel>{
	public Questions(String name, ScreenController<?> parent){
		super(name, parent);
		this.setModel(new QuestionsModel(this)); // the default model
	}

	public ScreenView getView(){
		return new FreemarkerView("QuestionsView.ftl", getModel());
	}

	/**
	 * At each page view: reload data from database into model and/or change.
	 * 
	 * Exceptions will be caught, logged and shown to the user automatically via
	 * setMessages(). All db actions are within one transaction.
	 */
	@Override
	public void reload(Database db) throws Exception{
		if(getModel().state.equals("")){
			selectSampleGroupsForDropdown(db);
			System.out.println("NOT filled");
			getModel().setState("filled");
		}
		else{
			System.out.println("already filled");
		}
	}

	/**
	 * When action="updateDate": update model and/or view accordingly.
	 * 
	 * Exceptions will be logged and shown to the user automatically. All db
	 * actions are within one transaction.
	 */

	public Show handleRequest(Database db, Tuple request, OutputStream out) throws HandleRequestDelegationException	{
		getModel().setAction(request.getAction());

		try{
//			GENE EXPRESSION DATA MATRIX SELECTION -> shows which type of gene expression will be used
			String geneExp = request.getString("geneExp");
			// TODO: select the right data matrix
			if (geneExp.equals("quanLog")){				// Chosen the quantile data matrix 
				System.out.println("QUANTILE");
			}
			else{				// Chosen raw expression
				System.out.println("RAW");
			}

//			LIST OF GENES SELECTION -> Gets all the listed genes from the website
			String genes = request.getString("geneText"); 
			// TODO: select these genes in data matrix
			if (genes != null){
				//TODO do something with the genes
				if (genes.isEmpty()){
					System.out.println("blabla");
				}
				else{
					System.out.println(genes);
				}
			}
			else{
				System.out.println("There are no genes specified. Please try again");
			}
			
//			SAMPLE SELECTION BASED ON SAMPLE GROUP NAMES -> Gets the selected groups from the website and retrieves the associated samples
			// TODO check which are checked
			// TODO select these groups in data matrix
			List<String> groups = request.getStringList("sampleGroups");
			System.out.println(groups);
			int numberGroups = groups.size();
			System.out.println("number of groups: " + numberGroups);
			
//					String beestje = "sample1";
//					List<HemoSample> x = db.find(HemoSample.class, new QueryRule(HemoSample.SAMPLEGROUP, Operator.EQUALS,
//							beestje));
					// (db.find(Investigation.class, new QueryRule(Investigation.NAME,
					// Operator.EQUALS, investigationName)).size() == 0);
//					for (HemoSample h : x){
//						h.getSample();
//					}
					// List<HemoSampleGroup> hsg = db.findById(HemoSampleGroup.class,id);
					// List<HemoSampleGroup> hsg = db.query(HemoSampleGroup);
					// Query<HemoSampleGroup> hsg = db.query(HemoSampleGroup.class);
					// hsg.addRules(new QueryRule(HemoSampleGroup.ID, Operator.values()));
			
			
		// SUBMIT BUTTON
			if (getModel().getAction().equals("verstuurJetty2")){
				System.out.println("We hebben Jetty");
			}
			else{
				System.out.println("Something went wrong, try again.");
			}
		}

		// TODO show selected data
		catch (Exception e1){
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return Show.SHOW_MAIN;
	}
	
	public void selectSampleGroupsForDropdown(Database db) throws DatabaseException{
//		SELECTION OF ALL THE SAMPLE GROUPS IN THE DATABASE
		// get all the HemoSampleGroups from database -> List <HemoSampleGroup> sampleGroups
		// make a list of all the groups -> ArrayList <String> names
		// make a dropdown/multiple selection field -> see QuestionsModel & QuestionsView
		List <HemoSampleGroup> sampleGroups = db.find(HemoSampleGroup.class);

		if(sampleGroups != null){
			for (HemoSampleGroup hsg : sampleGroups){
				String name = hsg.getName();
				getModel().getNames().add(name);
			}
		}
		else{
			System.out.println("OOPS! SOMETHING WENT WRONG");
		}
	}
}