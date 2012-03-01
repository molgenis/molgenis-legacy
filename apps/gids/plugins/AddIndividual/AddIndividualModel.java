/* Date:        February 27, 2012
 * Template:	EasyPluginModelGen.java.ftl
 * generator:   org.molgenis.generators.ui.EasyPluginModelGen 4.0.0-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.AddIndividual;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.molgenis.framework.ui.EasyPluginModel;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Individual;

/**
 * EditIndividualModel takes care of all state and it can have helper methods to query the database.
 * It should not contain layout or application logic which are solved in View and Controller.
 * @See org.molgenis.framework.ui.ScreenController for available services.
 */
public class AddIndividualModel extends EasyPluginModel
{
	//a system veriable that is needed by tomcat
	private static final long serialVersionUID = 1L;
	private List<Investigation> listInvest = new ArrayList<Investigation>();
	private List<Individual> listIndiv = new ArrayList<Individual>();
	String state = "start";
	String stateStart = "s";
	String chosenInv = "";
	String chosenInd = "";
	String protName = "";
	String newFamily = "";
	HashMap <String,List<String>> hashProtocols = new HashMap<String, List<String>>();
	List<String> knownFeatList = new ArrayList<String>();
	List<String> featList = new ArrayList<String>();
	List<String> listFamilies = new ArrayList<String>();
	
	
	public AddIndividualModel(AddIndividual controller)
	{
		//each Model can access the controller to notify it when needed.
		super(controller);
	}


	public List<Investigation> getListInvest() {
		return listInvest;
	}


	public void setListInvest(List<Investigation> listInvest) {
		this.listInvest = listInvest;
	}


	public List<Individual> getListIndiv() {
		return listIndiv;
	}


	public void setListIndiv(List<Individual> listIndiv) {
		this.listIndiv = listIndiv;
	}


	public String getState() {
		return state;
	}


	public void setState(String state) {
		this.state = state;
	}


	public String getChosenInv() {
		return chosenInv;
	}


	public void setChosenInv(String chosenInv) {
		this.chosenInv = chosenInv;
	}


	public String getChosenInd() {
		return chosenInd;
	}


	public void setChosenInd(String chosenInd) {
		this.chosenInd = chosenInd;
	}


	public String getStateStart() {
		return stateStart;
	}


	public void setStateStart(String stateStart) {
		this.stateStart = stateStart;
	}


	public HashMap<String, List<String>> getHashProtocols() {
		return hashProtocols;
	}


	public List<String> getKnownFeatList() {
		return knownFeatList;
	}


	public void setKnownFeatList(List<String> knownFeatList) {
		this.knownFeatList = knownFeatList;
	}


	public List<String> getFeatList() {
		return featList;
	}


	public void setFeatList(List<String> featList) {
		this.featList = featList;
	}


	public String getNewFamily() {
		return newFamily;
	}


	public void setNewFamily(String newFamily) {
		this.newFamily = newFamily;
	}


	public List<String> getListFamilies() {
		return listFamilies;
	}


	public void setListFamilies(List<String> listFamilies) {
		this.listFamilies = listFamilies;
	}


//	public void setHashProtocols(String protName, List<String> hashProtocols) {
//		this.protName = protName;
//		this.hashProtocols = hashProtocols;
//	}
	
	

}
