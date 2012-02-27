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

import app.FillMetadata;

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

		System.out.println("opening stream to create_tables.sql");
		InputStream fis = this.getClass().getResourceAsStream("../../create_tables.sql");
		System.out.println("start running create_tables.sql");
		empty(db, fis);
		System.out.println("done running create_tables.sql");
		fis.close();
		System.out.println("closing stream to create_tables.sql");

		if (insertMetadata == true)
		{
			//USE DISCOURAGED!
			//Please use:
			//new emptyDatabase(db, false);
			//FillMetadata.fillMetadata(db, false);
			System.out.println("opening stream to insert_metadata.sql");
			fis = this.getClass().getResourceAsStream("../../insert_metadata.sql");
			System.out.println("start running insert_metadata.sql");
			empty(db, fis);
			System.out.println("done running insert_metadata.sql");
			fis.close();
			System.out.println("closing stream to insert_metadata.sql");
		}
	}

	private void empty(Database db, InputStream sqlFile) throws Exception
	{
		Connection conn = null;
		try
		{
			conn = db.getConnection();
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
			// conn.close();
		}
	}
}
