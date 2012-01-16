/* Date:        October 5, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.wormqtl.header;

import org.molgenis.auth.MolgenisPermission;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.util.Tuple;

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
			addPanaceaPermissions(db);
		}
	}

	private void addPanaceaPermissions(Database db)
	{
		try
		{

			String[] qtlFinderPerms = new String[]
			{

					// allow to see the QTL finder
					"app.ui.QtlFinderPublicPlugin",
					
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
					"org.molgenis.core.MolgenisFile", "org.molgenis.core.RuntimeProperty",

					// allow to see how uploaded this dataset
					"org.molgenis.protocol.ProtocolApplication_Performer"

			};

			for (String e : qtlFinderPerms)
			{
				MolgenisPermission mp = new MolgenisPermission();
				mp.setEntity_ClassName(e);
				mp.setRole_Name("anonymous");
				mp.setPermission("read");
				db.add(mp);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage(), false));
		}
	}

}
