/* Date:        August 7, 2009
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.screen.PluginScreenJavaTemplateGen 3.3.0-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.view;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;



public class AllObservations extends PluginModel<Entity>
{

	private static final long serialVersionUID = 4656566870431483076L;
	List<String> features = new ArrayList<String>();
	List<String> targets = new ArrayList<String>();
	Map<String,String> values = new LinkedHashMap<String,String>();
	
	public List<String> getFeatures()
	{
		return features;
	}

	public List<String> getTargets()
	{
		return targets;
	}
	
	public String getValue(String feature, String target)
	{
		String val = values.get(feature +"_"+target);
		if(val == null) return "&nbsp;";
		return val;
	}
	
	public AllObservations(String name, ScreenModel<Entity> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugin_view_AllObservations";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/view/AllObservations.ftl";
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
		//parent is a menu, parent above that is Investigation screen
		//TODO: Danny: This is a code smell, Can we reload in such a way that the parent from the parent is not an Investigation??
		//This assumes implicit knowledge about Molgenis which shouldn't be part of a plugin, and we only use the ID of the parent
		Investigation parent = ((FormModel<Investigation>)getParent().getParent()).getRecords().get(0);
		try
		{
			List<ObservedValue> result = db.query(ObservedValue.class).equals("investigation",parent.getId()).find();
			for(ObservedValue v: result)
			{
				String f = v.getFeature_Name();
				if( !features.contains(f)) features.add(f);
				String t = v.getTarget_Name();
				if( !targets.contains(t)) targets.add(t);
				values.put(f+"_"+t, v.getValue());
			}
		}
		catch (DatabaseException e)
		{
			e.printStackTrace();
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean isVisible()
	{
		//you can use this to hide this plugin, e.g. based on user rights.
		//e.g.
		//if(!this.getLogin().hasEditPermission(myEntity)) return false;
		return true;
	}
}
