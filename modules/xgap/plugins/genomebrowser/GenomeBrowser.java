/* Date:        February 2, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.genomebrowser;

import java.util.List;

import org.molgenis.auth.MolgenisPermission;
import org.molgenis.core.MolgenisFile;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;
import org.molgenis.xgap.InvestigationFile;

public class GenomeBrowser extends PluginModel<Entity>
{

	private static final long serialVersionUID = 1L;

	private GenomeBrowserModel model = new GenomeBrowserModel();
	private String appLoc;

	public GenomeBrowserModel getMyModel()
	{
		return model;
	}

	public GenomeBrowser(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "GenomeBrowser";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/genomebrowser/GenomeBrowser.ftl";
	}

	public void handleRequest(Database db, Tuple request)
	{
		appLoc = ((MolgenisRequest) request).getAppLocation();
		if (request.getString("__action") != null)
		{
			String action = request.getString("__action");
			try
			{
				if (action.equals("__setRelease"))
				{
					String release = request.getString("__ucsc_release");
					if (release == null || release.trim().isEmpty())
					{
						throw new Exception("Please fill in a release code");
					}
					this.model.setRelease(release.trim());
					this.setMessages(new ScreenMessage("Release set to " + release.trim(), true));
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
			}
		}
	}

	@Override
	public void reload(Database db)
	{

		try
		{

			if (this.model.getAppUrl() == null)
			{
				boolean appUrlSet = false;
				while (!appUrlSet)
				{
					this.model.setAppUrl(appLoc);
					System.out.println("GenomeBrowser application URL set!");
					appUrlSet = true;
				}
			}

			if (this.model.getFilesAreVisible() == null)
			{
				// find out if molgenisfiles are readable by anonymous
				Query<MolgenisPermission> q = db.query(MolgenisPermission.class);
				q.addRules(new QueryRule(MolgenisPermission.ENTITY_CLASSNAME, Operator.EQUALS,
						"org.molgenis.xgap.InvestigationFile"));
				// q.addRules(new QueryRule(MolgenisPermission.ENTITY_CLASSNAME,
				// Operator.EQUALS, "org.molgenis.core.MolgenisFile"));
				q.addRules((new QueryRule(MolgenisPermission.ROLE__NAME, Operator.EQUALS, "anonymous")));
				if (q.find().size() > 0)
				{
					this.model.setFilesAreVisible(true);
				}
				else
				{
					this.model.setFilesAreVisible(false);
				}
			}

			if (this.model.getFilesAreVisible())
			{
				Query<InvestigationFile> q = db.query(InvestigationFile.class);
				q.addRules(new QueryRule(MolgenisFile.EXTENSION, Operator.EQUALS, "gff"));
				List<InvestigationFile> mf = q.find();
				this.model.setGffFiles(mf);
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
		}

	}

}
