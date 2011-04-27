/**
 * 
 */
package commonservice;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.molgenis.auth.MolgenisEntity;
import org.molgenis.batch.MolgenisBatch;
import org.molgenis.core.OntologyTerm;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.ngs.NgsPerson;
import org.molgenis.ngs.NgsSample;
import org.molgenis.ngs.Project;
import org.molgenis.organization.Institute;
import org.molgenis.organization.Investigation;
import org.molgenis.organization.Person;
import org.molgenis.pheno.Code;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Location;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.pheno.Panel;
import org.molgenis.protocol.Protocol;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.protocol.Workflow;
import org.molgenis.protocol.WorkflowElement;
import org.molgenis.protocol.WorkflowElement_PreviousSteps;
import org.molgenis.protocol.WorkflowElement_Workflow;
import org.molgenis.util.Tuple;

import app.JDBCDatabase;

import com.ibm.icu.util.Calendar;

/**
 * @author erikroos
 * @author Jessica Lundberg
 * @author Morris Swertz
 * 
 *         Changelog <li>MS: Added constants to replace the String values that
 *         lead to so many typos. Can't we autogenerate all these services?
 */
public class CommonService
{
	public static final String ANIMAL = "Animal";
	public static final String ACTIVE = "Active";
	public static final String LOCATION = "Location";
	public static final String PANEL = "Panel";

	private static Database db;
	private static int protAppCounter = 0;
	private boolean isFilled = false; //for fill database query
	private transient Logger logger = Logger.getLogger(CommonService.class);
	private static int customNameFeatureId = -1;
	private static Map<Integer, String> observationTargetNameMap = null;
	
	// --- Stuff for Singleton design pattern
	private static CommonService instance = null;
	
	private CommonService() {
		
	}
	
	public static CommonService getInstance() {
		if (instance == null) {
			instance = new CommonService();
		}
		return instance;
	}
	// ---
	
	public void setDatabase(Database db)
	{
	    CommonService.db = db;
	}

	/**
	 * Sets the id of the ObservableFeature the user has chosen as custom name for the
	 * ObservationTargets, and then makes a map of ObservationTargets and their names/labels.
	 * 
	 * @throws ParseException 
	 * @throws DatabaseException 
	 */
	public void setCustomNameFeatureId(int customNameFeatureId) throws DatabaseException, ParseException {
		CommonService.customNameFeatureId = customNameFeatureId;
		makeObservationTargetNameMap();
	}
	
	/**
	 * Returns the id of the ObservableFeature the user has chosen as custom name for the
	 * ObservationTargets, or -1 if none was set.
	 * 
	 * @return
	 */
	public int getCustomNameFeatureId() {
		return CommonService.customNameFeatureId;
	}
	
	/**
	 * Begins a database transaction.
	 * 
	 * @throws DatabaseException
	 */
	public void beginTransaction() throws DatabaseException
	{
		db.beginTx();
	}

	/**
	 * Commits a database transaction.
	 * 
	 * @throws DatabaseException
	 */
	public void commitTransaction() throws DatabaseException
	{
		db.commitTx();
	}

	/**
	 * Retrieve an investigation id based on an investigation name
	 * 
	 * @param invName investigation name
	 * @return id of the investigation
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public int getInvestigationId(String invName) throws DatabaseException,
			ParseException
	{
		Query<Investigation> q = db.query(Investigation.class);
		q.addRules(new QueryRule(Investigation.NAME, Operator.EQUALS, invName));
		List<Investigation> invList = q.find();
		
		if (invList.size() > 0) {
		    return invList.get(0).getId();
		} else
		    throw new DatabaseException("No investigation can be found matching name: " + invName);
	}
	

	/**
	 * Retrieve an observation target by id
	 * 
	 * @param targetId the id to look for
	 * @return an ObservationTarget entity with Id targetId
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public ObservationTarget getObservationTargetById(int targetId)
			throws DatabaseException, ParseException
	{
		return db.findById(ObservationTarget.class, targetId);
	}
	
	/**
	 * Retrieve an individual by id
	 * 
	 * @param individualId the id to look for
	 * @return an Individual entity with Id individualId
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public Individual getIndividualById(int individualId)
			throws DatabaseException, ParseException
	{
		return db.findById(Individual.class, individualId);
	}

	/**
	 * Get the Id of a given ObservationTarget, searching by name
	 * 
	 * @param targetName the name of the ObservationTarget element to look for
	 * @return an integer matching the id of the ObservationTarget element
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public int getObservationTargetId(String targetName)
			throws DatabaseException, ParseException
	{
		return ObservationTarget.findByName(db, targetName).getId();
	}

	/**
	 * Creates an Individual but does NOT add it to the database.
	 * 
	 * @param investigationId id to use for new Individual
	 * @return new Individual
	 * @throws DatabaseException
	 * @throws ParseException
	 * @throws IOException
	 */
	public Individual createIndividual(int investigationId, String individualName) throws DatabaseException,
			ParseException, IOException
	{
		Individual newInd = new Individual();
		newInd.setInvestigation_Id(investigationId);
		newInd.setName(individualName); // placeholder
		return newInd;
	}

	/**
	 * Return a list of all observation targets of a certain type. If desired,
	 * filtered down to only the currently active ones.
	 * 
	 * @param type
	 *            : observation target type (Animal, Actor, Group, Location) to filter on, may be null
	 * @param isActive
	 *            : whether or not to filter on Active state
	 * @return List of ObservationTargets, if desired of a certain type
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public List<Integer> getAllObservationTargetIds(String type, boolean isActive) 
	throws DatabaseException, ParseException
	{
		List<Integer> returnList = new ArrayList<Integer>();
		if (isActive == false) {
			Query<ObservationTarget> targetQuery = db.query(ObservationTarget.class);
			if (type != null) {
				targetQuery.addRules(new QueryRule(ObservationTarget.__TYPE, Operator.EQUALS, 
						type));
			}
			List<ObservationTarget> targetList = targetQuery.find();
			for (ObservationTarget target : targetList) {
				returnList.add(target.getId());
			}
			return returnList;
		} else {
			// Find 'Active' target id's
			int featureId = getMeasurementId(ACTIVE);
			Query<ObservedValue> valueQuery = db.query(ObservedValue.class);
			valueQuery.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featureId));
			valueQuery.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.EQUALS, null));
			List<ObservedValue> valueList = valueQuery.find();
			List<Integer> activeIdList = new ArrayList<Integer>();
			for (ObservedValue value : valueList) {
				activeIdList.add(value.getTarget_Id());
			}
			// Find target id's of right type
			List<Integer> typeIdList = new ArrayList<Integer>();
			if (type != null) {
				Query<ObservationTarget> targetQuery = db.query(ObservationTarget.class);
				targetQuery.addRules(new QueryRule(ObservationTarget.__TYPE, Operator.EQUALS, 
						type));
				List<ObservationTarget> targetList = targetQuery.find();
				for (ObservationTarget target : targetList) {
					typeIdList.add(target.getId());
				}
			}
			// Keep overlap and return corresponding targets
			typeIdList.retainAll(activeIdList);
			return typeIdList;
		}
	}
	
	/** Returns all ObservationTargets currently in the database
	 * 
	 * @return list of observation targets
	 * @throws DatabaseException 
	 */
	public List<ObservationTarget> getAllObservationTargets() {
		try {
		    List<ObservationTarget> targets = db.find(ObservationTarget.class);
		    return targets;
		} catch(DatabaseException dbe) {
		    return new ArrayList<ObservationTarget>();
		}
		
	}
	
	public List<MolgenisBatch> getAllBatches() {
	    try {
		    List<MolgenisBatch> batches = db.find(MolgenisBatch.class);
		    return batches;
		} catch(DatabaseException dbe) {
		    return new ArrayList<MolgenisBatch>();
		}
	}
	
	/** 
	 * Returns a list of ObservationTargets belonging to the ID's provided.
	 * Returns an empty list when no ID's are passed.
	 * 
	 * @param idList : the ID's of the desired ObservationTargets
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public List<ObservationTarget> getObservationTargets(List<Integer> idList) throws DatabaseException, ParseException {
		if (idList.size() > 0) {
			Query<ObservationTarget> targetQuery = db.query(ObservationTarget.class);
			targetQuery.addRules(new QueryRule(ObservationTarget.ID, Operator.IN, idList));
			return targetQuery.find();
		} else {
		    return new ArrayList<ObservationTarget>();
		}
	}
	
	/**
	 * Get the name or -if existent- custom label for the ObservationTarget.
	 * 
	 * @param targetId
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public String getObservationTargetLabel(int targetId) throws DatabaseException, ParseException {
		if (observationTargetNameMap == null) {
			return getObservationTargetById(targetId).getName();
		}
		if (observationTargetNameMap.get(targetId) != null) {
			return observationTargetNameMap.get(targetId);
		}
		return getObservationTargetById(targetId).getName();
	}
	
	/** 
	 * Returns a list of Individuals belonging to the ID's provided.
	 * Returns an empty list when no ID's are passed.
	 * 
	 * @param idList : the ID's of the desired Individuals
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public List<Individual> getIndividuals(List<Integer> idList) throws DatabaseException, ParseException {
		if (idList.size() > 0) {
			Query<Individual> targetQuery = db.query(Individual.class);
			targetQuery.addRules(new QueryRule(Individual.ID, Operator.IN, idList));
			return targetQuery.find();
		} else {
		    return new ArrayList<Individual>();
		}
	}
	
	/** Makes a map of all ObservationTarget id's and names. 
	 * The names are retrieved using the feature name specified, or -if no feature is specified-
	 * the normal database name is taken.
	 * 
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public void makeObservationTargetNameMap() throws DatabaseException, ParseException {
		observationTargetNameMap = new HashMap<Integer, String>();
		List<Integer> targetIdList = new ArrayList<Integer>();
		// First fill with standard names
		try {
			targetIdList = getAllObservationTargetIds(null, false);
		} catch (DatabaseException e) {
			// targetIdList will remain empty
		}
		for (Integer targetId : targetIdList) {
			observationTargetNameMap.put(targetId, getObservationTargetById(targetId).getName());
		}
		// Then overwrite with custom names, if existing
		if (CommonService.customNameFeatureId != -1) {
			Query<ObservedValue> valueQuery = db.query(ObservedValue.class);
			valueQuery.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, CommonService.customNameFeatureId));
			valueQuery.addRules(new QueryRule(ObservedValue.TARGET, Operator.IN, targetIdList));
			List<ObservedValue> valueList = valueQuery.find();
			for (ObservedValue value : valueList) {
				if (value.getValue() != null) {
					// We have a String value that we can use
					observationTargetNameMap.put(value.getTarget_Id(), value.getValue());
				} else {
					// No value, so use relation
					observationTargetNameMap.put(value.getTarget_Id(), value.getRelation_Name());
				}
			}
		}
	}

	/**
	 * Creates a Location and adds it to the database.
	 * 
	 * @param investigationId
	 * @param locationName
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 * @throws IOException
	 */
	public int makeLocation(int investigationId, String locationName)
			throws DatabaseException, ParseException, IOException
	{
		Location locationToAdd = new Location();
		locationToAdd.setName(locationName);
		locationToAdd.setInvestigation(investigationId);
		db.add(locationToAdd);
		return locationToAdd.getId();
	}

	/**
	 * Creates a Panel and adds it to the database.
	 * 
	 * @param investigationId
	 * @param panelName
	 * @return
	 * @throws DatabaseException
	 * @throws IOException
	 * @throws ParseException
	 */
	public int makePanel(int investigationId, String panelName)
			throws DatabaseException, IOException, ParseException
	{
		Panel newGroup = new Panel();
		newGroup.setName(panelName);
		newGroup.setInvestigation(investigationId);
		db.add(newGroup);
		return newGroup.getId();
	}

	/**
	 * Returns a list of all Panels that have been given a certain mark
	 * using a 'TypeOfGroup' ObservedValue.
	 * 
	 * @param mark
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	// TODO: think of what to do with species, sexes, sources etc.
	// Do we want to keep using the 'TypeOfGroup' Measurement or do we want to use Pheno
	// entities like Species etc.
	public List<Panel> getAllMarkedPanels(String mark)
			throws DatabaseException, ParseException
	{
		List<Panel> returnList = new ArrayList<Panel>();
		List<Panel> panelList = db.find(Panel.class);

		int featureid = getMeasurementId("TypeOfGroup");

		for (Panel tmpPanel : panelList) {
			Query<ObservedValue> valueQuery = db.query(ObservedValue.class);
			valueQuery.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, tmpPanel.getId()));
			valueQuery.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featureid));
			valueQuery.addRules(new QueryRule(ObservedValue.VALUE, Operator.EQUALS, mark));
			List<ObservedValue> valueList = valueQuery.find();
			if (valueList.size() > 0) {
				returnList.add(tmpPanel);
			}
		}

		return returnList;
	}

	/**
	 * Creates an ObservedValue for adding an ObservationTarget to a Group, but does NOT add this to the database.
	 * 
	 * @param investigationId
	 * @param targetid
	 * @param tmpDate
	 * @param groupid
	 * @throws DatabaseException
	 * @throws ParseException
	 * @throws IOException
	 */
	// TODO: keep using this or use normal mref?
	public ObservedValue addObservationTargetToPanel(int investigationId, int targetid,
			Date tmpDate, int groupid) throws DatabaseException,
			ParseException, IOException
	{
		// First, check is target is already in this Panel
		int featureid = getMeasurementId("Group");
		Query<ObservedValue> q = db.query(ObservedValue.class);
		q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featureid));
		q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, targetid));
		q.addRules(new QueryRule(ObservedValue.RELATION, Operator.EQUALS, groupid));
		// TODO: check for end date?
		List<ObservedValue> valueList = q.find();
		
		if (valueList.size() == 0) {
			int protocolId = getProtocolId("SetGroup");
			ProtocolApplication app = createProtocolApplication(investigationId, protocolId);
			db.add(app);
			return createObservedValue(investigationId, app.getId(), tmpDate, null, featureid, targetid, null, groupid);
		}
		// Target is already in group, so do not make value
		return null;
	}

	/**
	 * Create a new actor and adds it to the database
	 * 
	 * @param investigationId investigation id
	 * @param actorName name of actor
	 * @return the id of the new actor
	 * @throws DatabaseException
	 * @throws ParseException
	 * @throws IOException
	 */
	@Deprecated
	public int makeActor(int investigationId, String actorName)
			throws DatabaseException, ParseException, IOException
	{
		ObservationTarget actorToAdd = new ObservationTarget();
		actorToAdd.setName(actorName);
		actorToAdd.setInvestigation_Id(investigationId);
		//actorToAdd.setOntologyReference_Name(ACTOR);
		db.add(actorToAdd);
		return actorToAdd.getId();
	}
	
	/**
	 * For a given MolgenisUser id, returns the corresponding Actor id.
	 * 
	 * @param userId
	 * @return the id of the Actor
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	@Deprecated
	public int getActorId(int userId) throws DatabaseException, ParseException {
		int measurementId = getMeasurementId("MolgenisUserId");
		Query<ObservedValue> q = db.query(ObservedValue.class);
		q.addRules(new QueryRule(ObservedValue.VALUE, Operator.EQUALS, Integer.toString(userId)));
		q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, measurementId));
		List<ObservedValue> valueList = q.find();
		if (valueList.size() > 0) {
			return valueList.get(0).getTarget_Id();
		} else {
			throw new DatabaseException("No Actor found for MolgenisUser id " + userId);
		}
	}

	/**
	 * Makes a new application of the given protocol
	 * but does NOT add it to the database.
	 * 
	 * @param investigationId
	 * @param protocolId
	 * @return the ProtocolApplication that was created
	 * @throws ParseException
	 * @throws DatabaseException
	 * @throws IOException
	 */
	public ProtocolApplication createProtocolApplication(int investigationId, int protocolId) throws ParseException,
			DatabaseException, IOException
	{
		Date now = Calendar.getInstance().getTime();
		ProtocolApplication pa = new ProtocolApplication();
		pa.setInvestigation(investigationId);
		pa.setName(protocolId + "_" + protAppCounter++ + "_" + now.toString()); // strange but unique name
		pa.setProtocol(protocolId);
		pa.setTime(now);
		return pa;
	}
	
	/**
	 * Makes a new ProtocolApplication, adds it to the database and return its id.
	 * 
	 * @param investigationId
	 * @param protocolId
	 * @return the id of the ProtocolApplication that was added to the database
	 * @throws DatabaseException
	 * @throws IOException
	 */
	public int makeProtocolApplication(int investigationId, int protocolId) throws DatabaseException, IOException {
		Date now = Calendar.getInstance().getTime();
		ProtocolApplication pa = new ProtocolApplication();
		pa.setInvestigation(investigationId);
		pa.setName(protocolId + "_" + protAppCounter++ + "_" + now.toString()); // strange but unique name
		pa.setProtocol(protocolId);
		pa.setTime(now);
		db.add(pa);
		return pa.getId();
	}

	/**
	 * Find all protocol applications by protocol
	 * 
	 * @param protocolid the id of the protocol to search for
	 * @return A list of matching ProtocolApplications
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public List<ProtocolApplication> getAllProtocolApplicationsByType(
			int protocolid) throws DatabaseException, ParseException
	{
		Query<ProtocolApplication> q = db.query(ProtocolApplication.class);
		q.addRules(new QueryRule("protocol", Operator.EQUALS, protocolid));
		return q.find();
	}

	/**
	 * Retrieve a protocol application based on id
	 * 
	 * @param protappid the id to use for searching
	 * @return ProtocolApplication matching given id
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public ProtocolApplication getProtocolApplicationById(int protappid)
			throws DatabaseException, ParseException
	{
		Query<ProtocolApplication> q = db.query(ProtocolApplication.class);
		q.addRules(new QueryRule("id", Operator.EQUALS, protappid));
		List<ProtocolApplication> eventList = q.find();
		
		if(eventList.get(0) != null)
		    return eventList.get(0);
		else
		    throw new DatabaseException("No protocol application with id " + 
			    protappid + " was found.");
	}

	/**
	 * Creates an ObservedValue with the given parameters, but does NOT add it to the database.
	 * 
	 * @param investigationId
	 * @param protappid
	 * @param starttime
	 * @param endtime
	 * @param featureid
	 * @param subjectTargetId
	 * @param valueString
	 * @param targetRef
	 * @return ObservedValue
	 * @throws DatabaseException
	 * @throws IOException
	 * @throws ParseException
	 */
	public ObservedValue createObservedValue(int investigationId, int protappid,
			Date starttime, Date endtime, int featureId,
			int subjectTargetId, String valueString, int targetRef)
			throws DatabaseException, IOException, ParseException
	{
		ObservedValue newValue = new ObservedValue();
		newValue.setInvestigation(investigationId);
		newValue.setProtocolApplication(protappid);
		newValue.setFeature(featureId);
		newValue.setTime(starttime);
		newValue.setEndtime(endtime);
		newValue.setTarget(subjectTargetId);
		if (targetRef != 0) {
			newValue.setRelation(targetRef);
		} else {
			newValue.setValue(valueString);
		}
		return newValue;
	}
	
	/**
	 * First makes a ProtocolApplication and adds it to the Database.
	 * Then creates an ObservedValue with the given parameters and a link to the ProtocolApplication just made,
	 * but does NOT add it to the database.
	 * Warning: because this method involves a database transaction, it is not suited for use
	 * with batch lists.
	 * 
	 * @param investigationId
	 * @param starttime
	 * @param endtime
	 * @param protocolName
	 * @param featureName
	 * @param subjectTargetId
	 * @param valueString
	 * @param targetRef
	 * @return ObservedValue
	 * @throws DatabaseException
	 * @throws IOException
	 * @throws ParseException
	 */
	public ObservedValue createObservedValueWithProtocolApplication(int investigationId,
			Date starttime, Date endtime, int protocolId, int featureId,
			int subjectTargetId, String valueString, int targetRef)
			throws DatabaseException, IOException, ParseException
	{
		// Make and add ProtocolApplication
		ProtocolApplication app = createProtocolApplication(investigationId, protocolId);
		db.add(app);
		
		ObservedValue newValue = new ObservedValue();
		newValue.setInvestigation(investigationId);
		newValue.setProtocolApplication(app.getId());
		newValue.setFeature(featureId);
		newValue.setTime(starttime);
		newValue.setEndtime(endtime);
		newValue.setTarget(subjectTargetId);
		if (targetRef != 0) {
			newValue.setRelation(targetRef);
		} else {
			newValue.setValue(valueString);
		}
		return newValue;
	}

	/**
	 * For a given ObservationTarget and ObservableFeature, returns
	 * the value of the most recent ObservedValue,
	 * based on the timestamp of its ProtocolApplication. 
	 * 
	 * @param targetid
	 * @param featureid
	 * @return String: the most recent value for given feature and target
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public String getMostRecentValueAsString(int targetid, int featureid)
			throws DatabaseException, ParseException
	{
		Query<ObservedValue> q = db.query(ObservedValue.class);
		q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, targetid));
		q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featureid));
		// Discussion: if you uncomment the previous line, only values are retrieved
		// that have endtime 'null', i.e. values that are still valid.
		// Is this desirable? Maybe we could use a boolean to switch this behavior on and off?
		// q.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.EQUALS, null));
		List<ObservedValue> valueList = q.find();
		if (valueList.size() > 0) {
			ObservedValue returnValue = null;
			Date storedTime = null;
			for (ObservedValue currentValue : valueList) {
				int protappId = currentValue.getProtocolApplication_Id();
				ProtocolApplication protapp = getProtocolApplicationById(protappId);
				Date protappTime = protapp.getTime();
				if (storedTime == null || protappTime.after(storedTime)) {
					returnValue = currentValue;
					storedTime = protappTime;
				}
			}
			return returnValue.getValue();
		} else {
			return "";
			// Discussion: I'm unhappy with the solution below (commented out) because
			// it's perfectly normal for a target-feature combination not to have a value,
			// so this should not cause an exception but just return an empty string.
			//throw new DatabaseException("No valid values were found for targetid: " + 
			//	targetid + " and featureid: " + featureid);
		}
	}

	/**
	 * For a given ObservationTarget and ObservableFeature, returns
	 * the ObservationTarget related to in the most recent ObservedValue,
	 * based on the timestamp of its ProtocolApplication. 
	 * 
	 * @param targetid
	 * @param featureid
	 * @return int: ID of relation in most recent value for given feature and target
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public int getMostRecentValueAsXref(int targetid, int featureid)
			throws DatabaseException, ParseException
	{
		Query<ObservedValue> q = db.query(ObservedValue.class);
		q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, targetid));
		q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, featureid));
		// Discussion: if you uncomment the previous line, only values are retrieved
		// that have endtime 'null', i.e. values that are still valid.
		// Is this desirable? Maybe we could use a boolean to switch this behavior on and off?
		//q.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.EQUALS, null));
		q.addRules(new QueryRule(Operator.SORTDESC, ObservedValue.TIME));
		List<ObservedValue> valueList = q.find();
		if (valueList.size() > 0) {
			ObservedValue returnValue = null;
			Date storedTime = null;
			for (ObservedValue currentValue : valueList) {
				int protappId = currentValue.getProtocolApplication_Id();
				ProtocolApplication protapp = getProtocolApplicationById(protappId);
				Date protappTime = protapp.getTime();
				if (storedTime == null || protappTime.after(storedTime)) {
					returnValue = currentValue;
					storedTime = protappTime;
				}
			}
			return returnValue.getRelation_Id();
		} else {
			return -1;
			// Discussion: I'm unhappy with the solution below (commented out) because
			// it's perfectly normal for a target-feature combination not to have a value,
			// so this should not cause an exception but just return a dummy xref id.
		    //throw new DatabaseException("No valid values were found for targetid: " + 
			//	targetid + " and featureid: " + featureid);
		}
	}

	/**
	 * Makes a new protocol and adds it to the database
	 * 
	 * @param investigationId
	 * @param protocolName
	 * @return
	 * @throws ParseException
	 * @throws DatabaseException
	 * @throws IOException
	 */
	public int makeProtocol(int investigationId, String protocolName)
			throws ParseException, DatabaseException, IOException
	{
		Protocol newProtocol = new Protocol();
		newProtocol.setName(protocolName);
		if (investigationId != -1)
		{
			newProtocol.setInvestigation(investigationId);
		}
		db.add(newProtocol);
		return newProtocol.getId();
	}

	/**
	 * Makes a new protocol and adds it to the database
	 * 
	 * @param investigationId
	 * @param protocolName
	 * @param description
	 * @param locFeatIdList
	 * @return
	 * @throws DatabaseException
	 * @throws IOException
	 * @throws ParseException
	 */
	public int makeProtocol(int investigationId, String protocolName,
			String description, List<Integer> locFeatIdList)
			throws DatabaseException, IOException, ParseException
	{
		Protocol newProtocol = new Protocol();
		newProtocol.setName(protocolName);
		newProtocol.setInvestigation(investigationId);
		newProtocol.setFeatures_Id(locFeatIdList);
		newProtocol.setDescription(description);
		db.add(newProtocol);
		return newProtocol.getId();
	}

	/**
	 * Finds a protocol id based on protocol name
	 * 
	 * @param eventtypeName
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public int getProtocolId(String protocolName) throws DatabaseException,
			ParseException
	{
		Query<Protocol> q = db.query(Protocol.class);
		q.addRules(new QueryRule(Protocol.NAME, Operator.EQUALS,
					protocolName));
		List<Protocol> protocolList = q.find();
		return protocolList.get(0).getId();
	}

	/**
	 * Returns all protocols existing in the database
	 * 
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public List<Protocol> getAllProtocols() throws DatabaseException,
			ParseException
	{
		Query<Protocol> q = db.query(Protocol.class);
		return q.find();
	}

	/**
	 * Find a protocol based on protocol name.
	 * 
	 * @param eventtypeName
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public Protocol getProtocol(String protocolName) throws DatabaseException,
			ParseException
	{
		Query<Protocol> q = db.query(Protocol.class);
		q.eq(Protocol.NAME, protocolName);
		if(q.find() != null)
		    return q.find().get(0);
		else
		    throw new DatabaseException("No protocol with name " + protocolName +
			    " could be found");
	}

	/**
	 * Get all protocols, sorted on name (ASC or DESC).
	 * 
	 * @param sortField : field to sort on
	 * @param sortOrder: ASCending or DESCending
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public List<Protocol> getAllProtocolsSorted(String sortField,
			String sortOrder) throws DatabaseException, ParseException
	{
		Query<Protocol> q = db.query(Protocol.class);
		if (sortOrder.equals("ASC")) {
			q.sortASC(sortField);
		} else {
			q.sortDESC(sortField);
		}
		return q.find();
	}

	/**
	 * Find a protocol based on protocolId
	 * 
	 * @param protocolId
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public Protocol getProtocolById(int protocolId) throws DatabaseException,
			ParseException
	{
		return db.findById(Protocol.class, protocolId);
	}

	/**
	 * Find an ontologyterm based on an ontologyterm id
	 * 
	 * @param ontologyId
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public OntologyTerm getOntologyTermById(int ontologyId)
			throws DatabaseException, ParseException
	{
		return db.findById(OntologyTerm.class, ontologyId);
	}

	/**
	 * Find an ontologyterm id, based on ontologyterm name 
	 * 
	 * @param name
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public int getOntologyTermId(String name) throws DatabaseException,
			ParseException
	{
		Query<OntologyTerm> q = db.query(OntologyTerm.class);
		q.eq(OntologyTerm.NAME, name);
		if(q.find() != null)
		    return q.find().get(0).getId();
		else
		    throw new DatabaseException("No ontologyterm id could be located for" +
			    "ontologyterm with name " + name);
	}

	/**
	 * Create a new ontologyterm and add it to the database, returning the id
	 * 
	 * @param name
	 * @param ontologyId
	 * @param description
	 * @return new ontologyterm id
	 * @throws DatabaseException
	 * @throws IOException
	 * @throws ParseException
	 */
	public int makeOntologyTerm(String name, int ontologyId, String description)
			throws DatabaseException, IOException, ParseException
	{
		OntologyTerm newOnt = new OntologyTerm();
		newOnt.setName(name);
		newOnt.setOntology(ontologyId);
		newOnt.setDefinition(description);
		db.add(newOnt);
		return newOnt.getId();
	}

	/**
	 * Gets the ID of the observable feature with the name "featureName"
	 * 
	 * @param featureName
	 * @return int ID
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public int getMeasurementId(String measurementName) throws DatabaseException,
			ParseException
	{
		Query<Measurement> q = db.query(Measurement.class);
		q.eq(Measurement.NAME, measurementName);
		List<Measurement> featList = q.find();
		if (featList.size() > 0) 
		    return featList.get(0).getId();
		else
		    throw new DatabaseException("Id could be found for" +
			    " Measurement with name: " + measurementName);
	}


	/**
	 * Return a list of all measurements in the database
	 * 
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public List<Measurement> getAllMeasurements()
			throws DatabaseException, ParseException
	{
		Query<Measurement> q = db.query(Measurement.class);
		q.sortASC("id");
		return q.find();
	}
	
	/**
	 * Get all Measurement, sorted on name (ASC or DESC).
	 * 
	 * @param sortField : field to sort on
	 * @param sortOrder: ASCending or DESCending
	 * @return A list of all ObservableFeatures sorted by argument
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public List<Measurement> getAllMeasurementsSorted(String sortField,
		String sortOrder) throws DatabaseException, ParseException
	{
		Query<Measurement> q = db.query(Measurement.class);
		if (sortOrder.equals("ASC")) {
			q.sortASC(sortField);
		} else {
			q.sortDESC(sortField);
		}
		return q.find();
	}
	
	/**
	 * Returns a list of all existing ObservableFeatures.
	 * 
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public List<ObservableFeature> getAllObservableFeatures()
			throws DatabaseException, ParseException
	{
		Query<ObservableFeature> q = db.query(ObservableFeature.class);
		q.sortASC(ObservableFeature.ID);
		return q.find();
	}

	/**
	 * Returns a list of all ObservableFeatures for a Protocol
	 * 
	 * @param protocolId
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public List<ObservableFeature> getObservableFeaturesByProtocol(
			int protocolId) throws DatabaseException, ParseException {
		
		Query<Protocol> q = db.query(Protocol.class);
		q.eq(Protocol.ID, protocolId);
		List<Protocol> protocols = q.find();

		List<ObservableFeature> features = new ArrayList<ObservableFeature>();
		if (!protocols.isEmpty()) { 
		    List<Integer> featureIds = protocols.get(0).getFeatures_Id();
		    for (Integer i : featureIds) {
				Query<ObservableFeature> r = db.query(ObservableFeature.class);
				r.eq(ObservableFeature.ID, i);
				features.addAll(r.find());
		    }
		}
		
		return features;
	}

	/**
	 * Finds a Measurement entity by its id
	 * 
	 * @param featureId
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public Measurement getMeasurementById(int featureId)
			throws DatabaseException, ParseException
	{
		return db.findById(Measurement.class, featureId);
	}
	
	/**
	 * Create and add a Measurement to the database.
	 * Note: unlike the other makeMeasurement(), this method does NOT set the Investigation, the Unit,
	 * the TargettypeAllowedForRelation, the panelLabelAllowedForRelation and the Temporal fields.
	 * 
	 * @param name
	 *            observablefeature name
	 * @param description
	 *            observablefeature description
	 * @param dataType
	 *            the data type of the observablefeature
	 * @throws IOException
	 * @throws DatabaseException
	 */
	public void makeMeasurement(String name, String description, String dataType)
			throws DatabaseException, IOException
	{
		Measurement feature = new Measurement();
		feature.setName(name);
		feature.setDataType(dataType);
		feature.setDescription(description);
		db.add(feature);
	}

	/**
	 * Create a new Measurement and add it to the databases
	 * 
	 * @param investigationId
	 * @param name
	 * @param unitId
	 * @param targettypeAllowedForRelation
	 * @param panelLabelAllowedForRelation
	 * @param focal
	 * @param dataType
	 * @param description
	 * @return
	 * @throws DatabaseException
	 * @throws IOException
	 * @throws ParseException
	 */
	public int makeMeasurement(int investigationId, String name, int unitId,
			MolgenisEntity targettypeAllowedForRelation, String panelLabelAllowedForRelation,
			boolean temporal, String dataType, String description)
	throws DatabaseException, IOException, ParseException
	{
		Measurement newFeat = new Measurement();
		newFeat.setName(name);
		newFeat.setInvestigation(investigationId);
		newFeat.setUnit(unitId);
		if (targettypeAllowedForRelation != null) {
			newFeat.setTargettypeAllowedForRelation(targettypeAllowedForRelation);
		}
		if (panelLabelAllowedForRelation != null) {
			newFeat.setPanelLabelAllowedForRelation(panelLabelAllowedForRelation);
		}
		newFeat.setTemporal(temporal);
		newFeat.setDataType(dataType);
		newFeat.setDescription(description);
		db.add(newFeat);
		return newFeat.getId();
	}

	/**
	 * Creates a new entry in the Code table.
	 * 
	 * @param code : the code itself
	 * @param desc : the description of what the code stands for
	 * @param feat : the name of the ObservableFeature this Code can be used for
	 * @throws DatabaseException
	 * @throws ParseException
	 * @throws IOException
	 */
	public void makeCode(String code, String desc, String feat)
			throws DatabaseException, ParseException, IOException
	{
		Code newCode = new Code();
		newCode.setCode_String(code);
		newCode.setDescription(desc);
		List<Integer> locFeatIdList = new ArrayList<Integer>();
		locFeatIdList.add(getMeasurementId(feat));
		newCode.setFeature_Id(locFeatIdList);
		db.add(newCode);
	}

	/**
	 * Finds all Code entities for a given feature name and returns their descriptions.
	 * 
	 * @param featurename
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public List<String> getAllCodesForFeatureAsStrings(String featurename)
			throws DatabaseException, ParseException
	{
		int featureid = getMeasurementId(featurename);
		Query<Code> q = db.query(Code.class);
		q.eq(Code.FEATURE, featureid);
		List<String> returnList = new ArrayList<String>();
		List<Code> tmpList = q.find();
		for (Code code : tmpList) {
			returnList.add(code.getDescription());
		}
		return returnList;
	}

	/**
	 * Finds all code entities for a given feature name
	 * 
	 * @param featureName
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public List<Code> getAllCodesForFeature(String featureName)
			throws DatabaseException, ParseException
	{
		int featureId = getMeasurementId(featureName);
		return db.query(Code.class).eq(Code.FEATURE, featureId).find();
	}

	/**
	 * Add one or more ObservationTargets to the database.
	 * 
	 * @param targets : the observation targets to be added
	 * @throws DatabaseException
	 * @throws IOException
	 */
	public void addObservationTargets(List<ObservationTarget> targets)
			throws DatabaseException, IOException
	{
		for (ObservationTarget o : targets)
		{
			db.add(o);
		}

	}

	/**
	 * Return all lab workers from the database.
	 * 
	 * @return Collection of Contacts
	 * @throws ParseException
	 * @throws DatabaseException
	 */
	public Collection<NgsPerson> getAllLabWorkers() throws DatabaseException,
			ParseException
	{
		Query<NgsPerson> q = db.query(NgsPerson.class);
		q.addRules(new QueryRule("labworker", Operator.EQUALS, true));
		return q.find();
	}

	/**
	 * Add a protocol to the database
	 * 
	 * @param name
	 *            protocol name
	 * @param description
	 *            protocol description
	 * @throws IOException
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public void addProtocol(String name, String description,
			List<String> observableFeatureNames) throws DatabaseException,
			IOException, ParseException
	{
		Protocol protocol = new Protocol();
		protocol.setName(name);
		protocol.setDescription(description);

		List<Integer> featureIds = new ArrayList<Integer>();

		for (String s : observableFeatureNames)
		{
			// TODO: This query is inefficient - returns a bunch of objects when
			// only IDs are needed
			// How can only IDs be returned in this query system?
			Query<ObservableFeature> q = db.query(ObservableFeature.class);
			q.eq(ObservableFeature.NAME, s);
			if (!q.find().isEmpty())
			{
				featureIds.add(q.find().get(0).getId());
			}
			else
			{ // this should not happen
				logger.error("Name could not be found and feature:" + s
						+ " will not be added to the protocol");
			}
		}

		protocol.setFeatures_Id(featureIds);
		db.add(protocol);
	}

	/**
	 * Add a lab worker to the database
	 * 
	 * @param fName
	 *            first name of lab tech
	 * @param lName
	 *            last name of lab tech
	 * @param email
	 *            email of lab tech
	 * @throws IOException
	 * @throws DatabaseException
	 */
	public void addLabWorker(String fName, String lName, String email)
			throws DatabaseException, IOException
	{
		NgsPerson worker = new NgsPerson();
		worker.setFirstName(fName);
		worker.setLastName(lName);
		worker.setEmail(email);
		worker.setLabworker(true);
		db.add(worker);
	}

	/**
	 * Find a lab worker based on first and last name (Exact matching)
	 * 
	 * @param firstName
	 *            first name of lab worker
	 * @param lastName
	 *            last name of lab worker
	 * @return a List of Contacts matching this name (Guaranteed to be a list of
	 *         0 or 1 = firstname + lastname must be unique, and exact matching
	 *         only)
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public List<NgsPerson> getLabWorkerByName(String firstName, String lastName)
			throws DatabaseException, ParseException
	{
		Query<NgsPerson> q = db.query(NgsPerson.class);
		q.eq(Person.FIRSTNAME, firstName);
		q.eq(Person.LASTNAME, lastName);
		
		q.addRules(new QueryRule("labworker", Operator.EQUALS, true));

		return q.find();
	}

	/**
	 * Retrieve the sampletype from a project Note: Each project's samples must
	 * be of the same type
	 * 
	 * @param projectName
	 *            name of the project
	 * @return string containing the sample type (i.e. "dna" or "rna")
	 * @throws DatabaseException
	 *             database error occured
	 */
	public String getSampleTypeByProject(String projectName)
			throws DatabaseException
	{

		String answer = "";
		List<Tuple> tuples;

		tuples = ((JDBCDatabase) db)
				.sql("Select s.sampletype from NgsSample s, Project p where p.name = '"
						+ projectName + "' AND s.project = p.id;");

		if (!tuples.isEmpty())
		{
			answer = tuples.get(0).getString("sampletype");
		}
		else
		    logger.error("A database inconsistency occured while trying to retrieve sampletype for a project");

		return answer;
	}

	/**
	 * Find all samples for a project
	 * 
	 * @param projectName
	 *            project for whom we want to find samples.
	 * @return a list containing all samples found for a given project
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public List<NgsSample> getAllSamplesForProject(String projectName)
	throws DatabaseException, ParseException {

	    Query<Project> q = db.query(Project.class);
	    q.equals(Project.NAME, projectName);
	    List<Project> project = q.find();

	    if (!project.isEmpty())
	    { 

		Integer id = project.get(0).getId(); 

		Query<NgsSample> p = db.query(NgsSample.class);
		p.eq(NgsSample.PROJECT, id);
		return p.find();

	    }
	    else
	    {
		logger.warn("Project " + projectName
			+ " could not be found in the database");
		return new ArrayList<NgsSample>();
	    }

	}

	/**
	 * Return all samples found in database
	 * 
	 * @return list of samples
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public List<NgsSample> getAllSamples() throws DatabaseException,
			ParseException
	{
		Query<NgsSample> q = db.query(NgsSample.class);
		q.sortASC(NgsSample.ID);

		return q.find();
	}

	/**
	 * Find a sample by it's name
	 * 
	 * @param sampleName
	 *            name of sample to find information for
	 * @return Sample object containing all information for given sample
	 * @throws ParseException
	 * @throws DatabaseException
	 */
	public NgsSample getSampleByName(String sampleName) throws DatabaseException,
			ParseException
	{
		Query<NgsSample> q = db.query(NgsSample.class);
		q.eq(NgsSample.NAME, sampleName);
		if (!q.find().isEmpty())
		{
			return q.find().get(0);
		}
		else
			throw new DatabaseException("Sample not found");

	}

	/**
	 * Returns all observed values for a given sample and list of features.
	 * NOTE: Creates a default "" value if observedValue doesn't exist yet for
	 * given feature and sample. This created default does not link to a
	 * ProtocolApplication and this method isn't responsible for storing this
	 * object in the database.
	 * 
	 * @param sampleId
	 * @param features
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public List<ObservedValue> getObservedValueBySampleAndFeatures(
			int sampleId, List<ObservableFeature> features)
			throws DatabaseException, ParseException
	{

		List<ObservedValue> values = new ArrayList<ObservedValue>();

		for (ObservableFeature f : features)
		{ // for each feature, find value
			Query<ObservedValue> q = db.query(ObservedValue.class);
			q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS,
					sampleId));
			q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, f
					.getId()));

			if (q.find().isEmpty())
			{ // if value doesnt exist, create new one
				ObservedValue newOV = new ObservedValue();
				newOV.setFeature(f);
				newOV.setValue("");
				newOV.setTarget(sampleId);
				newOV.setInvestigation(this.getSampleById(sampleId).getInvestigation_Id());
				values.add(newOV);
			}
			else
			{
				values.add(q.find().get(0));
			}

		}

		return values;
	}
	
	/** TODO: Change name and clean up to make above method more generic
	 * Returns all observed values for a given sample and list of features.
	 * NOTE: Creates a default "" value if observedValue doesn't exist yet for
	 * given feature and sample. This created default does not link to a
	 * ProtocolApplication and this method isn't responsible for storing this
	 * object in the database.
	 * 
	 * @param sampleId
	 * @param features
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public List<ObservedValue> getObservedValueByTargetAndFeatures(
			int sampleId, List<Integer> features)
			throws DatabaseException, ParseException
	{

		List<ObservedValue> values = new ArrayList<ObservedValue>();

		for (Integer i : features)
		{ // for each feature, find value
			Query<ObservedValue> q = db.query(ObservedValue.class);
			q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS,
					sampleId));
			q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, i));
			List<ObservedValue> vals = q.find();
			
			if (vals.isEmpty())
			{ // if value doesn't exist, create new one
				ObservedValue newOV = new ObservedValue();
				newOV.setFeature(i);
				newOV.setValue("");
				// don't set relation, as that can then never be reset to null
				newOV.setTarget(sampleId);
				newOV.setInvestigation(this.getObservationTargetById(sampleId).getInvestigation_Id());
				values.add(newOV);
			}
			else
			{
				ObservedValue returnValue = null;
				Date storedTime = null;
				
				for (ObservedValue currentValue : vals) {
					int protappId = currentValue.getProtocolApplication_Id();
					ProtocolApplication protapp = getProtocolApplicationById(protappId);
					Date protappTime = protapp.getTime();
					if (storedTime == null || protappTime.after(storedTime)) {
						returnValue = currentValue;
						storedTime = protappTime;
					}
				}
				values.add(returnValue);
			}

		}

		return values;
	}

	/** Finds a sample entity by its id
	 * 
	 * @param id
	 * @return
	 * @throws DatabaseException
	 */
	public NgsSample getSampleById(int id) throws DatabaseException {
	    NgsSample q = db.findById(NgsSample.class, id);
	    if(q == null) {
		    throw new DatabaseException("WorkflowElement not found");
		}
		else
		    return q;
	}

	/** Finds a protocolapplication entity by its name
	 * 
	 * @param name
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public ProtocolApplication getProtocolApplicationByName(String name)
			throws DatabaseException, ParseException
	{
		Query<ProtocolApplication> q = db.query(ProtocolApplication.class);
		q.addRules(new QueryRule(ProtocolApplication.NAME, Operator.EQUALS,
				name));
		if (!q.find().isEmpty())
		{
			return q.find().get(0);
		}
		else
		    throw new DatabaseException("No protocolapplication with name " +
			    name + "could be found.");
	}

	/**
	 * Gets a workflow
	 * 
	 * @param workflowName
	 * @throws ParseException
	 * @throws DatabaseException
	 */
	public Workflow getWorkflow(String workflowName) throws DatabaseException,
			ParseException
	{
		Query<Workflow> q = db.query(Workflow.class);
		q.addRules(new QueryRule(Workflow.NAME, Operator.EQUALS, workflowName));
		if (!q.find().isEmpty())
		{
			return q.find().get(0);
		}
		else
		    throw new DatabaseException("No workflow with name " +
			    workflowName + "could be found.");
	}

	/**
	 * Gets a workflowelement based on a workflowelement name
	 * 
	 * @param workflowName
	 * @throws ParseException
	 * @throws DatabaseException
	 */
	public WorkflowElement getWorkflowElement(String workflowElementName)
			throws DatabaseException, ParseException
	{
		Query<WorkflowElement> q = db.query(WorkflowElement.class);
		q.addRules(new QueryRule(WorkflowElement.NAME, Operator.EQUALS,
				workflowElementName));
		if (!q.find().isEmpty())
		{
			return q.find().get(0);
		}
		else
		    throw new DatabaseException("No workflowelement with name " +
			    workflowElementName + "could be found.");
	}
	
	/**
	 * Gets a workflowelement associated with a sample
	 * 
	 * @param workflowName
	 * @throws ParseException
	 * @throws DatabaseException
	 */
	public WorkflowElement getWorkflowElement(NgsSample sample) throws DatabaseException, ParseException
			
			
	{
	    Integer i = sample.getWorkflowElement_Id();
		WorkflowElement q = db.findById(WorkflowElement.class, i);
		if(q == null) {
		    throw new DatabaseException("WorkflowElement not found");
		}
		else
		    return q;
	}

	
	/** Returns a list of all workflow entities in the database
	 * 
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public List<Workflow> getAllWorkflows() throws DatabaseException,
			ParseException
	{
		Query<Workflow> q = db.query(Workflow.class);
		return q.find();
	}
	
	/** Queries to see if database is filled or not.
	 * 
	 * @return
	 */
	public boolean isNgsDatabaseFilled() {
	  if(isFilled) 
	      return true;
	  else
	      isFilled = true;
	      return false;
	}
	
	/** Finds an ontologyterm based on ontology name
	 * 
	 * @param name
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public OntologyTerm getOntologyTerm(String name) throws DatabaseException, ParseException {
	    Query<OntologyTerm> q = db.query(OntologyTerm.class);
		q.addRules(new QueryRule(OntologyTerm.NAME, Operator.EQUALS,
				name));
		if (!q.find().isEmpty())
		{
			return q.find().get(0);
		}
		else
			throw new DatabaseException("OntologyTerm not found");
	}
	
	/**
	 * 
	 * @param elementId
	 * @return
	 * @throws ParseException 
	 * @throws DatabaseException 
	 */
	public WorkflowElement getWorkflowElement(int elementId) throws DatabaseException, ParseException {
	    Query<WorkflowElement> q = db.query(WorkflowElement.class);
	    q.addRules(new QueryRule(WorkflowElement.ID, Operator.EQUALS,
		    elementId));
	    
	    if(!q.find().isEmpty()) {
		return q.find().get(0);
	    }
	    else
		throw new DatabaseException("Element does not exist");
	}
	/**
	 * 
	 * @param currentElementId
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public Set<WorkflowElement> getCandidateWorkflowElements(Integer currentElementId) throws DatabaseException, ParseException {
	    Set<WorkflowElement> list1 = new LinkedHashSet<WorkflowElement>();
	    Set<WorkflowElement> list2 = new LinkedHashSet<WorkflowElement>();
	    Set<WorkflowElement> answer = new LinkedHashSet<WorkflowElement>();

	    Query<WorkflowElement_PreviousSteps> q = db.query(WorkflowElement_PreviousSteps.class);
	    q.addRules(new QueryRule(WorkflowElement_PreviousSteps.PREVIOUSSTEPS, Operator.EQUALS,
		    currentElementId));

	    for(WorkflowElement_PreviousSteps w : q.find()) {
		Query<WorkflowElement> p = db.query(WorkflowElement.class);
		p.addRules(new QueryRule(WorkflowElement.ID, Operator.EQUALS,
			w.getWorkflowElement_Id()));
		list1.addAll(p.find()); // matching by ids, should only be 1 at a time..
	    }


	    List<WorkflowElement_Workflow> wkflowElements = new ArrayList<WorkflowElement_Workflow>();

	    for(Integer i : this.getWorkflowElement(currentElementId).getWorkflow_Id()) {
		Query<WorkflowElement_Workflow> r = db.query(WorkflowElement_Workflow.class);
		r.addRules(new QueryRule(WorkflowElement_Workflow.WORKFLOWELEMENT, Operator.EQUALS, i));
		wkflowElements.addAll(r.find());
	    }


	    for(WorkflowElement_Workflow t : wkflowElements) {
		Query<WorkflowElement> s = db.query(WorkflowElement.class);
		s.addRules(new QueryRule(WorkflowElement.ID, Operator.EQUALS,
			t.getAutoid()));
		list2.addAll(s.find()); // matching by ids, should only be 1 at a time..
	    }

	    for(WorkflowElement w : list1) {
		if(list2.contains(w)) {
		    answer.add(w);
		}
	    }

	    return answer;    


	}

	/** Get Investigation based on name
	 * 
	 * @param name
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public Investigation getInvestigation(String name) throws DatabaseException, ParseException {
	    Query<Investigation> s = db.query(Investigation.class);
	    s.addRules(new QueryRule(Investigation.NAME, Operator.EQUALS,
			name));
	
		if(s.find().isEmpty()) {
		    throw new DatabaseException("Investigation with name " + name + " does not exist.");
		}
		else
		    return s.find().get(0);
	}

	/** Get institute based on name
	 * 
	 * @param name
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public Institute getInstitute(String name) throws DatabaseException, ParseException {
	    Query<Institute> s = db.query(Institute.class);
	    s.addRules(new QueryRule(Institute.NAME, Operator.EQUALS,
		    name));
	
		if(s.find().isEmpty()) {
		    throw new DatabaseException("Institute with name " + name + " does not exist.");
		}
		else
		    return s.find().get(0);
	}

	/** Get NgsPerson by first name and last name
	 * 
	 * @param firstName
	 * @param lastName
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public NgsPerson getPerson(String firstName, String lastName) throws DatabaseException, ParseException {
	    Query<NgsPerson> s = db.query(NgsPerson.class);
	    s.addRules(new QueryRule(NgsPerson.FIRSTNAME, Operator.EQUALS,
		    firstName));
	    s.addRules(new QueryRule(NgsPerson.LASTNAME, Operator.EQUALS,
		    lastName));

	    if(s.find().isEmpty()) {
		throw new DatabaseException("Person with name " + firstName + " " + lastName + " does not exist.");
	    }
	    else
		return s.find().get(0);	
	}

	/** Get project by contract code
	 * 
	 * @param contractcode
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public Project getProject(String contractcode) throws DatabaseException, ParseException {
	    Query<Project> s = db.query(Project.class);
	    s.addRules(new QueryRule(Project.CONTRACTCODE, Operator.EQUALS,
		    contractcode));
	
		if(s.find().isEmpty()) {
		    throw new DatabaseException("Project with contractcode " + contractcode + " does not exist.");
		}
		else
		    return s.find().get(0);
	}

}
