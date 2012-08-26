/* Date:        May 15, 2009
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.screen.PluginScreenJavaTemplateGen 3.3.0-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.investigationoverview;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import matrix.DataMatrixInstance;
import matrix.general.DataMatrixHandler;

import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.FormController;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;
import org.molgenis.xgap.InvestigationFile;
import org.molgenis.xgap.Marker;

import app.JDBCMetaDatabase;

public class InvestigationOverviewPlugin extends PluginModel<Entity>
{

	private static final long serialVersionUID = -7068554327138233108L;
	private InvestigationOverviewModel model = new InvestigationOverviewModel();
	
	private Integer currentInvId;

	public InvestigationOverviewModel getMyModel()
	{
		return model;
	}

	public InvestigationOverviewPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	@Override
	public String getCustomHtmlHeaders()
	{
		return "<script type=\"text/javascript\" src=\"res/jquery-plugins/tagcloud/jquery.dynacloud-5_xQTL.js\"></script>" +
				"<link rel=\"stylesheet\" href=\"res/jquery-plugins/tagcloud/taghighlighting.css\">";
	}

	@Override
	public String getViewName()
	{
		return "plugins_investigationoverview_InvestigationOverviewPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/investigationoverview/InvestigationOverviewPlugin.ftl";
	}

	public void handleRequest(Database db, Tuple request)
	{
		if (request.getString("__action") != null)
		{

			String action = request.getString("__action");

			try
			{
				if (action.equals("showAllAnnotations"))
				{
					this.model.setShowAllAnnotations(true);
				}
				else if (action.equals("showFourAnnotations"))
				{
					this.model.setShowAllAnnotations(false);
				}
				else if (action.equals("showAllExperiments"))
				{
					this.model.setShowAllExperiments(true);
				}
				else if (action.equals("showFourExperiments"))
				{
					this.model.setShowAllExperiments(false);
				}
				else if (action.equals("showAllOther"))
				{
					this.model.setShowAllOther(true);
				}
				else if (action.equals("showFourOther"))
				{
					this.model.setShowAllOther(false);
				}
				else if (action.equals("viewDataByTags"))
				{
					this.model.setViewDataByTags(true);
				}
				else if (action.equals("viewDataAsList"))
				{
					this.model.setViewDataByTags(false);
				}
				else if(action.equals("refresh"))
				{
					currentInvId = null;
				}

			}
			catch (Exception e)
			{
				e.printStackTrace();
				this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
			}
		}
	}

	@Override
	public void reload(Database db)
	{
		try
		{
			if (this.model.getShowAllAnnotations() == null)
			{
				this.model.setShowAllAnnotations(false);
			}
			if (this.model.getShowAllExperiments() == null)
			{
				this.model.setShowAllExperiments(false);
			}
			if (this.model.getShowAllOther() == null)
			{
				this.model.setShowAllOther(false);
			}
			if(this.model.getViewDataByTags() == null)
			{
				this.model.setViewDataByTags(false);
			}
			
			//find out if File viewer is visible for linkouts
			if(this.getLogin().canRead(this.get("Files")))
			{
				this.model.setFileLinkoutIsVisible(true);
			}
			else
			{
				this.model.setFileLinkoutIsVisible(false);
			}

			ScreenController<?> parentController = (ScreenController<?>) this.getParent().getParent();
			FormModel<Investigation> parentForm = (FormModel<Investigation>) ((FormController)parentController).getModel();
			Investigation inv = parentForm.getRecords().get(0);
			
			boolean reload = false;
			if(currentInvId == null || !inv.getId().equals(currentInvId))
			{
				currentInvId = inv.getId();
				reload = true;
			}
			
			if(reload)
			{
				this.model.setSelectedInv(inv);
				QueryRule thisInv = new QueryRule(ObservationElement.INVESTIGATION_NAME, Operator.EQUALS, inv.getName());

				JDBCMetaDatabase metadb = new JDBCMetaDatabase();
				Map<String, Integer> annotationTypeAndNr = new HashMap<String, Integer>();
				Vector<org.molgenis.model.elements.Entity> entityList = metadb.getEntities();

				// iterate over all entity types
				for (org.molgenis.model.elements.Entity entityType : entityList)
				{
					// get the ancestors for one such entity
					for (org.molgenis.model.elements.Entity e : entityType.getAllAncestors())
					{
						// if one of the ancestors is ObservationElement..
						if (e.getName().equals(ObservationElement.class.getSimpleName()))
						{
							Class<? extends Entity> entityClass = db.getClassForName(entityType.getName());
							// and the class is not ObservableFeature or ObservationTarget or Data..
							if	(
								!entityClass.getSimpleName().equals(ObservableFeature.class.getSimpleName())
								&& !entityClass.getSimpleName().equals(ObservationTarget.class.getSimpleName())
								&& !entityClass.getSimpleName().equals(Data.class.getSimpleName())
								)
							{
								// count the number of entities in the database, who are also of this exact type
								QueryRule thisType = new QueryRule(ObservationElement.__TYPE, Operator.EQUALS, (entityType.getName()));
								int count = db.count(entityClass, thisInv, thisType);
								if (count > 0)
								{
									annotationTypeAndNr.put(entityType.getName(), count);
								}
							}
							break;
						}
					}
	
				}
	
				// merge type+amount and add hyperlink instead (note: hyperlink may
				// NOT actually match!
				HashMap<String, String> annotationWithLinks = new HashMap<String, String>();
				for (String key : annotationTypeAndNr.keySet())
				{
					annotationWithLinks.put(key, annotationTypeAndNr.get(key)+"");
				}
	
				this.model.setAnnotationList(annotationWithLinks);
	
				HashMap<String, Data> expList = new HashMap<String, Data>();
				HashMap<String, String> expDimensions = new HashMap<String, String>();
				List<Data> dataList = db.find(Data.class, thisInv);
				for (Data d : dataList)
				{
					String name = d.getName();
					if(name.length() > 25) name = name.substring(0, 10) + "(..)"+name.substring(name.length()-10);
					expList.put(name, d);
					expDimensions.put(name, getDataInfo(d, db));
				}
				this.model.setExpList(expList);
				this.model.setExpDimensions(expDimensions);
	
				HashMap<String, String> otherList = new HashMap<String, String>();
	
				List<InvestigationFile> ifList = db.find(InvestigationFile.class, thisInv);
				for (InvestigationFile invFile : ifList)
				{
					String name = invFile.getName();
					if(name.length() > 25) name = name.substring(0, 10) + "(..)"+name.substring(name.length()-10);
					otherList.put(name + "." + invFile.getExtension(),
							"?select=Files&__target=Files&__action=filter_set&__filter_attribute=InvestigationFile_id&__filter_operator=EQUALS&__filter_value="
									+ invFile.getId());
				}
				this.model.setOtherList(otherList);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
		}
	}
	
	private String getDataInfo(Data data, Database db) throws Exception
	{
		String res = "";
		
		try{
			DataMatrixHandler dmh = new DataMatrixHandler(db);
			if(dmh.hasSource(data, db))
			{
				DataMatrixInstance matrix = dmh.createInstance(data, db);
				res += "(";
				res += matrix.getNumberOfRows();
				res += " x ";
				res += matrix.getNumberOfCols();
				res += ")";
			}
		}
		catch(Exception e)
		{
			// may not crash or the plugin breaks
			// in case backend is missing or something else went wrong,
			// just return an empty string
			return "";
		}
		return res;
	}

}
