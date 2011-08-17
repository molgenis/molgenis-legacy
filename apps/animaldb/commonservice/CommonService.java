package commonservice;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.molgenis.animaldb.CustomLabelFeature;
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
	private static int protAppCounter = 0;
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
	 * Gets the ID of the investigation owned by the user with ID 'userId'.
	 * Assumption: a user can only own one investigation.
	 * Returns -1 if no or multiple investigations found.
	 * 
	 * @param userId
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public int getOwnUserInvestigationId(int userId) {
		Query<Investigation> q = db.query(Investigation.class);
		q.addRules(new QueryRule(Investigation.OWNS, Operator.EQUALS, userId));
		List<Investigation> invList;
		try {
			invList = q.find();
		} catch (Exception e) {
			return -1;
		}
		if (invList.size() == 1) {
			return invList.get(0).getId();
		} else {
			return -1;
		}
	}
	
	/**
	 * Gets the IDs of all the investigations owned by the user with ID 'userId'.
	 * Returns null if no investigation found.
	 * 
	 * @param userId
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public List<Integer> getOwnUserInvestigationIds(int userId) {
		Query<Investigation> q = db.query(Investigation.class);
		q.addRules(new QueryRule(Investigation.OWNS, Operator.EQUALS, userId));
		List<Investigation> invList;
		List<Integer> returnList = new ArrayList<Integer>();
		try {
			invList = q.find();
		} catch (Exception e) {
			return null;
		}
		if (invList != null && invList.size() > 0) {
			for (Investigation inv : invList) {
				returnList.add(inv.getId());
			}
		} else {
			return null;
		}
		return returnList;
	}
	
	/**
	 * Gets the ID's of the investigation owned, readable or writable by the user with ID 'userId'.
	 * 
	 * @param userId
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public List<Integer> getAllUserInvestigationIds(int userId) {
		Query<Investigation> q = db.query(Investigation.class);
		q.addRules(new QueryRule(Investigation.OWNS, Operator.EQUALS, userId));
		q.addRules(new QueryRule(Operator.OR));
		q.addRules(new QueryRule(Investigation.CANREAD, Operator.EQUALS, userId));
		q.addRules(new QueryRule(Operator.OR));
		q.addRules(new QueryRule(Investigation.CANWRITE, Operator.EQUALS, userId));
		List<Integer> returnList = new ArrayList<Integer>();
		List<Investigation> invList;
		try {
			invList = q.find();
		} catch (Exception e) {
			return null;
		}
		for (Investigation inv : invList) {
			if (!returnList.contains(inv.getId())) {
				returnList.add(inv.getId());
			}
		}
		return returnList;
	}
	
	/**
	 * Gets the ID's of the investigation owned or writable by the user with ID 'userId'.
	 * 
	 * @param userId
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public List<Integer> getWritableUserInvestigationIds(int userId) {
		Query<Investigation> q = db.query(Investigation.class);
		q.addRules(new QueryRule(Investigation.OWNS, Operator.EQUALS, userId));
		q.addRules(new QueryRule(Operator.OR));
		q.addRules(new QueryRule(Investigation.CANWRITE, Operator.EQUALS, userId));
		List<Integer> returnList = new ArrayList<Integer>();
		List<Investigation> invList;
		try {
			invList = q.find();
		} catch (Exception e) {
			return null;
		}
		for (Investigation inv : invList) {
			returnList.add(inv.getId());
		}
		return returnList;
	}

	/**
	 * Retrieve an observation target by id. Gives back null if not found.
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
	 * Return a list of all observation targets of a certain type in the nvestigations with ID's in investigationIds
	 * or in the System investigation.
	 * If desired, filtered down to only the currently active ones.
	 * 
	 * @param type
	 *            : observation target type (Animal, Actor, Group, Location) to filter on, may be null
	 * @param isActive
	 *            : whether or not to filter on Active state
	 * @return List of ObservationTargets, if desired of a certain type
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public List<Integer> getAllObservationTargetIds(String type, boolean isActive, List<Integer> investigationIds) 
	throws DatabaseException, ParseException
	{
		List<Integer> returnList = new ArrayList<Integer>();
		
		if (investigationIds == null) {
			investigationIds = new ArrayList<Integer>();
		}
		
		Integer systemId = getInvestigationId("System");
		if (!investigationIds.contains(systemId)) {
			investigationIds.add(systemId);
		}
		
		if (isActive == false) {
			Query<ObservationTarget> targetQuery = db.query(ObservationTarget.class);
			targetQuery.addRules(new QueryRule(ObservationTarget.INVESTIGATION, Operator.IN, investigationIds));
			if (type != null) {
				targetQuery.addRules(new QueryRule(ObservationTarget.__TYPE, Operator.EQUALS, type));
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
			valueQuery.addRules(new QueryRule(ObservedValue.INVESTIGATION, Operator.IN, investigationIds));
			List<ObservedValue> valueList = valueQuery.find();
			List<Integer> activeIdList = new ArrayList<Integer>();
			for (ObservedValue value : valueList) {
				activeIdList.add(value.getTarget_Id());
			}
			// Find target id's of right type
			List<Integer> typeIdList = new ArrayList<Integer>();
			if (type != null) {
				Query<ObservationTarget> targetQuery = db.query(ObservationTarget.class);
				targetQuery.addRules(new QueryRule(ObservationTarget.INVESTIGATION, Operator.IN, investigationIds));
				targetQuery.addRules(new QueryRule(ObservationTarget.__TYPE, Operator.EQUALS, type));
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
	
	/** Returns all ObservationTargets in the Investigations with ID's investigationIds
	 * 
	 * @return list of observation targets
	 * @throws DatabaseException 
	 */
	public List<ObservationTarget> getAllObservationTargets(List<Integer> investigationIds) {
		try {
		    return db.query(ObservationTarget.class).in(ObservationTarget.INVESTIGATION, investigationIds).find();
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
	public List<ObservationTarget> getObservationTargets(List<Integer> idList) throws DatabaseException, ParseException {
		if (idList.size() > 0) {
			Query<ObservationTarget> targetQuery = db.query(ObservationTarget.class);
			targetQuery.addRules(new QueryRule(ObservationTarget.ID, Operator.IN, idList));
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
	 * 
	 * @param investigationId
	 * @param locationName
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 * @throws IOException
	 */
	public int makeLocation(int investigationId, String locationName, int userId)
			throws DatabaseException, ParseException, IOException
	{
		Location locationToAdd = new Location();
		locationToAdd.setName(locationName);
		locationToAdd.setInvestigation(investigationId);
		locationToAdd.setOwns(userId);
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
	public int makePanel(int investigationId, String panelName, int userId)
			throws DatabaseException, IOException, ParseException
	{
		Panel newGroup = new Panel();
		newGroup.setName(panelName);
		newGroup.setInvestigation_Id(investigationId);
		newGroup.setOwns_Id(userId);
		db.add(newGroup);
		return newGroup.getId();
	}
	
	/**
	 * Creates a Panel but does NOT add it to the database.
	 * 
	 * @param investigationId
	 * @param panelName
	 * @return
	 * @throws DatabaseException
	 * @throws IOException
	 * @throws ParseException
	 */
	public Panel createPanel(int investigationId, String panelName, int userId)
			throws DatabaseException, IOException, ParseException
	{
		Panel newGroup = new Panel();
		newGroup.setName(panelName);
		newGroup.setInvestigation(investigationId);
		newGroup.setOwns(userId);
		return newGroup;
	}
	
	/**
	 * Creates a Panel but does NOT add it to the database.
	 * Uses Investigation and User Names so it can be used with lists.
	 * 
	 * @param investigationName
	 * @param panelName
	 * @param userName
	 * @return
	 * @throws DatabaseException
	 * @throws IOException
	 * @throws ParseException
	 */
	public Panel createPanel(String investigationName, String panelName, String userName)
			throws DatabaseException, IOException, ParseException
	{
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
	public List<ObservationTarget> getAllMarkedPanels(String mark, List<Integer> investigationIds)
			throws DatabaseException, ParseException
	{
		List<Integer> panelIdList = new ArrayList<Integer>();

		Query<ObservedValue> valueQuery = db.query(ObservedValue.class);
		valueQuery.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, getMeasurementId("TypeOfGroup")));
		valueQuery.addRules(new QueryRule(ObservedValue.VALUE, Operator.EQUALS, mark));
		QueryRule qr1 = new QueryRule(Measurement.INVESTIGATION, Operator.IN, investigationIds);
		QueryRule qr2 = new QueryRule(Operator.OR);
		QueryRule qr3 = new QueryRule(Measurement.INVESTIGATION_NAME, Operator.EQUALS, "System");
		valueQuery.addRules(new QueryRule(qr1, qr2, qr3)); // only user's own OR System investigations
		List<ObservedValue> valueList = valueQuery.find();
		for (ObservedValue value : valueList) {
			panelIdList.add(value.getTarget_Id());
		}

		return getObservationTargets(panelIdList);
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
		pa.setInvestigation_Id(investigationId);
		pa.setName(protocolId + "_" + protAppCounter++ + "_" + now.toString()); // strange but unique name
		pa.setProtocol_Id(protocolId);
		pa.setTime(now);
		return pa;
	}
	
	/**
	 * Makes a new application of the given protocol
	 * but does NOT add it to the database.
	 * Uses Investigation and Protocol Names so it can be used with lists.
	 * 
	 * @param investigationName
	 * @param protocolName
	 * @return
	 * @throws ParseException
	 * @throws DatabaseException
	 * @throws IOException
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
		pa.setInvestigation_Id(investigationId);
		pa.setName(protocolId + "_" + protAppCounter++ + "_" + now.toString()); // strange but unique name
		pa.setProtocol_Id(protocolId);
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
		newValue.setInvestigation_Id(investigationId);
		newValue.setProtocolApplication_Id(app.getId());
		newValue.setFeature_Id(featureId);
		newValue.setTime(starttime);
		newValue.setEndtime(endtime);
		newValue.setTarget_Id(subjectTargetId);
		if (targetRef != 0) {
			newValue.setRelation_Id(targetRef);
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
		q.addRules(new QueryRule(Operator.SORTDESC, ObservedValue.TIME));
		// Discussion: if you uncomment the previous line, only values are retrieved
		// that have endtime 'null', i.e. values that are still valid.
		// Is this desirable? Maybe we could use a boolean to switch this behavior on and off?
		// q.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.EQUALS, null));
		List<ObservedValue> valueList = q.find();
		if (valueList.size() > 0) {
			ObservedValue returnValue = valueList.get(0); // default is first one
			Date storedTime = null;
			for (ObservedValue currentValue : valueList) {
				if (currentValue.getProtocolApplication_Id() == null) {
					int protappId = currentValue.getProtocolApplication_Id();
					ProtocolApplication protapp = getProtocolApplicationById(protappId);
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
			// Discussion: I'm unhappy with the solution below (commented out) because
			// it's perfectly normal for a target-feature combination not to have a value,
			// so this should not cause an exception but just return an empty string.
			//throw new DatabaseException("No valid values were found for targetid: " + 
			//	targetid + " and featureid: " + featureid);
		}
	}

	/**
	 * For a given ObservationTarget and ObservableFeature, returns
	 * the ID of the ObservationTarget related to in the most recent ObservedValue,
	 * based on the timestamp of its ProtocolApplication.
	 * Returns -1 if none found.
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
		q.addRules(new QueryRule(Operator.SORTDESC, ObservedValue.TIME));
		// Discussion: if you uncomment the previous line, only values are retrieved
		// that have endtime 'null', i.e. values that are still valid.
		// Is this desirable? Maybe we could use a boolean to switch this behavior on and off?
		//q.addRules(new QueryRule(ObservedValue.ENDTIME, Operator.EQUALS, null));
		List<ObservedValue> valueList = q.find();
		if (valueList.size() > 0) {
			ObservedValue returnValue = valueList.get(0); // default is first one
			Date storedTime = null;
			for (ObservedValue currentValue : valueList) {
				if (currentValue.getProtocolApplication_Id() == null) {
					int protappId = currentValue.getProtocolApplication_Id();
					ProtocolApplication protapp = getProtocolApplicationById(protappId);
					Date protappTime = protapp.getTime();
					if (storedTime == null || protappTime.after(storedTime)) {
						returnValue = currentValue;
						storedTime = protappTime;
					}
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
			String sortOrder, List<Integer> investigationIds) throws DatabaseException, ParseException
	{
		Query<Protocol> q = db.query(Protocol.class);
		QueryRule qr1 = new QueryRule(Measurement.INVESTIGATION, Operator.IN, investigationIds);
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
	public List<Measurement> getAllMeasurements(List<Integer> investigationIds)
			throws DatabaseException, ParseException
	{
		Query<Measurement> q = db.query(Measurement.class);
		q.sortASC("id");
		if (investigationIds.size() > 0) {
			QueryRule qr1 = new QueryRule(Measurement.INVESTIGATION, Operator.IN, investigationIds);
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
		String sortOrder, List<Integer> investigationIds) throws DatabaseException, ParseException
	{
		Query<Measurement> q = db.query(Measurement.class);
		if (sortOrder.equals("ASC")) {
			q.sortASC(sortField);
		} else {
			q.sortDESC(sortField);
		}
		if (investigationIds.size() > 0) {
			QueryRule qr1 = new QueryRule(Measurement.INVESTIGATION, Operator.IN, investigationIds);
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
	public List<ObservableFeature> getAllObservableFeatures(List<Integer> investigationIds)
			throws DatabaseException, ParseException
	{
		Query<ObservableFeature> q = db.query(ObservableFeature.class);
		q.sortASC(ObservableFeature.ID);
		if (investigationIds.size() > 0) {
			QueryRule qr1 = new QueryRule(Measurement.INVESTIGATION, Operator.IN, investigationIds);
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
	public List<Measurement> getMeasurementsByProtocol(int protocolId) throws DatabaseException, ParseException {
		
		Protocol protocol = db.findById(Protocol.class, protocolId);

		List<Measurement> features = new ArrayList<Measurement>();
	    for (Integer i : protocol.getFeatures_Id()) {
			features.add(db.findById(Measurement.class, i));
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
			boolean temporal, String dataType, String description, int userId)
	throws DatabaseException, IOException, ParseException
	{
		Measurement newFeat = new Measurement();
		newFeat.setName(name);
		newFeat.setInvestigation_Id(investigationId);
		newFeat.setUnit_Id(unitId);
		if (targettypeAllowedForRelation != null) {
			newFeat.setTargettypeAllowedForRelation(targettypeAllowedForRelation);
		}
		if (panelLabelAllowedForRelation != null) {
			newFeat.setPanelLabelAllowedForRelation(panelLabelAllowedForRelation);
		}
		newFeat.setTemporal(temporal);
		newFeat.setDataType(dataType);
		newFeat.setDescription(description);
		newFeat.setOwns_Id(userId);
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
	 * 
	 * @param sampleId
	 * @param features
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public List<ObservedValue> getObservedValuesByTargetAndFeatures(int targetId, 
			List<Measurement> measurements, List<Integer> investigationIds, 
			int investigationToAddToId) throws DatabaseException, ParseException
	{

		List<ObservedValue> values = new ArrayList<ObservedValue>();

		for (Measurement m : measurements)
		{ // for each feature, find/make value(s)
			Query<ObservedValue> q = db.query(ObservedValue.class);
			q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, targetId));
			q.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, m.getId()));
			q.addRules(new QueryRule(ObservedValue.INVESTIGATION, Operator.IN, investigationIds));
			q.addRules(new QueryRule(Operator.SORTDESC, ObservedValue.TIME));
			List<ObservedValue> vals = q.find();
			
			if (vals.isEmpty())
			{ // if value doesn't exist, create new one
				ObservedValue newOV = new ObservedValue();
				newOV.setFeature_Id(m.getId());
				newOV.setValue("");
				// don't set relation, as that can then never be reset to null
				newOV.setTarget_Id(targetId);
				newOV.setInvestigation_Id(investigationToAddToId);
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
	 * 
	 * @param targetId
	 * @param measurement
	 * @param investigationIds
	 * @param investigationToBeAddedToId
	 * @return
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public List<ObservedValue> getObservedValuesByTargetAndFeature(int targetId, 
			Measurement measurement, List<Integer> investigationIds, 
			int investigationToBeAddedToId) throws DatabaseException, ParseException
	{
		List<Measurement> measurementList = new ArrayList<Measurement>();
		measurementList.add(measurement);
		return getObservedValuesByTargetAndFeatures(targetId, measurementList, investigationIds, 
				investigationToBeAddedToId);
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
	public int getCustomNameFeatureId(int userId)
	{
		if (userId == -1) {
			return -1;
		}
		
		List<CustomLabelFeature> featList;
		try {
			featList = db.query(CustomLabelFeature.class).eq(CustomLabelFeature.USERID, userId).find();
		} catch (Exception e) {
			return -1;
		}
		if (featList.size() > 0) {
			return featList.get(0).getFeatureId_Id();
		} else {
			return -1;
		}
	}

	/** 
	 * Makes a map of all ObservationTarget id's and names. 
	 * The names are retrieved using the feature name specified, or -if no feature is specified-
	 * the normal database name is taken.
	 * To improve performance, does not make a map if one already exists.
	 */
	public void makeObservationTargetNameMap(int userId, boolean force)
	{
		
		if (observationTargetNameMap != null && force == false) {
			return;
		}
		
		observationTargetNameMap = new HashMap<Integer, String>();
		
		List<Integer> invIdList = getAllUserInvestigationIds(userId);
		
		// First fill with standard names for all the investigations the current user has rights on
		List<ObservationTarget> targetList = new ArrayList<ObservationTarget>();
		try {
			targetList = getAllObservationTargets(invIdList);
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
		int customNameFeatureId = getCustomNameFeatureId(userId);
		if (customNameFeatureId != -1) {
			try {
				Query<ObservedValue> valueQuery = db.query(ObservedValue.class);
				valueQuery.addRules(new QueryRule(ObservedValue.FEATURE, Operator.EQUALS, 
						customNameFeatureId));
				valueQuery.addRules(new QueryRule(ObservedValue.TARGET, Operator.IN, 
						getAllObservationTargetIds(null, false, invIdList)));
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
	 * Get all the bases (non-numeric parts) of the ObservationTarget names that are in the DB.
	 * Example: say there are ObservationTargets with names Morris1, Morris2 and Jessica99. This
	 * method will then return [Morris, Jessica].
	 * 
	 * @return
	 */
	public List<String> getNameBases() throws DatabaseException {
		
		List<String> returnList = new ArrayList<String>();
		
		List<ObservationTarget> allTargetList = db.find(ObservationTarget.class);
		for (ObservationTarget target : allTargetList) {
			String name = target.getName();
			name = name.replaceAll("\\d+$", "");
			if (!name.equals("") && !returnList.contains(name)) {
				returnList.add(name);
			}
		}
		
		return returnList;
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
	public int getHighestNumberForNameBase(String base) throws DatabaseException {
		
		int maxTrailingNumber = 0;
		
		List<ObservationTarget> targetList = db.find(ObservationTarget.class, 
				new QueryRule(ObservationTarget.NAME, Operator.LIKE, base));
		for (ObservationTarget target : targetList) {
			String name = target.getName();
			// Extra check on name
			if (!name.startsWith(base)) {
				continue;
			}
			Pattern p;
			if (base.equals("")) {
				// With an empty base, name may consist of numbers only
				p = Pattern.compile("^\\d+$");
			} else {
				p = Pattern.compile("\\d+$");
			}
			Matcher m = p.matcher(name);
			if (!m.find()) {
				continue;
			}
			int trailingNumber = Integer.parseInt(m.group());
			if (trailingNumber > maxTrailingNumber) {
				maxTrailingNumber = trailingNumber;
			}
		}
		
		return maxTrailingNumber;
	}

}
