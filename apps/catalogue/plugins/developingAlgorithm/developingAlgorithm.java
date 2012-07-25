package plugins.developingAlgorithm;


import gcc.catalogue.MappingMeasurement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Measurement;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import plugins.HarmonizationComponent.LevenshteinDistanceModel;
import plugins.HarmonizationComponent.OWLFunction;


public class developingAlgorithm extends PluginModel<Entity> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 244998330024877396L;
	public developingAlgorithm(String name, ScreenController<?> parent) {
		super(name, parent);
		// TODO Auto-generated constructor stub
	}

	public String getCustomHtmlHeaders() {
		return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/download_list.css\">";
	}

	@Override
	public String getViewName() {
		return "plugins_developingAlgorithm_developingAlgorithm";
	}

	@Override
	public String getViewTemplate() {
		return "plugins/developingAlgorithm/developingAlgorithm.ftl";
	}

	private testModel testModel = new testModel();
	private RScriptGenerator generator = new RScriptGenerator();
	private OWLFunction owlFunction = null;
	private String validationStudyName = "";
	private String messageForAlgorithm = "";
	private List<String> listOfParameters = new ArrayList<String>();
	private List<Measurement> measurementsInStudy = new ArrayList<Measurement>();
	private HashMap<String, String> variableFormula = new HashMap<String, String>();
	private LevenshteinDistanceModel model = new LevenshteinDistanceModel();
	private List<Investigation> arrayInvestigations = new ArrayList<Investigation>();

	public void handleRequest(Database db, Tuple request) throws Exception {

		if (request.getAction().equals("generateAlgorithm")) {

			String ontologyFileName = request.getString("ontologyFileForAlgorithm");

			owlFunction = new OWLFunction(ontologyFileName);
			owlFunction.labelMapURI(listOfParameters, "alternative_term");
			variableFormula  = owlFunction.getFormula();

			validationStudyName = request.getString("validationStudy");

			if(db.find(Measurement.class, new QueryRule(Measurement.INVESTIGATION_NAME, 
					Operator.EQUALS, validationStudyName)).size() > 0){

				measurementsInStudy = db.find(Measurement.class, new QueryRule(Measurement.INVESTIGATION_NAME, 
						Operator.EQUALS, validationStudyName));
			}

			generateAlgorithm(db);

			messageForAlgorithm = "The algorithms for "+ validationStudyName +" has been generated successfully!";
		}

	}


	@Override
	public void reload(Database db) {
		
		try{
			arrayInvestigations.clear();
			for (Investigation i : db.find(Investigation.class)) {
				this.arrayInvestigations.add(i);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}	

	private void generateAlgorithm(Database db) throws DatabaseException {

		if(db.find(MappingMeasurement.class, new QueryRule(MappingMeasurement.INVESTIGATION_NAME, 
				Operator.EQUALS, validationStudyName)).size() > 0){

			List<MappingMeasurement> listOfMappings = db.find(MappingMeasurement.class, new QueryRule(MappingMeasurement.INVESTIGATION_NAME, 
					Operator.EQUALS, validationStudyName));

			HashMap<String, String> variableToScript = testModel.getListOfVariable();

			testModel.setInvestigationName(validationStudyName);

			for(MappingMeasurement mapping : listOfMappings){

				String mappedParameter = mapping.getMapping_Name();
				String derivedParameter = mapping.getTarget_Name();
				List<String> featureNames = mapping.getFeature_Name();

				HashMap<String, String> featureToBuildingBlock = new HashMap<String, String>();

				String script = derivedParameter + " = 0;\n";
				String variableType = "none";

				if(owlFunction.getAnnotation(mappedParameter, "Variable_Type").size() > 0){
					variableType = owlFunction.getAnnotation(mappedParameter, "Variable_Type").get(0);
				}

				if(variableFormula.containsKey(mappedParameter) && featureNames.size() > 1){

					String formula = variableFormula.get(mappedParameter);

					String substitute = "SUBSTITUTESTRING";

					formula = formula.replaceAll(mappedParameter.toLowerCase(), substitute);

					System.out.println(formula);

					List<String> composites = owlFunction.getComposites(mappedParameter);

					composites.add(mappedParameter);

					for(String variable : featureNames){

						//TODO replace baseline with empty string right now, but later on it needs to be more generic
						//String description = identifierAndDescription.get(variable).replaceAll("[B|b]aseline", "");

						Measurement m = db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.EQUALS, variable)).get(0);

						String description = m.getDescription();

						List<String> tokensForDataItem = model.createNGrams(description.toLowerCase().trim(), false);

						double maxSimilarity = 0;

						String matchedItem = "";

						for(String buildingBlock : composites){

							List<String> synonyms = owlFunction.getSynonyms(buildingBlock);

							synonyms.add(buildingBlock);

							for(String eachMatchingString : synonyms){

								List<String> tokensForBuildingBlock = model.createNGrams(eachMatchingString.toLowerCase().trim(), false);

								double similarity = model.calculateScore(tokensForBuildingBlock, tokensForDataItem);		

								if(maxSimilarity < similarity){
									maxSimilarity = similarity;
									matchedItem = variable;
									featureToBuildingBlock.put(matchedItem, buildingBlock);
								}
							}
						}
						//						featureNames.remove(matchedItem);
						//						formula = formula.replaceAll(buildingBlock.toLowerCase(), "as.numeric(dataSet[rowIndex,\"" + matchedItem + "\"])");

					}

					composites.removeAll(featureToBuildingBlock.values());


					for(Entry<String, String> eachEntry : featureToBuildingBlock.entrySet()){

						Pattern pattern = Pattern.compile(eachEntry.getValue().toLowerCase());

						Matcher matcher = pattern.matcher(formula);

						if(matcher.find()){
							featureNames.remove(eachEntry.getKey());
						}

						formula = formula.replaceAll("\"" + eachEntry.getValue().toLowerCase() + "\"", "as.numeric(dataSet[rowIndex,\"" + eachEntry.getKey() + "\"])");

					}

					for(String restOfComposite : composites){
						formula = formula.replaceAll("\"" + restOfComposite.toLowerCase() + "\"", "NULL");
					}

					formula = formula.replaceAll(substitute, derivedParameter);

					System.out.println(formula);

					script += "\n" + formula + "\n";
				} 

				//There are two types of variables, one is continuous and the other is categorical
				if(variableType.equalsIgnoreCase("Categorical")){

					if(featureNames.size() == 1){

						List<String> listOfCode = owlFunction.getAnnotation(mappedParameter, "codeString");

						if(listOfCode.size() > 0){

							String codeString = listOfCode.get(0);

							int codeValue = 0;

							if(db.find(Category.class, new QueryRule(Category.NAME, Operator.IN, listOfCode)).size() > 0){

								Category category = db.find(Category.class, new QueryRule(Category.NAME, Operator.IN, listOfCode)).get(0);

								codeValue = Integer.parseInt(category.getCode_String().split("=")[0].trim());

							}else{

								List<String> synonymsForCodeString = owlFunction.getSynonyms(codeString);

								List<Category> categories = db.find(Category.class, new QueryRule(Category.NAME, Operator.IN, synonymsForCodeString));

								codeValue = Integer.parseInt(categories.get(0).getCode_String().split("=")[0].trim());
							}

							script += "if(as.numeric(dataSet[rowIndex, \"" + featureNames.get(0) + "\"]) == " + codeValue + "){\n" 
									+ "\t" + derivedParameter + " = 1;\n" 
									+ "}";

						}else{
							script += derivedParameter + " = as.numeric(dataSet[rowIndex, \"" + featureNames.get(0) + "\"]);";
						}

					}else{

						for(String variable : featureNames){

							script += "if(as.numeric(dataSet[rowIndex, \"" + variable +"\"]) == 1 || as.numeric(dataSet[rowIndex, \"" + variable +"\"]) == \"yes\"){\n" 
									+ "\t" + derivedParameter + " = 1;\n" 
									+ "}\n";
						}
					}

				}else{

					if(featureNames.size() == 1){
						script += derivedParameter + " = as.numeric(dataSet[rowIndex, \"" + featureNames.get(0) + "\"]);";
					}
				}

				variableToScript.put(derivedParameter, script);

				mapping.setValue(script);

				db.update(mapping);

			}

			testModel.setListOfVariable(variableToScript);

			generator.setModel(testModel);

			try
			{
				generator.start();
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public String getMessageForAlgorithm()
	{
		return messageForAlgorithm;
	}
	public String getValidationStudyName() {
		return validationStudyName;
	}
	public List<Investigation> getArrayInvestigations() {
		return arrayInvestigations;
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
