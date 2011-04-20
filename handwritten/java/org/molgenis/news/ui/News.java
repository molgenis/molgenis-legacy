/* Date:        September 28, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.news.ui;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.news.MolgenisNews;
import org.molgenis.news.service.NewsService;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class News extends PluginModel<Entity>
{

	private static final long serialVersionUID = -7677999897948691120L;
	private static final int NUM_NEWS          = 5; // how many news to be shown in right box?
	private String action                      = "init";
	private NewsService newsService;
	private String title                       = "";
	private List<MolgenisNews> news            = new ArrayList<MolgenisNews>();
	private MolgenisNews newsItem;

	public News(String name, ScreenModel<Entity> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_news_ui_News";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/news/ui/News.ftl";
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
				this.title    = "Updates of the database, both user features and insertion of new data, will be announced on this page. All news items are stored in the news archive.<hr/><br/>";
				this.newsItem = this.newsService.getNewsById(request.getInt("id"));
			}
			else if ("top".equals(this.action))
			{
				this.title    = "";
				this.news     = this.newsService.getAllNews(NUM_NEWS);
			}
			else if ("all".equals(this.action))
			{
				this.title    = "Updates of the database, both user features and insertion of new data, will be announced on this page. All news items are stored in the news archive.<hr/><br/>";
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
			if (!"View".equals(this.getParent().getName()))
			{
				this.action = "top";
				this.title  = "";
				this.news   = this.newsService.getAllNews(News.NUM_NEWS);
			}
			if ("init".equals(this.action))
			{
				this.title = "Updates of the database, both user features and insertion of new data, will be announced on this page. All news items are stored in the news archive.<hr/><br/>";
				this.news  = this.newsService.getAllNews();
			}
		}
		catch (Exception e)
		{
			logger.error(e.toString());
		}
	}
	
	@Override
	public boolean isVisible()
	{
		return true;
	}
	
	public String getAction()
	{
		return this.action;
	}

	public String getTitle()
	{
		return this.title;
	}

	public List<MolgenisNews> getNews()
	{
		return this.news;
	}
	
	public MolgenisNews getNewsItem()
	{
		return this.newsItem;
	}

	public static int getNumNews() {
		return NUM_NEWS;
	}
}
