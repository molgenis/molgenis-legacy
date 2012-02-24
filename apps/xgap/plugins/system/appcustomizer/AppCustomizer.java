/*
 * Date: April 8, 2011 Template: PluginScreenJavaTemplateGen.java.ftl generator:
 * org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.system.appcustomizer;

import java.io.File;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.util.TarGz;
import org.molgenis.util.Tuple;

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

	@Override
	public String getViewTemplate()
	{
		return "plugins/system/appcustomizer/AppCustomizer.ftl";
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
				
				TarGz.delete(oldBanner, true);
				
				boolean success = newBanner.renameTo(oldBanner);
				
				if(!success)
				{
					throw new Exception("Putting the new banner in place failed.");
				}
				
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
				
				TarGz.delete(oldCss, true);
				
				boolean success = newCss.renameTo(oldCss);
				
				if(!success)
				{
					throw new Exception("Putting the new CSS in place failed.");
				}
				
				this.setMessages(new ScreenMessage("New CSS uploaded.", true));
			}


		}
		catch (Exception e)
		{
			this.setMessages(new ScreenMessage(e.getMessage(), false));
		}
	}
	

	@Override
	public void reload(Database db)
	{
		

	}


}
