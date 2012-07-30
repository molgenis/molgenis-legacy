package plugins.findingProxy;


import gcc.catalogue.MappingMeasurement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Measurement;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import plugins.HarmonizationComponent.LevenshteinDistanceModel;
import plugins.HarmonizationComponent.LinkedInformation;
import plugins.HarmonizationComponent.MappingList;


public class findingProxy extends PluginModel<Entity> {

	private List<String> listOfJSON = new ArrayList<String>();
	private List<String> listOfValidationStudy = new ArrayList<String>();
	private List<String> listOfPredictionModel = new ArrayList<String>();
	private List<String> listOfParameters = new ArrayList<String>();
	private List<String> manualMappingResultTable = new ArrayList<String>();
	private String selectedPredictionModel = null;
	private String selectedValidationStudy = null;
	private String selectedManualParameter = null;
	private boolean stage = true;
	private LevenshteinDistanceModel model = new LevenshteinDistanceModel();	
	private int maxQuerySize = 0;
	private double cutOffValue = 40;
	private String userDefinedQuery = "";
	private List<Measurement> measurementsInStudy = new ArrayList<Measurement>();
	private HashMap<String, MappingList> mappingResultAndSimiarity = new HashMap<String, MappingList>();
	private HashMap<String, List<String>> parameterToExpandedQuery = new HashMap<String, List<String>>();
	private HashMap<String, List<String>> expandedQueries = new HashMap<String, List<String>>();

	/**
	 * 
	 */
	private static final long serialVersionUID = 7938039670107105296L;

	public findingProxy(String name, ScreenController<?> parent) {
		super(name, parent);
		// TODO Auto-generated constructor stub
	}

	public String getCustomHtmlHeaders() {
		return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/download_list.css\">";
	}

	@Override
	public String getViewName() {
		return "plugins_findingProxy_findingProxy";
	}

	@Override
	public String getViewTemplate() {
		return "plugins/findingProxy/findingProxy.ftl";
	}

	public void handleRequest(Database db, Tuple request) {

		try{

			if(request.getAction().equals("chooseModelAndStudy")){

				listOfParameters.clear();

				if(selectedPredictionModel != null){

					Protocol p = db.find(Protocol.class, new QueryRule(Protocol.NAME, Operator.EQUALS, selectedPredictionModel)).get(0);
					List<Measurement> parameters = db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.IN, p.getFeatures_Name()));
					for(Measurement m : parameters){
						String displayName = "";
						if(m.getLabel() != null && !m.getLabel().equals("")){
							displayName = m.getLabel();
						}else{
							displayName = m.getName();
						}
						listOfParameters.add(displayName);
					}
				}

				selectedPredictionModel = request.getString("predictionModel");
				selectedValidationStudy = request.getString("validationStudy");

				stage = false;

			}else if(request.getAction().equals("customizedSearch")){

				manualMappingResultTable.clear();

				userDefinedQuery  = request.getString("userDefinedQuery");

				selectedManualParameter = request.getString("selectParameter");

				System.out.println(userDefinedQuery);

				if(request.getString("cutOffValue") != null && !request.getString("cutOffValue").equals("")){
					cutOffValue = Double.parseDouble(request.getString("cutOffValue"));
				}else{
					cutOffValue = 50;
				}

				if(userDefinedQuery != null){

					if(db.find(Measurement.class, new QueryRule(Measurement.INVESTIGATION_NAME, 
							Operator.EQUALS, selectedValidationStudy)).size() > 0){

						measurementsInStudy = db.find(Measurement.class, new QueryRule(Measurement.INVESTIGATION_NAME, 
								Operator.EQUALS, selectedValidationStudy));
					}

					List<String> query = new ArrayList<String>();

					query.add(userDefinedQuery);

					this.stringMatching(query, " ", request, true);

					List<JSONObject> listOfJSON  = new ArrayList<JSONObject> (makeHtmlTable(mappingResultAndSimiarity).values());

					for(JSONObject eachJSON : listOfJSON){
						manualMappingResultTable.add(eachJSON.toString());
					} 
				}
			}else if(request.getAction().equals("addToExistingMapping")){

				if(selectedValidationStudy != null && selectedValidationStudy.equals("")){

					setMessages(new ScreenMessage("Please input a new name for this validation study", false));

				}else{

					Protocol validationStudyProtocol = null;

					if(db.find(Protocol.class, new QueryRule(Protocol.NAME, Operator.EQUALS, selectedValidationStudy)).size() != 0){

						validationStudyProtocol = db.find(Protocol.class, new QueryRule(Protocol.NAME, Operator.EQUALS, selectedValidationStudy)).get(0);

					}else{

						validationStudyProtocol = new Protocol();
						validationStudyProtocol.setName(selectedValidationStudy);
						//validationStudyProtocol.setInvestigation_Name("Validation Study");
						validationStudyProtocol.setInvestigation_Name(selectedValidationStudy);
						db.add(validationStudyProtocol);
					}

					for(Entry<String, MappingList> entry : mappingResultAndSimiarity.entrySet()){

						String originalQuery = entry.getKey();

						if(request.getAction().equals("addToExistingMapping")){
							originalQuery = request.getString("selectParameter");
						}

						List<LinkedInformation> listOfMatchedResult = entry.getValue().getSortedInformation();
						List<String> listOFMatchedItem = new ArrayList<String>();

						MappingMeasurement mapping = new MappingMeasurement();

						for(LinkedInformation eachMatching : listOfMatchedResult){

							String identifier =  eachMatching.measurementName + "_" + entry.getKey();

							if(request.getBool(identifier.replaceAll(" ", "_")) != null){

								String dataItemName = eachMatching.measurementName;

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

							for(Measurement m : measurements){
								measurementIds.add(m.getId());
							}

							if(measurementIds.size() > 0){

								if(db.find(Measurement.class, new QueryRule(Measurement.NAME, 
										Operator.EQUALS, originalQuery.replaceAll(" ", "_") + "_" + selectedValidationStudy)).size() == 0){

									Measurement m = new Measurement();
									m.setName(originalQuery.toLowerCase().replaceAll(" ", "_") + "_" + selectedValidationStudy);
									m.setInvestigation_Name(selectedValidationStudy);
									db.add(m);

									List<String> listOfFeatures = validationStudyProtocol.getFeatures_Name();
									listOfFeatures.add(m.getName());
									validationStudyProtocol.setFeatures_Name(listOfFeatures);
								}

								Query<MappingMeasurement> queryForMapping = db.query(MappingMeasurement.class);

								queryForMapping.addRules(new QueryRule(MappingMeasurement.TARGET_NAME, 
										Operator.EQUALS, originalQuery.replaceAll(" ", "_") + "_" + selectedValidationStudy));

								queryForMapping.addRules(new QueryRule(MappingMeasurement.MAPPING_NAME, 
										Operator.EQUALS, originalQuery));

								if(queryForMapping.find().size() > 0){

									mapping = queryForMapping.find().get(0);

									for(Integer id : mapping.getFeature_Id()){
										if(!measurementIds.contains(id)){
											measurementIds.add(id);
										}
									}

									mapping.setFeature_Id(measurementIds);

									db.update(mapping);

								}else{

									mapping.setTarget_Name(originalQuery.toLowerCase().replaceAll(" ", "_") + "_" + selectedValidationStudy);

									mapping.setInvestigation_Name(selectedValidationStudy);

									mapping.setDataType("pairingrule");

									mapping.setMapping_Name(originalQuery);

									mapping.setFeature_Id(measurementIds);

									db.add(mapping);

								}
							}
						}
					}
					//TODO this needs to be revisited. Little bug here
					db.update(validationStudyProtocol);
				}

			}else if(request.getAction().equals("backToSelection")){
				stage = true;
			}

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void reload(Database db)  {

		try {

			if(selectedPredictionModel == null){

				listOfPredictionModel.clear();

				List<Protocol> predictionModels = db.find(Protocol.class, new QueryRule(Protocol.INVESTIGATION_NAME, Operator.EQUALS, "Prediction Model")); 

				for(Protocol p : predictionModels){
					listOfPredictionModel.add(p.getName());
				}
				if(predictionModels.size() > 0){
					selectedPredictionModel = listOfPredictionModel.get(0);
				}
			}

			if(selectedValidationStudy == null){

				listOfValidationStudy.clear();

				List<Investigation> listOfInvestigation = db.find(Investigation.class, new QueryRule(Investigation.NAME, Operator.NOT, "Prediction Model"));

				for(Investigation inv : listOfInvestigation){
					listOfValidationStudy.add(inv.getName());
				}
				if(listOfValidationStudy.size() > 0){
					selectedValidationStudy = listOfValidationStudy.get(0);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}	

	public void stringMatching(List<String> listOfParameters, String separator, Tuple request, boolean cutOff) throws Exception{

		mappingResultAndSimiarity.clear();

		for(String eachParameter : listOfParameters){

			List<String> expandedQuery = new ArrayList<String>();

			List<String> finalQuery = new ArrayList<String>();

			if(expandedQueries.containsKey(eachParameter.toLowerCase())){
				expandedQuery = expandedQueries.get(eachParameter.toLowerCase());
			}else{
				expandedQuery.add(eachParameter.toLowerCase());
			}

			if(expandedQuery.contains("Prediction Model")){
				System.out.println();
			}

			for(String eachQuery : expandedQuery){

				String[] blocks = eachQuery.split(separator);

				finalQuery.add(eachQuery.replaceAll(separator, " "));

				if(request.getBool("baseline") != null){
					finalQuery.add(eachQuery.replaceAll(separator, " ") + " baseline");
				}
				//				else{
				//					finalQuery.add(eachQuery.replaceAll(separator, " "));
				//				}

				for(int i = 0; i < blocks.length; i++){

					if(!finalQuery.contains(blocks[i].toLowerCase()))
						finalQuery.add(blocks[i].toLowerCase());

					if(request.getBool("baseline") != null){
						if(!finalQuery.contains(blocks[i].toLowerCase() + " baseline"))
							finalQuery.add(blocks[i].toLowerCase() + " baseline");
					}
					//					else{
					//						if(!finalQuery.contains(blocks[i].toLowerCase()))
					//							finalQuery.add(blocks[i].toLowerCase());
					//					}
				}
			}

			parameterToExpandedQuery.put(eachParameter, finalQuery);

			for(String eachQuery : finalQuery){

				double maxSimilarity = 0;

				String matchedDataItem = "";

				String measurementName = "";

				List<String> tokens = model.createNGrams(eachQuery.toLowerCase().trim(), true);

				for(Measurement m : measurementsInStudy){

					List<String> fields = new ArrayList<String>();

					if(m.getDescription() != null && !m.getDescription().equals("")){

						fields.add(m.getDescription());

						if(m.getCategories_Name().size() > 0){
							for(String categoryName : m.getCategories_Name()){
								fields.add(categoryName + " " + m.getDescription());
							}
						}
					}

					if(cutOff == true)
						fields.add(m.getName());

					for(String question : fields){

						List<String> dataItemTokens = model.createNGrams(question.toLowerCase().trim(), true);

						double similarity = model.calculateScore(dataItemTokens, tokens);

						if(cutOff == false && similarity > maxSimilarity){

							if(m.getDescription() != null){
								matchedDataItem = m.getDescription();
							}else{
								matchedDataItem = question;
							}
							maxSimilarity = similarity;
							measurementName = m.getName();
						}

						if(cutOff == true && similarity >= cutOffValue ){

							MappingList temp = null;

							if(mappingResultAndSimiarity.containsKey(eachParameter)){
								temp = mappingResultAndSimiarity.get(eachParameter);
							}else{
								temp = new MappingList();
							}

							if(m.getDescription() != null){
								matchedDataItem = m.getDescription();
							}else{
								matchedDataItem = question;
							}

							temp.add(eachQuery, matchedDataItem, similarity, m.getName());

							mappingResultAndSimiarity.put(eachParameter, temp);
						}
					}
				}

				if(cutOff == false){

					MappingList temp = null;

					if(mappingResultAndSimiarity.containsKey(eachParameter)){
						temp = mappingResultAndSimiarity.get(eachParameter);
					}else{
						temp = new MappingList();
					}

					temp.add(eachQuery, matchedDataItem, maxSimilarity, measurementName);

					mappingResultAndSimiarity.put(eachParameter, temp);
				}
			}
		}
	}

	public HashMap<String, JSONObject> makeHtmlTable (HashMap<String, MappingList>mappingResultAndSimiarity) throws Exception {

		HashMap<String, JSONObject> parameterWithHtmlTable = new HashMap<String, JSONObject>();

		for(String eachOriginalQuery : mappingResultAndSimiarity.keySet()){

			MappingList map = mappingResultAndSimiarity.get(eachOriginalQuery);

			List<LinkedInformation> links = map.getSortedInformation();

			int size = links.size();

			String tableId = eachOriginalQuery + " table";

			String matchingResult = "<table class='dataResult' id='" + tableId.replaceAll(" ", "_") + "' border='1'>";

			Map<String, Map<String, Double>> uniqueMapping = new HashMap<String, Map<String, Double>>();

			matchingResult += "<tr style='background: blue; color: white;'>" 
					+"<td>Data Item</td><td>Description</td><td>Select mapping</td></tr>";

			for(int i = size; i > 0; i--){

				LinkedInformation eachRow = links.get(i - 1);
				String expandedQuery = eachRow.expandedQuery;
				String matchedItem = eachRow.matchedItem;
				Double similarity = eachRow.similarity;
				String measurementName = eachRow.measurementName;
				String identifier = measurementName + "_" + eachOriginalQuery ;

				if(!uniqueMapping.containsKey(identifier)){

					Map<String, Double> queryAndSimilarity = new HashMap<String, Double>();

					queryAndSimilarity.put(expandedQuery, similarity);

					uniqueMapping.put(identifier, queryAndSimilarity);

					matchingResult += "<tr class='clickRow' border='1' id='" + identifier.replaceAll(" ", "_") + "' style='cursor:pointer'>" 
							+ "<td>"+ measurementName +"</td>"
							+ "<td><div id='" + identifier.replaceAll(" ", "_") + "_div'>" 
							+ matchedItem + "</div></td><td><input type='checkbox' name='" 
							+ identifier.replaceAll(" ", "_") + "'></td></tr>";
				}else{

					Map<String, Double> queryAndSimilarity = uniqueMapping.get(identifier);

					queryAndSimilarity.put(expandedQuery, similarity);

					uniqueMapping.put(identifier, queryAndSimilarity);

				}

				//				System.out.print(eachOriginalQuery + "\t" + expandedQuery + "\t" + matchedItem + "\t" + similarity);
				//				System.out.println();
			}

			matchingResult += "</table>";

			String executiveScript  = "";

			if(maxQuerySize < uniqueMapping.keySet().size()){
				maxQuerySize = uniqueMapping.keySet().size();
			}

			for(String identifier : uniqueMapping.keySet()){

				if(uniqueMapping.get(identifier).size() > 0){
					String table = "<table class='insertTable' id='" + identifier.replaceAll(" ", "_") +"_table' style='display:none;position:absolute;'>" 
							+ "<tr><td>expanded query</td><td>similarity</td></tr>";

					for(Entry<String, Double> entry : uniqueMapping.get(identifier).entrySet()){

						String expandedQuery = entry.getKey();
						Double similarity = entry.getValue();

						table += "<tr class='insertTable'><td>" + expandedQuery + "</td><td>" + similarity + "</td></tr>";
					}

					table += "</table>";

					executiveScript += table;
				}
			}

			JSONObject json = new JSONObject();

			json.put("term", eachOriginalQuery);
			json.put("result", matchingResult);
			json.put("script", executiveScript);


			//			if(!listOfParameters.contains(executiveScript))
			//				listOfScripts.add(executiveScript);

			parameterWithHtmlTable.put(eachOriginalQuery, json);
		}

		//		return parameterWithHtmlTable;


		return parameterWithHtmlTable;
	}


	public List<String> getListOfValidationStudy() {
		return listOfValidationStudy;
	}

	public List<String> getListOfParameters() {
		return listOfParameters;
	}

	public String getSelectedValidationStudyName() {
		return selectedValidationStudy;
	}

	public String getSelectedPredictionModel() {
		return selectedPredictionModel;
	}

	public List<String> getlistOfPredictionModel()
	{
		return listOfPredictionModel;
	}
	public List<String> getManualMappingResultTable()
	{
		return manualMappingResultTable;
	}
	public String getSelectedManualParameter()
	{
		return selectedManualParameter;
	}
	public boolean getStage() {
		return stage;
	}
	public String getUserDefinedQuery()
	{
		return userDefinedQuery;
	}
	public List<String> getListOfJSON(){
		return listOfJSON;
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