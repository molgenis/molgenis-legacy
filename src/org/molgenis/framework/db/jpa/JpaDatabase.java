package org.molgenis.framework.db.jpa;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.molgenis.MolgenisOptions;
import org.molgenis.framework.db.AbstractDatabase;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.ExampleData;
import org.molgenis.model.elements.Model;
import org.molgenis.util.Entity;

/**
 * Java Persistence API (JPA) implementation of Database to query relational
 * databases.
 * <p>
 * In order to function, {@link org.molgenis.framework.db.JpaMapper} must be
 * added for each {@link org.molgenis.framework.util.Entity} E that can be
 * queried. These mappers take care of the interaction with a database.
 * 
 * @author Morris Swertz
 * @author Joris Lops
 */
public abstract class JpaDatabase extends AbstractDatabase
{
	//not thread safe to use static!
	private EntityManager singleton;
	private String persistenceUnitName = "molgenis"; // default
	private Map<String,Object> configOverrides = null;

	public JpaDatabase(Model model) throws DatabaseException
	{
		if (singleton == null)
		{
			this.singleton = Persistence.createEntityManagerFactory(persistenceUnitName).createEntityManager();
		}
		this.model = model;

		initMappers(this);
	}

	/**
	 * @param model
	 *            preloaded meta model (required)
	 * @param persistenceUnitName
	 *            name of the jpa unit
	 * @throws DatabaseException
	 */
	@SuppressWarnings("static-access")
	protected JpaDatabase(Model model, String persistenceUnitName) throws DatabaseException
	{
		this.persistenceUnitName = persistenceUnitName;
		
		if (singleton == null)
		{
			this.singleton = Persistence.createEntityManagerFactory(persistenceUnitName).createEntityManager();
		}
		this.model = model;

		initMappers(this);
	}

	@SuppressWarnings("static-access")
	public JpaDatabase(Model model, Map<String, Object> configOverrides) throws DatabaseException
	{
		this.configOverrides = configOverrides;
		
		if (singleton == null)
		{
			this.singleton = Persistence.createEntityManagerFactory(persistenceUnitName, configOverrides)
					.createEntityManager();
		}
		this.model = model;

		initMappers(this);
	}

	@SuppressWarnings("static-access")
	public JpaDatabase(Model model, MolgenisOptions options) throws DatabaseException
	{
		if (singleton == null)
		{
			this.singleton = Persistence.createEntityManagerFactory(persistenceUnitName).createEntityManager();
		}
		this.model = model;

		initMappers(this);
	}

	public abstract void initMappers(Database db);

	@Override
	public EntityManager getEntityManager()
	{
		return singleton;
	}

	@Override
	public void beginTx() throws DatabaseException
	{
		try
		{
			if (singleton.getTransaction() != null && !singleton.getTransaction().isActive())
			{
				singleton.getTransaction().begin();
			}
		}
		catch (Exception e)
		{
			throw new DatabaseException(e);
		}
	}

	@Override
	public boolean inTx()
	{
		return singleton.getTransaction().isActive();
	}

	@Override
	public void commitTx() throws DatabaseException
	{
		try
		{
			if (singleton.getTransaction() != null && singleton.getTransaction().isActive())
			{
				singleton.getTransaction().commit();
			}
		}
		catch (Exception e)
		{
			throw new DatabaseException(e);
		}
	}

	@Override
	public void rollbackTx() throws DatabaseException
	{
		try
		{
			if (singleton.getTransaction().isActive())
			{
				singleton.getTransaction().rollback();
			}
		}
		catch (Exception e)
		{
			throw new DatabaseException(e);
		}
	}

	@Override
	public void close() throws DatabaseException
	{
		try
		{
			singleton.close();
		}
		catch (Exception e)
		{
			throw new DatabaseException(e);
		}
	}

	@Override
	public void createTables()
	{
		JpaUtil.dropAndCreateTables(this, null);
	}

	@Override
	public void updateTables()
	{
		JpaUtil.updateTables(this, null);
	}

	@Override
	public void dropTables()
	{
		JpaUtil.dropTables(this, null);
	}

	@Override
	public void loadExampleData(ExampleData exampleData) throws DatabaseException
	{
		exampleData.load(this);
	}

	@Override
	@Deprecated
	public Connection getConnection() throws DatabaseException
	{
		return JpaFrameworkFactory.createFramework().getConnection(singleton);
	}

	@Override
	public void flush()
	{
		singleton.flush();
	}

	@SuppressWarnings("rawtypes")
	public List executeSQLQuery(String sqlQuery)
	{
		return singleton.createNativeQuery(sqlQuery).getResultList();
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> executeSQLQuery(String sqlQuery, Class<T> resultClass)
	{
		return singleton.createNativeQuery(sqlQuery, resultClass).getResultList();
	}

	public String getPersistenceUnitName()
	{
		return persistenceUnitName;
	}

	public void index()
	{
		try
		{
			FullTextEntityManager ftem = Search.getFullTextEntityManager(this.singleton);
			ftem.createIndexer().startAndWait();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	public <E extends Entity> List<E> search(Class<E> entityClass, String fieldList, String searchString)
	{
		FullTextEntityManager ftem = Search.getFullTextEntityManager(this.singleton);
		QueryBuilder qb = ftem.getSearchFactory().buildQueryBuilder().forEntity(entityClass).get();
		org.apache.lucene.search.Query query = qb.keyword().onFields(fieldList).matching(searchString).createQuery();
		javax.persistence.Query persistenceQuery = ftem.createFullTextQuery(query, entityClass);
		@SuppressWarnings("unchecked")
		List<E> result = persistenceQuery.getResultList();
		return result;
	}
}