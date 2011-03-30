package org.molgenis.news.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.news.MolgenisNews;

import org.apache.commons.lang.StringUtils;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;

public class NewsService
{
	private static NewsService newsService = null;
	private Database db                    = null;
	
	// private constructor, use singleton instance
	private NewsService(Database db)
	{
		this.db = db;
	}
	
	public static NewsService getInstance(Database db)
	{
		if (newsService == null)
			newsService = new NewsService(db);
		
		return newsService;
	}
	
	public List<MolgenisNews> getAllNews() throws DatabaseException, ParseException
	{
		List<MolgenisNews> result = new ArrayList<MolgenisNews>();
		List<MolgenisNews> news   = db.query(MolgenisNews.class).sortDESC("date_").find();

		for (MolgenisNews newsItem : news)
		{
			newsItem.setText(StringUtils.abbreviate(newsItem.getText(), 50));
			result.add(newsItem);
		}
		return result;
	}
	
	public List<MolgenisNews> getAllNews(int limit) throws DatabaseException, ParseException
	{
		List<MolgenisNews> result = new ArrayList<MolgenisNews>();
		List<MolgenisNews> news   = db.query(MolgenisNews.class).sortDESC("date_").find();
		int toIndex               = (limit > news.size() ? news.size() : limit);

		for (MolgenisNews newsItem : news.subList(0, toIndex))
		{
			newsItem.setText(StringUtils.abbreviate(newsItem.getText(), 50));
			result.add(newsItem);
		}
		
		return result;
	}
	
	public MolgenisNews getNewsById(Integer id) throws DatabaseException
	{
		return db.findById(MolgenisNews.class, id);
	}
}
