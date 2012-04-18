/* Date:        February 2, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.genomebrowser;

import java.net.URL;
import java.util.List;

import org.molgenis.auth.MolgenisPermission;
import org.molgenis.core.MolgenisFile;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.util.Entity;
import org.molgenis.util.HtmlTools;
import org.molgenis.util.Tuple;

public class GenomeBrowser extends PluginModel<Entity>
{

	private static final long serialVersionUID = 1L;

	private GenomeBrowserModel model = new GenomeBrowserModel();
	
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
		if (request.getString("__action") != null)
		{
			String action = request.getString("__action");
			try
			{
			
				if(this.model.getAppUrl() == null)
				{
					//TODO: does this always work?
					//in R API we use String server = "http://" + request.getRequest().getLocalName() + ":" + request.getRequest().getLocalPort() + "/"+mc.getVariant();

					//String appUrl = this.getApplicationController().getApplicationUrl();
					//this.model.setAppUrl(appUrl);
					
					String host = HtmlTools.getExposedIPAddress();
				

					URL reconstructedURL = HtmlTools.getExposedProjectURL(request, host, this.getApplicationController().getMolgenisContext().getVariant());

					this.model.setAppUrl(reconstructedURL.toString());
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
			if(this.model.getFilesAreVisible() == null){
				//find out if molgenisfiles are readable by anonymous
				Query q = db.query(MolgenisPermission.class);
				q.addRules(new QueryRule(MolgenisPermission.ENTITY_CLASSNAME, Operator.EQUALS, "org.molgenis.core.MolgenisFile"));
				q.addRules((new QueryRule(MolgenisPermission.ROLE__NAME, Operator.EQUALS, "anonymous")));
				if(q.find().size() > 0)
				{
					this.model.setFilesAreVisible(true);
				}
				else
				{
					this.model.setFilesAreVisible(false);
				}
			}
			
			
			if(this.model.getFilesAreVisible())
			{
				Query q = db.query(MolgenisFile.class);
				q.addRules(new QueryRule(MolgenisFile.EXTENSION, Operator.EQUALS, "gff"));
				List<MolgenisFile> mf = q.find();
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
