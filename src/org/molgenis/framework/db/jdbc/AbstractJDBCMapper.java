package org.molgenis.framework.db.jdbc;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.molgenis.fieldtypes.FieldType;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.util.CsvReader;
import org.molgenis.util.CsvReaderListener;
import org.molgenis.util.Entity;
import org.molgenis.util.ResultSetTuple;
import org.molgenis.util.TupleWriter;
import org.molgenis.util.Tuple;

/**
 * Factory for creating SQL statements
 * 
 * @author Morris Swertz
 * 
 */
public abstract class AbstractJDBCMapper<E extends Entity> implements JDBCMapper<E>
{
	/** database */
	JDBCDatabase database;
	/** log messages */
	private static transient final Logger logger = Logger.getLogger(AbstractJDBCMapper.class.getSimpleName());
	/** batch size */
	public static final int BATCH_SIZE = 5000;

	public AbstractJDBCMapper(JDBCDatabase database)
	{
		this.database = database;
	}

	public JDBCDatabase getDatabase()
	{
		return database;
	}

	@SuppressWarnings("unchecked")
	public int add(List<E> entities) throws DatabaseException
	{
		int updatedRows = 0;
		final String TX_TICKET = "ADD_" + this.getClass().getSimpleName();
		try
		{
			// begin transaction for all batches
			database.beginPrivateTx(TX_TICKET);

			// prepare all file attachments
			this.prepareFileAttachements(entities, database.getFilesource());

			// add to superclass first
			int superUpdatedRows = 0;
			if (this.getSuperTypeMapper() != null)
			{
				superUpdatedRows = getSuperTypeMapper().add(entities);
			}
			
			// attempt to resolve foreign keys by label (ie. 'name')
			this.resolveForeignKeys(entities);
				
			// insert this class in batches
			for (int i = 0; i < entities.size(); i += BATCH_SIZE)
			{
				int endindex = Math.min(i + BATCH_SIZE, entities.size());
				List<E> sublist = entities.subList(i, endindex);
				updatedRows += Math.max(this.executeAdd(sublist), superUpdatedRows);
			}

			// update any mrefs for this entity
			this.storeMrefs(entities);

			// store file attachments and then update the file paths to them
			if (this.saveFileAttachements(entities, database.fileSource))
			{
				this.update(entities);
			}

			// commit all batches
			database.commitPrivateTx(TX_TICKET);

			logger.info(updatedRows + " " + this.create().getClass().getSimpleName() + " objects added");
			return updatedRows;
		}
		catch (Exception sqle)
		{
			sqle.printStackTrace();
			database.rollbackPrivateTx(TX_TICKET);
			logger.error("ADD failed on " + this.create().getClass().getSimpleName() + ": " + sqle.getMessage());
			throw new DatabaseException(sqle);
		}
	}

	// FIXME: can we merge the two add functions by wrapping list/reader into an
	// iterator of some kind?
	public int add(CsvReader reader, TupleWriter writer) throws DatabaseException
	{
		int rowsAffected = 0;
		final String TX_TICKET = "ADD+" + this.create().getClass().getCanonicalName() + "_CSV";
		try
		{
			database.beginPrivateTx(TX_TICKET);

			List<E> entities = toList(reader, BATCH_SIZE);

			if (writer != null)
			{
				writer.setHeaders(entities.get(0).getFields());
				writer.writeHeader();
			}

			while (entities.size() > 0)
			{
				// resolve foreign keys
				this.resolveForeignKeys(entities);

				// add to the database
				rowsAffected += database.add(entities);
				if (writer != null)
				{
					for (E entity : entities)
					{
						writer.writeRow(entity);
					}
				}
				entities = toList(reader, BATCH_SIZE);
			}

			database.commitPrivateTx(TX_TICKET);
		}
		catch (Exception e)
		{
			database.rollbackPrivateTx(TX_TICKET);
			throw new DatabaseException(e);
		}
		return rowsAffected;
	}

	@SuppressWarnings("unchecked")
	public int update(List<E> entities) throws DatabaseException
	{
		int updatedRows = 0;
		final String TX_TICKET = "UPDATE" + this.getClass().getSimpleName();
		try
		{
			// start anonymous transaction for the batched update
			database.beginPrivateTx(TX_TICKET);

			// prepare file attachments
			this.prepareFileAttachements(entities, database.fileSource);

			// update the superclass first
			int superUpdatedRows = 0;
			if (this.getSuperTypeMapper() != null)
			{
				superUpdatedRows = getSuperTypeMapper().update(entities);
			}

			// update in batches
			for (int i = 0; i < entities.size(); i += BATCH_SIZE)
			{
				int endindex = Math.min(i + BATCH_SIZE, entities.size());
				List<E> sublist = entities.subList(i, endindex);

				// put the files in their place
				this.saveFileAttachements(sublist, database.fileSource);

				updatedRows += Math.max(this.executeUpdate(sublist), superUpdatedRows);
			}

			// rename file attachments with right name and update database
			this.storeMrefs(entities);

			database.commitPrivateTx(TX_TICKET);

			logger.info(updatedRows + " " + this.create().getClass().getSimpleName() + " objects updated");
			return updatedRows;
		}
		catch (Exception sqle)
		{
			database.rollbackPrivateTx(TX_TICKET);
			logger.error("update failed on " + this.create().getClass().getSimpleName() + ": " + sqle.getMessage());
			throw new DatabaseException(sqle);
		}
	}

	public int update(CsvReader reader) throws DatabaseException
	{
		int rowsAffected = 0;
		final String TX_TICKET = "ADD+" + this.create().getClass().getCanonicalName() + "_CSV";
		try
		{
			database.beginPrivateTx(TX_TICKET);
			List<E> entities = toList(reader, BATCH_SIZE);
			while (entities.size() > 0)
			{
				// resolve foreign keys
				this.resolveForeignKeys(entities);

				// update to the database
				rowsAffected += database.update(entities);
				entities = toList(reader, BATCH_SIZE);
			}

			database.commitPrivateTx(TX_TICKET);
		}
		catch (Exception e)
		{
			database.rollbackPrivateTx(TX_TICKET);
			throw new DatabaseException(e);
		}
		return rowsAffected;
	}

	@SuppressWarnings("unchecked")
	public int remove(List<E> entities) throws DatabaseException
	{
		int updatedRows = 0;
		final String TX_TICKET = "REMOVE_" + this.getClass().getSimpleName();
		try
		{
			// start anonymous transaction for the batched remove
			database.beginPrivateTx(TX_TICKET);

			// prepare file attachments
			this.prepareFileAttachements(entities, database.fileSource);

			// remove in batches
			for (int i = 0; i < entities.size(); i += BATCH_SIZE)
			{
				int endindex = Math.min(i + BATCH_SIZE, entities.size());
				List<E> sublist = entities.subList(i, endindex);

				// remove mrefs before the entity itself
				this.removeMrefs(sublist);
				updatedRows += this.executeRemove(sublist);
			}
			// store mrefs
			// ConnectionHelper.storeMrefs(this, connection,entities);

			// if delete, delete supertype after subtype
			// delete super type if necessary
			if (this.getSuperTypeMapper() != null)
			{
				updatedRows = Math.max(getSuperTypeMapper().remove(entities), updatedRows);
			}

			database.commitPrivateTx(TX_TICKET);

			logger.info(updatedRows + " " + this.create().getClass().getSimpleName() + " objects removed");
			return updatedRows;
		}
		catch (Exception sqle)
		{
			database.rollbackPrivateTx(TX_TICKET);
			logger.error("remove failed on " + this.create().getClass().getSimpleName() + ": " + sqle.getMessage());
			sqle.printStackTrace();
			throw new DatabaseException(sqle);
		}
	}

	/**
	 * Helper method for retrieving keys.
	 * 
	 * @param entities
	 * @param fromIndex
	 * @param stmt
	 * @throws DatabaseException
	 */
	public void getGeneratedKeys(List<E> entities, Statement stmt, int fromIndex) throws DatabaseException
	{
		E entity = null;
		ResultSet rs_keys = null;
		int i = 0;
		try
		{
			rs_keys = stmt.getGeneratedKeys();
			while (rs_keys.next())
			{
				entity = entities.get(fromIndex + i);
				setAutogeneratedKey(rs_keys.getInt(1), entity);
				entities.set(fromIndex + i, entity); // put it back again...
				i++;
			}
		}
		catch (Exception e)
		{
			logger.error("executeKeys(): " + e);
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		}
		finally
		{
			try
			{
				rs_keys.close();
			}
			catch (Exception e)
			{
			}
			rs_keys = null;
		}
	}

	/**
	 * Maps to another mapping strategy for superclasses
	 */
	@SuppressWarnings("rawtypes")
	public abstract JDBCMapper getSuperTypeMapper();

	/**
	 * helper method create a new instance of E
	 */
	public abstract E create();

	/**
	 * Method to build a list for Entity E. This allows the finder to pick a
	 * more efficient list implementation than the generic lists.
	 * 
	 * @param size
	 *            of the list
	 * @return list
	 */
	public abstract List<E> createList(int size);

	/**
	 * maps {@link org.molgenis.framework.db.Database#add(List)}
	 * 
	 * @throws DatabaseException
	 */
	public abstract int executeAdd(List<E> entities) throws DatabaseException;

	/**
	 * maps {@link org.molgenis.framework.db.Database#update(List)}
	 * 
	 * @throws DatabaseException
	 */
	public abstract int executeUpdate(List<E> entities) throws DatabaseException;

	/**
	 * maps {@link org.molgenis.framework.db.Database#remove(List)}
	 */
	public abstract int executeRemove(List<E> entities) throws DatabaseException;

	/**
	 * maps {@link org.molgenis.framework.db.Database#find(Class, QueryRule[])}
	 * 
	 * @throws DatabaseException
	 */
	public abstract String createFindSql(QueryRule... rules) throws DatabaseException;

	/**
	 * maps {@link org.molgenis.framework.db.Database#count(Class, QueryRule[])}
	 * 
	 * @throws DatabaseException
	 * 
	 * @throws DatabaseException
	 * 
	 * @throws SQLException
	 */
	public abstract String createCountSql(QueryRule... rules) throws DatabaseException;

	/**
	 * Translate object field name to table fieldname
	 */
	public abstract String getTableFieldName(String fieldName);

	/**
	 * Retrieve the type of the field
	 */
	public abstract FieldType getFieldType(String fieldName);

	/**
	 * helper method to set the auto-generated keys
	 */
	public abstract void setAutogeneratedKey(int key, E entity);

	/**
	 * helper method to prepares file for saving.
	 * 
	 * @throws IOException
	 */
	public abstract void prepareFileAttachements(List<E> entities, File dir) throws IOException;

	/**
	 * helper method to do some actions after the transaction. For example:
	 * write files to disk. FIXME make a listener?
	 * 
	 * @return true if files were saved (will cause additional update to the
	 *         database)
	 * @throws IOException
	 */
	public abstract boolean saveFileAttachements(List<E> entities, File dir) throws IOException;

	/**
	 * helper method for mapping multiplicative references (mref). This function
	 * is used when retrieving the entity. It should retrieve the mref elements
	 * and add them to each mref field.
	 * 
	 * @param entities
	 * @throws DatabaseException
	 */
	public abstract void mapMrefs(List<E> entities) throws DatabaseException;

	/**
	 * Rewrite mref rules: mref fields are actually not in the table but in a
	 * link table. To filter on an mref id or mref label one has to first query
	 * this table to extract ids for 'this' table. This function provides the
	 * functionality therefore.
	 * 
	 * @param db
	 * @param user_rule
	 *            the original rule
	 * @return a rewritten rule for mrefs, typically of the form 'id' IN 'list
	 *         of ids'
	 * @throws DatabaseException
	 */
	protected abstract QueryRule rewriteMrefRule(Database db, QueryRule user_rule) throws DatabaseException;

	/**
	 * Helper method for storing multiplicative references. This function should
	 * check wether any mref values have been newly selected or deselected. The
	 * newly selected elements should be added, the deselected elements should
	 * be removed (from the entity that holds the mrefs).
	 * 
	 * @param entities
	 * @throws DatabaseException
	 * @throws IOException
	 * @throws ParseException 
	 */
	public abstract void storeMrefs(List<E> entities) throws DatabaseException, IOException, ParseException;

	/**
	 * Foreign key values may be only given via the 'label'. This function
	 * allows resolves the underlying references for a list of entities.
	 * 
	 * @param entities
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public abstract void resolveForeignKeys(List<E> entities) throws DatabaseException, ParseException;

	/**
	 * Helper method for removing multiplicative references ('mrefs')
	 * 
	 * @param entities
	 * @throws SQLException
	 * @throws IOException
	 * @throws DatabaseException
	 * @throws ParseException 
	 */
	public abstract void removeMrefs(List<E> entities) throws SQLException, IOException, DatabaseException, ParseException;

	public int remove(CsvReader reader) throws DatabaseException
	{
		int rowsAffected = 0;
		final String TX_TICKET = "REMOVE+" + this.create().getClass().getCanonicalName() + "_CSV";
		try
		{
			database.beginPrivateTx(TX_TICKET);
			List<E> entities = toList(reader, BATCH_SIZE);
			while (entities.size() > 0)
			{
				// resolve foreign keys
				this.resolveForeignKeys(entities);

				// update to the database
				rowsAffected += database.remove(entities);
				entities = toList(reader, BATCH_SIZE);
			}

			database.commitPrivateTx(TX_TICKET);
		}
		catch (Exception e)
		{
			database.rollbackPrivateTx(TX_TICKET);
			throw new DatabaseException(e);
		}
		return rowsAffected;
	}

	public List<E> toList(CsvReader reader, int limit) throws DatabaseException
	{
		final List<E> entities = createList(10);
		try
		{
			reader.parse(limit, new CsvReaderListener()
			{
				public void handleLine(int line_number, Tuple line) throws Exception
				{
					E e = create();
					e.set(line, false); // parse the tuple
					entities.add(e);
				}
			});
		}
		catch (Exception ex)
		{
			throw new DatabaseException(ex);
		}
		return entities;
	}

	public int count(QueryRule... rules) throws DatabaseException
	{
		try
		{
			String sql = createCountSql(rules)
					+ JDBCDatabase.createWhereSql(this, false, true,
							this.rewriteRules(getDatabase(), rules));
			// + createWhereSql(getMapperFor(klazz), false, true, rules);
			ResultSet rs = getDatabase().executeQuery(sql);
			rs.next();
			int result = rs.getInt("num_rows");
			logger.debug("counted " + this.create().getClass().getSimpleName() + " objects");
			rs.close(); // closes connection too?
			return result;
		}
		catch (SQLException sqle)
		{
			logger.error("count of " + this.create().getClass().getSimpleName() + "failed: " + sqle.getMessage());
			throw new DatabaseException(sqle);
		}
		finally
		{
			getDatabase().closeConnection();
		}
	}

	public List<E> find(QueryRule... rules) throws DatabaseException
	{
		try
		{
			ResultSet rs = executeSelect(rules);
			// transform result set in entity list
			List<E> entities = createList(10);
			if (rs != null)
			{
				while (rs.next())
				{
					E entity = create();
					entity.set(new ResultSetTuple(rs));
					entities.add(entity);
				}
			}
			rs.close();

			// load mrefs
			mapMrefs(entities);

			logger.debug(entities.size() + " " + create().getClass().getSimpleName() + " objects found");
			return entities;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new DatabaseException(e);
		}
		finally
		{
			getDatabase().closeConnection();
		}
	}

	public void find(TupleWriter writer, QueryRule... rules) throws DatabaseException
	{
		// default should be false for regular behaviour
		// FIXME: java warning? "Varargs methods should only override or be
		// overridden by other varargs methods"
		this.find(writer, null, rules);
	}
	
	/**
	 * Helper function to write an already known list of entites to a TupleWriter.
	 * @param entities
	 * @param writer
	 * @param fieldsToExport
	 * @throws Exception 
	 */
	public static void find(List<? extends Entity> entities, TupleWriter writer, List<String> fieldsToExport) throws Exception
	{	
		writer.setHeaders(fieldsToExport);
		writer.writeHeader();
		for(Entity e : entities){
			writer.writeRow(e);
		}
		writer.close();
	}
	
	private boolean hasXrefByNameEquivalent(String field, Vector<String> fields){
		for(String checkField: fields){
			//must at least be "{field}" plus " _name" as length
			if(checkField.length() >= (field.length() + 5)){
				// ie. investigation_name vs. investigation
				if(checkField.substring(0, checkField.length()-5).equals(field)){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Reason for backup: function needs to be looked at - possible bug
	 * See other find() function below for explanation!
	 */
	public void findBACKUP(TupleWriter writer, boolean skipIdFields, QueryRule... rules) throws DatabaseException
	{
		try
		{
			logger.debug("new ResultSetTuple(executeSelect(rules)....");
			ResultSetTuple rs = new ResultSetTuple(executeSelect(rules));
			logger.debug("executeSelect(rules)");
			for(QueryRule q : rules){
				logger.debug("rule: " + q.toString());
			}
			// transform result set in writer
			E entity = create();
			Vector<String> fields;
			
			if (skipIdFields)
			{
				fields = entity.getFields();
				Vector<String> fieldsCopy = entity.getFields();
				fieldsCopy.remove("id");
				for(String field : fields){
					if(hasXrefByNameEquivalent(field, fields)){
						fieldsCopy.remove(field);
					}
				}
				fields = fieldsCopy;
			}
			else
			{
				fields = entity.getFields();
			}
			writer.setHeaders(fields);
			writer.writeHeader();
			int i = 0;
			List<E> entityBatch = new ArrayList<E>();
			while (rs.next())
			{
				entity = create();
				entity.set(rs);
				entityBatch.add(entity);
				i++;

				// write batch
				if (i % BATCH_SIZE == 0)
				{
					// load mrefs
					mapMrefs(entityBatch);
					for (E e : entityBatch)
					{
						writer.writeRow(e);
					}
					entityBatch.clear();
				}
			}
			// write remaining
			// load mrefs
			mapMrefs(entityBatch);
			for (E e : entityBatch)
			{
				writer.writeRow(e);
			}
			entityBatch.clear();

			rs.close();

			logger.debug("find(" + create().getClass().getSimpleName() + ", CsvWriter, " + Arrays.asList(rules)
					+ "): wrote " + i + " lines.");
		}
		catch (Exception e)
		{
			throw new DatabaseException(e);
		}
		finally
		{
			getDatabase().closeConnection();
		}
	}
	
	public void find(TupleWriter writer, List<String> fieldsToExport, QueryRule... rules) throws DatabaseException
	{
		try
		{
			//logger.debug("new ResultSetTuple(executeSelect(rules)....");
			ResultSetTuple rs = new ResultSetTuple(executeSelect(rules));
			/*logger.debug("executeSelect(rules)");
			for(QueryRule q : rules){
				logger.debug("rule: " + q.toString());
			}*/
			// transform result set in writer
			E entity = create();				
			List<String> fields = fieldsToExport; 
			if(fieldsToExport == null) fields = entity.getFields();
			
			writer.setHeaders(fields);
			writer.writeHeader();
			int i = 0;
			List<E> entityBatch = new ArrayList<E>();
			while (rs.next())
			{
				entity = create();
				entity.set(rs);
				entityBatch.add(entity);
				i++;
				
			}
			// write remaining
			// load mrefs
			logger.debug("*** mapMrefs -> LEFTOVERS"); //program does NOT crash after this
			mapMrefs(entityBatch);
			for (E e : entityBatch)
			{
				writer.writeRow(e);
			}
			entityBatch.clear();
			rs.close();
			writer.close();

			logger.debug("find(" + create().getClass().getSimpleName() + ", TupleWriter, " + 
					Arrays.asList(rules) + "): wrote " + i + " lines.");
		}
		catch (Exception e)
		{
			throw new DatabaseException(e);
		}
		finally
		{
			getDatabase().closeConnection();
		}
	}

	/**
	 * Helper function of various find functions.
	 * 
	 * @param <E>
	 * @param klazz
	 * @param rules
	 * @return
	 * @throws DatabaseException
	 * @throws SQLException
	 */
	private ResultSet executeSelect(QueryRule... rules) throws DatabaseException, SQLException
	{
		String sql = createFindSqlInclRules(rules);
		if(rules != null){
		// FIXME too complicated
			for (QueryRule rule : rules)
			{
				if (rule.getOperator() == Operator.LAST)
				{
					sql = "select * from (" + sql + ") as " + this.getClass().getSimpleName().toLowerCase() + " "
							+ JDBCDatabase.createSortSql(null, true, rules);
					break;
				}
			}
		}
		// execute the query
		logger.info("TEST\n"+sql);
		return getDatabase().executeQuery(sql);
	}
	
	@Override
	public String createFindSqlInclRules(QueryRule ... rules) throws DatabaseException
	{
		 return createFindSql()
			+ JDBCDatabase.createWhereSql((JDBCMapper<?>) this, false, true, this.rewriteRules(getDatabase(), rules));
	}

	/**
	 * Mref fields do not really exist in the table but instead in a separate
	 * link table. This method should query this link table to rewrite the query
	 * rules.
	 * 
	 * @param db
	 * @param user_rules
	 * @return
	 * @throws DatabaseException
	 */
	protected QueryRule[] rewriteRules(Database db, QueryRule... user_rules) throws DatabaseException
	{
		if(user_rules == null) return null;
		List<QueryRule> rules = this.rewriteRules(db, Arrays.asList(user_rules));
		return rules.toArray(new QueryRule[rules.size()]);
		
	}

	/**
	 * Mref fields do not really exist in the table but instead in a separate
	 * link table. This method should query this link table to rewrite the query
	 * rules.
	 * 
	 * @param db
	 * @param user_rules
	 * @return
	 * @throws DatabaseException
	 */
	protected List<QueryRule> rewriteRules(Database db, List<QueryRule> user_rules) throws DatabaseException
	{
		List<QueryRule> rules = new ArrayList<QueryRule>();
		for (QueryRule rule : user_rules)
		{
			if (rule.getOperator() != null && rule.getOperator().equals(Operator.NESTED))
			{
				QueryRule r = new QueryRule(this.rewriteRules(db, rule.getNestedRules()));
				//r.setOr(rule.isOr());
				//rules.add(new QueryRule(Operator.AND));
				rules.add(r);
			}
			else
			{
				QueryRule r = this.rewriteMrefRule(db, rule);
				//r.setOr(rule.isOr());
				//rules.add(new QueryRule(Operator.OR));
				rules.add(r);
			}
		}
		return rules;
	}
}
