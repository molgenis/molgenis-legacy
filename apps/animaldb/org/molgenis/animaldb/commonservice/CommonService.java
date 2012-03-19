package org.molgenis.animaldb.commonservice;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.molgenis.animaldb.CustomLabelFeature;
import org.molgenis.animaldb.NamePrefix;
import org.molgenis.auth.MolgenisUser;
import org.molgenis.batch.MolgenisBatch;
import org.molgenis.batch.MolgenisBatchEntity;
import org.molgenis.core.MolgenisEntity;
import org.molgenis.core.OntologyTerm;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Location;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.pheno.Panel;
import org.molgenis.protocol.Protocol;
import org.molgenis.protocol.ProtocolApplication;

import com.ibm.icu.util.Calendar;

/**
 * @author erikroos
 * 
 *         Changelog
 *         <li>MS: Added constants to replace the String values that
 *         lead to so many typos. Can't we autogenerate all these services?</li>
 */
public class CommonService
{
	public static final String ANIMAL = "Animal";
	public static final String ACTIVE = "Active";
	public static final String LOCATION = "Location";
	public static final String PANEL = "Panel";

	protected static Database db;
	private static int protAppCounter = 0; // for generating unique protocol application names
	private transient Logger logger = Logger.getLogger(CommonService.class);
	protected static Map<Integer, String> observationTargetNameMap = null;
	
	// --- Stuff for Singleton design pattern
	protected static CommonService instance = null;
	
	protected CommonService() {
	}
	
	public static CommonService getInstance() {
		if (instance == null) {
			instance = new CommonService();
		}
		return instance;
	}
	// ---
	
	public Database getDatabase() {
		return db;
	}
	
	public void setDatabase(Database db)
	{
	    CommonService.db = db;
	}

	/**
	 * Retrieve an investigation id based on an investigation name.
	 * Returns -1 if none found.
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
		} else {
		    return -1;
		}
	}
	
	/**
	 * Gets the name of the first investigation owned by the user with name 'userName'.
	 * Returns null if no investigations found.
	 * 
	 * @param userId
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public String getOwnUserInvestigationName(String userName) {
		Query<Investigation> q = db.query(Investigation.class);
		q.addRules(new QueryRule(Investigation.OWNS_NAME, Operator.EQUALS, userName));
		List<Investigation> invList;
		try {
			invList = q.find();
		} catch (Exception e) {
			return null;
		}
		if (invList.size() > 0) {
			return invList.get(0).getName();
		} else {
			return null;
		}
	}
	
	/**
	 * Gets the names of all the investigations owned by the user with name 'userName'.
	 * Returns null if no investigation found.
	 * 
	 * @param userName
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public List<String> getOwnUserInvestigationNames(String userName) {
		Query<Investigation> q = db.query(Investigation.class);
		q.addRules(new QueryRule(Investigation.OWNS_NAME, Operator.EQUALS, userName));
		List<Investigation> invList;
		List<String> returnList = new ArrayList<String>();
		try {
			invList = q.find();
		} catch (Exception e) {
			return null;
		}
		if (invList != null && invList.size() > 0) {
			for (Investigation inv : invList) {
				returnList.add(inv.getName());
			}
		} else {
			return null;
		}
		return returnList;
	}
	
	/**
	 * Gets the investigations owned, readable or writable by the user with name 'userName'.
	 * TODO: also take groups into account.
	 * 
	 * @param userName
	 * @return
	 */
	public List<Investigation> getAllUserInvestigations(String userName) {
		Query<Investigation> q = db.query(Investigation.class);
		q.addRules(new QueryRule(Investigation.OWNS_NAME, Operator.EQUALS, userName));
		q.addRules(new QueryRule(Operator.OR));
		q.addRules(new QueryRule(Investigation.CANREAD_NAME, Operator.EQUALS, userName));
		q.addRules(new QueryRule(Operator.OR));
		q.addRules(new QueryRule(Investigation.CANREAD_NAME, Operator.EQUALS, "AllUsers")); // FIXME evil!!!
		q.addRules(new QueryRule(Operator.OR));
		q.addRules(new QueryRule(Investigation.CANWRITE_NAME, Operator.EQUALS, userName));
		List<Investigation> invList;
		try {
			invList = q.find();
		} catch (Exception e) {
			return null;
		}
		return invList;
	}
	
	/**
	 * Gets the names of the investigations owned, readable or writable by the user with name 'userName'.
	 * 
	 * @param userName
	 * @return
	 */
	public List<String> getAllUserInvestigationNames(String userName) {
		
		List<String> returnList = new ArrayList<String>();
		List<Investigation> invList = getAllUserInvestigations(userName);
		if (invList != null) {
			for (Investigation inv : invList) {
				if (!returnList.contains(inv.getName())) {
					returnList.add(inv.getName());
				}
			}
		}
		return returnList;
	}
	
	/**
	 * Gets the names of the investigation owned or writable by the user with name 'userName'.
	 * 
	 * @param userName
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public List<String> getWritableUserInvestigationNames(String userName) {
		Query<Investigation> q = db.query(Investigation.class);
		q.addRules(new QueryRule(Investigation.OWNS_NAME, Operator.EQUALS, userName));
		q.addRules(new QueryRule(Operator.OR));
		q.addRules(new QueryRule(Investigation.CANWRITE_NAME, Operator.EQUALS, userName));
		List<String> returnList = new ArrayList<String>();
		List<Investigation> invList;
		try {
			invList = q.find();
		} catch (Exception e) {
			return null;
		}
		for (Investigation inv : invList) {
			returnList.add(inv.getName());
		}
		return returnList;
	}

	/**
	 * Retrieve an observation target by id. Gives back null if not found.
	 * 
	 */
	public ObservationTarget getObservationTargetById(int targetId)
			throws DatabaseException, ParseException
	{
		return db.findById(ObservationTarget.class, targetId);
	}
	
	/**
	 * Retrieve an observation target by name. Gives back null if not found.
	 * 
	 */
	public ObservationTarget getObservationTargetByName(String targetName)
			throws DatabaseException, ParseException
	{
		return db.query(ObservationTarget.class).eq(ObservationTarget.NAME, targetName).find().get(0);
	}
	
	/**
	 * Retrieve a location by id
	 * 
	 * @param locationId the id to look for
	 * @return a Location entity with Id locationId
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public Location getLocationById(int locationId)
			throws DatabaseException, ParseException
	{
		return db.findById(Location.class, locationId);
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
	 * @return an integer matching the id of the ObservationTarget element, or -1 if none was found
	 */
	public int getObservationTargetId(String targetName)
	{
		ObservationTarget tmpTarget;
		try {
			tmpTarget = ObservationTarget.findByName(db, targetName);
		} catch (Exception e) {
			return -1;
		}
		if (tmpTarget != null) {
			return tmpTarget.getId();
		} else {
			return -1;
		}
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
	public Individual createIndividual(int investigationId, String individualName, int userId) throws DatabaseException,
			ParseException, IOException
	{
		Individual newInd = new Individual();
		newInd.setInvestigation_Id(investigationId);
		newInd.setName(individualName); // placeholder
		newInd.setOwns(userId);
		return newInd;
	}
	
	/**
	 * Creates an Individual but does NOT add it to the database.
	 * Uses Investigation and User Names so it can be used with lists.
	 * 
	 * @param investigationName
	 * @param individualName
	 * @param userName
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 * @throws IOException
	 */
	public Individual createIndividual(String investigationName, String individualName, String userName) throws DatabaseException,
			ParseException, IOException
	{
		Individual newInd = new Individual();
		newInd.setInvestigation_Name(investigationName);
		newInd.setName(individualName); // placeholder
		newInd.setOwns_Name(userName);
		return newInd;
	}

	/**
	 * Return a list of all observation targets of a certain type in the investigations with ID's in investigationIds
	 * or in the System investigation.
	 * If desired, filtered down to only the currently active ones.
	 * 
	 * @param type
	 *            : observation target type (Individual, Panel, Location etc.) to filter on, may be null
	 * @param isActive
	 *            : whether or not to filter on Active state (endtime is null)
	 * @return List of ObservationTargets, if desired of a certain type
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public List<String> getAllObservationTargetNames(String type, boolean isActive, List<String> investigationNames) 
	throws DatabaseException, ParseException
	{
		List<String> returnList = new ArrayList<String>();
		
		if (investigationNames == null) {
			investigationNames = new ArrayList<String>();
		}
		if (!investigationNames.contains("System")) {
			investigationNames.add("System");
		}
		
		@SuppressWarnings("rawtypes")
		Class targetClass = db.getClassForName("ObservationTarget");
		if (type != null) {
			targetClass = db.getClassForName(type);
		}
		
		if (isActive == false) {
			Query<ObservationTarget> targetQuery = db.query(targetClass);
			targetQuery.addRules(new QueryRule(ObservationTarget.INVESTIGATION_NAME, Operator.IN, investigationNames));
			targetQuery.addRules(new QueryRule(Operator.SORTASC, ObservationTarget.NAME));
			List<ObservationTarget> targetList = targetQuery.find();
			for (ObservationTarget target : targetList) {
				returnList.add(target.getName());
			}
			return returnList;
		} else {
			// Find 'Active' target names
			Query<ObservedValue> valueQuery = db.query(ObservedValue.class);
			valueQuery.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Active"));
			valueQuery.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.EQUALS, null));
			valueQuery.addRules(new QueryRule(ObservedValue.INVESTIGATION_NAME, Operator.IN, investigationNames));
			List<ObservedValue> valueList = valueQuery.find();
			List<String> activeNameList = new ArrayList<String>();
			for (ObservedValue value : valueList) {
				if (!activeNameList.contains(value.getTarget_Name())) {
					activeNameList.add(value.getTarget_Name());
				}
			}
			// Find target names of right type
			List<String> typeNameList = new ArrayList<String>();
			Query<ObservationTarget> targetQuery = db.query(targetClass);
			targetQuery.addRules(new QueryRule(ObservationTarget.INVESTIGATION_NAME, Operator.IN, investigationNames));
			targetQuery.addRules(new QueryRule(Operator.SORTASC, ObservationTarget.NAME));
			List<ObservationTarget> targetList = targetQuery.find();
			for (ObservationTarget target : targetList) {
				if (!typeNameList.contains(target.getName())) {
					typeNameList.add(target.getName());
				}
			}
			// Keep overlap and return corresponding targets
			typeNameList.retainAll(activeNameList);
			return typeNameList;
		}
	}
	
	/**
	 * Returns all ObservedValues for the given Measurement ID,
	 * sorted descending on 'time'.
	 * 
	 * @param measurementId
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public List<ObservedValue> getAllObservedValues(int measurementId, List<Integer> investigationIds) 
		throws DatabaseException, ParseException {
		Query<ObservedValue> q = db.query(ObservedValue.class);
		q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, measurementId));
		q.addRules(new QueryRule(ObservedValue.INVESTIGATION, Operator.IN, investigationIds));
		q.addRules(new QueryRule(Operator.SORTDESC, ObservedValue.TIME));
		return q.find();
	}
	
	/** Returns all ObservationTargets in the Investigations with the given names
	 * 
	 * @return list of observation targets
	 * @throws DatabaseException 
	 */
	public List<ObservationTarget> getAllObservationTargets(List<String> investigationNames) {
		try {
		    return db.query(ObservationTarget.class).in(ObservationTarget.INVESTIGATION_NAME, 
		    		investigationNames).find();
		} catch (Exception e) {
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
	 * Get the ID's of all targets in the batch as strings.
	 * 
	 * @param id : id of the batch
	 * @return
	 */
	public List<String> getTargetsFromBatch(int id) {
		List<String> returnList = new ArrayList<String>();
		Query<MolgenisBatchEntity> q = db.query(MolgenisBatchEntity.class);
		q.addRules(new QueryRule(MolgenisBatchEntity.BATCH, Operator.EQUALS, id));
		// TODO: check if type is ObservationTarget? Is type stored anyway?
		
		try {
			List<MolgenisBatchEntity> entities = q.find();
		
			for(MolgenisBatchEntity m : entities) {
			    returnList.add(m.getObjectId().toString());
			}
		} catch (Exception e) {
			// Do nothing, return empty list
		}
	
		return returnList;
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
	public List<ObservationTarget> getObservationTargets(List<String> nameList) throws DatabaseException, ParseException {
		if (nameList.size() > 0) {
			Query<ObservationTarget> targetQuery = db.query(ObservationTarget.class);
			targetQuery.addRules(new QueryRule(ObservationTarget.NAME, Operator.IN, nameList));
			targetQuery.addRules(new QueryRule(Operator.SORTASC, ObservationTarget.NAME));
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
			throw new DatabaseException("Target label map not initialized");
			//return getObservationTargetById(targetId).getName();
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
	
	/**
	 * Creates a Location and adds it to the database.
	 */
	public int makeLocation(String investigationName, String locationName, String userName)
			throws DatabaseException, ParseException, IOException
	{
		Location locationToAdd = new Location();
		locationToAdd.setName(locationName);
		locationToAdd.setInvestigation_Name(investigationName);
		locationToAdd.setOwns_Name(userName);
		db.add(locationToAdd);
		return locationToAdd.getId();
	}
	
	public List<Location> getAllLocations() throws DatabaseException {
		return db.find(Location.class);
	}

	/**
	 * Creates a Panel and adds it to the database.
	 */
	public int makePanel(String investigationName, String panelName, String userName)
			throws DatabaseException, IOException, ParseException
	{
		Panel newGroup = new Panel();
		newGroup.setName(panelName);
		newGroup.setInvestigation_Name(investigationName);
		newGroup.setOwns_Name(userName);
		db.add(newGroup);
		return newGroup.getId();
	}
	
	/**
	 * Creates a Panel but does NOT add it to the database.
	 * Uses Investigation and User Names so it can be used with lists.
	 */
	public Panel createPanel(String investigationName, String panelName, String userName)
			throws DatabaseException, IOException, ParseException
	{
		if (panelName == null) {
			throw new DatabaseException("Panel name cannot be null!");
		}
		Panel newGroup = new Panel();
		newGroup.setName(panelName);
		newGroup.setInvestigation_Name(investigationName);
		newGroup.setOwns_Name(userName);
		return newGroup;
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
	public List<ObservationTarget> getAllMarkedPanels(String mark, List<String> investigationNames)
			throws DatabaseException, ParseException
	{
		List<String> panelNameList = new ArrayList<String>();
		Query<ObservedValue> valueQuery = db.query(ObservedValue.class);
		valueQuery.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "TypeOfGroup"));
		valueQuery.addRules(new QueryRule(ObservedValue.VALUE, Operator.EQUALS, mark));
		if (investigationNames.size() > 0) {
			QueryRule qr1 = new QueryRule(Measurement.INVESTIGATION_NAME, Operator.IN, investigationNames);
			QueryRule qr2 = new QueryRule(Operator.OR);
			QueryRule qr3 = new QueryRule(Measurement.INVESTIGATION_NAME, Operator.EQUALS, "System");
			valueQuery.addRules(new QueryRule(qr1, qr2, qr3)); // only user's own OR System investigations
		} else {
			valueQuery.addRules(new QueryRule(Measurement.INVESTIGATION_NAME, Operator.EQUALS, "System"));
		}
		valueQuery.addRules(new QueryRule(Operator.SORTASC, ObservedValue.TARGET_NAME));
		List<ObservedValue> valueList = valueQuery.find();
		for (ObservedValue value : valueList) {
			panelNameList.add(value.getTarget_Name());
		}

		return getObservationTargets(panelNameList);
	}

	/**
	 * Creates an ObservedValue for adding an ObservationTarget to a Panel, but does NOT add this to the database.
	 * However, a protocol application is created and added to the database, so there is a database transaction
	 * involved, rendering this method unsuitable for use with batch lists.
	 */
	public ObservedValue addObservationTargetToPanel(String investigationName, String targetName,
			Date tmpDate, String panelName) throws DatabaseException,
			ParseException, IOException
	{
		// First, check is target is already in this Panel
		Query<ObservedValue> q = db.query(ObservedValue.class);
		q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Group"));
		q.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, targetName));
		q.addRules(new QueryRule(ObservedValue.RELATION_NAME, Operator.EQUALS, panelName));
		q.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.EQUALS, null));
		List<ObservedValue> valueList = q.find();
		
		if (valueList.size() == 0) {
			ProtocolApplication app = createProtocolApplication(investigationName, "SetGroup");
			db.add(app);
			return createObservedValue(investigationName, app.getName(), tmpDate, null, "Group", 
					targetName, null, panelName);
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
		Query<ObservedValue> q = db.query(ObservedValue.class);
		q.addRules(new QueryRule(ObservedValue.VALUE, Operator.EQUALS, Integer.toString(userId)));
		q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "MolgenisUserId"));
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
	 * Uses Investigation and Protocol Names so it can be used with lists.
	 */
	public ProtocolApplication createProtocolApplication(String investigationName, String protocolName) throws ParseException,
			DatabaseException, IOException
	{
		Date now = Calendar.getInstance().getTime();
		ProtocolApplication pa = new ProtocolApplication();
		pa.setInvestigation_Name(investigationName);
		pa.setName(protocolName + "_" + protAppCounter++ + "_" + now.toString()); // strange but unique name
		pa.setProtocol_Name(protocolName);
		pa.setTime(now);
		return pa;
	}
	
	/**
	 * Makes a new ProtocolApplication, adds it to the database and return its name.
	 */
	public String makeProtocolApplication(String investigationName, String protocolName) throws DatabaseException, IOException, ParseException {
		ProtocolApplication pa = createProtocolApplication(investigationName, protocolName);
		db.add(pa);
		return pa.getName();
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
			String protocolName) throws DatabaseException, ParseException
	{
		Query<ProtocolApplication> q = db.query(ProtocolApplication.class);
		q.addRules(new QueryRule(ProtocolApplication.PROTOCOL_NAME, Operator.EQUALS, protocolName));
		return q.find();
	}
	
	/**
	 * Creates an ObservedValue with the given parameters, but does NOT add it to the database.
	 * Uses investigation, feature, target, protocol application and relation names to link, 
	 * so those do not have to have been added to the DB already.
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
	public ObservedValue createObservedValue(String investigationName, String protappName,
			Date starttime, Date endtime, String featureName,
			String subjectTargetName, String valueString, String targetName)
			throws DatabaseException, IOException, ParseException
	{
		ObservedValue newValue = new ObservedValue();
		newValue.setInvestigation_Name(investigationName);
		newValue.setProtocolApplication_Name(protappName);
		newValue.setFeature_Name(featureName);
		newValue.setTime(starttime);
		newValue.setEndtime(endtime);
		newValue.setTarget_Name(subjectTargetName);
		if (targetName != null) {
			newValue.setRelation_Name(targetName);
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
	 */
	public ObservedValue createObservedValueWithProtocolApplication(String investigationName,
			Date starttime, Date endtime, String protocolName, String featureName,
			String subjectTargetName, String valueString, String targetRefName)
			throws DatabaseException, IOException, ParseException
	{
		// Make and add ProtocolApplication
		ProtocolApplication app = createProtocolApplication(investigationName, protocolName);
		db.add(app);
		
		ObservedValue newValue = new ObservedValue();
		newValue.setInvestigation_Name(investigationName);
		newValue.setProtocolApplication_Name(app.getName());
		newValue.setFeature_Name(featureName);
		newValue.setTime(starttime);
		newValue.setEndtime(endtime);
		newValue.setTarget_Name(subjectTargetName);
		if (targetRefName != null) {
			newValue.setRelation_Name(targetRefName);
		} else {
			newValue.setValue(valueString);
		}
		return newValue;
	}
	
	/**
	 * For a given ObservationTarget and ObservableFeature, returns
	 * the value of the most recent ObservedValue,
	 * based on the timestamp of its ProtocolApplication.
	 * Returns "" if none found.
	 * 
	 * @param targetName
	 * @param featureName
	 * @return String: the most recent value for given feature and target
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public String getMostRecentValueAsString(String targetName, String featureName)
			throws DatabaseException, ParseException
	{
		Query<ObservedValue> q = db.query(ObservedValue.class);
		q.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, targetName));
		q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, featureName));
		q.addRules(new QueryRule(Operator.SORTDESC, ObservedValue.TIME));
		// Discussion: if you uncomment the next line, only values are retrieved
		// that have endtime 'null', i.e. values that are still valid.
		// Is this desirable? Maybe we could use a boolean to switch this behavior on and off?
		// q.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.EQUALS, null));
		List<ObservedValue> valueList = q.find();
		if (valueList.size() > 0) {
			ObservedValue returnValue = valueList.get(0); // default is first one
			Date storedTime = null;
			for (ObservedValue currentValue : valueList) {
				if (currentValue.getProtocolApplication_Id() != null) {
					String protappName = currentValue.getProtocolApplication_Name();
					ProtocolApplication protapp = getProtocolApplicationByName(protappName);
					Date protappTime = protapp.getTime();
					if (storedTime == null || protappTime.after(storedTime)) {
						returnValue = currentValue;
						storedTime = protappTime;
					}
				}
			}
			return returnValue.getValue();
		} else {
			return "";
		}
	}
	
	/**
	 * For a given ObservationTarget and ObservableFeature, returns
	 * the ID of the ObservationTarget related to in the most recent ObservedValue,
	 * based on the timestamp of its ProtocolApplication.
	 * Returns null if none found.
	 */
	public String getMostRecentValueAsXrefName(String targetName, String featureName)
			throws DatabaseException, ParseException
	{
		Query<ObservedValue> q = db.query(ObservedValue.class);
		q.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, targetName));
		q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, featureName));
		q.addRules(new QueryRule(Operator.SORTDESC, ObservedValue.TIME));
		// Discussion: if you uncomment the next line, only values are retrieved
		// that have endtime 'null', i.e. values that are still valid.
		// Is this desirable? Maybe we could use a boolean to switch this behavior on and off?
		//q.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.EQUALS, null));
		List<ObservedValue> valueList = q.find();
		if (valueList.size() > 0) {
			ObservedValue returnValue = valueList.get(0); // default is first one
			Date storedTime = null;
			for (ObservedValue currentValue : valueList) {
				if (currentValue.getProtocolApplication_Id() != null) {
					String protappName = currentValue.getProtocolApplication_Name();
					ProtocolApplication protapp = getProtocolApplicationByName(protappName);
					Date protappTime = protapp.getTime();
					if (storedTime == null || protappTime.after(storedTime)) {
						returnValue = currentValue;
						storedTime = protappTime;
					}
				}
			}
			return returnValue.getRelation_Name();
		} else {
			return null;
		}
	}

	/**
	 * Creates a new protocol but does NOT add it to the database.
	 * 
	 * @param investigationName
	 * @param protocolName
	 * @param description
	 * @param measurementNameList
	 * @return
	 * @throws DatabaseException
	 * @throws IOException
	 * @throws ParseException
	 */
	public Protocol createProtocol(String investigationName, String protocolName,
			String description, List<String> measurementNameList)
			throws DatabaseException, IOException, ParseException
	{
		Protocol newProtocol = new Protocol();
		newProtocol.setName(protocolName);
		newProtocol.setInvestigation_Name(investigationName);
		newProtocol.setFeatures_Name(measurementNameList);
		newProtocol.setDescription(description);
		return newProtocol;
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
	public List<Protocol> getAllProtocols(List<Integer> investigationIds) throws DatabaseException,
			ParseException
	{
		Query<Protocol> q = db.query(Protocol.class);
		QueryRule qr1 = new QueryRule(Measurement.INVESTIGATION, Operator.IN, investigationIds);
		QueryRule qr2 = new QueryRule(Operator.OR);
		QueryRule qr3 = new QueryRule(Measurement.INVESTIGATION_NAME, Operator.EQUALS, "System");
		q.addRules(new QueryRule(qr1, qr2, qr3)); // only user's own OR System investigation
		return q.find();
	}

	/**
	 * Find a protocol based on protocol name.
	 * 
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
			String sortOrder, List<String> investigationNames) throws DatabaseException, ParseException
	{
		Query<Protocol> q = db.query(Protocol.class);
		QueryRule qr1 = new QueryRule(Measurement.INVESTIGATION_NAME, Operator.IN, investigationNames);
		QueryRule qr2 = new QueryRule(Operator.OR);
		QueryRule qr3 = new QueryRule(Measurement.INVESTIGATION_NAME, Operator.EQUALS, "System");
		q.addRules(new QueryRule(qr1, qr2, qr3)); // only user's own OR System investigation
		if (sortOrder.equals("ASC")) {
			q.sortASC(sortField);
		} else {
			q.sortDESC(sortField);
		}
		return q.find();
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
	 * Gets the ID of the measurement with the name "measurementName"
	 * 
	 * @param measurementName
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
	public List<Measurement> getAllMeasurements(List<String> investigationNames)
			throws DatabaseException, ParseException
	{
		Query<Measurement> q = db.query(Measurement.class);
		q.sortASC(Measurement.ID);
		if (investigationNames.size() > 0) {
			QueryRule qr1 = new QueryRule(Measurement.INVESTIGATION_NAME, Operator.IN, investigationNames);
			QueryRule qr2 = new QueryRule(Operator.OR);
			QueryRule qr3 = new QueryRule(Measurement.INVESTIGATION_NAME, Operator.EQUALS, "System");
			q.addRules(new QueryRule(qr1, qr2, qr3)); // only user's own OR System investigation
		} else {
			q.addRules(new QueryRule(Measurement.INVESTIGATION_NAME, Operator.EQUALS, "System"));
		}
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
		String sortOrder, List<String> investigationNames) throws DatabaseException, ParseException
	{
		Query<Measurement> q = db.query(Measurement.class);
		if (sortOrder.equals("ASC")) {
			q.sortASC(sortField);
		} else {
			q.sortDESC(sortField);
		}
		if (investigationNames.size() > 0) {
			QueryRule qr1 = new QueryRule(Measurement.INVESTIGATION_NAME, Operator.IN, investigationNames);
			QueryRule qr2 = new QueryRule(Operator.OR);
			QueryRule qr3 = new QueryRule(Measurement.INVESTIGATION_NAME, Operator.EQUALS, "System");
			q.addRules(new QueryRule(qr1, qr2, qr3)); // only user's own OR System investigation
		} else {
			q.addRules(new QueryRule(Measurement.INVESTIGATION_NAME, Operator.EQUALS, "System"));
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
	public List<ObservableFeature> getAllObservableFeatures(List<String> investigationNames)
			throws DatabaseException, ParseException
	{
		Query<ObservableFeature> q = db.query(ObservableFeature.class);
		q.sortASC(ObservableFeature.ID);
		if (investigationNames.size() > 0) {
			QueryRule qr1 = new QueryRule(Measurement.INVESTIGATION_NAME, Operator.IN, investigationNames);
			QueryRule qr2 = new QueryRule(Operator.OR);
			QueryRule qr3 = new QueryRule(Measurement.INVESTIGATION_NAME, Operator.EQUALS, "System");
			q.addRules(new QueryRule(qr1, qr2, qr3)); // only user's own OR System investigation
		} else {
			q.addRules(new QueryRule(Measurement.INVESTIGATION_NAME, Operator.EQUALS, "System"));
		}
		return q.find();
	}

	/**
	 * Returns a list of all Measurements for a Protocol
	 * 
	 * @param protocolId
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public List<Measurement> getMeasurementsByProtocol(String protocolName) throws DatabaseException, ParseException {
		
		Protocol protocol = db.query(Protocol.class).eq(Protocol.NAME, protocolName).find().get(0);
		List<Measurement> features = new ArrayList<Measurement>();
	    for (Integer i : protocol.getFeatures_Id()) {
			features.add(db.findById(Measurement.class, i));
	    }
		return features;
	}
	
	/**
	 * Finds a Measurement entity by its name
	 * 
	 * @param featureId
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public Measurement getMeasurementByName(String featureName)
			throws DatabaseException, ParseException
	{
		return db.find(Measurement.class, new QueryRule(Measurement.NAME, Operator.EQUALS, 
				featureName)).get(0);
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
	@Deprecated
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
	 * Creates a new Measurement and adds it to the databases
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
	public int makeMeasurement(String investigationName, String name, String unitName,
			String targettypeAllowedForRelationClassName, String panelLabelAllowedForRelation,
			boolean temporal, String dataType, String description, String userName)
	throws DatabaseException, IOException, ParseException
	{
		Measurement newFeat = createMeasurement(investigationName, name, unitName,
				targettypeAllowedForRelationClassName, panelLabelAllowedForRelation,
				temporal, dataType, description, userName);
		db.add(newFeat);
		return newFeat.getId();
	}
	
	/**
	 * Creates a new Measurement but does NOT add it to the database.
	 * 
	 * @param investigationName
	 * @param name
	 * @param unitName
	 * @param targettypeAllowedForRelationName
	 * @param panelLabelAllowedForRelation
	 * @param temporal
	 * @param dataType
	 * @param description
	 * @param userName
	 * @return
	 * @throws DatabaseException
	 * @throws IOException
	 * @throws ParseException
	 */
	public Measurement createMeasurement(String investigationName, String name, String unitName,
			String targettypeAllowedForRelationClassName, String panelLabelAllowedForRelation,
			boolean temporal, String dataType, String description, String userName)
	throws DatabaseException, IOException, ParseException
	{
		Measurement newFeat = new Measurement();
		newFeat.setName(name);
		newFeat.setInvestigation_Name(investigationName);
		newFeat.setUnit_Name(unitName);
		if (targettypeAllowedForRelationClassName != null) {
			newFeat.setTargettypeAllowedForRelation_ClassName(targettypeAllowedForRelationClassName);
		}
		if (panelLabelAllowedForRelation != null) {
			newFeat.setPanelLabelAllowedForRelation(panelLabelAllowedForRelation);
		}
		newFeat.setTemporal(temporal);
		newFeat.setDataType(dataType);
		newFeat.setDescription(description);
		newFeat.setOwns_Name(userName);
		return newFeat;
	}

	/**
	 * Creates a new entry in the Category table and updates the Measurement for which it is a Category.
	 * 
	 * @param code : the code itself
	 * @param desc : the description of what the code stands for
	 * @param feat : the name of the ObservableFeature this Code can be used for
	 * @throws DatabaseException
	 * @throws ParseException
	 * @throws IOException
	 */
	public void makeCategory(String code, String desc, String feat)
			throws DatabaseException, ParseException, IOException
	{
		Category newCode = new Category();
		newCode.setName(feat + "_" + code);
		newCode.setCode_String(code);
		newCode.setLabel(code);
		newCode.setDescription(desc);
		db.add(newCode);
		
		//now add it to the Measurement
		Measurement m = db.query(Measurement.class).eq(Measurement.NAME, feat).find().get(0);
		m.getCategories_Id().add(newCode.getId());
		db.update(m);
	}
	
	/**
	 * Creates a new Category table but does NOT add it to the database 
	 * and does NOT update the Measurement for which it is a Category.
	 * 
	 * @param code : the code itself
	 * @param desc : the description of what the code stands for
	 * @throws DatabaseException
	 * @throws ParseException
	 * @throws IOException
	 */
	public Category createCategory(String code, String desc, String feat)
			throws DatabaseException, ParseException, IOException
	{
		Category newCode = new Category();
		newCode.setName(feat + "_" + code);
		newCode.setCode_String(code);
		newCode.setLabel(code);
		newCode.setDescription(desc);
		return newCode;
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
		Measurement m = db.query(Measurement.class).eq(Measurement.NAME,featurename).find().get(0);
		List<Category> tmpList = db.query(Category.class).in(Category.ID, m.getCategories_Id()).find();
//		int featureid = getMeasurementId(featurename);
//		Query<Category> q = db.query(Category.class);
//		q.eq(Category.FEATURE, featureid);
		List<String> returnList = new ArrayList<String>();
//		List<Category> tmpList = q.find();
		for (Category code : tmpList) {
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
	public List<Category> getAllCodesForFeature(String featureName)
			throws DatabaseException, ParseException
	{
		Measurement m = db.query(Measurement.class).eq(Measurement.NAME, featureName).find().get(0);
		return db.query(Category.class).in(Category.ID, m.getCategories_Id()).find();
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
	 * Returns all observed values for a given ObservationTarget and list of features,
	 * sorted so that the most recent one comes first.
	 * NOTE: Creates a default "" value if observedValue doesn't exist yet for
	 * given feature and sample. This created default does not link to a
	 * ProtocolApplication and this method isn't responsible for storing this
	 * object in the database.
	 */
	public List<ObservedValue> getObservedValuesByTargetAndFeatures(String targetName, 
			List<String> measurementNames, List<String> investigationNames, 
			String investigationToAddToName) throws DatabaseException, ParseException
	{

		List<ObservedValue> values = new ArrayList<ObservedValue>();
		for (String mName : measurementNames)
		{ // for each feature, find/make value(s)
			Query<ObservedValue> q = db.query(ObservedValue.class);
			q.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, targetName));
			q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, mName));
			q.addRules(new QueryRule(ObservedValue.INVESTIGATION_NAME, Operator.IN, investigationNames));
			q.addRules(new QueryRule(Operator.SORTDESC, ObservedValue.TIME));
			List<ObservedValue> vals = q.find();
			if (vals.isEmpty())
			{ // if value doesn't exist, create new one
				ObservedValue newOV = new ObservedValue();
				newOV.setFeature_Name(mName);
				newOV.setValue("");
				// don't set relation, as that can then never be reset to null
				newOV.setTarget_Name(targetName);
				newOV.setInvestigation_Name(investigationToAddToName);
				values.add(newOV);
			} else {
				values.addAll(vals);
			}

		}
		return values;
	}
	
	/**
	 * Returns all observed values for a given ObservationTarget and ObservableFeature,
	 * sorted so that the most recent one comes first.
	 * NOTE: Creates a default "" value if observedValue doesn't exist yet for
	 * given feature and sample. This created default does not link to a
	 * ProtocolApplication and this method isn't responsible for storing this
	 * object in the database.
	 */
	public List<ObservedValue> getObservedValuesByTargetAndFeature(String targetName, 
			String measurementName, List<String> investigationNames, 
			String investigationToBeAddedToName) throws DatabaseException, ParseException
	{
		List<String> measurementNameList = new ArrayList<String>();
		measurementNameList.add(measurementName);
		return getObservedValuesByTargetAndFeatures(targetName, measurementNameList, investigationNames, 
				investigationToBeAddedToName);
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
	
	/**
	 * Get the name of the given MolgenisEntity. Returns null if it doesn't exist.
	 * 
	 * @param entityId
	 * @return
	 */
	public String getEntityName(int entityId) {
		try {
			return db.findById(MolgenisEntity.class, entityId).getName();
		} catch (DatabaseException e) {
			return null;
		}
	}

	/**
	 * Returns the id of the ObservableFeature the user has chosen as custom name for the
	 * ObservationTargets, or -1 if none was set.
	 * 
	 * @param userId
	 * @return
	 */
	public int getCustomNameFeatureId(String userName)
	{
		if (userName == null) {
			return -1;
		}
		try {
			int userId = db.query(MolgenisUser.class).eq(MolgenisUser.NAME, userName).find().get(0).getId();
			return db.query(CustomLabelFeature.class).eq(CustomLabelFeature.USERID, userId).find().get(0).getFeatureId_Id();
		} catch (Exception e) {
			return -1;
		}
	}

	/** 
	 * Makes a map of all ObservationTarget id's and names. 
	 * The names are retrieved using the feature name specified, or -if no feature is specified-
	 * the normal database name is taken.
	 * To improve performance, does not make a map if one already exists.
	 */
	public void makeObservationTargetNameMap(String userName, boolean force)
	{
		
		if (observationTargetNameMap != null && force == false) {
			return;
		}
		
		observationTargetNameMap = new HashMap<Integer, String>();
		
		List<String> invNameList = getAllUserInvestigationNames(userName);
		
		// First fill with standard names for all the investigations the current user has rights on
		List<ObservationTarget> targetList = new ArrayList<ObservationTarget>();
		try {
			targetList = getAllObservationTargets(invNameList);
		} catch (Exception e) {
			// targetList will remain empty
			return;
		}
		for (ObservationTarget target : targetList) {
			try {
				observationTargetNameMap.put(target.getId(), target.getName());
			} catch (Exception e) {
				// no name found, so put ID as name
				observationTargetNameMap.put(target.getId(), target.getId().toString());
			}
		}
			
		// Then overwrite with custom names, if existing
		int customNameFeatureId = getCustomNameFeatureId(userName);
		if (customNameFeatureId != -1) {
			try {
				Query<ObservedValue> valueQuery = db.query(ObservedValue.class);
				valueQuery.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, 
						customNameFeatureId));
				valueQuery.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.IN, 
						getAllObservationTargetNames(null, false, invNameList)));
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
			} catch (Exception e) {
				// No fancy names then...
			}
		}
		
	}
	
	/**
	 * Get all the prefixes (non-numeric parts) of the ObservationTarget names that are in the DB.
	 * Example: say there are ObservationTargets with names Morris1, Morris2 and Jessica99. This
	 * method will then return [Morris, Jessica].
	 * 
	 * @return
	 */
	public List<String> getPrefixes(String targetType) throws DatabaseException {
		
		List<String> returnList = new ArrayList<String>();
		
		List<NamePrefix> prefixList = db.find(NamePrefix.class);
		if (prefixList != null && prefixList.size() > 0) {
			for (NamePrefix prefix : prefixList) {
				if (prefix.getTargetType().equals(targetType)) {
					returnList.add(prefix.getPrefix());
				}
			}
		}
		
		return returnList;
		
//		List<ObservationTarget> allTargetList = db.find(ObservationTarget.class);
//		for (ObservationTarget target : allTargetList) {
//			String name = target.getName();
//			name = name.replaceAll("\\d+$", "");
//			if (!name.equals("") && !returnList.contains(name)) {
//				returnList.add(name);
//			}
//		}
	}
	
	/**
	 * Get the highest number found in the DB following the given ObservationTarget name base.
	 * Example: say there are ObservationTargets with names Morris1, Morris2 and Jessica99. This
	 * method, when passed 'Morris', will return 2.
	 * 
	 * @param base
	 * @return
	 * @throws DatabaseException
	 */
	public int getHighestNumberForPrefix(String prefix) throws DatabaseException {
		
		List<NamePrefix> prefixList = db.find(NamePrefix.class, new QueryRule(NamePrefix.PREFIX, Operator.EQUALS, prefix));
		if (prefixList != null && prefixList.size() > 0) {
			return prefixList.get(0).getHighestNumber();
		}
		return 0;
		
//		int maxTrailingNumber = 0;
//		List<ObservationTarget> targetList = db.find(ObservationTarget.class, 
//				new QueryRule(ObservationTarget.NAME, Operator.LIKE, base));
//		for (ObservationTarget target : targetList) {
//			String name = target.getName();
//			// Extra check on name
//			if (!name.startsWith(base)) {
//				continue;
//			}
//			Pattern p;
//			if (base.equals("")) {
//				// With an empty base, name may consist of numbers only
//				p = Pattern.compile("^\\d+$");
//			} else {
//				p = Pattern.compile("\\d+$");
//			}
//			Matcher m = p.matcher(name);
//			if (!m.find()) {
//				continue;
//			}
//			int trailingNumber = Integer.parseInt(m.group());
//			if (trailingNumber > maxTrailingNumber) {
//				maxTrailingNumber = trailingNumber;
//			}
//		}
//		return maxTrailingNumber;
	}
	
	public void updatePrefix(String targetType, String prefix, int highestNr) throws DatabaseException {
		
		List<NamePrefix> prefixList = db.find(NamePrefix.class, new QueryRule(NamePrefix.PREFIX, Operator.EQUALS, prefix));
		if (prefixList != null && prefixList.size() > 0) {
			// Update
			NamePrefix namePrefix = prefixList.get(0);
			namePrefix.setHighestNumber(highestNr);
			db.update(namePrefix);
		} else {
			// New
			NamePrefix namePrefix = new NamePrefix();
			namePrefix.setTargetType(targetType);
			namePrefix.setPrefix(prefix);
			namePrefix.setHighestNumber(highestNr);
			db.add(namePrefix);
		}
	}
	
	public String prependZeros(String name, int totalNrOfChars) {
		char[] nameChars = name.toCharArray();
		if (nameChars.length >= totalNrOfChars) {
			return name;
		}
		char[] fullNameChars = new char[totalNrOfChars];
		int i = 0;
		for (; i < totalNrOfChars - nameChars.length; i++) {
			fullNameChars[i] = '0';
		}
		for (; i < totalNrOfChars; i++) {
			fullNameChars[i] = nameChars[i - (totalNrOfChars - nameChars.length)];
		}
		return String.valueOf(fullNameChars);
	}
	
	/**
	 * Get a list of all the remarks that have been set on the target with name 'targetName'.
	 * 
	 * @param targetId
	 * @return
	 * @throws DatabaseException
	 */
	public List<String> getRemarks(String targetName) throws DatabaseException {
		Query<ObservedValue> q = db.query(ObservedValue.class);
		q.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, "Remark"));
		q.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, targetName));
		List<ObservedValue> valueList = q.find();
		List<String> returnList = new ArrayList<String>();
		if (valueList != null && valueList.size() > 0) {
			for (ObservedValue value : valueList) {
				returnList.add(value.getValue());
			}
		}
		return returnList;
	}

}
