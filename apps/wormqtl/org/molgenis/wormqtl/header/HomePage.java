/* Date:        October 5, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.wormqtl.header;

import java.io.File;

import matrix.general.DataMatrixHandler;

import org.molgenis.auth.MolgenisPermission;
import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.util.Tuple;

import plugins.system.database.Settings;
import app.CsvImport;
import app.ExcelImport;

public class HomePage extends plugins.cluster.demo.ClusterDemo
{

	private static final long serialVersionUID = -3744678801173089268L;

	public HomePage(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_wormqtl_header_HomePage";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/wormqtl/header/HomePage.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		String action = request.getString("__action");
		if (action.equals("setPathAndLoad"))
		{
			setupStorageAndLoadExample(db, request.getString("fileDirPath"));
			//addPanaceaPermissionsAndTryToImportData(db);
		}
	}

	private void addPanaceaPermissionsAndTryToImportData(Database db)
	{
		try
		{

			String[] qtlFinderPerms = new String[]
			{

					// allow to see the QTL finder
					"app.ui.QtlFinderPublic2Plugin",
					
					// enable the Browse Data menu (minus Inspector and matrix removal)
					"app.ui.InvestigationsFormController",
					"app.ui.DatasFormController",
					"app.ui.OverviewPlugin",
					"app.ui.ManagerPlugin",
					
					// needed to query elements for investigation overview
					"org.molgenis.pheno.ObservationElement",
					
					// needed to view the generated annotation menus
					"app.ui.IndividualsFormController",
					"org.molgenis.pheno.Individual",
					
					"app.ui.PanelsFormController",
					"org.molgenis.pheno.Panel",
					
					"app.ui.ChromosomesFormController",
					"org.molgenis.xgap.Chromosome",
					
					"app.ui.MarkersFormController",
					"org.molgenis.xgap.Marker",
					
					"app.ui.GenesFormController",
					"org.molgenis.xgap.Gene",
					
					"app.ui.TranscriptsFormController",
					"org.molgenis.xgap.Transcript",
					
					"app.ui.MeasurementsFormController",
					"org.molgenis.pheno.Measurement",
					
					"app.ui.DerivedTraitsFormController",
					"org.molgenis.xgap.DerivedTrait",
					
					"app.ui.EnvironmentalFactorsFormController",
					"org.molgenis.xgap.EnvironmentalFactor",
					
					"app.ui.MassPeaksFormController",
					"org.molgenis.xgap.MassPeak",
					
					"app.ui.MetabolitesFormController",
					"org.molgenis.xgap.Metabolite",
					
					"app.ui.ProbesFormController",
					"org.molgenis.xgap.Probe",
					
					"app.ui.ProbeSetsFormController",
					"org.molgenis.xgap.ProbeSet",
					
					"app.ui.SNPsFormController",
					"org.molgenis.xgap.SNP",
					
					"app.ui.PolymorphismsFormController",
					"org.molgenis.xgap.Polymorphism",
					
					"app.ui.SamplesFormController",
					"org.molgenis.xgap.Sample",
					
					"app.ui.SpotsFormController",
					"org.molgenis.xgap.Spot",

					// allow reading datasets and investigations
					"org.molgenis.organization.Investigation", "org.molgenis.data.Data", "org.molgenis.data.BinaryDataMatrix", "org.molgenis.data.CSVDataMatrix",
					"org.molgenis.data.DecimalDataElement", "org.molgenis.data.TextDataElement",

					// allow reading dataset backend files
					"org.molgenis.core.MolgenisFile",

					// allow to see how uploaded this dataset
					"org.molgenis.protocol.ProtocolApplication_Performer",
					
					// allow to see analysis metadata
					"org.molgenis.cluster.DataSet", "org.molgenis.cluster.DataName", "org.molgenis.cluster.DataValue", 

			};

			for (String e : qtlFinderPerms)
			{
				MolgenisPermission mp = new MolgenisPermission();
				mp.setEntity_ClassName(e);
				mp.setRole_Name("anonymous");
				mp.setPermission("read");
				db.add(mp);
			}
			
			DataMatrixHandler dmh = new DataMatrixHandler(db);
			
			if(dmh.hasValidFileStorage(db))
			{
				String path = dmh.getFileStorage(true, db).getAbsolutePath();
				
				//import WormQTL annotations from 'imports' location
				String importDir = path + File.separator + "imports";
				
				//excel with everything minus USA probes
				File wormQtlAnnotations = new File(importDir + File.separator + "wormqtl_set1_annotations_minusUSAprobes.xls");
				if(!wormQtlAnnotations.exists())
				{
					throw new Exception("Annotation Excel file is missing!");
				}
				
				//USA probes (original name: 'probes_usa.txt', but renamed for CsvImport)
				File probes = new File(importDir + File.separator + "probe.txt");
				if(!probes.exists())
				{
					throw new Exception("USA probe file is missing!");
				}
				
				ExcelImport.importAll(wormQtlAnnotations, db, null);
				CsvImport.importAll(new File(importDir), db, null);
				
				//relink datasets
				relinkDatasets(db, dmh);
				
				//remove clusterdemo example investigation
				Settings.deleteExampleInvestigation("ClusterDemo", db);
				
				//all done
				this.setMessages(new ScreenMessage("WormQTL specific annotation import and data relink succeeded", true));
			}
			else
			{
				this.setMessages(new ScreenMessage("WormQTL permissions loaded, but could not import annotations because storagedir setup failed", false));
			}
			
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage(), false));
		}
	}

	/**
	 * Relink datasets if needed: but expected is that ALL are relinked when the function ends, or else error
	 * @param db
	 * @param dmh
	 * @throws Exception 
	 */
	private void relinkDatasets(Database db, DataMatrixHandler dmh) throws Exception
	{
		for(Data data : db.find(Data.class))
		{
			//find out if the 'Data' has a proper backend
			boolean hasLinkedStorage = dmh.isDataStoredIn(data, data.getStorage(), db);
			
			//if not, it doesn't mean the source file is not there! e.g. after updating your database
			if(!hasLinkedStorage)
			{
				//attempt to relink
				boolean relinked = dmh.attemptStorageRelink(data, data.getStorage(), db);
				
				if(!relinked)
				{
					throw new Exception("Could not relink data matrix '" + data.getName() + "'");
				}
				
				if(!dmh.isDataStoredIn(data, data.getStorage(), db))
				{
					throw new Exception("SEVERE: Data matrix '" + data.getName() + "' is supposed to be relinked, but the isDataStoredIn check failed!");
				}
			
			}
		}
		
	}	
	
}
