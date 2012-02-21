package org.molgenis.framework.db.jpa;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.molgenis.framework.db.AbstractDatabase;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
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
public class JpaDatabase extends AbstractDatabase implements Database
{

	protected static class EMFactory
	{

		private static Map<String, EntityManagerFactory> emfs = new HashMap<String, EntityManagerFactory>();
		private static EMFactory instance = null;

		private EMFactory(String persistenceUnit)
		{
			addEntityManagerFactory(persistenceUnit, null);
		}

		private static void addEntityManagerFactory(String persistenceUnitName,
				Map<String, Object> configOverwrite)
		{
			if (!emfs.containsKey(persistenceUnitName))
			{
				EntityManagerFactory emFactory = null;
				if (configOverwrite != null)
				{
					emFactory = Persistence.createEntityManagerFactory(
							persistenceUnitName, configOverwrite);
				}
				else
				{
					emFactory = Persistence
							.createEntityManagerFactory(persistenceUnitName);
				}
				emfs.put(persistenceUnitName, emFactory);
			}
		}

		public static EntityManager createEntityManager(String persistenceUnit)
		{
			if (instance == null)
			{
				instance = new EMFactory(persistenceUnit);
			}
			if (!emfs.containsKey(persistenceUnit))
			{
				addEntityManagerFactory(persistenceUnit, null);
			}
			EntityManager result = emfs.get(persistenceUnit)
					.createEntityManager();
			return result;
		}

		public static EntityManager createEntityManager()
		{
			if (instance == null)
			{
				instance = new EMFactory("molgenis");
			}
			EntityManager result = emfs.get("molgenis").createEntityManager();
			return result;
		}

		public static EntityManagerFactory getEntityManagerFactoryByName(
				String name)
		{
			return emfs.get(name);
		}

		public static EntityManager createEntityManager(
				String persistenceUnitName, Map<String, Object> configOverrides)
		{
			if (instance == null)
			{
				instance = new EMFactory(persistenceUnitName);
			}
			if (!emfs.containsKey(persistenceUnitName))
			{
				addEntityManagerFactory(persistenceUnitName, configOverrides);
			}
			EntityManager result = emfs.get(persistenceUnitName)
					.createEntityManager();
			return result;
		}
	}

	private EntityManager em = null;
	private FullTextEntityManager ftem = null;
	private String persistenceUnitName;

	protected JpaDatabase(String persistenceUnitName)
	{
		this.persistenceUnitName = persistenceUnitName;
		this.em = EMFactory.createEntityManager();
	}

	public JpaDatabase(EntityManager em, Model model)
	{
		this.em = em;
		this.model = model;
	}

	public JpaDatabase(Model model)
	{
		this.model = model;
	}

	private int transactionCount = 0;

	protected void setEntityManager(EntityManager em)
	{
		this.em = em;
	}

	@Override
	public EntityManager getEntityManager()
	{
		return em;
	}

	@Override
	public void beginTx() throws DatabaseException
	{
		try
		{
			if (em.getTransaction() != null && !em.getTransaction().isActive())
			{
				em.getTransaction().begin();
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
		return em.getTransaction().isActive();
	}

	@Override
	public void commitTx() throws DatabaseException
	{
		try
		{
			if (em.getTransaction() != null && em.getTransaction().isActive())
			{
				em.getTransaction().commit();
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
			if (em.getTransaction().isActive())
			{
				em.getTransaction().rollback();
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
			em.close();
		}
		catch (Exception e)
		{
			throw new DatabaseException(e);
		}
	}

	@Override
	@Deprecated
	public Connection getConnection() throws DatabaseException
	{
		return JpaFrameworkFactory.createFramework().getConnection(em);
	}

	@Override
	public void flush()
	{
		em.flush();
	}

	public List executeSQLQuery(String sqlQuery)
	{
		return em.createNativeQuery(sqlQuery).getResultList();
	}

	public String getPersistenceUnitName()
	{
		return persistenceUnitName;
	}

	public void initFullTextSearch() throws InterruptedException
	{
		this.ftem = Search.getFullTextEntityManager(this.em);
		this.ftem.createIndexer().startAndWait();
	}

	public <E extends Entity> List<E> findByFullTextSearch(String searchString,
			Class<E> entityClass, String... fieldList)
	{
		QueryBuilder qb = this.ftem.getSearchFactory().buildQueryBuilder()
				.forEntity(entityClass).get();
		org.apache.lucene.search.Query query = qb.keyword().onFields(fieldList)
				.matching(searchString).createQuery();
		javax.persistence.Query persistenceQuery = this.ftem
				.createFullTextQuery(query, entityClass);
		List result = persistenceQuery.getResultList();
		return result;
	}
}