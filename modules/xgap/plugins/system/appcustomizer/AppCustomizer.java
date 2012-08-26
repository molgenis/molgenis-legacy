/*
 * Date: April 8, 2011 Template: PluginScreenJavaTemplateGen.java.ftl generator:
 * org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.system.appcustomizer;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.molgenis.core.RuntimeProperty;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.util.TarGz;
import org.molgenis.util.Tuple;

import plugins.cluster.demo.ClusterDemo;

public class AppCustomizer extends PluginModel
{

	public AppCustomizer(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugins_system_appcustomizer_AppCustomizer";
	}
	
	private Boolean hideLoginButtons;

	@Override
	public String getViewTemplate()
	{
		return "plugins/system/appcustomizer/AppCustomizer.ftl";
	}
	
	

	public Boolean getHideLoginButtons() {
		return hideLoginButtons;
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try
		{
		
			if ("uploadBanner".equals(request.getAction()))
			{
				File newBanner = request.getFile("uploadBannerFile");
				
				if(newBanner == null)
				{
					throw new Exception("Please provide an image file.");
				}
				
				File oldBanner = new File("WebContent/clusterdemo/bg/xqtl_default_banner.png");
				
				FileUtils.forceDelete(oldBanner);
				
				FileUtils.copyFile(newBanner, oldBanner);
				
//				if(!success)
//				{
//					throw new Exception("Putting the new banner in place failed.");
//				}
				
				this.setMessages(new ScreenMessage("New banner uploaded.", true));
			
			}
			else if ("uploadCss".equals(request.getAction()))
			{
				File newCss = request.getFile("uploadCssFile");
				
				if(newCss == null)
				{
					throw new Exception("Please provide a CSS file.");
				}
				
				File oldCss = new File("WebContent/clusterdemo/colors.css");
				
				FileUtils.forceDelete(oldCss);
				
				FileUtils.copyFile(newCss, oldCss);
				
//				if(!success)
//				{
//					throw new Exception("Putting the new CSS in place failed.");
//				}
				
				this.setMessages(new ScreenMessage("New CSS uploaded.", true));
			}
			
			else if("showHomeButtons".equals(request.getAction()))
			{
				setHideHomeScreenButtons(db, "false");
			}
			
			else if("hideHomeButtons".equals(request.getAction()))
			{
				setHideHomeScreenButtons(db, "true");
			}
			
			else
			{
				throw new Exception("unknown request action: " + request.getAction());
			}


		}
		catch (Exception e)
		{
			this.setMessages(new ScreenMessage(e.getMessage(), false));
		}
	}
	
	private void setHideHomeScreenButtons(Database db, String setMe) throws DatabaseException
	{
		List<RuntimeProperty> rp = db.find(RuntimeProperty.class, new QueryRule(RuntimeProperty.NAME, Operator.EQUALS, ClusterDemo.XQTL_HOMESCREEN_HIDELOGINBUTTONS));
		if(rp.size() == 0)
		{
			RuntimeProperty newRp = new RuntimeProperty();
			newRp.setName(ClusterDemo.XQTL_HOMESCREEN_HIDELOGINBUTTONS);
			newRp.setValue(setMe);
			db.add(newRp);
		}
		else if(rp.size() == 1)
		{
			rp.get(0).setValue(setMe);
			db.update(rp);
		}
		else
		{
			throw new DatabaseException("runtime prop size exceeds 1");
		}
	}
	

	@Override
	public void reload(Database db)
	{
		try
		{
			List<RuntimeProperty> rp = db.find(RuntimeProperty.class, new QueryRule(RuntimeProperty.NAME, Operator.EQUALS, ClusterDemo.XQTL_HOMESCREEN_HIDELOGINBUTTONS));
			
			if(rp.size() == 1 && rp.get(0).getValue().equals("false"))
			{
				this.hideLoginButtons = false;
			}
			else if(rp.size() == 1 && rp.get(0).getValue().equals("true"))
			{
				this.hideLoginButtons = true;
			}
			else
			{
				this.hideLoginButtons = false;
			}
		}
		catch(DatabaseException e)
		{
			this.setMessages(new ScreenMessage("Could not query runtime propery: " + e.getMessage(), false));
		}

	}


}
