/* Date:        December 3, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 */

package plugins.predictionSelection;

import gcc.catalogue.MappingMeasurement;
import gcc.catalogue.ui.MappingMeasurementForm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.molgenis.compute.ComputeProtocol;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.html.EntityTable;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;




public class predictionSelectionPlugin extends PluginModel<Entity>
{

	private static final long serialVersionUID = -4576352827620517694L;

	//A list that contains all the prediction model name
	private List<String> preidctionModel = new ArrayList<String>();
	//A list that contains all the validation study name
	private List<String> validationStudy = new ArrayList<String>();
	//A string storing investigation name for compute protocol
	private String computeInvestigationName = null;
	//A string storing investigation name for validation study protocol
	private String validationInvestigationName = null;
	//A list of string that contains the variables needed by selected prediction model
	private List<String> featureNamesInModel = new ArrayList<String>();

	private String htmlTable = "";

	private String selectedComputeProtocolName = null;

	private String selectedStudyProtocolName = null;

	private String editAndViewFlag = "viewData";

	//This hashMap takes the entity Id as key and its corresponding entity as content.
	private HashMap<Integer, MappingMeasurement> mappingMeasurementIdToEntity = new HashMap<Integer, MappingMeasurement>();;

	public predictionSelectionPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugins_predictionSelection_predictionSelectionPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/predictionSelection/predictionSelectionPlugin.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request) throws Exception
	{
		if(request.getAction().equals("refreshSelection")){

			selectedComputeProtocolName = request.getString("selectPredictionModel");

			selectedStudyProtocolName = request.getString("selectValidationStudy");

			mappingMeasurementIdToEntity.clear();

			makeHtmlTable(db, request);

		}else if(request.getAction().equals("editData")){

			editAndViewFlag  = request.getAction();

			int selectedRow = request.getInt("clickedRow");

			makeEditableTable(selectedRow);

			//this.setView(this.getParent().getParent().get("mappingMeasurement").getApplicationController().getView());

		}else if(request.getAction().equals("viewData")){

			String entityName = request.getString(MappingMeasurement.class.getSimpleName() + "_" + MappingMeasurement.ID);

			MappingMeasurement entity = mappingMeasurementIdToEntity.get(Integer.parseInt(entityName));

			//Before reload the table, the database needs to be updated by the changes that users have made.
			for(String eachField : entity.getFields()){

				eachField = MappingMeasurement.class.getSimpleName() + "_" + eachField;

				if(eachField.equals(MappingMeasurement.class.getSimpleName() + "_" + MappingMeasurement.FEATURE)){

					List<Integer> listOfFeatureIds = new ArrayList<Integer>();

					for(Object eachFeatureName : request.getList(eachField)){
						String textValue = eachFeatureName.toString();
						System.out.println(textValue);
						listOfFeatureIds.add(Integer.parseInt(textValue));
					}
					entity.setFeature_Id(listOfFeatureIds);

				}else{

					entity.set(eachField, request.getString(eachField));

				}

			}

			db.update(entity);
			editAndViewFlag  = request.getAction();
			makeHtmlTable(db, request);
		}
	}

	@Override
	public void reload(Database db)
	{
		try
		{	
			setComputeInvestigationName("Prediction Model");

			setValidationInvestigationName("Validation Study");

			preidctionModel.clear();

			validationStudy.clear();

			if(selectedComputeProtocolName != null)
				preidctionModel.add(selectedComputeProtocolName);


			if(selectedStudyProtocolName != null)
				validationStudy.add(selectedStudyProtocolName);



			List<ComputeProtocol> listOfComputeProtocol = db.find(ComputeProtocol.class, new QueryRule(ComputeProtocol.INVESTIGATION_NAME, Operator.EQUALS, getComputeInvestigationName()));

			if(listOfComputeProtocol.size() > 0){

				for(ComputeProtocol compute : listOfComputeProtocol){

					if(!preidctionModel.contains(compute.getName()))
						preidctionModel.add(compute.getName());
				}
			}

			List<Protocol> listOfValidationProtocol = db.find(Protocol.class, new QueryRule(Protocol.INVESTIGATION_NAME, Operator.EQUALS, getValidationInvestigationName()));

			if(listOfValidationProtocol.size() > 0){

				for(Protocol study : listOfValidationProtocol){

					if(!validationStudy.contains(study.getName()))
						validationStudy.add(study.getName());
				}

			}

		}catch (DatabaseException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * This method gets the selected prediction model and validation study from GUI. It gets back all the variables
	 * in the prediction model from DB. It also searches for the corresponding variables in the validation study.
	 * At end, it could generate a HTML table which has prediction model variables, derived variables that corresponds to
	 * the prediction model, original variables in validation study from which the derived variables were constructed,
	 * and the R-scripts on how the original variables were converted into the derived variables.  
	 * 
	 * @param db
	 * @param request
	 * @throws DatabaseException
	 */
	private void makeHtmlTable(Database db, Tuple request) throws DatabaseException
	{
		if(selectedComputeProtocolName != null && selectedStudyProtocolName != null){

			List<ComputeProtocol> selectedComputeProtocol = db.find(ComputeProtocol.class, new QueryRule(ComputeProtocol.NAME, Operator.EQUALS, selectedComputeProtocolName));

			if(selectedComputeProtocol.size() > 0){
				featureNamesInModel = selectedComputeProtocol.get(0).getFeatures_Name();
			}else{
				this.getModel().setError("The prediction model dose not contain any variables");
			}

			//Search for the corresponding variables in a new study
			List<MappingMeasurement> listOfMappings = db.find(MappingMeasurement.class, 
					new QueryRule(MappingMeasurement.INVESTIGATION_NAME, Operator.EQUALS, validationInvestigationName));

			for(MappingMeasurement mapping : listOfMappings){
				mappingMeasurementIdToEntity.put(mapping.getId(), mapping);
			}

			htmlTable = new EntityTable(listOfMappings, true, MappingMeasurement.MAPPING_NAME, MappingMeasurement.FEATURE_NAME, MappingMeasurement.VALUE).toHtml();
			return;
		}

	}

	private void makeEditableTable(int selectedRow)
	{
		MappingMeasurement mapping = mappingMeasurementIdToEntity.get(selectedRow);

		htmlTable = new MappingMeasurementForm(mapping).getHtml();
	}

	public List<String> getValidationStudy()
	{
		return validationStudy;
	}

	public List<String> getPreidctionModel()
	{
		return preidctionModel;
	}

	public String getComputeInvestigationName()
	{
		return computeInvestigationName;
	}

	public void setComputeInvestigationName(String investigationName)
	{
		this.computeInvestigationName = investigationName;
	}

	public String getValidationInvestigationName()
	{
		return validationInvestigationName;
	}

	public void setValidationInvestigationName(String investigationName)
	{
		this.validationInvestigationName = investigationName;
	}

	public String getHtmlTable()
	{
		return htmlTable;
	}

	public String getEditAndViewFlag()
	{
		return editAndViewFlag;
	}

}
