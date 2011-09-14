
package org.molgenis.ngs.ui;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.jdbc.JDBCDatabase;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.util.Tuple;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;

/**
 * AdminDBPluginController takes care of all user requests and application logic.
 *
 * <li>Each user request is handled by its own method based action=methodName. 
 * <li> MOLGENIS takes care of db.commits and catches exceptions to show to the user
 * <li>AdminDBPluginModel holds application state and business logic on top of domain model. Get it via this.getModel()/setModel(..)
 * <li>AdminDBPluginView holds the template to show the layout. Get/set it via this.getView()/setView(..).
 */
public class AdminDBPlugin extends EasyPluginController<AdminDBPluginModel>
{
	public AdminDBPlugin(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(new AdminDBPluginModel(this)); //the default model
		this.setView(new FreemarkerView("AdminDBPluginView.ftl", getModel())); //<plugin flavor="freemarker"
	}
	
	/**
	 * At each page view: reload data from database into model and/or change.
	 *
	 * Exceptions will be caught, logged and shown to the user automatically via setMessages().
	 * All db actions are within one transaction.
	 */ 
	@Override
	public void reload(Database db) throws Exception
	{	
	}
	
	public void updateDate(Database db, Tuple request) throws Exception
	{
		getModel().date = request.getDate("date");
		getModel().setSuccess("update succesfull");
	}
	
	public void resetDatabase(Database db, Tuple request) throws Exception
	{

		print("resetDatabase pressed");

        //hardcoded to working directory
        String strSQL = readFile("create_tables.sql");
        execute(db, strSQL);
        strSQL = readFile("insert_metadata.sql");
		execute(db, strSQL);
        print("done with resetDatabase");
        getModel().setSuccess("reset successful");

	}
	
	private void print(String str) {
		System.out.println(">> " + str);
	}

    private String readFile(String filename) throws IOException
    {
        File file = new File(filename);

        if (!file.exists())
        {
            System.out.println("sql script does not exist");
            return null;
        }
        final BufferedInputStream bis = new BufferedInputStream(
                new FileInputStream(file));
        final byte[] bytes = new byte[(int) file.length()];
        bis.read(bytes);
        bis.close();
        return new String(bytes);
    }

    private void execute(Database db, String sqlFile) throws Exception
    {
        Connection conn = null;
        try
        {
            conn = ((JDBCDatabase) db).getConnection();

            Statement stmt = conn.createStatement();
            int i = 0;
            for (String command : sqlFile.split(";"))
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