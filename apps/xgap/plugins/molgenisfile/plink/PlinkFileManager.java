/* Date:        October 28, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.molgenisfile.plink;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.organization.Investigation;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;
import org.molgenis.xgap.InvestigationFile;

import decorators.MolgenisFileHandler;
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

	private MolgenisFileHandler mfh = null;

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
					
					if (fileSetName == null)
					{
						NameConvention.validateFileName(fileSetName);
						throw new Exception("Please provide a name for this set of files.");
					}

					File bimFile = request.getFile("bim_file");
					File famFile = request.getFile("fam_file");
					File bedFile = request.getFile("bed_file");

					if (bimFile == null || famFile == null || bedFile == null)
					{
						throw new Exception("One or more files were empty. Please provide them all.");
					}
					
					InvestigationFile bimInvFile = new InvestigationFile();
					bimInvFile.setName(fileSetName+"_bim_plink_file");
					bimInvFile.setExtension("bim");
					bimInvFile.setInvestigation(this.model.getSelectedInv());
					db.add(bimInvFile);
					
					InvestigationFile famInvFile = new InvestigationFile();
					famInvFile.setName(fileSetName+"_fam_plink_file");
					famInvFile.setExtension("fam");
					famInvFile.setInvestigation(this.model.getSelectedInv());
					db.add(famInvFile);
					
					InvestigationFile bedInvFile = new InvestigationFile();
					bedInvFile.setName(fileSetName+"_bed_plink_file");
					bedInvFile.setExtension("bed");
					bedInvFile.setInvestigation(this.model.getSelectedInv());
					db.add(bedInvFile);
					
					PerformUpload.doUpload(db, bimInvFile, bimFile, false);
					PerformUpload.doUpload(db, famInvFile, famFile, false);
					PerformUpload.doUpload(db, bedInvFile, bedFile, false);
					
					this.setMessages(new ScreenMessage("Files successfully uploaded", true));

				}
				else if (action.equals("uploadCsvPlink"))
				{
					this.getMyModel().setUploadMode("csv");
					
					String fileSetName = request.getString("csvFileSetName");
					
					if (fileSetName == null)
					{
						NameConvention.validateFileName(fileSetName);
						throw new Exception("Please provide a name for this set of files.");
					}
					
					File mapFile = request.getFile("map_file");
					File pedFile = request.getFile("ped_file");

					if (mapFile == null || pedFile == null)
					{
						throw new Exception("One or more files were empty. Please provide them all.");
					}
					
					InvestigationFile mapInvFile = new InvestigationFile();
					mapInvFile.setName(fileSetName+"_map_plink_file");
					mapInvFile.setExtension("map");
					mapInvFile.setInvestigation(this.model.getSelectedInv());
					db.add(mapInvFile);
					
					InvestigationFile pedInvFile = new InvestigationFile();
					pedInvFile.setName(fileSetName+"_ped_plink_file");
					pedInvFile.setExtension("ped");
					pedInvFile.setInvestigation(this.model.getSelectedInv());
					db.add(pedInvFile);
					
					PerformUpload.doUpload(db, mapInvFile, mapFile, false);
					PerformUpload.doUpload(db, pedInvFile, pedFile, false);
					
					this.setMessages(new ScreenMessage("Files successfully uploaded", true));

					
				}
				else if (action.equals("uploadOtherPlink"))
				{
					this.getMyModel().setUploadMode("other");
				}

			}
		}

		catch (Exception e)
		{
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
		}
	}

	public void clearMessage()
	{
		this.setMessages();
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
			
			List<InvestigationFile> invFiles = db.find(InvestigationFile.class, new QueryRule(InvestigationFile.EXTENSION, Operator.IN, extensions));
			
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
