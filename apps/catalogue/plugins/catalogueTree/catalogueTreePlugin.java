package plugins.catalogueTree;

import gcc.catalogue.ShoppingCart;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.molgenis.core.MolgenisFile;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.html.JQueryTreeView;
import org.molgenis.framework.ui.html.JQueryTreeViewElement;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.Entity;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;

public class catalogueTreePlugin extends PluginModel<Entity> {

	private static final long serialVersionUID = -6143910771849972946L;
	private JQueryTreeView<JQueryTreeViewElement> treeView = null;
	private HashMap<String, Protocol> nameToProtocol;
	private HashMap<String, JQueryTreeViewElement> protocolsAndMeasurementsinTree;
	private List<Measurement> shoppingCart = new ArrayList<Measurement>();
	private List<Investigation> arrayInvestigations = new ArrayList<Investigation>();
	private String selectedInvestigation = null;
	private boolean isSelectedInv = false; 
	private String InputToken=null;
	private String searchingInvestigation=null;
	private String comparison=null;

	private String selectedField = null;
	//private boolean isSelectedField = false;
	private List<String> arraySearchFields = new ArrayList<String>();

	//Multiple inheritance: some measurements might have multiple parents, therefore it
	//will complain about the branch already exists when constructing the tree, cheating by
	//changing the name of the branch but keeping display name the same
	private HashMap<String, Integer> multipleInheritance = new HashMap<String, Integer>();


	public catalogueTreePlugin(String name, ScreenController<?> parent) {
		super(name, parent);
	}

	public String getCustomHtmlHeaders()
	{
		return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/download_list.css\">";
	}

	@Override
	public String getViewName() {
		return "plugins_catalogueTree_catalogueTreePlugin";
	}

	@Override
	public String getViewTemplate() {
		return "plugins/catalogueTree/catalogueTreePlugin.ftl";
	}

	public void handleRequest(Database db, Tuple request) {

		try {

			if ("chooseInvestigation".equals(request.getAction())) 
			{
				selectedInvestigation = request.getString("investigation");
				this.setSelectedInvestigation(selectedInvestigation);
				System.out.println("The selected investigation is : "+ selectedInvestigation);
				arrayInvestigations.clear();

			} else if ("DownloadMeasurements".equals(request.getAction())) {

				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
				Date dat = new Date();
				String dateOfDownload = dateFormat.format(dat);
				System.out.println("selected investigaton >>>> " + selectedInvestigation);
				this.addMeasurementsForDownload(db, request, selectedInvestigation, dateOfDownload);

			} else if (request.getAction().startsWith("DeleteMeasurement")) {

				String measurementName  =  request.getString("measurementName"); //TODO :  this is not working
				measurementName = request.getAction().substring("DeleteMeasurement".length()+2+"measurementName".length(), request.getAction().length());
				this.deleteShoppingItem(measurementName);

			} if (request.getAction().startsWith("SearchCatalogueTree")) {
				this.setInputToken(request.getString("InputToken").trim());

				System.out.println("The request string : "+ request);
				System.out.println("The searching investigation is : "+ request.getString("searchingInvestigation").trim());
				this.setSearchingInvestigation(request.getString("searchingInvestigation").trim());
				searchingInvestigation = request.getString("searchingInvestigation").trim();
				this.setSelectedField(request.getString("selectedField"));

				//Search input token --> LIKE protocols
				System.out.println("Input token: >>>>>>"+ this.getInputToken() + ">>> selectedField >>"+ selectedField + "comparison >>>" + this.getComparison()+ "searchingInvestigation>>"+ this.getSearchingInvestigation());
				if (this.getSelectedField().equals("Protocols")) 
					RetrieveProtocols(db,2); 
				//Search "Any field" ==> All fields LIKE input token 
				if (this.getSelectedField().equals("Measurements")) 
					RetrieveProtocols(db,3); 

				if (this.getSelectedField().equals("All fields")) 
					RetrieveProtocols(db,4); 

				//if (this.getSelectedField().equals("All fields")) 
				//RetrieveProtocols(db,3); //too complicated to start the search from this function .  This filter is applied in addingLastMeasurementToTree()
				//else if (this.getSelectedField().equals("Measurements")) {}
				//else if (this.getSelectedField().equals("Details")) {}

			}

		} catch (Exception e) {
			e.printStackTrace();
			this.setError("There was a problem handling your Download: " + e.getMessage());
		}

	}

	@Override
	public void reload(Database db) {

		arraySearchFields.clear();
		//this.searchingInvestigation = null;
		//this.selectedInvestigation = null;

		arraySearchFields.add("Protocols");
		arraySearchFields.add("Measurements");
		arraySearchFields.add("Details");
		arraySearchFields.add("All fields");


		Query<ShoppingCart> q = db.query(ShoppingCart.class);
		q.addRules(new QueryRule(ShoppingCart.USERID, Operator.EQUALS, this.getLogin().getUserName()));
		q.addRules(new QueryRule(ShoppingCart.CHECKEDOUT, Operator.EQUALS, false));
		try {
			List<ShoppingCart> result = q.find();
			shoppingCart.clear();
			for(ShoppingCart cart : result){
				shoppingCart.addAll(cart.getMeasurements(db));
			}

			this.arrayInvestigations.clear();

			for (Investigation i: db.find(Investigation.class)) {
				this.arrayInvestigations.add(i);
			}
		} catch (DatabaseException e) {
			e.printStackTrace();
		}

		if (this.getInputToken() == null) 
			RetrieveProtocols(db,1); //mode 1: gets all protocols without filters!

		this.setInputToken(null);

	}

	/**
	 * This method is used to retrieve all the protocols from the database. Protocol is a bunch of measurements in Molgenis
	 * system, some of which could have sub-protocols. Therefore three different kinds of protocols are defined in the method
	 * to find the topmost protocols (the ancestors of all the protocols), which are stored in variable topProtocols. The topmost
	 * protocols are the starting point of the tree. The tree extends to the next level down with sub-protocols. Until the last
	 * level of tree (last branch in the tree), the measurements are stored in there. The topmost protocols are passed to the 
	 * another method called resursiveAddingNodesToTree, which could recursively add new branches to the tree.
	 * 
	 * @param db
	 * @param mode
	 */
	public void RetrieveProtocols(Database db, Integer mode) {

		List<String> topProtocols = new ArrayList<String>();
		List<String> bottomProtocols = new ArrayList<String>();
		List<String> middleProtocols = new ArrayList<String>();
		protocolsAndMeasurementsinTree = new HashMap<String, JQueryTreeViewElement>();
		nameToProtocol = new HashMap<String, Protocol>();

		try {

			Query<Protocol> q = db.query(Protocol.class);


			if (mode == 1) { //reload is calling

				q.addRules(new QueryRule(Protocol.INVESTIGATION_NAME, Operator.EQUALS, this.getSelectedInvestigation()));

			} else { 	
				//
				//				//Search input token --> LIKE protocols
				//				q.addRules(new QueryRule(Protocol.INVESTIGATION_NAME, Operator.EQUALS, this.getSearchingInvestigation()));
				//				q.addRules(new QueryRule(Protocol.NAME, Operator.LIKE, InputToken));
				//
				//			} else {
				//				//Seach for token in mode 3 or 4
				//				
				//			}

				q.addRules(new QueryRule(Protocol.INVESTIGATION_NAME, Operator.EQUALS, this.getSearchingInvestigation()));
			}
			//Iterate through all the found protocols
			for (Protocol p : q.find()) {

				setSelectedInv(true);
				List<String> subNames = p.getSubprotocols_Name();

				//keep a record of each protocol in a hashmap. Later on we could reference to the Protocol by name
				if (!nameToProtocol.containsKey(p.getName())) {
					nameToProtocol.put(p.getName(), p);
				}

				//Algorithm to find the topmost protocols. There are three kind of protocols needed. 
				//1. The protocols that are parents of other protocols
				//2. The protocols that are children of some other protocols and at the same time are parents of some other protocols
				//3. The protocols that are only children of other protocols
				//Therefore we could do protocol2 = protocol2.removeAll(protocol3) ----> parent protocols but not topmost
				//we then do protocol1 = protocol1.removeAll(protocol2) ------> topmost parent protocols 
				if (!subNames.isEmpty()) {

					if (!topProtocols.contains(p.getName())) {
						topProtocols.add(p.getName());
					}
					for (String subProtocol : subNames) {
						if (!middleProtocols.contains(subProtocol)) {
							middleProtocols.add(subProtocol);
						}
					}

				} else {

					if (!bottomProtocols.contains(p.getName())) {
						bottomProtocols.add(p.getName());
					}
				}

				middleProtocols.removeAll(bottomProtocols);
				topProtocols.removeAll(middleProtocols);
			}

		} catch (DatabaseException e) {
			e.printStackTrace();
		}

		//Create a starting point of the tree! The root of the tree!
		JQueryTreeViewElement protocolsTree = new JQueryTreeViewElement(
				"Protocols", null);

		//Variable indicating whether the input token has been found. 
		boolean foundInputToken = false;
		//		
		//		//in mode 2 where we search for protocols, we could ensure the token has been found. Because we use input token to query database, 
		//		//so the retrieved protocols must match the input token
		//		if(mode == 2)
		//			foundInputToken = true;

		if(topProtocols.size() == 0){ //The protocols don`t have sub-protocols and we could directly find the measurements of protocols
			recursiveAddingNodesToTree(bottomProtocols, protocolsTree.getName(), protocolsTree, db, foundInputToken, mode);

		}else{ //The protocols that have sub-protocols, then we recursively find sub-protocols
			recursiveAddingNodesToTree(topProtocols, protocolsTree.getName(), protocolsTree, db, foundInputToken, mode);
		}

		//After traverse through the tree, all the elements should have fallen in the right places of the tree, now create the tree view
		treeView = new JQueryTreeView<JQueryTreeViewElement>("Protocols", protocolsTree);
	}


	/**
	 * This method is used to recursively find all the sub-protocols of topmost protocols by recursively calling itself. The method returns
	 * a boolean value to indicate whether the input token has been found in its sub-nodes. 
	 * 
	 * @param nextNodes
	 * @param parentClassName
	 * @param parentTree
	 * @param db
	 * @param foundTokenInParentProtocol   found token in parent protocol but not in its sub-protocols or measurements. 
	 * @param mode
	 * @return
	 */

	public boolean recursiveAddingNodesToTree(List<String> nextNodes, String parentClassName, 
			JQueryTreeViewElement parentTree, Database db, boolean foundTokenInParentProtocol, Integer mode) {

		//Create a findInputInNextAllToken variable to keep track of whether the sub-nodes contain any input token. If neither of the children contains the input token
		//this variable should be false.
		boolean findInputTokenInNextAllNodes = false;

		//Create a variable to keep track of ONLY ONE sub-node of the current node. If the variable is false, that means there is no token found in this one branch.
		boolean findInputTokenInEachNode = true;

		//Loop through all the nodes on this level.
		for (String protocolName : nextNodes) {

			Protocol protocol = nameToProtocol.get(protocolName);

			if (!protocolName.equals(parentClassName) && protocol != null) {

				JQueryTreeViewElement childTree;

				//Resolve the issue of duplicated names in the tree. For any sub-protocols or measurements could
				//belong to multiple parent class, so it`ll throw an error if we try to create the same element twice
				//Therefore we need to give a unique identifier to the tree element but assign the same value to
				//the display name.
				if (protocolsAndMeasurementsinTree.containsKey(protocolName)) {
					if(!multipleInheritance.containsKey(protocolName)){
						multipleInheritance.put(protocolName, 1);
					}else{
						int number = multipleInheritance.get(protocolName);
						multipleInheritance.put(protocolName, ++number);
					}

					childTree = new JQueryTreeViewElement(protocolName + multipleInheritance.get(protocolName), protocolName, parentTree);

				} else {

					//The tree first time is being created.
					childTree = new JQueryTreeViewElement(protocolName, parentTree);
					childTree.setCollapsed(true);
					protocolsAndMeasurementsinTree.put(protocolName, childTree);
				}

				//find all the sub-protocols and recursively call itself
				if (protocol.getSubprotocols_Name() != null	&& protocol.getSubprotocols_Name().size() > 0) {

					findInputTokenInEachNode = recursiveAddingNodesToTree(protocol.getSubprotocols_Name(), protocol.getName(), childTree, db, foundTokenInParentProtocol, mode);
				}

				//On the last branch of the tree, we`ll find measurements and add them to the tree. 
				if (protocol.getFeatures_Name() != null	&& protocol.getFeatures_Name().size() > 0) { //error checking 

					List<String> filteredNode = new ArrayList<String> ();

					//if the input token is not null, only get the measurements that match the token
					if(InputToken != null){

						for(String eachMeasurementName : protocol.getFeatures_Name()){

							if(eachMeasurementName.toLowerCase().matches(".*" + InputToken.toLowerCase() + ".*")){
								filteredNode.add(eachMeasurementName);
							}
						}


					}else{	//if the input token is null, we are in the normal treeview mode. Get all the measurements

						filteredNode = protocol.getFeatures_Name();
					}

					//add measurements to the tree! The return boolean value indicates whether the measurements have been added
					//to the tree or not. However in mode 2 which is searching for input token only in protocols, we do not need
					//to search input token in measurements, so skip this part
					if(mode != 2){
						findInputTokenInEachNode = addingMeasurementsToTree(filteredNode, childTree, db, mode); //.. so normally it goes always this way
					}else{
						findInputTokenInEachNode = false;
					}

				}

				//If the input token is not null, the tree will be filtered, in another word, part of the tree elements
				//will be deleted according to different mode that has been selected.
				if(InputToken != null){

					//If none of the measurements of this protocol contains the input token, this protocol is not added but removed. 
					if(findInputTokenInEachNode == false){

						if(mode == 3){//filter in measurements

							//Remove all the protocols which don`t have measurements matching input token
							if(!protocolName.toLowerCase().matches(".*" + InputToken.toLowerCase() + ".*") ){
								childTree.remove();
							}

						}else if(mode == 2 || mode == 4){ //get all measurements and protocols in descendant class. 
							//Because the input token was found in current protocol!

							//Remove all protocols that don`t match the input token
							if(!foundTokenInParentProtocol && !protocolName.toLowerCase().matches(".*" + InputToken.toLowerCase() + ".*") ){

								childTree.remove();

							}else{
								//If the input token is found in the current protocol, re-add all its descendants to the tree. Because
								//its sub-nodes might not contain the input token therefore they might have been removed from the tree already.
								//Therefore need to be re-added
								findInputTokenInEachNode = recursiveAddingNodesToTree(protocol.getSubprotocols_Name(), protocol.getName(), childTree, db, true, mode);

								//This is the case where none of the measurements of this protocol match the input token, but the current protocol
								//matches input token. Therefore its measurements need to be re-added to the tree 
								if (protocol.getFeatures_Name() != null	&& protocol.getFeatures_Name().size() > 0) {
									findInputTokenInEachNode = addingMeasurementsToTree(protocol.getFeatures_Name(), childTree, db, mode); 
								}
							}
						}


					}
					//if any branch of node contains input token, we indicate to keep the parent node in the tree.
					//For example protocolA has protocolB and protocolC, protoclB contains input token whereas protocolC dose not. We`ll delete C and tells 
					//its parent node A that the token has been found
					if(findInputTokenInEachNode == true){
						findInputTokenInNextAllNodes = true;
					}
				}
			}
		}

		//If the input token is null, that means it`s not in the searching mode but in normal tree view. 
		if(InputToken == null){

			findInputTokenInNextAllNodes = true;
		}

		return findInputTokenInNextAllNodes;
	}

	/**
	 * this is adding the measurements as references in recursiveAddingNodesToTree().
	 * @param childNode
	 * @param parentTree
	 * @param db
	 */
	public boolean addingMeasurementsToTree(List<String> childNode, JQueryTreeViewElement parentTree, Database db, Integer mode) {

		//Create a variable to store the boolean value with which we could know whether we need to skip these measurements of the protocol.
		//if none of the measurements contain input token, it`s false meaning these measurements will not be shown in the tree. 
		boolean findTokenInMeasurements = true;

		List<Measurement> measurementList = new ArrayList<Measurement>();

		try {

			if(childNode.size() == 0){

				findTokenInMeasurements = false;

			}else{

				measurementList = db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.IN, childNode));
			}

			for (Measurement measurement : measurementList) {

				JQueryTreeViewElement childTree;

				if (protocolsAndMeasurementsinTree.containsKey(measurement.getName())) {

					if(!multipleInheritance.containsKey(measurement.getName())){
						multipleInheritance.put(measurement.getName(), 1);
					}else{
						int number = multipleInheritance.get(measurement.getName());
						multipleInheritance.put(measurement.getName(), ++number);
					}

					JQueryTreeViewElement previousChildTree = protocolsAndMeasurementsinTree.get(measurement.getName());

					childTree = new JQueryTreeViewElement(measurement.getName() + multipleInheritance.get(measurement.getName()), measurement.getName(), parentTree, previousChildTree.getHtmlValue());

				} else {

					List<String> categoryNames = measurement.getCategories_Name();

					String measurementDescription = measurement.getDescription();

					String measurementDataType = measurement.getDataType();

					//String htmlValue = 	"<table id = 'detailInformation'  border = 2>" +
					String htmlValue = 	"<table id = 'box-body'  >" +
							"<tr><th width=40>Item name</th><td>" + measurement.getName() +
							"</td></tr><tr><th>Category</th><td><table border=1>";


					for(String string : categoryNames){
						htmlValue += "<tr><td>";
						htmlValue += string;
						htmlValue += "</td></tr>";

					}
					htmlValue += "</table></td></tr>";

					htmlValue += "<tr><th>Description</th><td>" +
							measurementDescription + "</td></tr>";

					htmlValue += "<tr><th>Data type</th><td width=40>" +
							measurementDataType + "</td></tr>" ;
					//htmlValue = "<p>why?</p>";
					
//					Query<MolgenisFile> queryDisplayNamesFile = db.query(MolgenisFile.class);
//
//					System.out.println("Files : >>>>>>>>" + queryDisplayNamesFile);

					Query<ObservedValue> queryDisplayNames = db.query(ObservedValue.class);

					queryDisplayNames.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, measurement.getName()));

					String displayName = "";

					if(!queryDisplayNames.find().isEmpty()){

						for(ObservedValue ov : queryDisplayNames.find()){

							String featureName = ov.getFeature_Name();
							String value = ov.getValue();

							if(featureName.equals("display name_" + selectedInvestigation)){
								displayName = queryDisplayNames.find().get(0).getValue();
							}

							System.out.println("value featureName "+ value + featureName);
							if (featureName.startsWith("SOP")) {
								htmlValue += "<tr><th>" + featureName + "</th><td><a href=" + value + ">" + value + "</a></td></tr>";
							} else {
								htmlValue += "<tr><th>" + featureName + "</th><td> " + value + "</td></tr>";
							}
						}

					}
					


					htmlValue += "</table>";

					if(displayName.equals("")){
						childTree = new JQueryTreeViewElement(measurement.getName(),parentTree, htmlValue);
					}else{
						childTree = new JQueryTreeViewElement(displayName,parentTree, htmlValue);
					}

					protocolsAndMeasurementsinTree.put(measurement.getName(), childTree);

				}
			}

		} catch (DatabaseException e) {
			e.printStackTrace();
		}

		return findTokenInMeasurements;
	}

	public String getTreeView() {

		List<String> selected = new ArrayList<String>();

		for (Measurement m : shoppingCart) {
			selected.add(m.getName());
		}

		return treeView.toHtml(selected);
	}



	/**
	 * 
	 * @param db
	 * @param request
	 * @param selectedInvestigation
	 * @param dateOfDownload
	 * @throws DatabaseException
	 * @throws IOException
	 */
	private void addMeasurementsForDownload(Database db, Tuple request, String selectedInvestigation, String dateOfDownload) throws DatabaseException, IOException {

		// fill shopping cart using selected selectboxes (measurements)
		// the ID's and names of the selectboxes are the same as the measurement names,
		// so we can easily get them from the request

		this.shoppingCart.clear();

		List<Measurement> allMeasList  = db.find(Measurement.class);
		for (Measurement m : allMeasList) {
			if (request.getBool(m.getName()) != null) {
				this.shoppingCart.add(m);
			}
		}

		List<Integer> DownloadedMeasurementIds = new ArrayList<Integer>();

		if (this.shoppingCart.isEmpty())  {
			this.getModel().getMessages().add(new ScreenMessage("Your download list is empty. Please select item and proceed to download", true));
			this.setError("Your download list is empty. Please select item and proceed to download");

		} else {

			//System.out.println("DownloadedMeasurementIds >>>: " + this.shoppingCart);

			for (Measurement m : this.shoppingCart) {
				DownloadedMeasurementIds.add(m.getId());
				//System.out.println("DownloadedMeasurementIds >>>: " + m.getId());
			}

			Query<ShoppingCart> q = db.query(ShoppingCart.class);
			q.addRules(new QueryRule(ShoppingCart.USERID, Operator.EQUALS, this.getLogin().getUserName()));
			q.addRules(new QueryRule(ShoppingCart.CHECKEDOUT, Operator.EQUALS, false));
			List<ShoppingCart> result = q.find();

			if (result.isEmpty()) {
				//Add to database 
				ShoppingCart shoppingCart = new ShoppingCart();
				//shoppingCart.setMeasurements(DownloadedMeasurementIds);
				shoppingCart.setMeasurements_Id(DownloadedMeasurementIds);
				shoppingCart.setUserID(this.getLogin().getUserName());
				shoppingCart.setCheckedOut(false);
				shoppingCart.setDateOfOrder(dateOfDownload);
				shoppingCart.setApproved(false);
				db.add(shoppingCart);
				//System.out.println("Download list has been added to the DB");

			} else {
				ShoppingCart shoppingCart = result.get(0); // assuming user can have only one shopping cart that's NOT checked out
				//shoppingCart.setMeasurements(DownloadedMeasurementIds);
				shoppingCart.setMeasurements_Id(DownloadedMeasurementIds);
				db.update(shoppingCart);
				//System.out.println("Shopping cart has been updated in the DB");
			}

			HttpServletRequestTuple rt       = (HttpServletRequestTuple) request;
			HttpServletRequest httpRequest   = rt.getRequest();
			HttpServletResponse httpResponse = rt.getResponse();
			//System.out.println(">>> " + this.getParent().getName()+ "or >>>  "+ this.getSelected().getLabel());
			//String redirectURL = httpRequest.getRequestURL() + "?__target=" + this.getParent().getName() + "&select=MeasurementsDownloadForm";
			String redirectURL = httpRequest.getRequestURL() + "?__target=" + "Downloads" + "&select=MeasurementsDownloadForm";

			httpResponse.sendRedirect(redirectURL);

		}

	}

	private void deleteShoppingItem(String selected) {
		//search the item
		for (int i=0; i<this.shoppingCart.size(); i++) {
			if (this.shoppingCart.get(i).getName().equals(selected)) {
				this.shoppingCart.remove(i);
				this.getModel().getMessages().add(new ScreenMessage("The item \""+ selected + "\" has been successfully removed from your shopping cart", true));
			}
		}
	}




	public List<Measurement> getShoppingCart() {
		return shoppingCart;
	}

	public void setArrayInvestigations(List<Investigation> arrayInvestigations) {
		this.arrayInvestigations = arrayInvestigations;
	}

	public List<Investigation> getArrayInvestigations() {
		return arrayInvestigations;
	}

	public void setSelectedInv(boolean isSelectedInv) {
		this.isSelectedInv = isSelectedInv;
	}

	public boolean isSelectedInv() {
		return isSelectedInv;
	}

	public String getSelectedInvestigation() {
		return selectedInvestigation;
	}

	public void setSelectedInvestigation(String selectedInvestigation) {
		this.selectedInvestigation = selectedInvestigation;
	}

	public void setArraySearchFields(List<String> arraySearchFields) {
		this.arraySearchFields = arraySearchFields;
	}

	public List<String> getArraySearchFields() {
		return arraySearchFields;
	}

	public void setInputToken(String inputToken) {
		InputToken = inputToken;
	}

	public String getInputToken() {
		return InputToken;
	}

	public void setSelectedField(String selectedField) {
		this.selectedField = selectedField;
	}

	public String getSelectedField() {
		return selectedField;
	}

	private String getComparison() {
		return comparison;
	}

	public void setSearchingInvestigation(String searchingInvestigation) {
		this.searchingInvestigation = searchingInvestigation;
	}

	public String getSearchingInvestigation() {
		return searchingInvestigation;
	}
}
