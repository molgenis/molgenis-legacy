/* Date:        September 28, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.news;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.news.MolgenisNews;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class News extends PluginModel<Entity> 
{
	private static final long serialVersionUID = -5732318216660565455L;
	private static final int NUM_NEWS = 5; // how many news to be shown in right box?
	private String action = "init";
	private NewsService newsService;
	private List<MolgenisNews> news = new ArrayList<MolgenisNews>();
	private MolgenisNews newsItem;

	public News(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugin_news_News";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/news/News.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		this.reload(db);

		try
		{
			this.action = request.getAction();

			if ("entry".equals(this.action))
			{
				this.newsItem = this.newsService.getNewsById(request.getInt("id"));
			}
			else if ("all".equals(this.action))
			{
				this.news     = this.newsService.getAllNews();
			}
			else
			{
				this.action   = "init";
				this.reload(db);
			}
		}
		catch(Exception e)
		{
			logger.error(e.toString());
		}
	}

	@Override
	public void reload(Database db)
	{
		this.newsService = NewsService.getInstance(db);
		try
		{
			if ("init".equals(this.action))
				this.news = this.newsService.getAllNews(News.NUM_NEWS);
		}
		catch (Exception e)
		{
			logger.error(e.toString());
		}
	}
	
	@Override
	public boolean isVisible()
	{
//		return true;
		try {
			return this.getLogin().canRead(this);
		} catch (DatabaseException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public String getAction()
	{
		return this.action;
	}

	public List<MolgenisNews> getNews()
	{
		return this.news;
	}
	
	public MolgenisNews getNewsItem()
	{
		return this.newsItem;
	}
}
