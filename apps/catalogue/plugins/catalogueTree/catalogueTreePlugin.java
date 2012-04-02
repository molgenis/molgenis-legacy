package plugins.catalogueTree;

import gcc.catalogue.ShoppingCart;

import java.awt.Component;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.text.DateFormatter;

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
	private String InputToken = null;
	private String comparison = null;
	private String selectedField = null;

	private boolean isSelectedInv = false;
	private List<String> arraySearchFields = new ArrayList<String>();

	private static int SEARCHINGPROTOCOL = 2;

	private static int SEARCHINGMEASUREMENT = 3;

	private static int SEARCHINGALL = 4;

	private static int SEARCHINGDETAIL = 5;

	/** Multiple inheritance: some measurements might have multiple parents therefore it
	 *  will complain about the branch already exists when constructing the tree, cheating by
	 *  changing the name of the branch but keeping display name the same
	 */
	 
	private HashMap<String, Integer> multipleInheritance = new HashMap<String, Integer>();

	public catalogueTreePlugin(String name, ScreenController<?> parent) {
		super(name, parent);
	}

	public String getCustomHtmlHeaders() {
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

			if ("chooseInvestigation".equals(request.getAction())) {
				selectedInvestigation = request.getString("investigation");
				this.setSelectedInvestigation(selectedInvestigation);
				System.out.println("The selected investigation is : "
						+ selectedInvestigation);
				arrayInvestigations.clear();

			} else if ("DownloadMeasurements".equals(request.getAction())) {

				// a jframe here isn't strictly necessary, but it makes the example a little more real
			    JFrame frame = new JFrame("Save Selection");
			    String selectionName = JOptionPane.showInputDialog(frame, "Please insert a name for your selection.");

			    // if they press Cancel, 'name' will be null
			    System.out.printf("The selection's name is '%s'.\n", selectionName);
			    

				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
				Date dat = new Date();
				String dateOfDownload = dateFormat.format(dat);
				System.out.println("selected investigaton >>>> "+  selectedInvestigation);
				System.out.println("request >>" + request);
				this.addMeasurementsForDownload(db, request, selectedInvestigation, dateOfDownload, selectionName);

				
			} else if (request.getAction().startsWith("DeleteMeasurement")) {

				String measurementName = request.getString("measurementName"); 
																				
																				
				measurementName = request.getAction().substring( "DeleteMeasurement".length() + 2+ "measurementName".length(),	request.getAction().length());
				this.deleteShoppingItem(measurementName);

			}
			if (request.getAction().startsWith("SearchCatalogueTree")) {
				if (request.getString("InputToken") != null) {

					this.setInputToken(request.getString("InputToken").trim());

					System.out.println("The request string : " + request);
					
					this.setSelectedField(request.getString("selectedField"));

					System.out.println("Input token: >>>>>>"
							+ this.getInputToken() + ">>> selectedField >>"
							+ selectedField + "comparison >>>"
							+ this.getComparison());
					if (this.getSelectedField().equals("Protocols"))
						RetrieveProtocols(db, SEARCHINGPROTOCOL);
					// Search "Any field" ==> All fields LIKE input token
					if (this.getSelectedField().equals("Measurements"))
						RetrieveProtocols(db, SEARCHINGMEASUREMENT);

					if (this.getSelectedField().equals("All fields"))
						RetrieveProtocols(db, SEARCHINGALL);

					if (this.getSelectedField().equals("Details"))
						RetrieveProtocols(db, SEARCHINGDETAIL);

				} else {
					this.getModel().getMessages().add(new ScreenMessage("Empty search string", true));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			this.setError("There was a problem handling your Download: " + e.getMessage());
		}

	}

	@Override
	public void reload(Database db) {

		// default set selected investigation to first
		if (this.getSelectedInvestigation() == null) {
			try {
				List<Investigation> inv = db.query(Investigation.class)
						.limit(1).find();
				if (inv.size() > 0)
					this.setSelectedInvestigation(inv.get(0).getName());
			} catch (DatabaseException e1) {
				e1.printStackTrace();
			}
		}

		arraySearchFields.clear();
		// this.searchingInvestigation = null;
		// this.selectedInvestigation = null;

		arraySearchFields.add("All fields");
		arraySearchFields.add("Protocols");
		arraySearchFields.add("Measurements");
		arraySearchFields.add("Details");

		// Query<ShoppingCart> q = db.query(ShoppingCart.class);
		// q.addRules(new QueryRule(ShoppingCart.USERID, Operator.EQUALS, this
		// .getLogin().getUserName()));
		// q.addRules(new QueryRule(ShoppingCart.CHECKEDOUT, Operator.EQUALS,
		// false));
		try {
			// List<ShoppingCart> result = q.find();
			// shoppingCart.clear();
			// for (ShoppingCart cart : result) {
			// shoppingCart.addAll(cart.getMeasurements(db));
			// }

			this.arrayInvestigations.clear();

			for (Investigation i : db.find(Investigation.class)) {
				this.arrayInvestigations.add(i);
			}
		} catch (DatabaseException e) {
			e.printStackTrace();
		}

		if (this.getInputToken() == null)
			RetrieveProtocols(db, 1); // mode 1: gets all protocols without filters!

		this.setInputToken(null);

	}

	/**
	 * This method is used to retrieve all the protocols from the database.
	 * Protocol is a bunch of measurements in Molgenis system, some of which
	 * could have sub-protocols. Therefore three different kinds of protocols
	 * are defined in the method to find the topmost protocols (the ancestors of
	 * all the protocols), which are stored in variable topProtocols. The
	 * topmost protocols are the starting point of the tree. The tree extends to
	 * the next level down with sub-protocols. Until the last level of tree
	 * (last branch in the tree), the measurements are stored in there. The
	 * topmost protocols are passed to the another method called
	 * resursiveAddingNodesToTree, which could recursively add new branches to
	 * the tree.
	 * 
	 * @param db
	 * @param mode
	 */
	public void RetrieveProtocols(Database db, Integer mode) {

		List<String> topProtocols = new ArrayList<String>();
		List<String> bottomProtocols = new ArrayList<String>();
		List<String> middleProtocols = new ArrayList<String>();
		protocolsAndMeasurementsinTree = new HashMap<String, JQueryTreeViewElement>();

		// measurementsInTree = new HashMap<String, JQueryTreeViewElement>();
		// protocolsInTree = new HashMap<String, JQueryTreeViewElement>();

		nameToProtocol = new HashMap<String, Protocol>();

		try {

			Query<Protocol> q = db.query(Protocol.class);

			q.addRules(new QueryRule(Protocol.INVESTIGATION_NAME,
					Operator.EQUALS, this.selectedInvestigation));

			// Iterate through all the found protocols
			for (Protocol p : q.find()) {

				setSelectedInv(true);
				List<String> subNames = p.getSubprotocols_Name();

				// keep a record of each protocol in a hashmap. Later on we
				// could reference to the Protocol by name
				if (!nameToProtocol.containsKey(p.getName())) {
					nameToProtocol.put(p.getName(), p);
				}

				/**
				 *  Algorithm to find the topmost protocols. There are three kind
				 *   of protocols needed.
				 *   1. The protocols that are parents of other protocols
				 *   2. The protocols that are children of some other protocols
				 *   and at the same time are parents of some other protocols
				 *   3. The protocols that are only children of other protocols
				 *   Therefore we could do protocol2 =
				 *   protocol2.removeAll(protocol3) ----> parent protocols but not topmost
				 *   we then do protocol1 = protocol1.removeAll(protocol2) 
				 *   topmost parent protocols
				 */
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

		// Create a starting point of the tree! The root of the tree!
		JQueryTreeViewElement protocolsTree = new JQueryTreeViewElement(
				"Study: " + this.getSelectedInvestigation(), null);

		// Variable indicating whether the input token has been found.
		boolean foundInputToken = false;

		if (topProtocols.size() == 0) { // The protocols don`t have
										// sub-protocols and we could directly
										// find the measurements of protocols
			recursiveAddingNodesToTree(bottomProtocols,
					protocolsTree.getName(), protocolsTree, db,
					foundInputToken, mode);

		} else { // The protocols that have sub-protocols, then we recursively
					// find sub-protocols
			recursiveAddingNodesToTree(topProtocols, protocolsTree.getName(),
					protocolsTree, db, foundInputToken, mode);
		}

		System.out.println(protocolsTree.getName());
		System.out.println(">>>Protocols tree: "+ protocolsTree + "tree elements: "+ protocolsTree.getTreeElements().containsKey("Questionnaire"));
		if (!protocolsTree.getTreeElements().containsKey("Questionnaire") && 
				!protocolsTree.getTreeElements().containsKey("Measurement") &&
				!protocolsTree.getTreeElements().containsKey("Sample")) { 
			//Search result is empty or tree is empty 
			this.getModel().getMessages().add(new ScreenMessage("There are no results to show. Please, redifine your search or import some data.",true));
			this.setError("There are no results to show. Please, redifine your search or import some data.");
		} else {
			// After traverse through the tree, all the elements should have fallen
			// in the right places of the tree, now create the tree view
			treeView = new JQueryTreeView<JQueryTreeViewElement>("Protocols", protocolsTree);
		}
	}

	/**
	 * This method is used to recursively find all the sub-protocols of topmost
	 * protocols by recursively calling itself. The method returns a boolean
	 * value to indicate whether the input token has been found in its
	 * sub-nodes.
	 * 
	 * @param nextNodes
	 * @param parentClassName
	 * @param parentTree
	 * @param db
	 * @param foundTokenInParentProtocol
	 *            found token in parent protocol but not in its sub-protocols or
	 *            measurements.
	 * @param mode
	 * @return
	 */

	public boolean recursiveAddingNodesToTree(List<String> nextNodes,
			String parentClassName, JQueryTreeViewElement parentTree,
			Database db, boolean foundTokenInParentProtocol, Integer mode) {

		// Create a findInputInNextAllToken variable to keep track of whether
		// the sub-nodes contain any input token. If neither of the children
		// contains the input token
		// this variable should be false.
		boolean findInputTokenInNextAllNodes = false;

		// Create a variable to keep track of ONLY ONE sub-node of the current
		// node. If the variable is false, that means there is no token found in
		// this one branch.

		// Loop through all the nodes on this level.
		for (String protocolName : nextNodes) {

			boolean findInputTokenInEachNode = false;

			Protocol protocol = nameToProtocol.get(protocolName);

			if (!protocolName.equals(parentClassName) && protocol != null) {

				JQueryTreeViewElement childTree = null;

				/**
				 *  Resolve the issue of duplicated names in the tree. For any
				 *  sub-protocols or measurements could	 belong to multiple parent class, so it`ll throw an error if
				 *  we try to create the same element twice
				 *  Therefore we need to give a unique identifier to the tree element but assign the same value to the display name.
				 */
				
				if (protocolsAndMeasurementsinTree.containsKey(protocolName)) {
					if (!multipleInheritance.containsKey(protocolName)) {
						multipleInheritance.put(protocolName, 1);
					} else {
						int number = multipleInheritance.get(protocolName);
						multipleInheritance.put(protocolName, ++number);
					}

					childTree = new JQueryTreeViewElement(protocolName
							+ multipleInheritance.get(protocolName),
							protocolName, parentTree);

				} else {

					// The tree first time is being created.
					childTree = new JQueryTreeViewElement(protocolName,
							parentTree);
					childTree.setCollapsed(true);
					protocolsAndMeasurementsinTree.put(protocolName, childTree);
				}

				// find all the sub-protocols and recursively call itself
				if (protocol.getSubprotocols_Name() != null
						&& protocol.getSubprotocols_Name().size() > 0) {

					findInputTokenInEachNode = recursiveAddingNodesToTree(
							protocol.getSubprotocols_Name(),
							protocol.getName(), childTree, db,
							foundTokenInParentProtocol, mode);
				}

				// On the last branch of the tree, we`ll find measurements and
				// add them to the tree.
				if (protocol.getFeatures_Name() != null
						&& protocol.getFeatures_Name().size() > 0) { // error
																		// checking

					if (mode != SEARCHINGPROTOCOL) {
						findInputTokenInEachNode = addingMeasurementsToTree(
								protocol.getFeatures_Name(), childTree, db,
								false, mode); // .. so normally it goes always
												// this way
					}
				}

				// If the input token is not null, the tree will be filtered, in
				// another word, part of the tree elements
				// will be deleted according to different mode that has been
				// selected.
				if (InputToken != null) {

					// If none of the child nodes, such as none of the
					// measurements of this protocol, contains the input token,
					// this protocol is not added but removed.
					if (findInputTokenInEachNode == false) {

						if (mode == SEARCHINGMEASUREMENT || mode == SEARCHINGDETAIL) {// filter in measurements

							childTree.remove();

						} else if (mode == SEARCHINGPROTOCOL || mode == SEARCHINGALL) { // get all
															// measurements and
															// protocols in
															// descendant class.
							// Because the input token was found in current
							// protocol!

							// Remove all protocols that don`t match the input
							// token
							if (!foundTokenInParentProtocol
									&& !protocolName.toLowerCase().matches(
											".*" + InputToken.toLowerCase()
													+ ".*")) {

								childTree.remove();

							} else {
								// If the input token is found in the current
								// protocol, re-add all its descendants to the
								// tree. Because
								// its sub-nodes might not contain the input
								// token therefore they might have been removed
								// from the tree already.
								// Therefore need to be re-added
								if (protocol.getSubprotocols_Name().size() > 0) {
									findInputTokenInEachNode = recursiveAddingNodesToTree(
											protocol.getSubprotocols_Name(),
											protocol.getName(), childTree, db,
											true, mode);
									findInputTokenInEachNode = true;
								}
								// This is the case where none of the
								// measurements of this protocol match the input
								// token, but the current protocol
								// matches input token. Therefore its
								// measurements need to be re-added to the tree
								if (protocol.getFeatures_Name() != null
										&& protocol.getFeatures_Name().size() > 0) {
									findInputTokenInEachNode = addingMeasurementsToTree(
											protocol.getFeatures_Name(),
											childTree, db, true, mode);
									findInputTokenInEachNode = true;
								}
							}
						}
					}
					// if any branch of node contains input token, we indicate
					// to keep the parent node in the tree.
					// For example protocolA has protocolB and protocolC,
					// protoclB contains input token whereas protocolC dose not.
					// We`ll delete C and tells
					// its parent node A that the token has been found
					if (findInputTokenInEachNode == true) {
						findInputTokenInNextAllNodes = true;
					}
				}
			}
		}

		// If the input token is null, that means it`s not in the searching mode
		// but in normal tree view.
		if (InputToken == null) {

			findInputTokenInNextAllNodes = true;
		}

		return findInputTokenInNextAllNodes;
	}

	/**
	 * this is adding the measurements as references in
	 * recursiveAddingNodesToTree().
	 * 
	 * @param childNode
	 * @param parentTree
	 * @param db
	 */
	public boolean addingMeasurementsToTree(List<String> childNode,
			JQueryTreeViewElement parentTree, Database db,
			boolean foundInParent, Integer mode) {

		// Create a variable to store the boolean value with which we could know
		// whether we need to skip these measurements of the protocol.
		// if none of the measurements contain input token, it`s false meaning
		// these measurements will not be shown in the tree.
		boolean findTokenInMeasurements = false;

		// indicate with the input token has been found in detail information in
		// the measurement.
		boolean findTokenInDetailInformation = false;

		// This variables store the measurements that conform to the
		// requirements by the mode that has been selected.
		// For example, it only contains the measurement where the input token
		// has been found under mode searchingMeasurement
		List<String> filteredNode = new ArrayList<String>();

		// If the input token is available, we need to check which mode it is
		// and decide what we do with it here
		if (InputToken != null) {

			// In mode of searching for measurements, we check if the name of
			// measurements contain the input token
			// If the token is not in the name, the measurement is removed from
			// list.
			if (mode == SEARCHINGMEASUREMENT) {

				for (String eachMeasurementName : childNode) {

					if (eachMeasurementName.toLowerCase().matches(
							".*" + InputToken.toLowerCase() + ".*")) {
						filteredNode.add(eachMeasurementName);
						findTokenInMeasurements = true;
					}
				}

			} else {
				// In mode of searching for all fields, details, we need to loop
				// through all the measurements, therefore
				// we do not care whether the measurement name contains the
				// input token or not.
				filteredNode = childNode;
			}

		} else {
			// Normal mode when the input token is not available
			filteredNode = childNode;
		}

		try {

			List<Measurement> measurementList = new ArrayList<Measurement>();

			if (filteredNode.size() > 0)
				measurementList = db.find(Measurement.class, new QueryRule(
						Measurement.NAME, Operator.IN, filteredNode));

			for (Measurement measurement : measurementList) {

				// reset the the variable to false
				findTokenInDetailInformation = false;

				JQueryTreeViewElement childTree = null;

				// Query the display name! For some measurements, the labels
				// were stored in the observedValue with feature_name
				// "display name". If the display name is not available, we`ll
				// use the measurement name as label
				String displayName = "";

				Query<ObservedValue> queryDisplayNames = db
						.query(ObservedValue.class);

				queryDisplayNames.addRules(new QueryRule(
						ObservedValue.TARGET_NAME, Operator.EQUALS, measurement
								.getName()));

				queryDisplayNames.addRules(new QueryRule(
						ObservedValue.FEATURE_NAME, Operator.LIKE,
						"display name"));

				if (queryDisplayNames.find().size() > 0) {

					displayName = queryDisplayNames.find().get(0).getValue();
				} else {
					displayName = measurement.getName();
				}

				// Query the all the detail information about this measurement,
				// in molgenis terminology, the detail information
				// are all the observedValue and some of the fields from the
				// measurement
				String htmlValue = null;

				htmlValue = htmlTableForTreeInformation(db, measurement);

				// Check if the tree has already had the treeElement with the
				// same name cos the name can not be duplicated in
				// jquery tree here. Therefore if the element already existed, a
				// suffix will be added at the end of string to
				// make the name unique
				if (protocolsAndMeasurementsinTree.containsKey(displayName)) {

					if (!multipleInheritance.containsKey(displayName)) {
						multipleInheritance.put(displayName, 1);
					} else {
						int number = multipleInheritance.get(displayName);
						multipleInheritance.put(displayName, ++number);
					}

					JQueryTreeViewElement previousChildTree = protocolsAndMeasurementsinTree
							.get(displayName);

					childTree = new JQueryTreeViewElement(displayName
							+ multipleInheritance.get(displayName),
							displayName, parentTree,
							previousChildTree.getHtmlValue());

					childTree.setHtmlValue(htmlValue);

				} else {

					childTree = new JQueryTreeViewElement(displayName,
							parentTree, htmlValue);

					protocolsAndMeasurementsinTree.put(displayName, childTree);
				}

				// Searching for the details. Since htmlValue has all the
				// information about this measurement,
				// therefore we search for the input tokenin this variable
				if (mode == SEARCHINGDETAIL) {

					if (htmlValue.toLowerCase().matches(
							".*" + InputToken.toLowerCase() + ".*")) {
						findTokenInDetailInformation = true;
					} else {
						findTokenInDetailInformation = false;
						childTree.remove();
					}

					if (findTokenInDetailInformation == true) {
						findTokenInMeasurements = true;
					}
				}

				// Searching for the details and measurement name. If either
				// name of measurement or detail information
				// of measurement contains the input token, this is a matching!
				if (mode == SEARCHINGALL) {

					if (htmlValue.toLowerCase().matches(
							".*" + InputToken.toLowerCase() + ".*")) {
						findTokenInDetailInformation = true;
					} else {

						if (measurement
								.getName()
								.toLowerCase()
								.matches(".*" + InputToken.toLowerCase() + ".*")) {
							findTokenInDetailInformation = true;
						} else {

							if (foundInParent != true) {
								findTokenInDetailInformation = false;
								childTree.remove();
							} else {
								findTokenInDetailInformation = true;
							}
						}
					}

					if (findTokenInDetailInformation == true) {
						findTokenInMeasurements = true;
					}
				}
			}

		} catch (DatabaseException e) {
			e.printStackTrace();
		}

		// Return this round searching result back to the parent node
		return findTokenInMeasurements;
	}

	/**
	 * This method is used to create a html table populated with all the
	 * information about one specific measurement
	 * 
	 * @param db
	 * @param measurement
	 * @return
	 * @throws DatabaseException
	 */
	public String htmlTableForTreeInformation(Database db,
			Measurement measurement) throws DatabaseException {

		List<String> categoryNames = measurement.getCategories_Name();

		String measurementDescription = measurement.getDescription();

		String measurementDataType = measurement.getDataType();

		// String htmlValue = "<table id = 'detailInformation'  border = 2>" +
		String htmlValue = "<table style='border-spacing: 2px; width: 100%;' class='MeasurementDetails' id = 'measurementDetail"
				+ measurement.getId() + "'  >";
		htmlValue += "<tr><td class='box-body-label'>Item name:</th><td>"
				+ measurement.getName() + "</td></tr>";

		if (categoryNames.size() > 0) {
			htmlValue += "<tr><td  class='box-body-label'>Permitted values:</td><td><table>";

			for (String string : categoryNames) {
				htmlValue += "<tr><td>";
				htmlValue += string;
				htmlValue += "</td></tr>";

			}
			htmlValue += "</table></td></tr>";
		}

		htmlValue += "<tr><td class='box-body-label'>Description:</td><td>"
				+ (measurementDescription == null ? "not provided" : measurementDescription) + "</td></tr>";

		htmlValue += "<tr><td class='box-body-label'>Data type:</th><td>"
				+ measurementDataType + "</td></tr>";

		Query<ObservedValue> queryDetailInformation = db
				.query(ObservedValue.class);

		queryDetailInformation.addRules(new QueryRule(
				ObservedValue.TARGET_NAME, Operator.EQUALS, measurement
						.getName()));

		if (!queryDetailInformation.find().isEmpty()) {

			for (ObservedValue ov : queryDetailInformation.find()) {

				String featureName = ov.getFeature_Name();
				String value = ov.getValue();

				if (featureName.startsWith("SOP")) {
					htmlValue += "<tr><td class='box-body-label'>" + featureName + "</td><td><a href="
							+ value + ">" + value + "</a></td></tr>";
				} else {

					if (featureName.startsWith("display name")) {
						featureName = "display name";
					}

					htmlValue += "<tr><td class='box-body-label'>" + featureName + "</td><td> "
							+ value + "</td></tr>";
				}
			}
		}

		htmlValue += "</table>";

		return htmlValue;
	}

	public String getTreeView() {

		List<String> selected = new ArrayList<String>();

		// don't select, is confusing...
		// for (Measurement m : shoppingCart) {
		// selected.add(m.getName());
		// }

		return treeView.toHtml(selected);
	}

	/**
	 * 
	 * @param db
	 * @param request
	 * @param selectedInvestigation
	 * @param dateOfDownload
	 * @param selectionName 
	 * @throws DatabaseException
	 * @throws IOException
	 */
	private void addMeasurementsForDownload(Database db, Tuple request, String selectedInvestigation, String dateOfDownload, String selectionName) throws DatabaseException, IOException {

		// fill shopping cart using selected selectboxes (measurements)
		// the ID's and names of the selectboxes are the same as the measurement
		// names,
		// so we can easily get them from the request

		this.shoppingCart.clear();

		List<Measurement> allMeasList = db.find(Measurement.class);
		for (Measurement m : allMeasList) {
			if (request.getBool(m.getName()) != null) {
				this.shoppingCart.add(m);
			}
		}

		List<Integer> DownloadedMeasurementIds = new ArrayList<Integer>();

		if (this.shoppingCart.isEmpty()) {
			this.getModel().getMessages().add(new ScreenMessage("Your download list is empty. Please select item and proceed to download",true));
			this.setError("Your download list is empty. Please select item and proceed to download");

		} else {

			// System.out.println("DownloadedMeasurementIds >>>: " +
			// this.shoppingCart);

			for (Measurement m : this.shoppingCart) {
				DownloadedMeasurementIds.add(m.getId());
				// System.out.println("DownloadedMeasurementIds >>>: " +
				// m.getId());
			}

			// REWRITE SO USERS CAN HAVE MULTIPLE SHOPPINGCARTS-- there are no shopping carts any more . 

			// Query<ShoppingCart> q = db.query(ShoppingCart.class);
			// q.addRules(new QueryRule(ShoppingCart.USERID, Operator.EQUALS,
			// this
			// .getLogin().getUserName()));
			// q.addRules(new QueryRule(ShoppingCart.CHECKEDOUT,
			// Operator.EQUALS, false));
			List<ShoppingCart> result = new ArrayList<ShoppingCart>();// q.find();

			if (result.isEmpty()) {
				//String shoppingCartName = this.getLogin().getUserName() + "_" + System.currentTimeMillis();

				// Add to database
				ShoppingCart shoppingCart = new ShoppingCart();
				String shoppingCartName = selectionName;
				shoppingCart.setName(shoppingCartName );
				// shoppingCart.setMeasurements(DownloadedMeasurementIds);
				shoppingCart.setMeasurements_Id(DownloadedMeasurementIds);
				shoppingCart.setUserID(this.getLogin().getUserName());
				// shoppingCart.setCheckedOut(false);
				// shoppingCart.setDateOfOrder(dateOfDownload);
				shoppingCart.setApproved(false);
				db.add(shoppingCart);
				// System.out.println("Download list has been added to the DB");

				this.setSuccess("Selection saved to 'My Selections' under name "
						+ shoppingCartName);

			} else {
				ShoppingCart shoppingCart = result.get(0); // assuming user can
															// have only one
															// shopping cart
															// that's NOT
															// checked out
				// shoppingCart.setMeasurements(DownloadedMeasurementIds);
				shoppingCart.setMeasurements_Id(DownloadedMeasurementIds);
				db.update(shoppingCart);

				this.setSuccess("Selection saved to 'My Selections' under name "
						+ shoppingCart.getName());
				// System.out.println("Shopping cart has been updated in the DB");
			}

			HttpServletRequestTuple rt = (HttpServletRequestTuple) request;
			HttpServletRequest httpRequest = rt.getRequest();
			HttpServletResponse httpResponse = rt.getResponse();
			// System.out.println(">>> " + this.getParent().getName()+
			// "or >>>  "+ this.getSelected().getLabel());
			// String redirectURL = httpRequest.getRequestURL() + "?__target=" +
			// this.getParent().getName() + "&select=MeasurementsDownloadForm";
			String redirectURL = httpRequest.getRequestURL() + "?__target="
					+ "Downloads" + "&select=MeasurementsDownloadForm";

			httpResponse.sendRedirect(redirectURL);

		}

	}

	private void deleteShoppingItem(String selected) {
		// search the item
		for (int i = 0; i < this.shoppingCart.size(); i++) {
			if (this.shoppingCart.get(i).getName().equals(selected)) {
				this.shoppingCart.remove(i);
				this.getModel()
						.getMessages()
						.add(new ScreenMessage(
								"The item \""
										+ selected
										+ "\" has been successfully removed from your shopping cart",
								true));
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
	
//	@Override
//	public boolean isVisible()
//	{
//		// always visible
//		return true;
//	}
//	
	@Override
	public boolean isVisible()
	{
		//you can use this to hide this plugin, e.g. based on user rights.
		//e.g.
		//if(!this.getLogin().hasEditPermission(myEntity)) return false;
		if (!this.getLogin().isAuthenticated()) {
			return false;
		}
		return true;
	}


}
