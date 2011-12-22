/*
 * Date: December 24, 2010 Template: PluginScreenJavaTemplateGen.java.ftl
 * generator: org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.lifelines.plinkdownload;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.GenericPlugin;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.DivPanel;
import org.molgenis.framework.ui.html.XrefInput;
import org.molgenis.pheno.Measurement;
import org.molgenis.util.Tuple;
import org.molgenis.util.plink.datatypes.FamEntry;
import org.molgenis.util.plink.writers.FamFileWriter;

public class PlinkDownload extends GenericPlugin
{
	private static final long serialVersionUID = -4185405160313262242L;

	public PlinkDownload(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	DivPanel mainPanel = null;
	XrefInput measurements;
	ActionInput downloadThis;
	
	@Override
	public void reload(Database db)
	{
		try
		{
			if(mainPanel == null)
			{
				mainPanel = new DivPanel();
				
				measurements = new XrefInput(UUID.randomUUID().toString(), Measurement.class);
				measurements.setLabel("Select your measurement:");
				downloadThis = new ActionInput("downloadPlink", "", "Download");
				
				
				mainPanel.add(measurements);
				mainPanel.add(downloadThis);
			}
		}
		catch (Exception e)
		{
			
		}
	}
	
	@Override
	public String render() {
		return mainPanel.render();
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
	
		try {
			String action = request.getAction();
		
			if (action.equals("downloadPlink")) {
				mainPanel.setValuesFromRequest(request);
				System.out.println(measurements.getValue());
				//this is now broken again thanks to Eclipse crashing, Erik please repair!
				
				
				
				/*** fam file creation example: ***/
				//PLEASE NOTE THAT YOU MUST CALL IT A TFAM FILE
				//because it belongs to a transposed PED (--->TPED) file
				//while TFAM format is equivalent to FAM format..
				//info: http://pngu.mgh.harvard.edu/~purcell/plink/data.shtml
				
				File famExport = new File(System.getProperty("tmpdir ofzo" + File.separator + "myMeasurement_" + System.nanoTime() + ".tfam"));
			
				FamFileWriter ffw = new FamFileWriter(famExport);
				List<FamEntry> entries = new ArrayList<FamEntry>();
				
				for(int i = 0; i < 5000; i ++)
				{
					FamEntry fe = new FamEntry("LIFELINES", "iondv"+1, "father", "mother", (byte)0, Math.random());
					entries.add(fe);
				}
				
				ffw.writeAll(entries);
				
				
				/***/
			}

		} catch (Exception e) {
			if (e.getMessage() != null)
			{
				this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			}
		}
	}
	
}
