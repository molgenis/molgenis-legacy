package org.molgenis.framework.db.jpa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

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

	public static void createTables(final Database db, final Map<String, Object> configOverwrites)
	{
		JpaUtil.createTables((JpaDatabase) db, true, configOverwrites);
	}

	public static void createTables(final Database db, final boolean clearEm, final Map<String, Object> configOverwrites)
	{
		JpaUtil.createTables((JpaDatabase) db, clearEm, configOverwrites);
	}

	public static void createTables(final JpaDatabase db, final boolean clearEm, final Map<String, Object> configOverwrites)
	{
		if (clearEm)
		{
			db.getEntityManager().clear();
		}
		JpaFrameworkFactory.createFramework().createTables(
				db.getPersistenceUnitName(), configOverwrites);
	}

	public static void dropAndCreateTables(final Database db, final Map<String, Object> configOverwrites)
	{
		JpaUtil.dropAndCreateTables((JpaDatabase) db, true, configOverwrites);
	}

	public static void dropAndCreateTables(final Database db, final boolean clear, final Map<String, Object> configOverwrites)
	{
		JpaUtil.dropAndCreateTables((JpaDatabase) db, clear, configOverwrites);
	}

	public static void dropAndCreateTables(final JpaDatabase db, final boolean clear, final Map<String, Object> configOverwrites)
	{
		if (clear)
		{
			db.getEntityManager().clear();
		}
		JpaFrameworkFactory.createFramework().dropTables(
				db.getPersistenceUnitName(), configOverwrites);
		JpaFrameworkFactory.createFramework().createTables(
				db.getPersistenceUnitName(), configOverwrites);
	}

	public static void dropTables(Database db, final Map<String, Object> configOverwrites)
	{
		JpaUtil.dropTables((JpaDatabase) db, true, configOverwrites);
	}

	public static void dropTables(Database db, boolean clear, final Map<String, Object> configOverwrites)
	{
		JpaUtil.dropTables((JpaDatabase) db, clear, configOverwrites);
	}

	public static void dropTables(final JpaDatabase db, final boolean clear, final Map<String, Object> configOverwrites)
	{
		if (clear)
		{
			db.getEntityManager().clear();
		}
		JpaFrameworkFactory.createFramework().dropTables(db.getPersistenceUnitName(), configOverwrites);

	}

	public static void executeSQLScript(final String path, final Database db)
			throws IOException
	{
		executeSQLScript(new File(path), db);
	}

	public static void executeSQLScript(final File file, final Database db)
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
