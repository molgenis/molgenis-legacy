/* Date:        October 28, 2009
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.animaldb.plugins.accessibility;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.molgenis.animaldb.commonservice.CommonService;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.news.MolgenisNews;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;


public class AnimalDBWelcomeScreenPlugin extends EasyPluginController
{
	private static final long serialVersionUID = -5861419875983400033L;
	List<MolgenisNews> news;
	
	public AnimalDBWelcomeScreenPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	public ScreenView getView()
	{
		return new FreemarkerView("org/molgenis/animaldb/plugins/accessibility/AnimalDBWelcomeScreenPlugin.ftl",this);
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		//replace example below with yours
//		try
//		{
//		Database db = this.getDatabase();
//		String action = request.getString("__action");
//		
//		if( action.equals("do_add") )
//		{
//			Experiment e = new Experiment();
//			e.set(request);
//			db.add(e);
//		}
//		} catch(Exception e)
//		{
//			//e.g. show a message in your form
//		}
	}

	@Override
	public void reload(Database db)
	{
		// Entry point when logging in, so good place to (re)set the ObservationTarget label map
		CommonService cs = CommonService.getInstance();
		cs.setDatabase(db);
		cs.makeObservationTargetNameMap(db.getLogin().getUserName(), true);
		
		news = new ArrayList<MolgenisNews>();
		List<MolgenisNews> tmpNews;
		try {
			tmpNews = db.query(MolgenisNews.class).sortDESC(MolgenisNews.DATE_).find();
			for (MolgenisNews newsItem : tmpNews)
			{
				//newsItem.setText(StringUtils.abbreviate(newsItem.getText(), 100));
				news.add(newsItem);
			}
		} catch (DatabaseException e) {
			MolgenisNews tmpItem = new MolgenisNews();
			tmpItem.setAuthor("Administrator");
			tmpItem.setDate(new Date());
			tmpItem.setTitle("No frontpage items in database");
			tmpItem.setSubtitle("");
			tmpItem.setText("");
			news.add(tmpItem);
		}
	}
	
	public List<MolgenisNews> getNews() {
		return news;
	}
	
}
