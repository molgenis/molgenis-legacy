/* Date:        May 13, 2009
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.screen.PluginScreenJavaTemplateGen 3.3.0-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.archiveexportimport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.organization.Investigation;
import org.molgenis.util.Entity;
import org.molgenis.util.TarGz;
import org.molgenis.util.Tuple;

import decorators.NameConvention;

public class ArchiveExportImportPlugin extends PluginModel<Entity>
{

	private static final long serialVersionUID = 7547760015212593700L;
	private String tmpFileName;
	private String selectedInvestigation;
	private List<Investigation> investigationList = new ArrayList<Investigation>();
	private String selectedFormat;
	
	

	public String getSelectedFormat() {
		return selectedFormat;
	}

	public void setSelectedFormat(String selectedFormat) {
		this.selectedFormat = selectedFormat;
	}

	public String getSelectedInvestigation()
	{
		return selectedInvestigation;
	}

	public void setSelectedInvestigation(String selectedInvestigation)
	{
		this.selectedInvestigation = selectedInvestigation;
	}

	

	public List<Investigation> getInvestigationList()
	{
		return investigationList;
	}

	public void setInvestigationList(List<Investigation> investigationList)
	{
		this.investigationList = investigationList;
	}

	public String getTmpFileName()
	{
		return tmpFileName;
	}

	public void setTmpFileName(String tmpFileName)
	{
		this.tmpFileName = tmpFileName;
	}

	public ArchiveExportImportPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugins_archiveexportimport_ArchiveExportImportPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/archiveexportimport/ArchiveExportImportPlugin.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{

		try
		{
			String action = request.getString("__action");
			this.setSelectedInvestigation(request.getString("selectInvestigation"));
			this.setSelectedFormat(request.getString("format"));
			
			if (action.equals("exportAll"))
			{

				File tmpDir;

				if (this.getSelectedInvestigation().equals("__download_every_investigation_1256037232589246000"))
				{
					tmpDir = new File(System.getProperty("java.io.tmpdir") + File.separator
							+ "everyinvestigation" + "_export_" + System.nanoTime());
					tmpDir.mkdir();
					if(request.getString("format").equals("excel")){
						new XgapExcelExport(tmpDir, db);
					}else if(request.getString("format").equals("csv")){
						new XgapCsvExport(tmpDir, db);
					}else{
						throw new Exception("Unknown format selected: " + request.getString("format"));
					}
					
				}
				else
				{
					tmpDir = new File(System.getProperty("java.io.tmpdir") + File.separator
							+ NameConvention.escapeFileName(this.getSelectedInvestigation()) + "_export_" + System.nanoTime());
					tmpDir.mkdir();
					if(request.getString("format").equals("excel")){
						new XgapExcelExport(tmpDir, db, this.getSelectedInvestigation());
					}else if(request.getString("format").equals("csv")){
						new XgapCsvExport(tmpDir, db, this.getSelectedInvestigation());
					}else{
						throw new Exception("Unknown format selected: " + request.getString("format"));
					}
				}

				File tarFile = TarGz.tarDir(tmpDir);

				this.setTmpFileName(tarFile.getName());

			}
			else if (action.equals("importAll"))
			{
					File tarFile = request.getFile("importArchive");
					File extractDir = TarGz.tarExtract(tarFile);
					if(isExcelFormatXGAPArchive(extractDir)){
						new XgapExcelImport(extractDir, db, false);
					}else{
						new XgapCsvImport(extractDir, db, false);
					}
					
			}
			
			this.setMessages(new ScreenMessage("Success", true));
		}
		catch (Exception e) {
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage()!=null?e.getMessage():"null", false));
		}
	}
	
	public static boolean isExcelFormatXGAPArchive(File extractDir){
		String[] filesArr = extractDir.list();
		
		boolean hasExcelFile = false;
		boolean hasData = false;
		boolean hasLengthTwo = false;
		boolean hasLengthOne = false;
		
		for(String s : filesArr){
			if(s.endsWith(".xls")){
				hasExcelFile = true;
			}
			if(s.equals("data")){
				hasData = true;
			}
		}
		
		if(filesArr.length == 1){
			hasLengthOne = true;
		}else if(filesArr.length == 2){
			hasLengthTwo = true;
		}
		
		if(hasExcelFile && hasLengthTwo && hasData){
			return true;
		}else if(hasExcelFile && hasLengthOne){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public void reload(Database db)
	{
		try
		{
			this.setInvestigationList(db.find(Investigation.class));
		}
		catch (Exception e) {
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage()!=null?e.getMessage():"null", false));
		}
	}

}
