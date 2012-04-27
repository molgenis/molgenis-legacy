package plugins.harmonizationPlugin;

import gcc.catalogue.MappingMeasurement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import org.molgenis.util.Tuple;

public class harmonizationPlugin extends PluginModel<Entity> {

	private static final long serialVersionUID = -6143910771849972946L;
	private JQueryTreeView<JQueryTreeViewElement> treeView = null;
	private HashMap<String, Protocol> nameToProtocol;
	private HashMap<String, JQueryTreeViewElement> protocolsAndMeasurementsinTree;
	private HashMap<String, List<String>> parameterToExpandedQuery = new HashMap<String, List<String>>();

	private List<Investigation> arrayInvestigations = new ArrayList<Investigation>();
	private List<String> listOfParameters = new ArrayList<String>();
	private String selectedInvestigation = null;
	private String selectedField = null;
	private boolean isSelectedInv = false;

	/** Multiple inheritance: some measurements might have multiple parents therefore it
	 *  will complain about the branch already exists when constructing the tree, cheating by
	 *  changing the name of the branch but keeping display name the same
	 */

	private HashMap<String, Integer> multipleInheritance = new HashMap<String, Integer>();
	private HashMap<String, List<String>> expandedQueries = new HashMap<String, List<String>>();
	private LevenshteinDistanceModel model = new LevenshteinDistanceModel();
	private HashMap<String, String> parameterWithHtmlTable = new HashMap<String, String>();
	private HashMap<String, String> questionsAndIdentifier = new HashMap<String, String>();
	private ArrayList<String> validationStudy = new ArrayList<String>();
	private String validationStudyName = "";
	private int maxQuerySize = 0;
	private String hitSizeOption = "";
	private HashMap<String, LinkedMap> mappingResultAndSimiarity = new HashMap<String, LinkedMap>();
	private List<String> listOfScripts = new ArrayList<String>();


	public harmonizationPlugin(String name, ScreenController<?> parent) {
		super(name, parent);
	}

	public String getCustomHtmlHeaders() {
		return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/download_list.css\">";
	}

	@Override
	public String getViewName() {
		return "plugins_harmonizationPlugin_harmonizationPlugin";
	}

	@Override
	public String getViewTemplate() {
		return "plugins/harmonizationPlugin/harmonizationPlugin.ftl";
	}

	public void handleRequest(Database db, Tuple request) {

		try {

			if (request.getAction().equals("chooseInvestigation")) {
				selectedInvestigation = request.getString("investigation");
				this.setSelectedInvestigation(selectedInvestigation);
				System.out.println("The selected investigation is : "
						+ selectedInvestigation);
				arrayInvestigations.clear();

			}else if (request.getAction().equals("startMatching")){

				String uploadFileName = request.getString("ontologyFile");

				expandedQueries.clear();
				
				parameterWithHtmlTable.clear();

				questionsAndIdentifier.clear();

				parameterToExpandedQuery.clear();

				String separator = ";";

				validationStudyName = request.getString("validationStudy");

				if(db.find(Measurement.class, new QueryRule(Measurement.INVESTIGATION_NAME, 
						Operator.EQUALS, validationStudyName)).size() > 0){

					for(Measurement m : db.find(Measurement.class, new QueryRule(Measurement.INVESTIGATION_NAME, 
							Operator.EQUALS, validationStudyName))){

						//						if(m.getName().endsWith("_" + validationStudyName)){
						//							
						//							String displayName = m.getName().substring(0, 
						//									m.getName().length() - validationStudyName.length() -1);
						//							
						//							questionsAndIdentifier.put(displayName, m.getName());
						//							
						//						}else{
						//							questionsAndIdentifier.put(m.getName(), m.getName());
						//						}

						if(m.getDescription() != null && !m.getDescription().equals("")){
							questionsAndIdentifier.put(m.getDescription().replaceAll("[\\n;]", " "), m.getName());
						}
						if(m.getCategories_Name() != null && m.getCategories_Name().size() > 0){

							if(m.getDescription() != null && !m.getDescription().equals("")){
								for(String eachCategory : m.getCategories_Name()){
									questionsAndIdentifier.put(eachCategory + " " + m.getDescription().replaceAll("[\\n;]", " "),  m.getName());
								}
							}
						}
					}

				}

				if(questionsAndIdentifier.size() > 0){

					if(uploadFileName != null){
						OWLFunction	owlFunction = new OWLFunction(uploadFileName);
						//owlFunction.labelMapURI("http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#FULL_SYN");
						//OWLRDFVocabulary.RDFS_COMMENT.getIRI().toString(), 
						owlFunction.labelMapURI("http://www.semanticweb.org/ontologies/2012/2/PredictionModel.owl#alternative_term");
						expandedQueries = owlFunction.getExpandedQueries();
						expandedQueries.remove("Prediction Model");
						expandedQueries.remove("Composite");
						separator = owlFunction.getSeparator();
					}

					this.stringMatching(separator, request);

					int residue = maxQuerySize / 10;

					String optionForHits = "<select name='changeHits' id='changeHits' onChange='refreshByHits()'>";

					for(int i = 0; i < residue; i++){
						optionForHits += "<option>" + (i+1)*10 + "</option>";
					}
					if(residue == 0){
						optionForHits += "<option>10</option>";
					}
					optionForHits += "</select>";

					hitSizeOption  = "Choose how many results you want to view" 
							+ optionForHits;

				}else{

					this.setMessages(new ScreenMessage("Please choose the correct cohort study with data item", false));
				}

			}else if(request.getAction().equals("saveMapping")){

				validationStudyName  = "";

				if(request.getString("validationStudy") != null && !request.getString("validationStudy").equals("")){
					validationStudyName = request.getString("validationStudy");
				}else if(request.getString("validationStudyName") != null && !request.getString("validationStudyName").equals("")){
					validationStudyName = request.getString("validationStudyName");
				}

				if(validationStudyName.equals("")){

					setMessages(new ScreenMessage("Please input a new name for this validation study", false));

				}else{

					Protocol validationStudyProtocol = null;

					if(db.find(Protocol.class, new QueryRule(Protocol.NAME, Operator.EQUALS, validationStudyName)).size() != 0){

						validationStudyProtocol = db.find(Protocol.class, new QueryRule(Protocol.NAME, Operator.EQUALS, validationStudyName)).get(0);

					}else{

						validationStudyProtocol = new Protocol();
						validationStudyProtocol.setName(validationStudyName);
						validationStudyProtocol.setInvestigation_Name("Validation Study");
						db.add(validationStudyProtocol);
					}

					for(Entry<String, LinkedMap> entry : mappingResultAndSimiarity.entrySet()){

						String originalQuery = entry.getKey();
						List<LinkedInformation> listOfMatchedResult = entry.getValue().getSortedInformation();
						List<String> listOFMatchedItem = new ArrayList<String>();

						MappingMeasurement mapping = new MappingMeasurement();

						for(LinkedInformation eachMatching : listOfMatchedResult){

							String identifier = originalQuery + " " + eachMatching.matchedItem;

							if(request.getBool(identifier.replaceAll(" ", "_")) != null){

								String dataItemName = questionsAndIdentifier.get(eachMatching.matchedItem);

								listOFMatchedItem.add(dataItemName);
							}
						}

						List<Measurement> measurements = new ArrayList<Measurement>();

						if(listOFMatchedItem.size() > 0){
							measurements = db.find(Measurement.class, 
									new QueryRule(Measurement.NAME, Operator.IN, listOFMatchedItem));
						}

						if(measurements.size() > 0){

							List<Integer> measurementIds = new ArrayList<Integer>();

							List<Integer> featuresInMapping = validationStudyProtocol.getFeatures_Id();

							for(Measurement m : measurements){
								measurementIds.add(m.getId());
								if(!featuresInMapping.contains(m.getId())){
									featuresInMapping.add(m.getId());
								}
							}

							if(measurementIds.size() > 0){

								if(db.find(Measurement.class, new QueryRule(Measurement.NAME, 
										Operator.EQUALS, originalQuery + "_" + validationStudyName)).size() == 0){
									Measurement m = new Measurement();
									m.setName(originalQuery.toLowerCase() + "_" + validationStudyName);
									m.setInvestigation_Name("Validation Study");
									db.add(m);
								}

								Query<MappingMeasurement> queryForMapping = db.query(MappingMeasurement.class);

								queryForMapping.addRules(new QueryRule(MappingMeasurement.TARGET_NAME, 
										Operator.EQUALS, originalQuery + "_" + validationStudyName));

								queryForMapping.addRules(new QueryRule(MappingMeasurement.MAPPING_NAME, 
										Operator.EQUALS, originalQuery));

								if(queryForMapping.find().size() > 0){

									mapping = queryForMapping.find().get(0);

									mapping.setFeature_Id(measurementIds);

									db.update(mapping);

								}else{

									mapping.setTarget_Name(originalQuery.toLowerCase() + "_" + validationStudyName);

									mapping.setInvestigation_Name("Validation Study");

									mapping.setDataType("pairingrule");

									mapping.setMapping_Name(originalQuery);

									mapping.setFeature_Id(measurementIds);

									db.add(mapping);

								}

								List<Integer> oldFeatureIds = validationStudyProtocol.getFeatures_Id();

								for(Integer id : featuresInMapping){
									if(!oldFeatureIds.contains(id)){
										oldFeatureIds.add(id);
									}
								}

								oldFeatureIds.addAll(featuresInMapping);

								validationStudyProtocol.setFeatures_Id(oldFeatureIds);
							}
						}
					}
					db.update(validationStudyProtocol);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			this.setError("There was a problem handling your Download: " + e.getMessage());
		}

	}

	public void stringMatching(String separator, Tuple request) throws Exception{

		mappingResultAndSimiarity.clear();

		for(String eachParameter : listOfParameters){

			if(eachParameter.equalsIgnoreCase("Body Mass Index")){
				System.out.println();
			}

			List<String> expandedQuery = new ArrayList<String>();

			List<String> finalQuery = new ArrayList<String>();

			if(expandedQueries.containsKey(eachParameter.toLowerCase())){
				expandedQuery = expandedQueries.get(eachParameter.toLowerCase());
			}else{
				expandedQuery.add(eachParameter.toLowerCase());
			}

			for(String eachQuery : expandedQuery){

				String[] blocks = eachQuery.split(separator);

				finalQuery.add(eachQuery.replaceAll(separator, " "));

				if(request.getBool("baseline") != null){
					finalQuery.add(eachQuery.replaceAll(separator, " ") + " Baseline");
				}

				for(int i = 0; i < blocks.length; i++){
					if(!finalQuery.contains(blocks[i].toLowerCase()))
						finalQuery.add(blocks[i].toLowerCase());
					if(request.getBool("baseline") != null){
						finalQuery.add(blocks[i].toLowerCase() + " Baseline");
					}
				}
			}

			parameterToExpandedQuery.put(eachParameter, finalQuery);
			
			for(String eachQuery : finalQuery){

				double maxSimilarity = 0;

				String matchedDataItem = "";

				List<String> tokens = model.createNGrams(eachQuery.toLowerCase().trim(), false);

				for(Entry<String, String> eachEntry : questionsAndIdentifier.entrySet()){

					String question = eachEntry.getKey();

					List<String> dataItemTokens = model.createNGrams(question.toLowerCase().trim(), true);

					double similarity = model.calculateScore(dataItemTokens, tokens);

					if(similarity > maxSimilarity){

						maxSimilarity = similarity;
						matchedDataItem = question;
					}
				}

				LinkedMap temp = null;

				if(mappingResultAndSimiarity.containsKey(eachParameter)){
					temp = mappingResultAndSimiarity.get(eachParameter);
				}else{
					temp = new LinkedMap();
				}

				temp.add(eachQuery, matchedDataItem, maxSimilarity);

				mappingResultAndSimiarity.put(eachParameter, temp);
			}
		}

		makeHtmlTable(mappingResultAndSimiarity);
	}

	public void makeHtmlTable (HashMap<String, LinkedMap>mappingResultAndSimiarity) {

		listOfScripts.clear();
		
		for(String eachOriginalQuery : mappingResultAndSimiarity.keySet()){

			LinkedMap map = mappingResultAndSimiarity.get(eachOriginalQuery);

			List<LinkedInformation> links = map.getSortedInformation();

			int size = links.size();

			String matchingResult = "<table id='" + eachOriginalQuery + " table' border='1'>";

			Map<String, Map<String, Double>> uniqueMapping = new HashMap<String, Map<String, Double>>();
			
			matchingResult += "<tr style='background: blue; color: white;'>" 
					+"<td>Data Item</td><td>Description</td><td>Select mapping</td></tr>";
			
			for(int i = size; i > 0; i--){

				LinkedInformation eachRow = links.get(i - 1);
				String expandedQuery = eachRow.expandedQuery;
				String matchedItem = eachRow.matchedItem;
				Double similarity = eachRow.similarity;
				
				if(!uniqueMapping.containsKey(matchedItem)){
					
					Map<String, Double> queryAndSimilarity = new HashMap<String, Double>();
					
					queryAndSimilarity.put(expandedQuery, similarity);
					
					uniqueMapping.put(matchedItem, queryAndSimilarity);
					
					String identifier = eachOriginalQuery + "_" + matchedItem;
					
					matchingResult += "<tr border='1' id='" + identifier.replaceAll(" ", "_") + "'>" 
							+ "<td>"+ questionsAndIdentifier.get(matchedItem) +"</td>"
							+ "<td><div id='" + identifier.replaceAll(" ", "_") + "_div'>" 
							+ matchedItem + "</div></td><td><input type='checkbox' name='" 
							+ identifier.replaceAll(" ", "_") + "'></td></tr>";
				}else{
					
					Map<String, Double> queryAndSimilarity = uniqueMapping.get(matchedItem);
					
					queryAndSimilarity.put(expandedQuery, similarity);
					
					uniqueMapping.put(matchedItem, queryAndSimilarity);
					
				}
				
				//				System.out.print(eachOriginalQuery + "\t" + expandedQuery + "\t" + matchedItem + "\t" + similarity);
				//				System.out.println();
			}
			
			matchingResult += "</table></div>";
			
			String executiveScript  = "<script>";
			
			if(maxQuerySize < uniqueMapping.keySet().size()){
				maxQuerySize = uniqueMapping.keySet().size();
			}
			
			for(String matchedItem : uniqueMapping.keySet()){
				
				String identifier = eachOriginalQuery + "_" + matchedItem;
				
				if(uniqueMapping.get(matchedItem).size() > 0){
					String table = "<table class='insertTable' id='" + identifier.replaceAll(" ", "_") +"_table'>" 
							+ "<tr><td>expanded query</td><td>similarity</td></tr>";
					
					for(Entry<String, Double> entry : uniqueMapping.get(matchedItem).entrySet()){
					
						String expandedQuery = entry.getKey();
						Double similarity = entry.getValue();
						
						table += "<tr class='insertTable'><td>" + expandedQuery + "</td><td>" + similarity + "</td></tr>";
					}
					
					table += "</table>";
					
					executiveScript += " document.getElementById(\"" + identifier.replaceAll(" ", "_")
							+ "_div\").innerHTML += \"</br>" + table + "\";\n";
					executiveScript += "var divTable = document.getElementById(\"" + identifier.replaceAll(" ", "_")
							+ "_div\");" 
							+ "divTable.style.cursor = \"pointer\";"
							+ "divTable.onclick = function() {insertTable(\"" + identifier.replaceAll(" ", "_") +"_table\")};";
				}
			}
			
			executiveScript += "</script>\n\n\n";
			
			listOfScripts.add(executiveScript);
			
			parameterWithHtmlTable.put(eachOriginalQuery, matchingResult);
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

		// Query<ShoppingCart> q = db.query(ShoppingCart.class);
		// q.addRules(new QueryRule(ShoppingCart.USERID, Operator.EQUALS, this
		// .getLogin().getUserName()));
		// q.addRules(new QueryRule(ShoppingCart.CHECKEDOUT, Operator.EQUALS,
		// false));
		try {

			if(db.find(Protocol.class, new QueryRule(Protocol.INVESTIGATION_NAME, Operator.EQUALS, "Validation Study")) != null){

				List<Protocol> protocols = db.find(Protocol.class, new QueryRule(Protocol.INVESTIGATION_NAME, Operator.EQUALS, "Validation Study"));

				validationStudy.clear();

				for(Protocol p : protocols){
					validationStudy.add(p.getName());
				}
			}

			this.arrayInvestigations.clear();

			for (Investigation i : db.find(Investigation.class)) {
				this.arrayInvestigations.add(i);
			}
		} catch (DatabaseException e) {
			e.printStackTrace();
		}

		RetrieveProtocols(db); 
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
	public void RetrieveProtocols(Database db) {

		List<String> topProtocols = new ArrayList<String>();
		List<String> bottomProtocols = new ArrayList<String>();
		List<String> middleProtocols = new ArrayList<String>();
		protocolsAndMeasurementsinTree = new HashMap<String, JQueryTreeViewElement>();
		listOfParameters.clear();

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

		if (topProtocols.size() == 0) { // The protocols don`t have
			// sub-protocols and we could directly
			// find the measurements of protocols
			recursiveAddingNodesToTree(bottomProtocols,
					protocolsTree.getName(), protocolsTree, db);

		} else { // The protocols that have sub-protocols, then we recursively
			// find sub-protocols
			recursiveAddingNodesToTree(topProtocols, protocolsTree.getName(),
					protocolsTree, db);
		}


		treeView = new JQueryTreeView<JQueryTreeViewElement>("Protocols", protocolsTree);

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

	public void recursiveAddingNodesToTree(List<String> nextNodes, String parentClassName,
			JQueryTreeViewElement parentTree, Database db) {

		for (String protocolName : nextNodes) {

			Protocol protocol = nameToProtocol.get(protocolName);

			if (!protocolName.equals(parentClassName) && protocol != null) {

				JQueryTreeViewElement childTree;

				if (protocolsAndMeasurementsinTree.containsKey(protocolName)) {
					childTree = protocolsAndMeasurementsinTree.get(protocolName);
				} else {
					childTree = new JQueryTreeViewElement(protocolName, parentTree);
					childTree.setCollapsed(true);
					protocolsAndMeasurementsinTree.put(protocolName, childTree);
				}

				if (protocol.getSubprotocols_Name() != null	&& protocol.getSubprotocols_Name().size() > 0) {
					recursiveAddingNodesToTree(protocol.getSubprotocols_Name(), protocol.getName(), childTree, db);
				}
				if (protocol.getFeatures_Name() != null	&& protocol.getFeatures_Name().size() > 0) {
					addingMeasurementsToTree(protocol.getFeatures_Name(), childTree, db);
				}
			}
		}
	}

	/**
	 * this is adding the measurements as references in
	 * recursiveAddingNodesToTree().
	 * 
	 * @param childNode
	 * @param parentTree
	 * @param db
	 */
	public void addingMeasurementsToTree(List<String> childNode,
			JQueryTreeViewElement parentTree, Database db) {

		try {

			List<Measurement> measurementList = db.find(Measurement.class, new QueryRule(
					Measurement.NAME, Operator.IN, childNode));

			for (Measurement measurement : measurementList) {

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

				htmlValue = parameterWithHtmlTable.get(displayName);

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

					listOfParameters.add(displayName + multipleInheritance.get(displayName));

					childTree.setHtmlValue(htmlValue);

				} else {

					childTree = new JQueryTreeViewElement(displayName,
							parentTree, htmlValue);

					listOfParameters.add(displayName);

					protocolsAndMeasurementsinTree.put(displayName, childTree);
				}

			}

		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}

	public String getTreeView() {

		List<String> selected = new ArrayList<String>();

		// don't select, is confusing...
		// for (Measurement m : shoppingCart) {
		// selected.add(m.getName());
		// }

		String htmlTreeView = treeView.toHtml(selected);

		// This piece of javascript need to be here because some java calls are needed.  
		String measurementClickEvent = "<script>";

		List<String> uniqueMeasurementName = new ArrayList<String>();

		for(String eachMeasurement : listOfParameters){

			if(!uniqueMeasurementName.contains(eachMeasurement)){

				uniqueMeasurementName.add(eachMeasurement);

				if(eachMeasurement.equals("Year partner son daughter 3")){
					System.out.println();
				}
				measurementClickEvent += "$('#" + eachMeasurement.replaceAll(" ", "_") + "').click(function() {"
						+ "getClickedTable(\"" + eachMeasurement + "\");});"
						+ "";
			}
		}
		measurementClickEvent += "</script>";

		htmlTreeView += measurementClickEvent;

		return htmlTreeView;
	}

	public List<String> getExecutiveScript() {
		return listOfScripts;
	}

	public void setArrayInvestigations(List<Investigation> arrayInvestigations) {
		this.arrayInvestigations = arrayInvestigations;
	}

	public List<Investigation> getArrayInvestigations() {
		return arrayInvestigations;
	}

	public String getHitSizeOption() {
		return hitSizeOption;
	}

	public List<String> getValidationStudy() {
		return validationStudy;
	}

	public List<String> getListOfParameters() {
		return listOfParameters;
	}

	public String getValidationStudyName() {
		return validationStudyName;
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

	public void setSelectedField(String selectedField) {
		this.selectedField = selectedField;
	}

	public String getSelectedField() {
		return selectedField;
	}
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
