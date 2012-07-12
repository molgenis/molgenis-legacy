package plugins.findingProxy;


import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Measurement;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import plugins.HarmonizationComponent.LevenshteinDistanceModel;


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
	private double cutOffValue = 40;
	private String userDefinedQuery = "";

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
			
			selectedPredictionModel = request.getString("predictionModel");
			selectedValidationStudy = request.getString("validationStudy");
			
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

				stage = false;

			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void reload(Database db)  {

		try {
			
			listOfPredictionModel.clear();

			listOfValidationStudy.clear();

			if(selectedPredictionModel == null){

				List<Protocol> predictionModels = db.find(Protocol.class, new QueryRule(Protocol.INVESTIGATION_NAME, Operator.EQUALS, "Prediction Model")); 

				for(Protocol p : predictionModels){
					listOfPredictionModel.add(p.getName());
				}
				if(predictionModels.size() > 0){
					selectedPredictionModel = listOfPredictionModel.get(0);
				}
			}

			if(selectedValidationStudy == null){

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