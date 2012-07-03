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

import org.apache.commons.lang.StringUtils;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.DivPanel;
import org.molgenis.framework.ui.html.Paragraph;
import org.molgenis.framework.ui.html.VerticalLayout;
import org.molgenis.pheno.Measurement;
import org.molgenis.util.Tuple;
import org.molgenis.util.plink.datatypes.FamEntry;
import org.molgenis.util.plink.writers.FamFileWriter;

public class PlinkDownload extends EasyPluginController
{
	private static final long serialVersionUID = -4185405160313262242L;

	public PlinkDownload(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	private DivPanel mainPanel = null;
	private DivPanel downloadPanel = null;
//	private AbstractRefInput<Measurement> measurements;
	private ActionInput genPlink;
	
	@Override
	public void reload(Database db)
	{
		try
		{
			if (mainPanel == null)
			{
				mainPanel = new DivPanel();
				
				Paragraph manual = new Paragraph("<strong>Here you can generate PLINK files " +
						"for your GWAS analyses</strong><br /><br />After choosing a phenotype, two text files " +
						"will be ready for you on Target GPFS storage: one (TPED) containing SNP and genotype information where " +
						"one row is a SNP; one (TFAM) containing individual and family information, " +
						"where one row is an individual.<br />The first 4 columns of a TPED file are " +
						"the same as a standard 4-column MAP file. Then all genotypes are listed for " +
						"all individuals for each particular SNP on each line. The TFAM file is just " +
						"the first six columns of a standard PED file. In other words, we have just " +
						"taken the standard PED/MAP file format, but swapped all the genotype " + 
						"information between files, after rotating it 90 degrees.<br />Read more on the " +
						"<a href='http://pngu.mgh.harvard.edu/~purcell/plink/data.shtml' target='_blank'>" +
						"PLINK documentation website</a>.");
				
//				final Builder<Measurement> xrefInputBuilder = 
//					new XrefInput.Builder<Measurement>(Measurement.NAME, Measurement.class);
//				final List<QueryRule> queryRules = Arrays.asList(
//						new QueryRule(Measurement.DATATYPE, Operator.LIKE, "NUMBER%"));
//				xrefInputBuilder.setFilters(queryRules);
//
//				//create xrefInput
//				measurements = xrefInputBuilder.build();

//				measurements.setLabel("Select your phenotype:");
//				genPlink = new ActionInput("genPlink", "", "Generate");
//				
//				mainPanel.add(manual);
//				mainPanel.add(measurements);
//				mainPanel.add(genPlink);
			}
		}
		catch (Exception e)
		{
			this.setError("Something went wrong while loading the page: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Override
	public ScreenView getView() {
		VerticalLayout view = new VerticalLayout();
		if (downloadPanel != null) {
			view.add(downloadPanel);
		}
		view.add(mainPanel);
		return view;
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		String action = request.getAction();
	
		if (action.equals("genPlink")) {
			// Get selected phenotype
			int measurementId = -1;//request.getInt(measurements.getName());
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
				sexMeasId = db.query(Measurement.class).eq(Measurement.NAME, "GESLACHT").find().get(0).getId();
			}
			catch (Exception e) {
				this.setError("Something went wrong while finding the Sex(GESLACHT) phenotype: " + e.getMessage());
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
			
			List<Object[]> parts;
			try
			{
				@SuppressWarnings({ "deprecation", "rawtypes", "unchecked" })
				List<Object[]> parts2 = db.getEntityManager()
				.createQuery("SELECT ov.target.name, " 
							+"MAX ( CASE WHEN (ov.feature.id = :sexId) THEN ov.value ELSE null END ), " 
							+"MAX ( CASE WHEN (ov.feature.id = :mesId) THEN ov.value ELSE null END ) " 
							+"FROM ObservedValue ov WHERE ov.feature = :sexId OR ov.feature = :mesId GROUP BY ov.target.name")
				.setParameter("sexId", sexMeasId) 
				.setParameter("mesId", measurementId)
				.getResultList();
				parts = parts2; //strange!
				
				//parts = db.find(ObservationTarget.class);
			}
			catch (Exception e) {
				this.setError("Something went wrong while finding the participants: " + e.getMessage());
				e.printStackTrace();
				return;
			}
			List<String> skipList = new ArrayList<String>();
			List<String> skipListNoVal = new ArrayList<String>();
			List<String> skipListDouble = new ArrayList<String>();
			
			for (final Object[] part : parts) {
				final Integer targetId = Integer.parseInt(part[0].toString());
				final String sexVal = (String) part[1];
				final String phenoVal = (String) part[2];
				double pheno = StringUtils.isNotEmpty(phenoVal) ? Double.parseDouble(phenoVal) : -9;				
				try {
					entries.add(
							new FamEntry("LIFELINES", targetId.toString(), "0", "0", 
								(byte)Integer.parseInt(sexVal), pheno));
				} catch (NumberFormatException nfe) {
					this.setError(String.format("Dataset contains a non double value: %s", phenoVal));
					return;
				}
			}
			
			ffw.writeAll(entries);
			ffw.close();
			
			downloadPanel = new DivPanel();
			downloadPanel.setStyle("border: 1px solid red; margin: 10px 10px 10px 0px; padding: 10px; width: auto");
			downloadPanel.add(new Paragraph("<strong>Your PLINK files are ready on Target GPFS storage [under construction]:</strong>"));
			downloadPanel.add(new Paragraph("TPED file"));
			downloadPanel.add(new Paragraph("<a href='tmpfile/" + famExport.getName() + "'>TFAM file</a>"));
		}
	}
	
}
