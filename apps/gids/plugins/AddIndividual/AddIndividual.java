
package plugins.AddIndividual;


import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenModel.Show;
import org.molgenis.framework.ui.html.Table;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.HandleRequestDelegationException;
import org.molgenis.util.Tuple;

import com.sun.xml.fastinfoset.sax.Features;

/**
 * EditIndividualController takes care of all user requests and application logic.
 *
 * <li>Each user request is handled by its own method based action=methodName. 
 * <li> MOLGENIS takes care of db.commits and catches exceptions to show to the user
 * <li>AddIndividualModel holds application state and business logic on top of domain model. Get it via this.getModel()/setModel(..)
 * <li>AddIndividualView holds the template to show the layout. Get/set it via this.getView()/setView(..).
 */
public class AddIndividual extends EasyPluginController<AddIndividualModel>
{
	public AddIndividual(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(new AddIndividualModel(this)); //the default model
		this.setView(new FreemarkerView("AddIndividualView.ftl", getModel())); //<plugin flavor="freemarker"
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
		getModel().setListInvest(db.find(Investigation.class));
		
	}
	
	/**
	 * When action="updateDate": update model and/or view accordingly.
	 *
	 * Exceptions will be logged and shown to the user automatically.
	 * All db actions are within one transaction.
	 */
	
	public Show handleRequest(Database db, Tuple request, OutputStream out)
			throws HandleRequestDelegationException{	
		String action = request.getString("__action");

		try {
			List<Protocol> listProtocols = db.query(Protocol.class).find();
			
			if(action.equals("goToIndiv")){
				getModel().chosenInv = request.getString("investigationDropdown");
				System.out.println("getModel().chosenInv " +getModel().chosenInv);
				getModel().setStateStart("chooseIndividual");
				List<QueryRule> filterRules = new ArrayList<QueryRule>();
				filterRules.add(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, getModel().getIdentifier()));
				filterRules.add(new QueryRule(ObservedValue.INVESTIGATION_NAME, Operator.EQUALS, getModel().chosenInv));
				List<ObservedValue> listId = db.find(ObservedValue.class, new QueryRule(filterRules));
				for(ObservedValue o : listId){
					getModel().listIdentifiers.add(o.getValue());
				}
				
				//
				SelectFamily select = new SelectFamily();
				getModel().setNewFamily(select.family(db,getModel().chosenInv));
			}
			
			if(action.equals("goToTable")){
				getModel().setState("individual");
				getModel().chosenInd = request.getString("individual");
				for(Protocol p : listProtocols){	
					getModel().featList = new ArrayList<String>();
					for(String e: p.getFeatures_Name()){
						if(!getModel().knownFeatList.contains(e)){
							getModel().knownFeatList.add(e);
							getModel().featList.add(e);
						}
					}if(getModel().featList.size()!=0){
						getModel().hashProtocols.put(p.getName(),getModel().featList);
					}
					//makeProtocolDiv(p.getName(),p.getFeatures_Name());					
				}
				
			}
			
			if(action.equals("submitting")){
				
				for(Entry<String, List<String>> entry: getModel().hashProtocols.entrySet()){
					
					for(String e : entry.getValue()){
						
						if(request.getString(e+(entry.getKey()))!=null){
							System.out.println(request.getString(e+(entry.getKey())));
						}
						
						
					}
				}
				
			}
			
			
		} 
		catch (DatabaseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return Show.SHOW_MAIN;
	}
	


	
	
	
	
}