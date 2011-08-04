package org.molgenis.framework.db.inmemory;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.FileSourceHelper;
import org.molgenis.framework.db.JoinQuery;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryImp;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.security.Login;
import org.molgenis.model.elements.Model;
import org.molgenis.util.CsvReader;
import org.molgenis.util.SpreadsheetWriter;
import org.molgenis.util.Entity;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

/***
 * Naieve implementation of an in-memory database. Usefull, not terribly
 * efficient. No consraints are enforced like unique and not null.
 * 
 * @author Morris Swertz
 */
public class InMemoryDatabase implements Database
{
	private Map<Class<? extends Entity>, Map<Integer, Entity>> db = new LinkedHashMap<Class<? extends Entity>, Map<Integer, Entity>>();
	private int autoid = 0;
	private static transient final Logger logger = Logger
			.getLogger(InMemoryDatabase.class);

	@Override
	public <E extends Entity> int add(E entity) throws DatabaseException
	{
		Class<? extends Entity> c = entity.getClass();
		if (db.get(c) == null)
		{
			db.put(c, new LinkedHashMap<Integer, Entity>());
		}

		db.get(c).put(autoid++, (Entity) entity);

		// set autoid unless already set
		if (entity.get(entity.getIdField()) == null)
		{
			Tuple t = new SimpleTuple();
			t.set(entity.getIdField(), autoid);
			try
			{
				entity.set(t, false);
			}
			catch (Exception ex)
			{
				throw new DatabaseException(ex);
			}
		}
		return 1;
	}

	@Override
	public <E extends Entity> int add(List<E> entities)
			throws DatabaseException
	{
		for (E entity : entities)
		{
			// todo need to set autoid to index...
			this.add(entity);
		}
		return entities.size();
	}

	@Override
	public <E extends Entity> int add(Class<E> klazz, CsvReader reader,
			SpreadsheetWriter writer)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void beginTx() throws DatabaseException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() throws DatabaseException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void commitTx() throws DatabaseException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public <E extends Entity> int count(Class<E> klazz, QueryRule... rules)
			throws DatabaseException
	{
		return this.find(klazz, rules).size();
	}

	@Override
	public <E extends Entity> void find(Class<E> klazz, SpreadsheetWriter writer,
			QueryRule... rules) throws DatabaseException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public <E extends Entity> List<E> find(Class<E> klazz, QueryRule... rules)
			throws DatabaseException
	{
		if (db.get(klazz) == null) 
			return new ArrayList<E>();
		
		if (rules.length == 0)
		{
			return getValues(klazz);
		}
		else
		{
			List<E> result = new ArrayList<E>();
			for (E entity : getValues(klazz))
			{
				boolean match = true;

				for (QueryRule r : rules)
				{
					if (!r.getOperator().equals(Operator.EQUALS))
					{
						throw new DatabaseException(
								"Operators other that EQUALS are not yet supported");
					}
					if (!(entity.get(r.getField()) != null && entity.get(
							r.getField()).equals(r.getValue())))
					{
						match = false;
					}
				}

				if (match) result.add(entity);

			}
			return result;
		}
	}

	@SuppressWarnings("unchecked")
	private <E extends Entity> List<E> getValues(Class<E> klazz)
	{
		return (List<E>) db.get(klazz).values();
	}

	@Override
	public <E extends Entity> List<E> findByExample(E example)
			throws DatabaseException
	{
		Query<E> q = this.query(getClassForEntity(example));

		for (String field : example.getFields())
		{
			if (example.get(field) != null)
			{
				q.equals(field, example.get(field));
			}
		}

		return q.find();
	}

	@SuppressWarnings("unchecked")
	private <E extends Entity> Class<E> getClassForEntity(E entity)
	{
		return (Class<E>) entity.getClass();
	}

	@Override
	public List<Class<? extends Entity>> getEntityClasses()
	{
		return new ArrayList<Class<? extends Entity>>(db.keySet());
	}

	@Override
	public List<String> getEntityNames()
	{
		// TODO in common superclass
		List<String> names = new ArrayList<String>();
		for (Class<? extends Entity> c : this.getEntityClasses())
		{
			names.add(c.getSimpleName());
		}
		return names;
	}

	@Override
	public File getFilesource()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean inTx()
	{
		return true;
	}

	@Override
	public <E extends Entity> Query<E> query(Class<E> klazz)
	{
		// TODO move to common superclass
		return new QueryImp<E>(this, klazz);
	}

	@Override
	public <E extends Entity> int remove(E entity) throws DatabaseException
	{
		List<E> entities = this.findByExample(entity);
		Entity e;
		if (entities.size() == 1)
		{
			e = entities.get(0);
			db.get(e.getClass()).remove(e);
			return 1;
		}
		else
		{
			logger.warn("remove failed: keys not set so " + entities.size()
					+ " found");
			return 0;
		}
	}

	@Override
	public <E extends Entity> int remove(List<E> entities)
			throws DatabaseException
	{
		int count = 0;
		for (E entity : entities)
		{
			count += this.remove(entity);
		}
		return count;
	}

	@Override
	public <E extends Entity> int remove(Class<E> klazz, CsvReader reader)
			throws DatabaseException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void rollbackTx() throws DatabaseException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public <E extends Entity> List<E> toList(Class<E> klazz, CsvReader reader,
			int noEntities) throws DatabaseException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public <E extends Entity> int update(E entity) throws DatabaseException
	{
		// needs to know primary key for this...and that needs to be set...
		throw new UnsupportedOperationException();
	}

	@Override
	public <E extends Entity> int update(List<E> entities)
			throws DatabaseException
	{
		// needs to know primary key for this...and that needs to be set...
		throw new UnsupportedOperationException();
	}

	@Override
	public <E extends Entity> int update(Class<E> klazz, CsvReader reader)
			throws DatabaseException
	{
		// needs to know primary key for this...and that needs to be set...
		throw new UnsupportedOperationException();
	}

	public String toString()
	{
		StringBuffer buff = new StringBuffer();

		for (Class<?> c : db.keySet())
		{
			for (Entity e : db.get(c).values())
			{
				buff.append(e + "\n");
			}
		}

		buff.append("\nTotals per class: \n");
		int grandtotal = 0;
		for (Class<?> c : db.keySet())
		{
			buff.append(c.getSimpleName() + ":" + db.get(c).size() + "\n");
			grandtotal += db.get(c).size();
		}
		buff.append("\nTotal objects in memory database: " + grandtotal);
		return buff.toString();
	}

	@Override
	public Login getSecurity()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setLogin(Login login)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Class<? extends Entity> getClassForName(String name)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public JoinQuery join(Class<? extends Entity> ... classes) throws DatabaseException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Model getMetaData() throws DatabaseException
	{
		throw new UnsupportedOperationException("use a subclass of this class");
	}

	@Override
	public <E extends Entity> E findById(Class<E> klazz, Object id)
			throws DatabaseException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public <E extends Entity> int update(List<E> entities,
			DatabaseAction dbAction, String... keyName)
			throws DatabaseException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public EntityManager getEntityManager()
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public FileSourceHelper getFileSourceHelper() throws Exception
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public <E extends Entity> void find(Class<E> entityClass, SpreadsheetWriter writer,
			List<String> fieldsToExport, QueryRule... rules)
			throws DatabaseException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Tuple> sql(String query, QueryRule ...queryRules) throws DatabaseException
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public ResultSet executeQuery(String query, QueryRule ...queryRules)
	{
		throw new UnsupportedOperationException();
	}	
}
