package org.molgenis.framework.db.jpa;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.hibernate.ScrollableResults;
import org.molgenis.fieldtypes.FieldType;
import org.molgenis.framework.db.AbstractMapper;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.util.Entity;
import org.molgenis.util.TupleWriter;

/**
 * JPA implementation of the Mapper interface.
 */
public abstract class AbstractJpaMapper<E extends Entity> extends AbstractMapper<E>
{
	public AbstractJpaMapper(Database database)
	{
		super(database);
	}

	@Override
	public abstract E create();

	@SuppressWarnings("unchecked")
	public Class<E> getEntityClass()
	{
		return (Class<E>) create().getClass();
	}

	@Override
	public abstract String getTableFieldName(String field);

	@Override
	public abstract FieldType getFieldType(String field);

	@Override
	public abstract void resolveForeignKeys(List<E> enteties) throws ParseException, DatabaseException;

	@Override
	public abstract String createFindSqlInclRules(QueryRule[] rules) throws DatabaseException;

	private final static Logger logger = Logger.getLogger(AbstractJpaMapper.class);

	@Override
	public int count(QueryRule... rules) throws DatabaseException
	{
		TypedQuery<Long> query = JPAQueryGeneratorUtil.createCount(getDatabase(), getEntityClass(), this, getDatabase()
				.getEntityManager(), rules);
		Long result = query.getSingleResult();
		return result.intValue();
	}

	@Override
	public List<E> find(QueryRule... rules) throws DatabaseException
	{
		TypedQuery<E> query = JPAQueryGeneratorUtil.createQuery(this.getDatabase(), getEntityClass(), this,
				getDatabase().getEntityManager(), rules);
		return query.getResultList();
	}

	@Override
	public E findById(Object id)
	{
		return getDatabase().getEntityManager().find(getEntityClass(), id);
	}

	@Override
	public List<E> findByExample(E example)
	{
		return JpaFrameworkFactory.createFramework().findByExample(getEntityManager(), example);
	}

	public EntityManager getEntityManager()
	{
		return getDatabase().getEntityManager();
	}

	@Override
	public void storeMrefs(List<E> entities) throws DatabaseException, IOException, ParseException
	{
		// automatically done by JPA
	}

	@Override
	public void removeMrefs(List<E> entities) throws SQLException, IOException, DatabaseException, ParseException
	{
		// automatically done by JPA
	}

	@SuppressWarnings("unused")
	@Override
	public void find(TupleWriter writer, List<String> fieldsToExport, QueryRule[] rules) throws DatabaseException
	{
		// TODO: implement with scrolling results set
		try
		{
			// streaming result!!!!
			ScrollableResults rs = null; // getEntityManager().getNamedQuery("GetCustomers").scroll(ScrollMode.FORWARD_ONLY);
			// ResultSetTuple rs = new ResultSetTuple(executeSelect(rules));

			/*
			 * logger.debug("executeSelect(rules)"); for(QueryRule q : rules){
			 * logger.debug("rule: " + q.toString()); }
			 */
			// transform result set in writer
			E entity = create();
			List<String> fields = fieldsToExport;
			if (fieldsToExport == null) fields = entity.getFields();

			writer.setHeaders(fields);
			writer.writeHeader();
			int i = 0;

			if (true) throw new UnsupportedOperationException("needs fixing!");
			// while (rs.next())
			// {
			// entity = (E) rs.get(0);
			// writer.writeRow(entity);
			// i++;
			//
			// }
			// write remaining
			// load mrefs
			logger.debug("*** mapMrefs -> LEFTOVERS"); // program does NOT crash
														// after this

			rs.close();
			writer.close();

			logger.debug("find(" + create().getClass().getSimpleName() + ", TupleWriter, " + Arrays.asList(rules)
					+ "): wrote " + i + " lines.");
		}
		catch (Exception e)
		{
			throw new DatabaseException(e);
		}
	}

}
