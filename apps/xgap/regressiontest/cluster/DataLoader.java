package regressiontest.cluster;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.organization.Investigation;

import plugins.emptydb.emptyDatabase;
import plugins.molgenisfile.plink.PlinkFileManager;
import filehandling.storage.StorageHandler;

public class DataLoader
{

	public static ArrayList<String> load(Database db, boolean resetDbWhenApplicable)
	{
		ArrayList<String> result = new ArrayList<String>();

		result.add("DataLoader starting");

		// *** part 1 ***
		// check storage location
		try
		{
			File storage = new StorageHandler(db).getFileStorage(true, db);
			result.add("Storage location found. Path: " + storage.getAbsolutePath());
		}
		catch (Exception e)
		{
			result.add("Storage location is not set or invalid. Please use the system settings plugin to assign one. Exception: " + e.getMessage());
			e.printStackTrace();
			return result;
		}

		// *** part 2 ***
		// check if there are investigations present
		boolean noInvestigations = false;
		try
		{
			List<Investigation> invList = db.find(Investigation.class);
			if (invList.size() == 0)
			{
				result.add("No investigations found. Will attempt to load datamodel / reset db.");
				noInvestigations = true;
			}
			else
			{
				result.add("Investigation(s) present in the database, will not continue to load datamodel / reset db.");
			}
		}
		catch (Exception e)
		{
			result.add("Error while querying for investigations, will attempt to load datamodel / reset db anyway: "
					+ e.getMessage());
			noInvestigations = true;
		}

		// *** part 3 ***
		// if no investigations, load the datamodel thus resetting the database
		boolean loadDataModel = false;
		if (noInvestigations)
		{
			result.add("Starting to load datamodel / reset db..");
			try
			{
				if(resetDbWhenApplicable){
					new emptyDatabase(db, true);
				}else{
					result.add("Not executing database reset, already done? Continuing..");
				}
				loadDataModel = true;
				result.add("Success");
			}
			catch (Exception e)
			{
				result.add("Error: " + e.getMessage());
				return result;
			}

		}

		// *** part 4 ***
		// import example dataset
		boolean loadInvSuccess = false;
		if (!noInvestigations || loadDataModel)
		{
			result.add("Starting to load example investigation data..");
			try
			{
				new AddExampleData(db);
				loadInvSuccess = true;
				result.add("Success");
			}
			catch (Exception e)
			{
				result.add("Error, but continuing: " + e.getMessage());
			}
		}

		// *** part 5 ***
		// import cluster metadata
		if (loadInvSuccess)
		{
			result.add("Starting to load cluster meta data..");

			try
			{
				Investigation inv = db.find(Investigation.class, new QueryRule("name", Operator.EQUALS, "ClusterDemo")).get(0);
				result.add("Linking to investigation ClusterDemo");
				QueryRule investigationId = new QueryRule("investigation", Operator.EQUALS, inv.getId());
				QueryRule genoName = new QueryRule("name", Operator.EQUALS, "genotypes");
				QueryRule phenoName = new QueryRule("name", Operator.EQUALS, "metaboliteexpression");
				Data genoData = db.find(Data.class, investigationId, genoName).get(0);
				Data phenoData = db.find(Data.class, investigationId, phenoName).get(0);
				new AddClusterMetaModel(genoData, phenoData, db);
				
				PlinkFileManager.tagParameter("Plink_bin_params", "inputname", "hapmap1", db);
				System.out.println("tagged plink parameters");
				AddClusterMetaModel.tagPlinkBinaryDataSet(db);
				System.out.println("tagged plink dataset");
				
				result.add("Complete success");
			}
			catch (Exception e)
			{
				result.add("Error: " + e.getMessage());
				return result;
			}
		}

		result.add("DataLoader ended");

		return result;
	}
}
