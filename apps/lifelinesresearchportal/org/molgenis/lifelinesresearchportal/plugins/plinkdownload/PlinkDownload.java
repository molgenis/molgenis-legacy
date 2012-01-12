/*
 * Date: December 24, 2010 Template: PluginScreenJavaTemplateGen.java.ftl
 * generator: org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.lifelinesresearchportal.plugins.plinkdownload;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.GenericPlugin;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.DivPanel;
import org.molgenis.framework.ui.html.Paragraph;
import org.molgenis.framework.ui.html.XrefInput;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
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
	DivPanel downloadPanel = null;
	XrefInput measurements;
	ActionInput genPlink;
	
	@Override
	public void reload(Database db)
	{
		try
		{
			if (mainPanel == null)
			{
				mainPanel = new DivPanel();
				
				Paragraph manual = new Paragraph("<strong>Here you can generate and download PLINK files " +
						"for your GWAS analyses</strong><br /><br />After choosing a phenotype, two text files " +
						"will be generated: one (TPED) containing SNP and genotype information where " +
						"one row is a SNP; one (TFAM) containing individual and family information, " +
						"where one row is an individual.<br />The first 4 columns of a TPED file are " +
						"the same as a standard 4-column MAP file. Then all genotypes are listed for " +
						"all individuals for each particular SNP on each line. The TFAM file is just " +
						"the first six columns of a standard PED file. In other words, we have just " +
						"taken the standard PED/MAP file format, but swapped all the genotype " + 
						"information between files, after rotating it 90 degrees.<br />Read more on the " +
						"<a href='http://pngu.mgh.harvard.edu/~purcell/plink/data.shtml' target='_blank'>" +
						"PLINK documentation website</a>.");
				
				measurements = new XrefInput(UUID.randomUUID().toString(), Measurement.class);
				measurements.setLabel("Select your phenotype:");
				genPlink = new ActionInput("genPlink", "", "Generate");
				
				mainPanel.add(manual);
				mainPanel.add(measurements);
				mainPanel.add(genPlink);
			}
		}
		catch (Exception e)
		{
			this.setError("Something went wrong while loading the page: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Override
	public String render() {
		String result = "";
		if (downloadPanel != null) {
			result += downloadPanel.render();
		}
		result += mainPanel.render();
		return result;
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		String action = request.getAction();
	
		if (action.equals("genPlink")) {
			
			// Get selected phenotype
			int measurementId = request.getInt(measurements.getName());
			Measurement meas;
			try
			{
				meas = db.findById(Measurement.class, measurementId);
			}
			catch (Exception e) {
				this.setError("Something went wrong while finding your phenotype: " + e.getMessage());
				e.printStackTrace();
				return;
			}
			
			// Get measurement for sex
			int sexMeasId;
			try
			{
				sexMeasId = db.query(Measurement.class).eq(Measurement.NAME, "PATIENT_GESLACHT").find().get(0).getId();
			}
			catch (Exception e) {
				this.setError("Something went wrong while finding the Sex phenotype: " + e.getMessage());
				e.printStackTrace();
				return;
			}
			
			//PLEASE NOTE THAT YOU MUST CALL IT A TFAM FILE
			//because it belongs to a transposed PED (--->TPED) file
			//while TFAM format is equivalent to FAM format..
			//info: http://pngu.mgh.harvard.edu/~purcell/plink/data.shtml
			
			File tmpDir = new File(System.getProperty("java.io.tmpdir"));
			File famExport = new File(tmpDir.getAbsolutePath() + File.separatorChar + 
					meas.getName() + "_" + System.nanoTime() + ".tfam");
			FamFileWriter ffw;
			try
			{
				ffw = new FamFileWriter(famExport);
			}
			catch (Exception e) {
				this.setError("Something went wrong while initializing the TFAM file writer: " + e.getMessage());
				e.printStackTrace();
				return;
			}
			List<FamEntry> entries = new ArrayList<FamEntry>();
			
			List<Individual> parts;
			try
			{
				parts = db.find(Individual.class);
			}
			catch (Exception e) {
				this.setError("Something went wrong while finding the participants: " + e.getMessage());
				e.printStackTrace();
				return;
			}
			List<String> skipList = new ArrayList<String>();
			List<String> skipListNoVal = new ArrayList<String>();
			List<String> skipListDouble = new ArrayList<String>();
			for (Individual part : parts) {
				
				ObservedValue sexVal;
				ObservedValue phenoVal;
				double pheno;
				try
				{
					List<ObservedValue> valList = db.query(ObservedValue.class).eq(ObservedValue.TARGET, part.getId()).eq(ObservedValue.FEATURE, sexMeasId).find();
					if (valList == null || valList.size() == 0) {
						skipListNoVal.add(part.getName());
						continue;
					}
					sexVal = valList.get(0);
					valList = db.query(ObservedValue.class).eq(ObservedValue.TARGET, part.getId()).eq(ObservedValue.FEATURE, measurementId).find();
					if (valList == null || valList.size() == 0) {
						skipListNoVal.add(part.getName());
						continue;
					}
					phenoVal = valList.get(0);
				} catch (Exception e) {
					skipList.add(part.getName());
					continue;
				}
				try {
					pheno = Double.parseDouble(phenoVal.getValue());
				} catch (NumberFormatException nfe) {
					skipListDouble.add(part.getName());
					continue;
				}
				
				FamEntry fe = new FamEntry("LIFELINES", part.getName(), "NA", "NA", 
						(byte)Integer.parseInt(sexVal.getValue()), pheno);
				entries.add(fe);
			}
			
			ffw.writeAll(entries);
			ffw.close();
			
			downloadPanel = new DivPanel();
			downloadPanel.setStyle("border: 1px solid red; margin: 10px 10px 10px 0px; padding: 10px; width: auto");
			downloadPanel.add(new Paragraph("<strong>Your PLINK files are ready for download:</strong>"));
			downloadPanel.add(new Paragraph("Download TPED file [under construction]"));
			downloadPanel.add(new Paragraph("<a href='tmpfile/" + famExport.getName() + 
					"'>Download TFAM file</a>"));
			String parContents = "These participants were skipped because their phenotype values could not be found:";
			for (String name : skipListNoVal) {
				parContents += " " + name;
			}
			downloadPanel.add(new Paragraph(parContents));
			parContents = "These participants were skipped because their phenotype values could not be cast to doubles:";
			for (String name : skipListDouble) {
				parContents += " " + name;
			}
			downloadPanel.add(new Paragraph(parContents));
			parContents = "These participants were skipped because of other errors:";
			for (String name : skipListDouble) {
				parContents += " " + name;
			}
			downloadPanel.add(new Paragraph(parContents));
		}
	}
	
}
