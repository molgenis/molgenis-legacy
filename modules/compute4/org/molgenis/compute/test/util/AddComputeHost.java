package org.molgenis.compute.test.util;

import org.molgenis.compute.runtime.ComputeHost;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;

import app.DatabaseFactory;

/**
 * Created with IntelliJ IDEA. User: georgebyelas Date: 22/08/2012 Time: 14:52
 * To change this template use File | Settings | File Templates.
 */
public class AddComputeHost
{
	public static void main(String[] args)
	{
		Database db = null;
		try
		{
			db = DatabaseFactory.create();
			db.beginTx();

			ComputeHost host = new ComputeHost();
			host.setName("lsgrid");
			host.setHostDir("/home/byelas/pilot");
			host.setHostName("ui.grid.sara.nl");
			host.setHostUsername("byelas");
			host.setHostPassword("secret!");// K960u4jT
			host.setHostType("glite");

			db.add(host);
			db.commitTx();

		}
		catch (DatabaseException e)
		{
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
		}
	}
}
