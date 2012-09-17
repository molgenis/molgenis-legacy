/**
 * @author Jessica Lundberg
 * @author Erik Roos
 * @date Feb 24, 2011
 * 
 * This class is the model for the ApplyProtocolPlugin
 */
package org.molgenis.protocol;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.EasyPluginModel;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;


public class ApplyProtocolPluginModel extends EasyPluginModel {

	public ApplyProtocolPluginModel(EasyPluginController<?> controller)
	{
		super(controller);
	}

	private List<Measurement> featuresList = new ArrayList<Measurement>();
	private List<Integer> targetsIdList = new ArrayList<Integer>();
	private List<String> targetList = new ArrayList<String>();
	private List<String> fullTargetList = new ArrayList<String>();
	private List<String> batchesList = new ArrayList<String>();
	private String protocolName;
	private boolean newProtocolApplication = false;
	private boolean timeInfo = false;
	private boolean allValues = false;
	private int userId = -1;
	private List<Integer> investigationIds;
	//private Map<Measurement, List<Category>> codeMap = new HashMap<Measurement, List<Category>>();
	private Map<String, List<String>> catMap = new HashMap<String, List<String>>();
	private Map<Measurement, List<ObservationTarget>> panelMap = new HashMap<Measurement, List<ObservationTarget>>();
	private Map<Measurement, String> typeMap = new HashMap<Measurement, String>();
	private ApplyProtocolService service;
	
	public void setService(ApplyProtocolService service) {
		this.service = service;
	}

	public void setFeaturesLists(Database db, List<Measurement> featuresList) throws DatabaseException, ParseException {
		this.featuresList = featuresList;
		for (Measurement m : featuresList) {
			catMap.put(m.getName(), service.getAllCodesForFeatureAsStrings(db, m.getName()));
			String panelLabel = m.getPanelLabelAllowedForRelation();
			panelMap.put(m, service.getAllMarkedPanels(db, panelLabel, investigationIds));
			String observationTargetType = "org.molgenis.pheno.ObservationTarget";
			if (m.getTargettypeAllowedForRelation_Id() != null) {
				int entityId = m.getTargettypeAllowedForRelation_Id();
				observationTargetType = service.getEntityName(db, entityId);
			}
			typeMap.put(m, observationTargetType);
		}
	}
	
	public List<String> getAllCategoriesForFeatureAsStrings(String measurementName) {
		return catMap.get(measurementName);
	}
	
	public List<ObservationTarget> getAllPanelsForFeature(Measurement measurement) {
		return panelMap.get(measurement);
	}
	
	public String getTargettypeAllowedForRelation(Measurement measurement) {
		return typeMap.get(measurement);
	}

	public List<Measurement> getFeaturesList() {
		return featuresList;
	}

	public List<Integer> getTargetsIdList() {
		return targetsIdList;
	}

	public void setProtocolName(String protocolName) {
	    this.protocolName = protocolName;
	}

	public String getProtocolName() {
	    return protocolName;
	}

	public void setNewProtocolApplication(boolean newProtocolApplication) {
		this.newProtocolApplication = newProtocolApplication;
	}

	public boolean isNewProtocolApplication() {
		return newProtocolApplication;
	}

	public void setTimeInfo(boolean timeInfo) {
		this.timeInfo = timeInfo;
	}

	public boolean isTimeInfo() {
		return timeInfo;
	}

	public void setTargetList(List<String> targetList) {
		this.targetList = targetList;
	}
	
	public List<String> getTargetList() {
		return targetList;
	}
	
	public void setBatchesList(List<String> batchesList) {
		this.batchesList = batchesList;
	}

	public List<String> getBatchesList() {
		return batchesList;
	}

	public void setFullTargetList(List<String> fullTargetList) {
		this.fullTargetList = fullTargetList;
	}

	public List<String> getFullTargetList() {
		return fullTargetList;
	}

	public void setUserAndInvestigationIds(Database db, int userId) {
		this.userId = userId;
		this.investigationIds = service.getWritableUserInvestigationIds(db, userId);
	}

	public int getUserId() {
		return userId;
	}
	
	public int getOwnInvestigationId(Database db) {
		return service.getOwnUserInvestigationId(db, userId);
	}
	
	public List<Integer> getInvestigationIds() {
		return investigationIds;
	}

	public void setAllValues(boolean allValues) {
		this.allValues = allValues;
	}

	public boolean isAllValues() {
		return allValues;
	}
}
