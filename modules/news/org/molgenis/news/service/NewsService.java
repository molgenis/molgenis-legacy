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
		//if (newsService == null)
		newsService = new NewsService(db);
		
		return newsService;
	}
	
	public List<MolgenisNews> getAllNews() throws DatabaseException, ParseException
	{
		return db.query(MolgenisNews.class).sortDESC("date_").find();
	}
	
	public List<MolgenisNews> getAllNews(int limit) throws DatabaseException, ParseException
	{
		return db.query(MolgenisNews.class).sortDESC("date_").limit(limit).find();
	}
	
	public MolgenisNews getNewsById(Integer id) throws DatabaseException
	{
		return db.findById(MolgenisNews.class, id);
	}
}
