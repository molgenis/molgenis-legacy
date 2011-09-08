package plugins.emptydb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.jdbc.JDBCDatabase;
import org.molgenis.util.JarClass;
import org.molgenis.util.cmdline.CmdLineException;

public class emptyDatabase
{

	/**
	 * Empties the supplied database and then runs the generated
	 * create_tables.sql and, if desired, insert_metadata.sql.
	 * 
	 * @param db
	 * @param insertMetadata
	 *            Run generated insert_metadata.sql or not
	 * @throws Exception
	 */
	public emptyDatabase(Database db, boolean insertMetadata) throws Exception
	{

		System.out.println("start running create_tables.sql");
		try
		{
			InputStream fis = this.getClass().getResourceAsStream("../../create_tables.sql");
			empty(db, fis);
		}
		catch (NullPointerException e)
		{
			System.out.println("NullPointerException caught - trying to obtain SQL from JAR instead");
			// maybe we're running in a JAR?
			// TODO: name of the app is now hardcoded, booo
			InputStream f = JarClass.getFileFromJARFile("Application.jar", "create_tables.sql");
			empty(db, f);
		}
		System.out.println("done running create_tables.sql");

		if (insertMetadata == true)
		{

			System.out.println("start running insert_metadata.sql");
			try
			{
				InputStream fis = this.getClass().getResourceAsStream("../../insert_metadata.sql");
				empty(db, fis);
			}
			catch (NullPointerException e)
			{
				System.out.println("NullPointerException caught - trying to obtain SQL from JAR instead");
				// maybe we're running in a JAR?
				// TODO: name of the app is now hardcoded, booo
				InputStream f = JarClass.getFileFromJARFile("Application.jar", "insert_metadata.sql");
				empty(db, f);
			}
			System.out.println("done running insert_metadata.sql");
		}
	}

	private void empty(Database db, InputStream sqlFile) throws Exception
	{
		Connection conn = null;
		try
		{
			conn = ((JDBCDatabase) db).getConnection();
			String create_tables_sql = "";

			BufferedReader in = new BufferedReader(new InputStreamReader(sqlFile));
			String line;
			while ((line = in.readLine()) != null)
			{
				create_tables_sql += line + "\n";
			}
			in.close();

			Statement stmt = conn.createStatement();
			int i = 0;
			for (String command : create_tables_sql.split(";"))
			{
				if (command.trim().length() > 0)
				{
					stmt.executeUpdate(command + ";");
					if (i++ % 10 == 0)
					{
						// System.out.print(".");
					}

				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		finally
		{
//			conn.close();
		}
	}
}

/*
package plugins.emptydb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.jdbc.JDBCDatabase;
import org.molgenis.util.JarClass;
import org.molgenis.util.cmdline.CmdLineException;

public class emptyDatabase
{

	
	 * Empties the supplied database and then runs the generated
	 * create_tables.sql and, if desired, insert_metadata.sql.
	 * 
	 * @param db
	 * @param insertMetadata
	 *            Run generated insert_metadata.sql or not
	 * @throws Exception
	
	public emptyDatabase(Database db, boolean insertMetadata) throws Exception
	{

		System.out.println("start running create_tables.sql");
			
		InputStream fis = this.getClass().getResourceAsStream("../../create_tables.sql");
		if(fis != null) {
			empty(db, fis);
		} else {
			System.out.println("File not found - trying to obtain SQL from JAR instead");
			InputStream f = JarClass.getFileFromJARFile("Application.jar", "create_tables.sql");
			if(f == null) {
				throw new NullPointerException("Resouce not found in Jar or directory!");
			}
			empty(db, f);				
		}
		
		System.out.println("done running create_tables.sql");

		app.FillMetadata.fillMetadata(db);
	}

	private void empty(Database db, InputStream sqlFile) throws Exception
	{
		Connection conn = null;
		try
		{
			conn = ((JDBCDatabase) db).getConnection();
			String create_tables_sql = "";

			BufferedReader in = new BufferedReader(new InputStreamReader(sqlFile));
			String line;
			while ((line = in.readLine()) != null)
			{
				create_tables_sql += line + "\n";
			}
			in.close();

			Statement stmt = conn.createStatement();
			int i = 0;
			for (String command : create_tables_sql.split(";"))
			{
				if (command.trim().length() > 0)
				{
					stmt.executeUpdate(command + ";");
					if (i++ % 10 == 0)
					{
						// System.out.print(".");
					}

				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		finally
		{
			conn.close();
		}
	}
}
*/
