/* Date:        October 28, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.molgenisfile.plink;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.cluster.ParameterName;
import org.molgenis.cluster.ParameterSet;
import org.molgenis.cluster.ParameterValue;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.organization.Investigation;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;
import org.molgenis.util.plink.drivers.MapFileDriver;
import org.molgenis.util.plink.drivers.PedFileDriver;
import org.molgenis.xgap.InvestigationFile;

import decorators.NameConvention;
import filehandling.generic.PerformUpload;

public class PlinkFileManager extends PluginModel<Entity>
{

	private static final long serialVersionUID = 7832540415673199206L;

	public PlinkFileManager(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	private PlinkFileManagerModel model = new PlinkFileManagerModel();

	public PlinkFileManagerModel getMyModel()
	{
		return model;
	}

	// private MolgenisFileHandler mfh = null;

	@Override
	public String getViewName()
	{
		return "plugins_molgenisfile_plink_PlinkFileManager";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/molgenisfile/plink/PlinkFileManager.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try
		{
			if (request.getInt("invSelect") != null)
			{
				this.model.setSelectedInv(request.getInt("invSelect"));
			}

			if (request.getString("__action") != null)
			{
				String action = request.getString("__action");

				if (action.equals("uploadBinPlink"))
				{
					this.getMyModel().setUploadMode("bin");

					String fileSetName = request.getString("binFileSetName");

					if (fileSetName != null)
					{
						NameConvention.validateFileName(fileSetName);
					}
					else
					{
						throw new Exception("Please provide a name for this set of files.");
					}

					File bimFile = request.getFile("bim_file");
					File famFile = request.getFile("fam_file");
					File bedFile = request.getFile("bed_file");

					if (bimFile == null || famFile == null || bedFile == null)
					{
						throw new Exception("One or more files were empty. Please provide them all.");
					}

					boolean molgenisFilesAdded = false;
					InvestigationFile bimInvFile = null;
					InvestigationFile famInvFile = null;
					InvestigationFile bedInvFile = null;

					db.beginTx();

					try
					{
						bimInvFile = new InvestigationFile();
						bimInvFile.setName(fileSetName + "_bim");
						bimInvFile.setExtension("bim");
						bimInvFile.setInvestigation(this.model.getSelectedInv());
						db.add(bimInvFile);

						famInvFile = new InvestigationFile();
						famInvFile.setName(fileSetName + "_fam");
						famInvFile.setExtension("fam");
						famInvFile.setInvestigation(this.model.getSelectedInv());
						db.add(famInvFile);

						bedInvFile = new InvestigationFile();
						bedInvFile.setName(fileSetName + "_bed");
						bedInvFile.setExtension("bed");
						bedInvFile.setInvestigation(this.model.getSelectedInv());
						db.add(bedInvFile);

						db.commitTx();

						molgenisFilesAdded = true;
					}
					catch (Exception e)
					{
						db.rollbackTx();
						this.setMessages(new ScreenMessage(e.getMessage(), false));
					}

					if (molgenisFilesAdded)
					{
						PerformUpload.doUpload(db, bimInvFile, bimFile, false);
						PerformUpload.doUpload(db, famInvFile, famFile, false);
						PerformUpload.doUpload(db, bedInvFile, bedFile, false);

						boolean tagged = tagParameter("Plink_bin_params", "inputname", fileSetName, db);

						if (tagged)
						{
							this.setMessages(new ScreenMessage(
									"Files successfully uploaded and tagged in Plink parameters.", true));
						}
						else
						{
							this.setMessages(new ScreenMessage("Files successfully uploaded.", true));
						}
					}

				}
				else if (action.equals("uploadCsvPlink"))
				{
					this.getMyModel().setUploadMode("csv");

					String fileSetName = request.getString("csvFileSetName");

					if (fileSetName != null)
					{
						NameConvention.validateFileName(fileSetName);
					}
					else
					{
						throw new Exception("Please provide a name for this set of files.");
					}

					File mapFile = request.getFile("map_file");
					File pedFile = request.getFile("ped_file");

					if (mapFile == null || pedFile == null)
					{
						throw new Exception("One or more files were empty. Please provide them all.");
					}
					new MapFileDriver(mapFile).validate();
					new PedFileDriver(pedFile).validate();

					boolean molgenisFilesAdded = false;
					InvestigationFile mapInvFile = null;
					InvestigationFile pedInvFile = null;

					db.beginTx();

					try
					{
						mapInvFile = new InvestigationFile();
						mapInvFile.setName(fileSetName + "_map");
						mapInvFile.setExtension("map");
						mapInvFile.setInvestigation(this.model.getSelectedInv());
						db.add(mapInvFile);

						pedInvFile = new InvestigationFile();
						pedInvFile.setName(fileSetName + "_ped");
						pedInvFile.setExtension("ped");
						pedInvFile.setInvestigation(this.model.getSelectedInv());
						db.add(pedInvFile);

						db.commitTx();

						molgenisFilesAdded = true;
					}
					catch (Exception e)
					{
						db.rollbackTx();
						this.setMessages(new ScreenMessage(e.getMessage(), false));
					}

					if (molgenisFilesAdded)
					{
						PerformUpload.doUpload(db, mapInvFile, mapFile, false);
						PerformUpload.doUpload(db, pedInvFile, pedFile, false);

						boolean tagged = tagParameter("Plink_params", "inputname", fileSetName, db);

						if (tagged)
						{
							this.setMessages(new ScreenMessage(
									"Files successfully uploaded and tagged in Plink parameters.", true));
						}
						else
						{
							this.setMessages(new ScreenMessage("Files successfully uploaded.", true));
						}

					}

				}
				else if (action.equals("uploadOtherPlink"))
				{
					this.getMyModel().setUploadMode("other");
					this.setMessages(new ScreenMessage("Not supported yet.", false));
				}

			}
		}

		catch (Exception e)
		{
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
		}
	}

	public static boolean tagParameter(String paramSet, String paramName, String paramValue, Database db)
			throws DatabaseException, ParseException, IOException
	{
		List<ParameterSet> plinkParams = db.find(ParameterSet.class, new QueryRule(ParameterSet.NAME, Operator.EQUALS,
				paramSet));
		if (plinkParams.size() == 0)
		{
			// Trying to tag Plink fileset as '"+paramValue+"', but no
			// ParameterSet found with name '"+paramSet+"'.
			return false;
		}

		Query<ParameterName> q = db.query(ParameterName.class);
		q.addRules(new QueryRule(ParameterName.NAME, Operator.EQUALS, paramName));
		q.addRules(new QueryRule(ParameterName.PARAMETERSET, Operator.EQUALS, plinkParams.get(0).getId()));
		List<ParameterName> inputName = q.find();

		if (inputName.size() == 0)
		{
			// Trying to tag Plink fileset as '"+paramValue+"', but no
			// ParameterName found with name '"+paramName+"'.
			return false;
		}

		ParameterValue val = new ParameterValue();
		val.setParameterName(inputName.get(0));
		val.setValue(paramValue);
		val.setName("Plink_" + paramValue);
		db.add(val);

		// Tagged '"+paramValue+"' in ParameterSet '"+paramSet+"' under
		// ParameterName '"+paramName+"'.

		return true;
	}

	@Override
	public void reload(Database db)
	{

		try
		{
			List<Investigation> invList = db.find(Investigation.class);
			this.model.setInvestigations(invList);

			List<String> extensions = new ArrayList<String>();
			extensions.add("ped");
			extensions.add("map");
			extensions.add("bim");
			extensions.add("fam");
			extensions.add("bed");

			// List<InvestigationFile> invFiles =
			// db.find(InvestigationFile.class, new
			// QueryRule(InvestigationFile.EXTENSION, Operator.IN, extensions));

			if (this.getMyModel().getUploadMode() == null)
			{
				this.getMyModel().setUploadMode("bin");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));

		}
	}

}
