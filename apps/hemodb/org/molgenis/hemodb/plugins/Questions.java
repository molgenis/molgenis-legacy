package org.molgenis.hemodb.plugins;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
//import java.util.ListIterator;
//import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
//import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.hemodb.HemoGene;
import org.molgenis.hemodb.HemoProbe;
import org.molgenis.hemodb.HemoSample;
import org.molgenis.hemodb.HemoSampleGroup;
//import org.molgenis.organization.Investigation;
//import org.molgenis.pheno.Individual;
//import org.molgenis.pheno.ObservedValue;
//import org.molgenis.protocol.Protocol;
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
	 * Exceptions will be logged and shown to the user automatically. All db
	 * actions are within one transaction.
	 */

	public Show handleRequest(Database db, Tuple request, OutputStream out) throws HandleRequestDelegationException	{
		getModel().setAction(request.getAction());

		try{
//			GENE EXPRESSION DATA MATRIX SELECTION -> shows which type of gene expression will be used
			String geneExp = request.getString("geneExp");
			if (geneExp.equals("quanLog")){				// Chosen the quantile data matrix 
//				TODO: DO SOMETHING WITH THE QUANTILE MATRIX
			}
			else{				// Chosen raw expression
//				TODO: DO SOMETHING WITH THE RAW MATRIX
			}

//			LIST OF GENES SELECTION -> Gets all the listed genes from the website
//			List <String> genes = request.getStringList("geneText");
			String genesFromSite = request.getString("geneText");
			String[] genes = genesFromSite.split("\n");
//			System.out.println("GENES!: " + genes + " miep");	
//			
//			for(String e: genes){
//				System.out.println("bla " + e);
//			}
			if (genes != null){
				if (genes.length == 0){
					System.out.println("There are no genes in the inputfield. Try again.");
				}
				else{
					selectProbesWithGenes(db,genes);
				}
			}
			else{
				System.out.println("There are no genes specified. Please try again");
			}
			
//			SAMPLE SELECTION BASED ON SAMPLE GROUP NAMES -> Gets the selected groups from the website 
//			and retrieves the associated samples
			// TODO select these groups in data matrix
			List<String> groups = request.getStringList("sampleGroups");
//			System.out.println(groups);
			selectSamplesFromSampleGroups(db, groups);
		
			
		// SUBMIT BUTTON
			if (getModel().getAction().equals("verstuurJetty2")){
				System.out.println("we handled the information on the site submitted via the submit button");
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
//		SELECTION OF ALL THE SAMPLE GROUPS IN THE DATABASE TO DISPLAY ON THE SITE (MULTIPLE SELECT)
		List <HemoSampleGroup> sampleGroups = db.find(HemoSampleGroup.class);
		if(sampleGroups != null){
			for (HemoSampleGroup hsg : sampleGroups){
				String name = hsg.getName();
				getModel().getNames().add(name);
//				add names to arraylist
//				return arraylist to handleRequest
			}
		}
		else{
			System.out.println("OOPS! SOMETHING WENT WRONG");
		}
	}
	
	public void selectSamplesFromSampleGroups(Database db, List<String> sampleGroups) throws DatabaseException{
//		SELECTION OF THE SAMPLE NAMES WITHIN EACH GROUP THAT IS SPECIFIED
//		TODO: RETURN SAMPLE NAMES TO HANDLEREQUEST
		List<String> sampleNames = new ArrayList<String>();
		int numberGroups = sampleGroups.size(); 
		if(numberGroups == 0){
			System.out.println("there is no selection made");
		}
		else{
			System.out.println("\n" + "number of groups: " + numberGroups);
			for (String hsg : sampleGroups){
				System.out.println("\n" + "sampleGroup is: " + hsg);
				List <HemoSample> samplesPerGroup = db.find(HemoSample.class, new QueryRule(HemoSample.SAMPLEGROUP_NAME, Operator.EQUALS, hsg));
				for (HemoSample name : samplesPerGroup){
					sampleNames.add(name.getName());
				}
			}
			System.out.println("sampleNames OUTSIDE for: " + sampleNames + "\n");
	//		return list with sample names ;
		}
	}
	
	public void selectProbesWithGenes(Database db, String[] genes) throws DatabaseException{
//		GETS A STRING WITH GENE NAMES, CONVERTS THEM TO PROBES AND RETURNS THEM TO THE HANDLEREQUEST
		List<String> probes = new ArrayList<String>();
		for (String gene : genes){
			gene = gene.toUpperCase();
			gene = StringUtils.chomp(gene);
			System.out.println("gene is: " + gene);
			List <HemoProbe> probesPerGene = db.find(HemoProbe.class, new QueryRule(HemoProbe.REPORTSFOR_NAME, Operator.EQUALS, gene));	
			for (HemoProbe probe : probesPerGene){
				System.out.println("name of gene: " + probe);
				System.out.println("name of probe: " + probe.getName());
				probes.add(probe.getName());
			}
		}
		if(probes != null){
			System.out.println("probes found with this gene(s): " + probes);
		} 
	}
}

//gene = gene.replace(System.getProperty("line.separator"), "");