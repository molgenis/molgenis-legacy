/* Date:        March 11, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.location;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.matrix.component.MatrixViewer;
import org.molgenis.matrix.component.SliceablePhenoMatrix;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import commonservice.CommonService;

import convertors.locations.ImportAteLocations;

public class LocationPlugin extends PluginModel<Entity>
{
	private static final long serialVersionUID = 6637437260773077373L;
	private List<ObservationTarget> locationList;
	private String action = "init";
	private Map<Integer, String> superLocMap;
	private CommonService ct = CommonService.getInstance();
	MatrixViewer animalsInLocMatrixViewer = null;
	MatrixViewer animalsNotInLocMatrixViewer = null;
	static String ANIMALSINLOCMATRIX = "animalsinlocmatrix";
	static String ANIMALSNOTINLOCMATRIX = "animalsnotinlocmatrix";
	private int locId = -1;
	
	public LocationPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	public String getCustomHtmlHeaders()
    {
		return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">";
    }
	
	public List<ObservationTarget> getLocationList() {
		return locationList;
	}
	public void setLocationList(List<ObservationTarget> locationList) {
		this.locationList = locationList;
	}

	@Override
	public String getViewName()
	{
		return "plugins_location_LocationPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/location/LocationPlugin.ftl";
	}

	public String getAction()
	{
		return action;
	}

	public void setAction(String action)
	{
		this.action = action;
	}
	
	public String getSuperLocName(int locationId) {
		return superLocMap.get(locationId);
	}
	
	private String getSuperLoc(Database db, int locId) throws DatabaseException {
		
		Query<ObservedValue> q = db.query(ObservedValue.class);
		q.addRules(new QueryRule(ObservedValue.DELETED, Operator.EQUALS, false));
		q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Location"));
		q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, locId));
		q.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.EQUALS, null)); // only active one!
		if (q.find().size() == 1) {
			ObservedValue currentValue = q.find().get(0);
			if (currentValue.getRelation_Id() != null) {
				return currentValue.getRelation_Name();
			}
		}
		return "";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		ct.setDatabase(db);
		if (animalsInLocMatrixViewer != null) {
			animalsInLocMatrixViewer.setDatabase(db);
		}
		
		try {
			int invid = ct.getOwnUserInvestigationIds(this.getLogin().getUserId()).get(0);
			action = request.getString("__action");
			
			if (animalsInLocMatrixViewer != null && action.startsWith(animalsInLocMatrixViewer.getName())) {
				animalsInLocMatrixViewer.handleRequest(db, request);
				action = "Manage";
				return;
			}
			
			if (animalsNotInLocMatrixViewer != null && action.startsWith(animalsNotInLocMatrixViewer.getName())) {
				animalsNotInLocMatrixViewer.handleRequest(db, request);
				action = "AddAnimals";
				return;
			}
			
			if (action.equals("AddAnimals")) {
				String locName = ct.getObservationTargetLabel(locId);
				// Prepare matrix with all animals
//				List<String> investigationNames = ct.getAllUserInvestigationNames(this.getLogin().getUserId());
				List<String> measurementsToShow = new ArrayList<String>();
				measurementsToShow.add("Location");
//				List<MatrixQueryRule> filterRules = new ArrayList<MatrixQueryRule>();
//				filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.rowHeader, Individual.INVESTIGATION_NAME, 
//						Operator.IN, investigationNames));
//				filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, 
//						ct.getMeasurementId("Location"), ObservedValue.RELATION_NAME, Operator.NOT,
//						locName));
//				filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, 
//						ct.getMeasurementId("Location"), ObservedValue.ENDTIME, Operator.EQUALS,
//						null));
				// TODO: make MQRs combinable with OR so we can have animals with location NULL OR NOT current
				animalsNotInLocMatrixViewer = new MatrixViewer(this, ANIMALSNOTINLOCMATRIX, 
						new SliceablePhenoMatrix(Individual.class, Measurement.class), 
						true, true, false, null, 
						new MatrixQueryRule(MatrixQueryRule.Type.colHeader, Measurement.NAME, Operator.IN, measurementsToShow));
				animalsNotInLocMatrixViewer.setDatabase(db);
				animalsNotInLocMatrixViewer.setLabel("All animals:");
			}
			
			if (action.equals("ApplyAddAnimals")) {
				List<ObservationElement> rows = (List<ObservationElement>) animalsNotInLocMatrixViewer.getSelection(db);
				int rowCnt = 0;
				for (ObservationElement row : rows) {
					if (request.getBool(ANIMALSNOTINLOCMATRIX + "_selected_" + rowCnt) != null) {
						int animalId = row.getId();
						assignAnimalToLocation(db, invid, animalId, locId);
					}
					rowCnt++;
				}
				
				animalsInLocMatrixViewer.reloadMatrix(null, null);
				action = "Manage";
				return;
			}
			
			if (action.equals("Manage")) {
				locId = request.getInt("locId");
				prepareInLocMatrix(db, ct.getObservationTargetLabel(locId));
			}
			
			if (action.equals("Move")) {
				int newLocationId = request.getInt("moveto");
				List<ObservationElement> rows = (List<ObservationElement>) animalsInLocMatrixViewer.getSelection(db);
				int rowCnt = 0;
				for (ObservationElement row : rows) {
					if (request.getBool(ANIMALSINLOCMATRIX + "_selected_" + rowCnt) != null) {
						int animalId = row.getId();
						assignAnimalToLocation(db, invid, animalId, newLocationId);
					}
					rowCnt++;
				}
				
				prepareInLocMatrix(db, ct.getObservationTargetLabel(newLocationId));
				action = "Manage";
				return;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage() != null) {
				this.setError(e.getMessage());
			}
		}
	}

	private void prepareInLocMatrix(Database db, String locationName) throws Exception
	{
		List<String> investigationNames = ct.getAllUserInvestigationNames(this.getLogin().getUserId());
		List<String> measurementsToShow = new ArrayList<String>();
		measurementsToShow.add("Location");
		List<MatrixQueryRule> filterRules = new ArrayList<MatrixQueryRule>();
		filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.rowHeader, Individual.INVESTIGATION_NAME, 
				Operator.IN, investigationNames));
		filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, 
				ct.getMeasurementId("Location"), ObservedValue.RELATION_NAME, Operator.EQUALS,
				locationName));
		filterRules.add(new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, 
				ct.getMeasurementId("Location"), ObservedValue.ENDTIME, Operator.EQUALS,
				null));
		animalsInLocMatrixViewer = new MatrixViewer(this, ANIMALSINLOCMATRIX, 
				new SliceablePhenoMatrix(Individual.class, Measurement.class), 
				true, true, false, filterRules, 
				new MatrixQueryRule(MatrixQueryRule.Type.colHeader, Measurement.NAME, Operator.IN, measurementsToShow));
		animalsInLocMatrixViewer.setDatabase(db);
		animalsInLocMatrixViewer.setLabel("Animals in " + locationName + ":");
	}

	private void assignAnimalToLocation(Database db, int investigationId, int animalId, int locationId) throws DatabaseException, IOException, ParseException
	{
		// First end existing Location value(s)
		Query<ObservedValue> q = db.query(ObservedValue.class);
		q.addRules(new QueryRule(ObservedValue.DELETED, Operator.EQUALS, false));
		q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, animalId));
		q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Location"));
		q.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.EQUALS, null));
		List<ObservedValue> valueList = q.find();
		if (valueList != null) {
			for (ObservedValue value : valueList) {
				value.setEndtime(new Date());
				db.update(value);
			}
		}
		// Then make new one
		int protocolId = ct.getProtocolId("SetLocation");
		int featureId = ct.getMeasurementId("Location");
		db.add(ct.createObservedValueWithProtocolApplication(investigationId, new Date(), null, 
				protocolId, featureId, animalId, null, locationId));
	}

	public void reload(Database db)
	{
		ct.setDatabase(db);
		
		// Populate location list and superloc map
		try {
			List<Integer> investigationIds = ct.getAllUserInvestigationIds(this.getLogin().getUserId());
			List<Integer> locationIdList = ct.getAllObservationTargetIds("Location", false, investigationIds);
			if (locationIdList.size() > 0) {
				this.locationList = ct.getObservationTargets(locationIdList);
			} else {
				this.locationList = new ArrayList<ObservationTarget>();
			}
			
			superLocMap = new HashMap<Integer, String>();
			for (ObservationTarget loc : locationList) {
				superLocMap.put(loc.getId(), this.getSuperLoc(db, loc.getId()));
			}
			
		} catch (Exception e) {
			String message = "Something went wrong while loading location list";
			if (e.getMessage() != null) {
				message += (": " + e.getMessage());
			}
			this.setError(message);
			e.printStackTrace();
		}
	}
	
	public String renderAnimalsInLocMatrixViewer() {
		if (animalsInLocMatrixViewer != null) {
			return "<p>" + animalsInLocMatrixViewer.getLabel() + "</p>" + 
					animalsInLocMatrixViewer.render();
		}
		return "Error - location matrix not initialized";
	}
	
	public String renderAnimalsNotInLocMatrixViewer() {
		if (animalsNotInLocMatrixViewer != null) {
			return "<p>" + animalsNotInLocMatrixViewer.getLabel() + "</p>" + 
					animalsNotInLocMatrixViewer.render();
		}
		return "Error - location matrix not initialized";
	}
	
}
