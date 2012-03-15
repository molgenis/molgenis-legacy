package org.molgenis.framework.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.molgenis.MolgenisOptions;
import org.molgenis.fieldtypes.StringField;
import org.molgenis.fieldtypes.TextField;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.db.jdbc.JDBCQueryGernatorUtil;
import org.molgenis.framework.security.Login;
import org.molgenis.framework.security.SimpleLogin;
import org.molgenis.model.elements.Field;
import org.molgenis.model.elements.Model;
import org.molgenis.util.Entity;
import org.molgenis.util.ResultSetTuple;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;
import org.molgenis.util.TupleReader;
import org.molgenis.util.TupleWriter;

public abstract class AbstractDatabase implements Database
{

	/** Logger */
	protected final Log logger = LogFactory.getLog(AbstractDatabase.class);

	/** batch size */
	protected static final int BATCH_SIZE = 500;

	/** List of mappers, mapping entities backend */
	protected Map<String, Mapper<? extends Entity>> mappers = new TreeMap<String, Mapper<? extends Entity>>();

	/** The filesource associated to this database: takes care of "file" fields */
	public File fileSource; // should be changed to protected or private

	/** Current options used */
	protected MolgenisOptions options;

	/** Link to the original Molgenis model */
	protected Model model;

	/** The security login used */
	protected Login login;

	/** Default constructor */
	public AbstractDatabase()
	{
		this.login = new SimpleLogin();
	}

	@Override
	public Model getMetaData() throws DatabaseException
	{
		return model;
	}

	@Override
	public <E extends Entity> int count(Class<E> klazz, QueryRule... rules)
			throws DatabaseException
	{
		return getMapperFor(klazz).count(rules);
	}

	@Override
	public <E extends Entity> List<E> find(Class<E> klazz, QueryRule... rules)
			throws DatabaseException
	{
		return getMapperFor(klazz).find(rules);
	}

	@Override
	public <E extends Entity> void find(Class<E> entityClass,
			TupleWriter writer, QueryRule... rules) throws DatabaseException
	{
		this.getMapperFor(entityClass).find(writer, rules);
	}

	@Override
	public <E extends Entity> void find(Class<E> entityClass,
			TupleWriter writer, List<String> fieldsToExport, QueryRule... rules)
			throws DatabaseException
	{
		try
		{
			writer.setHeaders(fieldsToExport);
			writer.writeHeader();
			int count = 0;
			for (Entity e : find(entityClass, rules))
			{
				writer.writeRow(e);

				count++;
			}
			logger.debug(String.format("find(%s, writer) wrote %s lines",
					entityClass.getSimpleName(), count));
		}
		catch (Exception ex)
		{
			throw new DatabaseException(ex);
		}
	}

	@Override
	public <E extends Entity> List<E> findByExample(E example)
			throws DatabaseException
	{
		return (List<E>) getMapperFor(example.getClass()).findById(example);
	}

	@Override
	public <E extends Entity> E findById(Class<E> entityClass, Object id)
			throws DatabaseException
	{
		return getMapperFor(entityClass).findById(id);
	}

	@Override
	public <E extends Entity> Query<E> query(Class<E> entityClass)
	{
		return new QueryImp<E>(this, entityClass);
	}

	@Override
	public <E extends Entity> Query<E> queryByExample(E entity)
	{
		return new QueryImp<E>(this, (Class<E>) entity.getClass())
				.example(entity);
	}

	/**
	 * Only use when really needed!
	 * 
	 * Executes SQL using stmt.execute(), allowing data manipulation statements
	 * but does not return a ResultSet.
	 * 
	 * @param sql
	 * @return
	 */
	public boolean executeSql(String sql) throws DatabaseException
	{
		logger.info("stmt.execute(" + sql + ")");
		boolean success = false;
		Connection conn = getConnection();
		try
		{

			Statement stmt = conn.createStatement();
			stmt.execute(sql);
			success = true;
			stmt.close();
		}
		catch (Exception e)
		{
			throw new DatabaseException(e);
		}
		finally
		{
			try
			{
				conn.close();
			}
			catch (SQLException sqlEx)
			{
				throw new DatabaseException(sqlEx);
			}
		}
		return success;
	}

	/**
	 * Executes a JDBC query and returns the resultset.
	 * 
	 * comments: This function doesn't work correctly, the connection and
	 * ResultSet are not closed! This is responsibility of user but the user is
	 * not able to obtain the Resultset or Connection (is connection reused with
	 * open statement?)!
	 * 
	 * @param sql
	 * @param rules
	 * @throws SQLException
	 * @throws DatabaseException
	 */
	@Deprecated
	// see comments
	public ResultSet executeQuery(String sql, QueryRule... rules)
			throws DatabaseException
	{
		Connection con = getConnection();
		Statement stmt = null;
		try
		{
			stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			DatabaseMetaData dbmd = con.getMetaData();
			if (dbmd.getDatabaseProductName().toLowerCase().contains("mysql"))
			{
				stmt.setFetchSize(Integer.MIN_VALUE); // trigger streaming of
			}
			String allSql = sql;
			if (rules != null && rules.length > 0) allSql += JDBCQueryGernatorUtil
					.createWhereSql(null, false, true, rules);
			ResultSet rs = stmt.executeQuery(allSql);
			logger.debug("executeQuery: " + allSql);
			return rs;
		}
		catch (NullPointerException npe)
		{
			logger.error("executeQuery() failed with " + npe + " on sql: "
					+ sql + "\ncause: " + npe.getCause());
			throw new DatabaseException(npe);
		}
		catch (SQLException sqle)
		{
			logger.error("executeQuery(" + sql + ")" + sqle);
			throw new DatabaseException(sqle);
		}
	}

	/**
	 * Only use when really needed!
	 * 
	 * @throws DatabaseException
	 * 
	 * @throws DatabaseException
	 */
	public void executeUpdate(String sql) throws DatabaseException
	{
		Connection con = getConnection();
		try
		{
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		}
		catch (Exception e)
		{
			throw new DatabaseException(e);
		}
		finally
		{
			try
			{
				con.close();
			}
			catch (SQLException e)
			{
				throw new DatabaseException(e);
			}
		}
	}

	@Override
	/**
	 * Requires the keys to be set. In case of ADD we don't require the primary key if autoid.
	 */
	public <E extends Entity> int update(List<E> entities,
			DatabaseAction dbAction, String... keyNames)
			throws DatabaseException
	{
		if (keyNames.length == 0) throw new DatabaseException(
				"At least one key must be provided, e.g. 'name'");

		// nothing todo?
		if (entities.size() == 0) return 0;

		// retrieve entity class and name
		Class<E> entityClass = getClassForEntity(entities.get(0));
		String entityName = entityClass.getSimpleName();

		// create maps to store key values and entities
		// key is a concat of all key values for an entity
		Map<String, E> entityIndex = new LinkedHashMap<String, E>();
		// list of all keys, each list item a map of a (composite) key for one
		// entity e.g. investigation_name + name
		List<Map<String, Object>> keyIndex = new ArrayList<Map<String, Object>>();

		// select existing for update, only works if one (composit key allows
		// for nulls) the key values are set
		// otherwise skipped
		boolean keysMissing = false;
		for (E entity : entities)
		{
			// get all the value of all keys (composite key)
			// use an index to hash the entities
			String combinedKey = "";

			// extract its key values and put in map
			Map<String, Object> keyValues = new LinkedHashMap<String, Object>();
			boolean incompleteKey = true;

			// note: we can expect null values in composite keys but need at
			// least one key value.
			for (String key : keyNames)
			{
				// create a hash that concats all key values into one string
				combinedKey += ";"
						+ (entity.get(key) == null ? "" : entity.get(key));

				// if (entity.get(key) == null || entity.get(key).equals(""))
				// {
				// if (dbAction.equals(DatabaseAction.UPDATE) ||
				// dbAction.equals(DatabaseAction.REMOVE))
				// {
				// throw new DatabaseException(
				// entityName + " is missing key '" + key + "' in line " +
				// entity.toString());
				// }
				// }
				if (entity.get(key) != null)
				{
					incompleteKey = false;
					keyValues.put(key, entity.get(key));
				}
			}
			// check if we have missing key
			if (incompleteKey) keysMissing = true;

			// add the keys to the index, if exists
			if (!keysMissing)
			{
				keyIndex.add(keyValues);
				// create the entity index using the hash
				entityIndex.put(combinedKey, entity);
			}
			else
			{
				if ((dbAction.equals(DatabaseAction.ADD)
						|| dbAction.equals(DatabaseAction.ADD_IGNORE_EXISTING) || dbAction
							.equals(DatabaseAction.ADD_UPDATE_EXISTING))
						&& keyNames.length == 1
						&& keyNames[0].equals(entity.getIdField()))
				{
					// don't complain is 'id' field is emptyr
				}
				else
				{
					throw new DatabaseException("keys are missing: "
							+ entityClass.getSimpleName() + "."
							+ Arrays.asList(keyNames));
				}
			}
		}

		// split lists in new and existing entities, but only if keys are set
		List<E> newEntities = entities;
		List<E> existingEntities = new ArrayList<E>();
		if (!keysMissing && keyIndex.size() > 0)
		{
			newEntities = new ArrayList<E>();
			Query<E> q = this.query(getClassForEntity(entities.get(0)));

			// in case of one field key, simply query
			if (keyNames.length == 1)
			{
				List<Object> values = new ArrayList<Object>();
				for (Map<String, Object> keyValues : keyIndex)
				{
					values.add(keyValues.get(keyNames[0]));
				}
				q.in(keyNames[0], values);
			}
			// in case of composite key make massive 'OR' query
			// form (key1 = x AND key2 = X) OR (key1=y AND key2=y)
			else
			{
				// very expensive!
				for (Map<String, Object> keyValues : keyIndex)
				{
					for (int i = 0; i < keyNames.length; i++)
					{
						if (i > 0) q.or();
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
				&& (dbAction == DatabaseAction.ADD_UPDATE_EXISTING
						|| dbAction == DatabaseAction.UPDATE || dbAction == DatabaseAction.UPDATE_IGNORE_MISSING))
		{
			logger.info("existingEntities[0] before: "
					+ existingEntities.get(0).toString());
			matchByNameAndUpdateFields(existingEntities, entities);
			logger.info("existingEntities[0] after: "
					+ existingEntities.get(0).toString());
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
					throw new DatabaseException("Tried to add existing "
							+ entityName
							+ " elements as new insert: "
							+ Arrays.asList(keyNames)
							+ "="
							+ existingEntities.subList(0,
									Math.min(5, existingEntities.size()))
							+ (existingEntities.size() > 5 ? " and "
									+ (existingEntities.size() - 5) + "more"
									: "" + existingEntities));
				}

				// will not test for existing entities before add
				// (so will ignore existingEntities)
			case ADD_IGNORE_EXISTING:
				logger.debug("updateByName(List<" + entityName + "," + dbAction
						+ ">) will skip " + existingEntities.size()
						+ " existing entities");
				return add(newEntities);

				// will try to update(existingEntities) entities and
				// add(missingEntities)
				// so allows user to be sloppy in adding/updating
			case ADD_UPDATE_EXISTING:
				logger.debug("updateByName(List<" + entityName + "," + dbAction
						+ ">)  will try to update " + existingEntities.size()
						+ " existing entities and add " + newEntities.size()
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
					throw new DatabaseException("Tried to update non-existing "
							+ entityName + "elements "
							+ Arrays.asList(keyNames) + "="
							+ entityIndex.values());
				}

				// update that doesn't test for newEntities but just ignores
				// those
				// (so only updates exsiting)
			case UPDATE_IGNORE_MISSING:
				logger.debug("updateByName(List<" + entityName + "," + dbAction
						+ ">) will try to update " + existingEntities.size()
						+ " existing entities and skip " + newEntities.size()
						+ " new entities");
				return update(existingEntities);

				// remove all elements in list, test if no elements are missing
				// (so test for newEntities == 0)
			case REMOVE:
				if (newEntities.size() == 0)
				{
					logger.debug("updateByName(List<" + entityName + ","
							+ dbAction + ">) will try to remove "
							+ existingEntities.size() + " existing entities");
					return remove(existingEntities);
				}
				else
				{
					throw new DatabaseException("Tried to remove non-existing "
							+ entityName + " elements "
							+ Arrays.asList(keyNames) + "="
							+ entityIndex.values());

				}

				// remove entities that are in the list, ignore if they don't
				// exist in database
				// (so don't check the newEntities.size == 0)
			case REMOVE_IGNORE_MISSING:
				logger.debug("updateByName(List<" + entityName + "," + dbAction
						+ ">) will try to remove " + existingEntities.size()
						+ " existing entities and skip " + newEntities.size()
						+ " new entities");
				return remove(existingEntities);

				// unexpected error
			default:
				throw new DatabaseException(
						"updateByName failed because of unknown dbAction "
								+ dbAction);
		}
	}

	public <E extends Entity> void matchByNameAndUpdateFields(
			List<E> existingEntities, List<E> entities)
			throws DatabaseException
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
				if (entityInDb.getLabelFields().size() > 0)
				{
					match = true;
				}
				for (String labelField : entityInDb.getLabelFields())
				{
					if (!entityInDb.get(labelField).equals(
							newEntity.get(labelField)))
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
					try
					{
						entityInDb.set(newValues, false);
					}
					catch (Exception ex)
					{
						throw new DatabaseException(ex);
					}
				}
			}
		}
		// return entities;
	}

	@Override
	public <E extends Entity> int add(E entity) throws DatabaseException
	{
		List<E> entities = getMapperFor((Class<E>) entity.getClass())
				.createList(1);
		entities.add(entity);
		return add(entities);
	}

	@Override
	public <E extends Entity> int add(List<E> entities)
			throws DatabaseException
	{
		if (entities.size() > 0)
		{
			Class<E> klass = (Class<E>) entities.get(0).getClass();
			return getMapperFor(klass).add(entities);
		}
		return 0;
	}

	@Override
	public <E extends Entity> int add(Class<E> klazz, TupleReader reader,
			TupleWriter writer) throws DatabaseException
	{
		return getMapperFor(klazz).add(reader, writer);
	}

	@Override
	public <E extends Entity> int update(E entity) throws DatabaseException
	{
		List<E> entities = getMapperFor((Class<E>) entity.getClass())
				.createList(1);
		entities.add(entity);
		return update(entities);
	}

	@Override
	public <E extends Entity> int update(List<E> entities)
			throws DatabaseException
	{
		if (entities.size() > 0)
		{
			Class<E> klass = (Class<E>) entities.get(0).getClass();
			return getMapperFor(klass).update(entities);
		}
		return 0;
	}

	@Override
	public <E extends Entity> int update(Class<E> klazz, TupleReader reader)
			throws DatabaseException
	{
		return getMapperFor(klazz).update(reader);
	}

	@Override
	public <E extends Entity> int remove(E entity) throws DatabaseException
	{
		List<E> entities = getMapperFor((Class<E>) entity.getClass())
				.createList(1);
		entities.add(entity);
		return remove(entities);
	}

	@Override
	public <E extends Entity> int remove(List<E> entities)
			throws DatabaseException
	{
		if (entities.size() > 0)
		{
			Class<E> klass = (Class<E>) entities.get(0).getClass();
			return getMapperFor(klass).remove(entities);
		}
		return 0;
	}

	@Override
	public <E extends Entity> int remove(Class<E> klazz, TupleReader reader)
			throws DatabaseException
	{
		return getMapperFor(klazz).remove(reader);
	}

	/**
	 * Assign a mapper for a certain class.
	 * 
	 * <pre>
	 * putMapper(Example.class, new ExampleMapper());
	 * </pre>
	 * 
	 * @param klazz
	 *            the class of this Entity
	 * @param mapper
	 */
	protected <E extends Entity> void putMapper(Class<E> klazz, Mapper<E> mapper)
	{
		mappers.put(klazz.getName(), mapper);
	}

	@Override
	public <E extends Entity> Mapper<E> getMapperFor(Class<E> klazz)
			throws DatabaseException
	{
		// transform to generic exception
		Mapper<E> mapper = (Mapper<E>) mappers.get(klazz.getName());
		if (mapper == null)
		{
			throw new DatabaseException(
					"getMapperFor failed because no mapper available for "
							+ klazz.getName());
		}
		return mapper;
	}

	/**
	 * Find the mapper from this.mappers
	 * 
	 * @param className
	 *            the entity class to get the mapper from (simple or full name)
	 * @return a mapper or a exception
	 * @throws DatabaseException
	 */
	@Override
	public <E extends Entity> Mapper<E> getMapper(String name)
			throws DatabaseException
	{
		// transform to generic exception
		Mapper<E> mapper = (Mapper<E>) mappers.get(name);
		if (mapper == null)
		{
			throw new DatabaseException(
					"getMapperFor failed because no mapper available for "
							+ name);
		}
		return mapper;
	}

	@Override
	public List<String> getEntityNames()
	{
		List<String> entities = new ArrayList<String>();
		entities.addAll(mappers.keySet());
		return entities;
	}

	@Override
	public <E extends Entity> List<E> toList(Class<E> klazz,
			TupleReader reader, int limit) throws DatabaseException
	{
		return getMapperFor(klazz).toList(reader, limit);
	}

	@SuppressWarnings("unchecked")
	protected <E extends Entity> Class<E> getClassForEntity(E entity)
	{
		return (Class<E>) entity.getClass();
	}

	@Override
	public <E extends Entity> String createFindSql(Class<E> entityClass,
			QueryRule... rules) throws DatabaseException
	{
		return getMapperFor(entityClass).createFindSqlInclRules(rules);
	}

	@Override
	public File getFilesource()
	{
		return fileSource;
	}

	public Login getLogin()
	{
		return login;
	}

	public Login getSecurity()
	{
		return login;
	}

	public void setLogin(Login login)
	{
		this.login = login;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Class<? extends Entity>> getEntityClasses()
	{
		List<Class<? extends Entity>> classes = new ArrayList<Class<? extends Entity>>();
		try
		{
			for (String klazz : this.getEntityNames())
			{
				classes.add((Class<? extends Entity>) Class.forName(klazz));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return classes;
	}

	@Override
	public Class<? extends Entity> getClassForName(String simpleName)
	{
		for (Class<? extends Entity> c : getEntityClasses())
		{
			if (c.getSimpleName().equalsIgnoreCase(simpleName))
			{
				return c;
			}
		}
		return null;
	}

	/**
	 * Only use when really needed!
	 * 
	 * @throws DatabaseException
	 */
	@Override
	public List<Tuple> sql(String sql, QueryRule... rules)
			throws DatabaseException
	{
		Connection con = getConnection();
		ResultSet rs;
		try
		{
			String allSql = sql
					+ (rules.length > 0 ? JDBCQueryGernatorUtil.createWhereSql(
							null, false, true, rules) : "");
			rs = executeQuery(allSql);
			// transform result set in entity list
			List<Tuple> tuples = new ArrayList<Tuple>();
			if (rs != null)
			{
				while (rs.next())
				{
					tuples.add(new SimpleTuple(new ResultSetTuple(rs)));
				}
			}
			rs.close();

			logger.info("sql(" + allSql + ")" + tuples.size()
					+ " objects found");
			return tuples;
		}
		catch (Exception e)
		{
			throw new DatabaseException(e);
		}
		finally
		{
			try
			{
				con.close();
			}
			catch (SQLException e)
			{
				throw new DatabaseException(e);
			}
		}
	}

	@Override
	public <E extends Entity> List<E> search(Class<E> entityClass,
			String searchString) throws DatabaseException
	{
		return find(entityClass, new QueryRule(Operator.SEARCH, searchString));
		
//		// naive implementation, should use hibernate search when it comes
//		// available!
//		List<QueryRule> searchRules = new ArrayList<QueryRule>();
//		searchRules.addAll(Arrays.asList(rules));
//
//		try
//		{
//			boolean addAND = false;
//			
//			// try create big OR filter for all fields and all search elements
//			// todo: enable string term concat using quotes
//			for (String term : searchString.split(" "))
//			{
//				List<QueryRule> termRules = new ArrayList<QueryRule>();
//
//				// create different query rule depending on type
//				List<Field> fields = this.getMetaData()
//						.getEntity(entityClass.getSimpleName()).getAllFields();
//				
//				for (Field f : fields)
//				{
//					if (f.getType() instanceof StringField
//							|| f.getType() instanceof TextField)
//					{
//						termRules.add(new QueryRule(f.getName(), Operator.LIKE,
//								term.trim()));
//						termRules.add(new QueryRule(Operator.OR));
//					}
//				}
//			
//				//add as big X or Y or Z subquery to our rules
//				searchRules.add(new QueryRule(termRules));
//				
//				if(addAND) searchRules.add(new QueryRule(Operator.AND));
//				addAND = true;
//			}
//		}
//		catch (Exception e)
//		{
//			throw new DatabaseException(e);
//		}
//
//		return this.find(entityClass,
//				searchRules.toArray(new QueryRule[searchRules.size()]));
	}
}
