package regressiontest.molgenisfile.upload;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.organization.Investigation;

import plugins.cluster.helper.Command;
import plugins.cluster.implementations.LocalComputationResource;
import app.DatabaseFactory;
import filehandling.generic.PerformUpload;

public class UploadTest
{

	/**
	 * Test the uploading of files using a commandline call. We call 'curl' and
	 * upload to the local servlet that's accepting files.
	 * 
	 * @throws Exception
	 */
	public UploadTest(String method) throws Exception
	{

		Logger logger = Logger.getLogger(getClass().getSimpleName());
		logger.shutdown();
		
		// get database, filehandler, and computeresource to handle commandline
		// calls
		Database db = DatabaseFactory.create("handwritten/apps/org/molgenis/xgap/xgap.properties");
		LocalComputationResource lc = new LocalComputationResource();

		// names and type of files we want to upload
		String[] names = new String[]
		{ "b14.jpg", "b30.jpg", "b40.jpg" };
		String type = "InvestigationFile";

		// check if there are investigations, if not, add one
		List<Investigation> invList = db.find(Investigation.class);
		if (invList.size() == 0)
		{
			Investigation tmpInv = new Investigation();
			tmpInv.setName("tmp");
			db.add(tmpInv);
		}

		// get the first investigation from the db
		Investigation inv = db.find(Investigation.class).get(0);

		if (method.equals("java"))
		{
			for (String name : names)
			{
				File img = new File(this.getClass().getResource(name).getFile());
				String file = img.getAbsolutePath();
				HashMap<String, String> extraFields = new HashMap<String, String>();
				extraFields.put("Investigation_name", inv.getName());
				PerformUpload.doUpload(db, true, name, type, img, extraFields, false);
			}
		}
		if (method.equals("curl"))
		{
			// iterate through names and upload to database
			for (String name : names)
			{
				File img = new File(this.getClass().getResource(name).getFile());
				String file = img.getAbsolutePath();
				String curlCallToLocal = "curl -F \"file=@" + file + "\" -F \"name=" + name
						+ "\" -F \"investigation_name=" + inv.getName() + "\"" + " -F \"type=" + type + "\" "
						+ "http://localhost:8080/gcc/uploadfile";
				Command command = new Command(curlCallToLocal);
				lc.executeCommand(command);
			}
		}

	}

	public static void main(String[] args) throws Exception
	{
		new UploadTest("curl");

	}

}
