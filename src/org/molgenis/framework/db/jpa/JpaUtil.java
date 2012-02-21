package org.molgenis.framework.db.jpa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.common.util.StringUtils;
import org.molgenis.framework.db.Database;
import org.molgenis.util.HandleException;

public class JpaUtil
{
	/** Logger */
	protected final static Log logger = LogFactory.getLog(JpaUtil.class);

	public static void createTables(Database db)
	{
		JpaUtil.createTables((JpaDatabase) db, true);
	}

	public static void createTables(Database db, boolean clearEm)
	{
		JpaUtil.createTables((JpaDatabase) db, clearEm);
	}

	public static void createTables(JpaDatabase db, boolean clearEm)
	{
		if (clearEm)
		{
			db.getEntityManager().clear();
		}
		JpaFrameworkFactory.createFramework().createTables(
				db.getPersistenceUnitName());
	}

	public static void dropAndCreateTables(Database db)
	{
		JpaUtil.dropAndCreateTables((JpaDatabase) db, true);
	}

	public static void dropAndCreateTables(Database db, boolean clear)
	{
		JpaUtil.dropAndCreateTables((JpaDatabase) db, clear);
	}

	public static void dropAndCreateTables(JpaDatabase db, boolean clear)
	{
		if (clear)
		{
			db.getEntityManager().clear();
		}
		JpaFrameworkFactory.createFramework().dropTables(
				db.getPersistenceUnitName());
		JpaFrameworkFactory.createFramework().createTables(
				db.getPersistenceUnitName());
	}

	public static void dropTables(Database db)
	{
		JpaUtil.dropTables((JpaDatabase) db, true);
	}

	public static void dropTables(Database db, boolean clear)
	{
		JpaUtil.dropTables((JpaDatabase) db, clear);
	}

	public static void dropTables(JpaDatabase db, boolean clear)
	{
		if (clear)
		{
			db.getEntityManager().clear();
		}
		JpaFrameworkFactory.createFramework().createTables(
				db.getPersistenceUnitName());
	}

	public static void executeSQLScript(String path, Database db)
			throws IOException
	{
		executeSQLScript(new File(path), db);
	}

	public static void executeSQLScript(File file, Database db)
			throws IOException
	{
		if (!(db instanceof JpaDatabase))
		{
			throw new IllegalArgumentException("!(db instanceof JpaDatabase)");
		}

		FileReader fr = null;
		BufferedReader br = null;
		try
		{
			fr = new FileReader(file);
			br = new BufferedReader(fr);

			EntityManager em = db.getEntityManager();
			em.getTransaction().begin();
			while (br.ready())
			{
				String sql = br.readLine();
				if (StringUtils.isEmpty(sql))
				{
					continue;
				}
				int result = -1;
				try {
					 result = em.createNativeQuery(sql).executeUpdate();
				} catch (Exception ex) {
					logger.error(String.format("Error executing '%s'\n %s", sql,
							ex.getMessage()));
					throw ex;
				}
				logger.info(String.format("Got result %d from '%s'\n", result, sql));
			}
			em.getTransaction().commit();
			fr.close();
		}
		catch (Exception e)
		{
			HandleException.handle(e, logger);
		}
	}
}
