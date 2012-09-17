/* Date:        September 28, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.news.ui;

import java.text.ParseException;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.news.service.NewsService;
import org.molgenis.util.Tuple;

public class News extends EasyPluginController<NewsModel>
{
	private static final long serialVersionUID = -7677999897948691120L;

	public News(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.setModel(new NewsModel(this));
	}
	
	public ScreenView getView()
	{
		return new FreemarkerView("News.ftl", getModel());
	}

	public void entry(Database db, Tuple request) throws DatabaseException
	{
		this.getModel().setAction(request.getAction());
		NewsService service = NewsService.getInstance(db);
		this.getModel().setNewsItem(service.getNewsById(request.getInt("id")));
	}

	public void top(Database db, Tuple request)
	{
		this.getModel().setAction(request.getAction());
		// rest will be done by reload()
	}
	
	public void all(Database db, Tuple request) throws DatabaseException, ParseException
	{
		this.getModel().setAction(request.getAction());
		NewsService service = NewsService.getInstance(db);
		this.getModel().setAllNews(service.getAllNews());
	}

	@Override
	public void reload(Database db) throws DatabaseException, ParseException
	{
		// default is to load a predefined number of the newest news
		NewsService service = NewsService.getInstance(db);
		this.getModel().setTopNews(service.getAllNews(this.getModel().NUM_NEWS));
	}
}
