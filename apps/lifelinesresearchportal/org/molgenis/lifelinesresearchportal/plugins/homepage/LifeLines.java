package org.molgenis.lifelinesresearchportal.plugins.homepage;

import java.io.File;
import java.util.List;

import org.molgenis.auth.MolgenisUser;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.lifelinesresearchportal.importer.mainImporter;
import org.molgenis.lifelinesresearchportal.plugins.hl7parser.HL7Data;
import org.molgenis.lifelinesresearchportal.plugins.hl7parser.HL7LLData;
import org.molgenis.lifelinesresearchportal.plugins.hl7parser.HL7PhenoImporter;
import org.molgenis.organization.Investigation;
import org.molgenis.util.Entity;
import org.molgenis.util.TarGz;
import org.molgenis.util.Tuple;
import org.molgenis.xgap.InvestigationFile;
import org.molgenis.xgap.xqtlworkbench.ResetXgapDb;

import filehandling.generic.PerformUpload;
import filehandling.storage.StorageHandler;

public class LifeLines extends PluginModel<Entity>
{
	private static final long serialVersionUID = -5324788471624447907L;
	private Investigation inv = null;
	private StorageHandler sh;
	private boolean userIsAdminAndDatabaseIsEmpty;

	private boolean validFileStorage;

	public boolean isValidFileStorage()
	{
		return validFileStorage;
	}

	public void setValidFileStorage(boolean validFileStorage)
	{
		this.validFileStorage = validFileStorage;
	}

	@Override
	public boolean isVisible()
	{
		return true;
	}

	public void setUserIsAdminAndDatabaseIsEmpty(boolean userIsAdminAndDatabaseIsEmpty)
	{
		this.userIsAdminAndDatabaseIsEmpty = userIsAdminAndDatabaseIsEmpty;
	}

	public boolean isUserIsAdminAndDatabaseIsEmpty()
	{
		return userIsAdminAndDatabaseIsEmpty;
	}

	public LifeLines(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugins_cluster_demo_homepage_LifeLines";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/lifelinesresearchportal/plugins/homepage/LifeLines.ftl";
	}

	@Override
	public void reload(Database db)
	{
		if (sh == null)
		{
			sh = new StorageHandler(db);
		}
		try
		{
			List<Investigation> invList = db.find(Investigation.class);
			if (invList.size() > 0)
			{
				inv = invList.get(0);
			}
		}
		catch (DatabaseException e)
		{
			// do nothing
		}

		this.setValidFileStorage(false);

		try
		{
			// fails when there is no table 'MolgenisUser', or no MolgenisUser
			// named 'admin'
			// assume database has not been setup yet
			db.find(MolgenisUser.class, new QueryRule(MolgenisUser.NAME, Operator.EQUALS, "admin")).get(0);
		}
		catch (Exception e)
		{
			// setup database and report back
			String report = ResetXgapDb.reset(this.getDatabase(), true);
			if (report.endsWith("SUCCESS"))
			{
				this.setMessages(new ScreenMessage("Database setup success!", true));
			}
			else
			{
				this.setMessages(new ScreenMessage("Database setup fail! Review report: " + report, false));
			}
		}

		try
		{
			// show special dataloader box for admin when the database has no
			// investigations
			if (this.getLogin().getUserName().equals("admin"))
			{
				List<Investigation> invList = db.find(Investigation.class);
				if (invList.size() == 0)
				{

					// flip bool to enable box
					setUserIsAdminAndDatabaseIsEmpty(true);

					// since we're now showing the special box,
					// find out if there is a validated path and save this info
					if (sh.hasValidFileStorage(db))
					{
						this.setValidFileStorage(true);
					}

				}
				else
				{
					System.out.println("if (invList.size() == 0) failed!");
					setUserIsAdminAndDatabaseIsEmpty(false);
				}
			}
			else
			{
				setUserIsAdminAndDatabaseIsEmpty(false);
				System.out.println("this.getLogin().getUserName().equals(admin) failed!");
			}
		}
		catch (Exception e)
		{
			// something went wrong, set boolean to false for safety
			setUserIsAdminAndDatabaseIsEmpty(false);
			System.out.println(" something went wrong, set boolean to false for safety");
			e.printStackTrace();
		}

	}

	public String getStudyInfo()
	{
		return "test2";
		// if (inv != null) {
		// return inv.getName() + ": " + inv.getDescription();
		// } else {
		// return null;
		// }
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try
		{
			String action = request.getString("__action");
			if (action.equals("setPathAndLoad"))
			{
				setupStorageAndLoadExample(db);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage(), false));
		}

	}

	public void setupStorageAndLoadExample(Database db) throws Exception
	{

		File llrp_pheno = new File("./publicdata/llrp_testing/llrp_fake_pheno.tar.gz");
		if (llrp_pheno.exists())
		{
			File extractDir = TarGz.tarExtract(llrp_pheno);

			System.out.println("files extracted to " + extractDir.getAbsolutePath());

			File voorbeeld1_dataset = new File(extractDir, "voorbeeld1_dataset.csv");
			new mainImporter(voorbeeld1_dataset, db);

			System.out.println();

			File Catalog_EX04 = new File(extractDir, "Catalog-EX04.xml");
			File Catalog_EX04_valuesets = new File(extractDir, "Catalog-EX04-valuesets.xml");

			HL7Data ll = new HL7LLData(Catalog_EX04.getAbsolutePath(), Catalog_EX04_valuesets.getAbsolutePath());
			HL7PhenoImporter importer = new HL7PhenoImporter();
			importer.start(ll, db);

			this.setMessages(new ScreenMessage("LLRP pheno data loaded!", true));

		}
		else
		{
			throw new Exception("File " + llrp_pheno.getAbsolutePath() + " is missing!");
		}

		File llrp_geno = new File("./publicdata/llrp_testing/llrp_fake_geno.tar.gz");
		if (llrp_geno.exists())
		{
			File extractDir = TarGz.tarExtract(llrp_geno);
			InvestigationFile ped = new InvestigationFile();
			ped.setName("llrp_fake_geno");
			ped.setExtension("ped");
			ped.setInvestigation(db.find(Investigation.class,
					new QueryRule(Investigation.NAME, Operator.EQUALS, "LifeLines")).get(0));
			db.add(ped);

			File uploadPed = new File(extractDir, "llrp_fake_geno.ped");
			PerformUpload.doUpload(db, ped, uploadPed, true);

			InvestigationFile map = new InvestigationFile();
			map.setName("llrp_fake_snp");
			map.setExtension("map");
			map.setInvestigation(db.find(Investigation.class,
					new QueryRule(Investigation.NAME, Operator.EQUALS, "LifeLines")).get(0));
			db.add(map);

			File uploadMap = new File(extractDir, "llrp_fake_snp.map");
			PerformUpload.doUpload(db, map, uploadMap, true);

			this.setMessages(new ScreenMessage("LLRP geno data loaded!", true));
		}
		else
		{
			throw new Exception("File " + llrp_geno.getAbsolutePath() + " is missing!");
		}
	}
}
