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

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.Entity;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;

public class catalogueTreePlugin extends PluginModel<Entity> {

	private static final long serialVersionUID = -6143910771849972946L;
	private JQueryTreeViewMeasurement<JQueryTreeViewElementObject> treeView = null;
	private HashMap<String, Protocol> nameToProtocol;
	private HashMap<String, JQueryTreeViewElementObject> labelToTree;
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

	public void handleRequest(Database db, Tuple request) {

		try {
			if ("chooseInvestigation".equals(request.getAction())) {
				selectedInvestigation = request.getString("investigation");
				arrayInvestigations.clear();
			} else if ("DownloadMeasurements".equals(request.getAction())) {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
				Date dat = new Date();
				String dateOfDownload = dateFormat.format(dat);
				System.out.println("selected investigaton >>>> " + selectedInvestigation);
				this.addMeasurements(db, request, selectedInvestigation, dateOfDownload);
				RetrieveProtocols(db,1); 
			} else if (request.getAction().startsWith("DeleteMeasurement")) {

				String measurementName  =  request.getString("measurementName"); //TODO :  this is not working
				measurementName = request.getAction().substring("DeleteMeasurement".length()+2+"measurementName".length(), request.getAction().length());
				this.deleteShoppingItem(measurementName);
			} if (request.getAction().startsWith("SearchCatalogueTree")) {
				this.setInputToken(request.getString("InputToken").trim());
				this.setSearchingInvestigation(request.getString("searchingInvestigation").trim());
				//this.setComparison(request.getString("comparison").trim());
				this.setComparison("contains");
				this.setSelectedField(request.getString("selectedField"));

				System.out.println("Input token: >>>>>>"+ this.getInputToken() + ">>> selectedField>>"+ selectedField + "comparison >>>" + this.getComparison()+ "searchingInvestigation>>"+ this.getSearchingInvestigation());
				if (this.getSelectedField().equals("Protocols")) 
					RetrieveProtocols(db,2); 
				else if (this.getSelectedField().equals("Measurements")) {}
				else if (this.getSelectedField().equals("Details")) {}

			}

		} catch (Exception e) {
			e.printStackTrace();
			this.setError("There was a problem handling your Download: " + e.getMessage());
		}

	}



	private void addMeasurements(Database db, Tuple request, String selectedInvestigation, String dateOfDownload) throws DatabaseException, IOException {

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


	/**
	 * This function is recursively nodes in the tree except form the last measurement . 
	 * The last measurement is added by addingLastMeasurementToTree() because we add two different class : Protocol & Measurement, 
	 * so basically we need to recursivley traverse all the Protocols and in the end we have one measurement.  
	 * @param parentNode
	 * @param parentTree
	 * @param db
	 */
	public void recursiveAddingNodesToTree(List<String> nextNodes, String parentClassName,
			JQueryTreeViewElementObject parentTree, Database db) {

		for (String protocolName : nextNodes) {

			Protocol protocol = nameToProtocol.get(protocolName);

			if (!protocolName.equals(parentClassName) && protocol != null) {

				JQueryTreeViewElementObject childTree;

				if (labelToTree.containsKey(protocolName)) {

					if(!multipleInheritance.containsKey(protocolName)){
						multipleInheritance.put(protocolName, 1);
					}else{
						int number = multipleInheritance.get(protocolName);
						multipleInheritance.put(protocolName, number++);
					}

					JQueryTreeViewElementObject previousChildTree = labelToTree.get(protocolName);

					childTree = new JQueryTreeViewElementObject(protocolName + multipleInheritance.get(protocolName),protocolName, parentTree);

				} else {
					childTree = new JQueryTreeViewElementObject(protocolName, parentTree);
					childTree.setCollapsed(true);
					labelToTree.put(protocolName, childTree);
				}

				if (protocol.getSubprotocols_Name() != null	&& protocol.getSubprotocols_Name().size() > 0) {
					recursiveAddingNodesToTree(protocol.getSubprotocols_Name(), protocol.getName(), childTree, db);
				}
				if (protocol.getFeatures_Name() != null	&& protocol.getFeatures_Name().size() > 0) {
					addingLastMeasurementToTree(protocol.getFeatures_Name(), childTree, db);
				}
			}
		}
	}

	/**
	 * this is adding the last measurement as references in recursiveAddingNodesToTree().
	 * @param childNode
	 * @param parentTree
	 * @param db
	 */
	public void addingLastMeasurementToTree(List<String> childNode, JQueryTreeViewElementObject parentTree, Database db) {

		try {

			List<Measurement> measurementList = db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.IN, childNode));

			for (Measurement measurement : measurementList) {

				JQueryTreeViewElementObject childTree;

				if (labelToTree.containsKey(measurement.getName())) {

					if(!multipleInheritance.containsKey(measurement.getName())){
						multipleInheritance.put(measurement.getName(), 1);
					}else{
						int number = multipleInheritance.get(measurement.getName());
						multipleInheritance.put(measurement.getName(), number++);
					}

					JQueryTreeViewElementObject previousChildTree = labelToTree.get(measurement.getName());

					childTree = new JQueryTreeViewElementObject(measurement.getName() + multipleInheritance.get(measurement.getName()), measurement.getName(), parentTree, previousChildTree.getHtmlValue());

				} else {

					List<String> categoryNames = measurement.getCategories_Name();

					String measurementDescription = measurement.getDescription();

					String measurementDataType = measurement.getDataType();

					String htmlValue = 	"<table id = 'detailInformation'  border = 2>" +
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

							String link =" http://www.google.com";
							htmlValue += "<tr><th>" + featureName + "</th><td><a href=" + link + ">" +
									value + "</a></td></tr>";
						}

					}

					htmlValue += "</table>";

					if(displayName.equals("")){
						childTree = new JQueryTreeViewElementObject(measurement.getName(),parentTree, htmlValue);
					}else{
						childTree = new JQueryTreeViewElementObject(displayName,parentTree, htmlValue);
					}

					labelToTree.put(measurement.getName(), childTree);

					List<Category> categoryIds = new ArrayList<Category>();
					categoryIds = db.find(Category.class, new QueryRule(Category.NAME, Operator.EQUALS, measurement.getCategories_Name()));
					//System.out.println("category ids for "+ measurement.getName()+ ">>>>>>>>>>>>>>>>" +categoryIds);

				}
			}
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getViewName() {
		return "plugins_catalogueTree_catalogueTreePlugin";
	}

	@Override
	public String getViewTemplate() {
		return "plugins/catalogueTree/catalogueTreePlugin.ftl";
	}

	//				this.SearchTree(db, this.getInputToken(), this.getSearchingInvestigation(), this.getComparison());

	public void RetrieveProtocols(Database db, Integer mode) {
		List<String> topProtocols = new ArrayList<String>();
		List<String> bottomProtocols = new ArrayList<String>();
		List<String> middleProtocols = new ArrayList<String>();
		labelToTree = new HashMap<String, JQueryTreeViewElementObject>();
		nameToProtocol = new HashMap<String, Protocol>();
		
		try {

			Query<Protocol> q = db.query(Protocol.class);


			if (mode ==1) { //reload is calling
				q.addRules(new QueryRule(Protocol.INVESTIGATION_NAME, Operator.EQUALS, selectedInvestigation));
			} else if (mode==2) {
				q.addRules(new QueryRule(Protocol.INVESTIGATION_NAME, Operator.EQUALS, searchingInvestigation));
				q.addRules(new QueryRule(Protocol.NAME, Operator.LIKE, InputToken));
			}
			for (Protocol p : q.find()) {
				setSelectedInv(true);
				List<String> subNames = p.getSubprotocols_Name();
				System.out.println(">>>"+ p);

				if (!nameToProtocol.containsKey(p.getName())) {
					nameToProtocol.put(p.getName(), p);
				}

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


			}

		} catch (DatabaseException e) {
			e.printStackTrace();
		}

		middleProtocols.removeAll(bottomProtocols);
		topProtocols.removeAll(middleProtocols);

		JQueryTreeViewElementObject protocolsTree = new JQueryTreeViewElementObject(
				"Protocols", null);

		if(topProtocols.size() == 0){
			recursiveAddingNodesToTree(bottomProtocols,"Protocols", protocolsTree, db);

		}else{
			recursiveAddingNodesToTree(topProtocols, "Protocols", protocolsTree, db);
		}

		treeView = new JQueryTreeViewMeasurement<JQueryTreeViewElementObject>(
				"Protocols", protocolsTree);

	}


	@Override
	public void reload(Database db) {

		arraySearchFields.clear();
		this.searchingInvestigation = null;
		//this.selectedInvestigation = null;

		arraySearchFields.add("Protocols");
		arraySearchFields.add("Measurements");
		arraySearchFields.add("Details");

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

		if (this.getInputToken() == null) RetrieveProtocols(db,1); //mode 1: gets all protocols without filters!

		this.setInputToken(null);

	}

	public String getTreeView() {
		List<String> selected = new ArrayList<String>();
		for (Measurement m : shoppingCart) {
			selected.add(m.getName());
		}
		return treeView.toHtml(selected);
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

	private void setComparison(String comparison) {
		this.comparison = comparison;

	}

	public void setSearchingInvestigation(String searchingInvestigation) {
		this.searchingInvestigation = searchingInvestigation;
	}

	public String getSearchingInvestigation() {
		return searchingInvestigation;
	}
}
