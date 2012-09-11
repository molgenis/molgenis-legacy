package org.molgenis.knowledgecenter;

import java.util.ArrayList;
import java.util.List;

import knowledgecenter.KcPage;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.util.Tuple;

public class Wireframe extends EasyPluginController<Wireframe>
{
	KcPage currentPage;
	List<KcPage> about = new ArrayList<KcPage>();
	List<KcPage> projects = new ArrayList<KcPage>();
	List<KcPage> outputs = new ArrayList<KcPage>();
	List<KcPage> news = new ArrayList<KcPage>();

	public enum Layout
	{
		NEWS
	};

	public Wireframe(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.setModel(this); // you can create a seperate class as 'model'.
	}

	// what is shown to the user
	public ScreenView getView()
	{
		return new FreemarkerView("Wireframe.ftl", this);
	}

	public void show(Database db, Tuple request)
	{

	}

	@Override
	public void reload(Database db) throws Exception
	{
		// get page if missing
		currentPage = null;
		if (currentPage == null)
		{
			if (db.count(KcPage.class) > 0)
			{
				currentPage = db.query(KcPage.class).limit(1).find().get(0);
			}
		}

		// load related pages
		about.clear();
		projects.clear();
		outputs.clear();
		news.clear();
		for (KcPage p : db.query(KcPage.class).in(KcPage.ID, currentPage.getRelated_Id()).find())
		{
			// map to right box
			if ("about".equals(p.getPageType())) about.add(p);
			else if ("project".equals(p.getPageType()))
			{
				projects.add(p);
			}
			else if ("news".equals(p.getPageType()))
			{
				news.add(p);
			}
			else
			{
				outputs.add(p);
			}

		}

	}

	public KcPage getPage()
	{
		return currentPage;
	}

	public List<KcPage> getAbout()
	{
		return about;
	}

	@Override
	public String getCustomHtmlHeaders()
	{
		return " <link href=\"bootstrap/css/bootstrap.min.css\" rel=\"stylesheet\">";
	}

	public KcPage getCurrentPage()
	{
		return currentPage;
	}

	public void setCurrentPage(KcPage currentPage)
	{
		this.currentPage = currentPage;
	}

	public List<KcPage> getProjects()
	{
		return projects;
	}

	public void setProjects(List<KcPage> projects)
	{
		this.projects = projects;
	}

	public List<KcPage> getOutputs()
	{
		return outputs;
	}

	public void setOutputs(List<KcPage> outputs)
	{
		this.outputs = outputs;
	}

	public List<KcPage> getNews()
	{
		return news;
	}

	public void setNews(List<KcPage> news)
	{
		this.news = news;
	}

	public void setAbout(List<KcPage> about)
	{
		this.about = about;
	}
}