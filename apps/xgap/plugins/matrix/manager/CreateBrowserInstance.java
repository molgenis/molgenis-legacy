package plugins.matrix.manager;

import matrix.DataMatrixInstance;
import matrix.general.DataMatrixHandler;

import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.ApplicationController;

/**
 * Help to create a browser instance 
 */
public class CreateBrowserInstance
{
	private Browser br;
	
	public Browser getBrowser()
	{
		return br;
	}
	
	public CreateBrowserInstance(Database db, Data data, ApplicationController ac) throws Exception
	{
		boolean verifiedBackend = false;
		DataMatrixHandler dmh = new DataMatrixHandler(db); //must create new because function is static (reused)
		verifiedBackend = dmh.isDataStoredIn(data, data.getStorage(), db);
		if (verifiedBackend)
		{
			DataMatrixInstance m = dmh.createInstance(data, db);
			Browser br = new Browser(data, m, ac);
			this.br = br;
		}
		else
		{
			throw new Exception("Could not verify existence of data source");
		}
	}
}
