/* Date:        May 6, 2011
 * Template:	NewPluginModelGen.java.ftl
 * generator:   org.molgenis.generators.ui.NewPluginModelGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.news.ui;

import java.util.List;

import org.molgenis.framework.ui.EasyPluginModel;
import org.molgenis.news.MolgenisNews;

public class NewsModel extends EasyPluginModel
{
	private static final long serialVersionUID = 1L; //a system veriable that is needed by tomcat
	protected final int NUM_NEWS               = 5; // how many news to be shown in "top news" mode?
	private String action                      = "init";
	private List<MolgenisNews> allNews;
	private List<MolgenisNews> topNews;
	private MolgenisNews newsItem;

	public NewsModel(News controller)
	{
		super(controller);
	}

	public List<MolgenisNews> getAllNews()
	{
		return allNews;
	}

	public void setAllNews(List<MolgenisNews> news)
	{
		this.allNews = news;
	}

	public List<MolgenisNews> getTopNews()
	{
		return topNews;
	}

	public void setTopNews(List<MolgenisNews> topNews)
	{
		this.topNews = topNews;
	}

	public MolgenisNews getNewsItem()
	{
		return newsItem;
	}

	public void setNewsItem(MolgenisNews newsItem)
	{
		this.newsItem = newsItem;
	}

	public String getAction()
	{
		return this.action;
	}
	
	public void setAction(String action)
	{
		this.action = action;
	}
}
