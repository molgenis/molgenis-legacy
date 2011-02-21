package org.molgenis.framework.db.jpa;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryImp;
import org.molgenis.util.Entity;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractDatabase implements Database
{
	/** Logger */
	protected final Log logger = LogFactory.getLog(AbstractDatabase.class);

	@Override
	@SuppressWarnings(value="all")
	public <E extends Entity> int update(List<E> entities, DatabaseAction dbAction, String... keyNames)
			throws DatabaseException, ParseException, IOException
	{
		// nothing todo?
		if (entities.size() == 0) return 0;

		// retrieve entity class and name
		Class entityClass = entities.get(0).getClass();
		String entityName = entityClass.getSimpleName();

		// create maps to store key values and entities
		// key is a concat of all key values for an entity
		Map<String, E> entityIndex = new LinkedHashMap<String, E>();
		// list of all keys, each list item a map of a (composite) key for one
		// entity e.g. investigation_name + name
		List<Map<String, Object>> keyIndex = new ArrayList<Map<String, Object>>();

		// select existing for update, only works if key values are set
		// otherwise skipped
		for (E entity : entities)
		{
			// get all the value of all keys (composite key)
			// use an index to hash the entities
			String combinedKey = "";
			boolean keysMissing = false;

			// extract its key values and put in map
			Map<String, Object> keyValues = new LinkedHashMap<String, Object>();
			for (String key : keyNames)
			{
				if (entity.get(key) == null)
				{
					if (dbAction.equals(DatabaseAction.UPDATE) || dbAction.equals(DatabaseAction.REMOVE)) throw new DatabaseException(
							entityName + " is missing key '" + key + "' in line " + entity.toString());
					keysMissing = true;
				}
				else
				{
					keyValues.put(key, entity.get(key));
					// create a hash that concats all key values into one string
					combinedKey += ";" + entity.get(key);
				}
			}
			// add the keys to the index, if exists
			if (!keysMissing)
			{
				keyIndex.add(keyValues);
				// create the entity index using the hash
				entityIndex.put(combinedKey, entity);
			}
		}

		// split lists in new and existing entities
		List<E> newEntities = entities;
		List<E> existingEntities = new ArrayList<E>();
		if (keyIndex.size() > 0)
		{
			newEntities = new ArrayList<E>();
			Query q = this.query(entities.get(0).getClass());

			// in case of one field key, simply query
			if (keyNames.length == 1)
			{
				List<Object> values = new ArrayList<Object>();
				for (Map<String, Object> keyValues : keyIndex)
					values.add(keyValues.get(keyNames[0]));
				q.in(keyNames[0], values);
			}
			// in case of composite key make massive 'OR' query
			// form (key1 = x AND key2 = X) OR (key1=y AND key2=y)
			else
			{
				// very expensive!
				int idx = 0;
				for (Map<String, Object> keyValues : keyIndex)
				{
					if (idx++ > 0) q.or();
					for (int i = 0; i < keyNames.length; i++)
					{
						q.equals(keyNames[i], keyValues.get(keyNames[i]));
					}					
				}
			}
			List<E> selectForUpdate = q.find();

			// separate existing from new entities
			for (E p : selectForUpdate)
			{
				// reconstruct composite key so we can use the entityIndex
				String combinedKey = "";
				for (String key : keyNames)
				{
					combinedKey += ";" + p.get(key);
				}
				// copy existing from entityIndex to existingEntities
				entityIndex.remove(combinedKey);
				existingEntities.add(p);
			}
			// copy remaining to newEntities
			newEntities = new ArrayList<E>(entityIndex.values());
		}

		// if existingEntities are going to be updated, they will need to
		// receive new values from 'entities' in addition to be mapped to the
		// database as is the case at this point
		if (existingEntities.size() > 0
				&& (dbAction == DatabaseAction.ADD_UPDATE_EXISTING || dbAction == DatabaseAction.UPDATE || dbAction == DatabaseAction.UPDATE_IGNORE_MISSING))
		{
			logger.debug("existingEntities[0] before: " + existingEntities.get(0).toString());
			matchByNameAndUpdateFields(existingEntities, entities);
			logger.debug("existingEntities[0] after: " + existingEntities.get(0).toString());
		}

		switch (dbAction)
		{

			// will test for existing entities before add
			// (so only add if existingEntities.size == 0).
			case ADD:
				if (existingEntities.size() == 0)
				{
					return add(newEntities);
				}
				else
				{
					List result = new ArrayList(entityIndex.values());
					throw new DatabaseException("Tried to add existing " + entityName + " elements as new insert: "
							+ Arrays.asList(keyNames) + "=" + result.subList(0, Math.min(5, result.size()))
							+ (result.size() > 5 ? " and " + (result.size() - 5) + "more" : ""));
				}

				// will not test for existing entities before add
				// (so will ignore existingEntities)
			case ADD_IGNORE_EXISTING:
				logger.debug("updateByName(List<" + entityName + "," + dbAction + ">) will skip "
						+ existingEntities.size() + " existing entities");
				return add(newEntities);

				// will try to update(existingEntities) entities and
				// add(missingEntities)
				// so allows user to be sloppy in adding/updating
			case ADD_UPDATE_EXISTING:
				logger.debug("updateByName(List<" + entityName + "," + dbAction + ">)  will try to update "
						+ existingEntities.size() + " existing entities and add " + newEntities.size()
						+ " new entities");
				return add(newEntities) + update(existingEntities);

				// update while testing for newEntities.size == 0
			case UPDATE:
				if (newEntities.size() == 0)
				{
					return update(existingEntities);
				}
				else
				{
					throw new DatabaseException("Tried to update non-existing " + entityName + "elements "
							+ Arrays.asList(keyNames) + "=" + entityIndex.values());
				}

				// update that doesn't test for newEntities but just ignores
				// those
				// (so only updates exsiting)
			case UPDATE_IGNORE_MISSING:
				logger.debug("updateByName(List<" + entityName + "," + dbAction + ">) will try to update "
						+ existingEntities.size() + " existing entities and skip " + newEntities.size()
						+ " new entities");
				return update(existingEntities);

				// remove all elements in list, test if no elements are missing
				// (so test for newEntities == 0)
			case REMOVE:
				if (newEntities.size() == 0)
				{
					logger.debug("updateByName(List<" + entityName + "," + dbAction + ">) will try to remove "
							+ existingEntities.size() + " existing entities");
					return remove(existingEntities);
				}
				else
				{
					throw new DatabaseException("Tried to remove non-existing " + entityName + " elements "
							+ Arrays.asList(keyNames) + "=" + entityIndex.values());

				}

				// remove entities that are in the list, ignore if they don't
				// exist in database
				// (so don't check the newEntities.size == 0)
			case REMOVE_IGNORE_MISSING:
				logger.debug("updateByName(List<" + entityName + "," + dbAction + ">) will try to remove "
						+ existingEntities.size() + " existing entities and skip " + newEntities.size()
						+ " new entities");
				return remove(existingEntities);

				// unexpected error
			default:
				throw new DatabaseException("updateByName failed because of unknown dbAction " + dbAction);
		}

	}

	public <E extends Entity> void matchByNameAndUpdateFields(List<E> existingEntities, List<E> entities)
			throws ParseException
	{
		// List<E> updatedDbEntities = new ArrayList<E>();
		for (E entityInDb : existingEntities)
		{
			for (E newEntity : entities)
			{
				// FIXME very wrong! this assumes every data model has 'name' as
				// secondary key.
				boolean match = false;
				// check if there are any label fields otherwise check
				// impossible
				if (entityInDb.getLabelFields().size() > 0) match = true;
				for (String labelField : entityInDb.getLabelFields())
				{
					if (!entityInDb.get(labelField).equals(newEntity.get(labelField)))
					{
						match = false;
						break;
					}
				}
				if (match)
				{
					Tuple newValues = new SimpleTuple();
					for (String field : newEntity.getFields())
					{
						// as they are new entities, should include 'id'
						if (!(newEntity.get(field) == null))
						{
							// logger.debug("entity name = " +
							// newEntity.get("name") + " has null field: " +
							// field);
							newValues.set(field, newEntity.get(field));

						}
					}
					entityInDb.set(newValues, false);
				}
			}
		}
		// return entities;
	}

	@Override
	public <E extends Entity> List<E> findByExample(E example) throws DatabaseException
	{
		try
		{
			return this.queryByExample(example).find();
		}
		catch (ParseException e)
		{
			throw new DatabaseException(e);
		}
	}

	
	private <E extends Entity> Query<E> queryByExample(E entity)
	{
		return new QueryImp<E>(this, (Class<E>) entity.getClass()).example(entity);
	}
}
