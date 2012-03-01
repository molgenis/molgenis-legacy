
package plugins.EditIndividual;


import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import org.molgenis.protocol.Protocol;
import org.molgenis.util.HandleRequestDelegationException;
import org.molgenis.util.Tuple;

import com.sun.xml.fastinfoset.sax.Features;

/**
 * EditIndividualController takes care of all user requests and application logic.
 *
 * <li>Each user request is handled by its own method based action=methodName. 
 * <li> MOLGENIS takes care of db.commits and catches exceptions to show to the user
 * <li>EditIndividualModel holds application state and business logic on top of domain model. Get it via this.getModel()/setModel(..)
 * <li>EditIndividualView holds the template to show the layout. Get/set it via this.getView()/setView(..).
 */
public class EditIndividual extends EasyPluginController<EditIndividualModel>
{
	public EditIndividual(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(new EditIndividualModel(this)); //the default model
		this.setView(new FreemarkerView("EditIndividualView.ftl", getModel())); //<plugin flavor="freemarker"
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
				
				getModel().setListIndiv(db.find(Individual.class, new QueryRule(Individual.INVESTIGATION_NAME, Operator.EQUALS, getModel().chosenInv)));
				
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
		} 
		catch (DatabaseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return Show.SHOW_MAIN;
	}
	


	
	
	
	
}