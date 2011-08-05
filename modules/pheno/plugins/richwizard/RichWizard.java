/* Date:        February 2, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.richwizard;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.model.MolgenisModel;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;
import org.molgenis.util.ValueLabel;

import app.JDBCMetaDatabase;

public class RichWizard extends PluginModel<Entity>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4843859850681009925L;
	private RichWizardModel model = new RichWizardModel(this);
	private JDBCMetaDatabase metadb;

	public RichWizardModel getModel()
	{
		return model;
	}

	public RichWizard(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getCustomHtmlHeaders()
	{
		return "<script src=\"res/scripts/overlib.js\" language=\"javascript\"></script>";

	}

	@Override
	public String getViewName()
	{
		return "RichWizard";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/richwizard/RichWizard.ftl";
	}

	@Override
	public boolean isVisible()
	{
		return true;
	}

	public void handleRequest(Database db, Tuple request)
	{
		if (request.getString("__action") != null)
		{
			String action = request.getString("__action");

			System.out.println("*** handleRequest __action: " + request.getString("__action"));

			try
			{
				if (metadb == null)
				{
					metadb = new JDBCMetaDatabase();
				}

				if (action.equals("toScreen1"))
				{
					this.model.setState("screen1");
				}
				else if (action.equals("toScreen2"))
				{
					this.model.setState("screen2");

					List<org.molgenis.model.elements.Entity> entities = new ArrayList<org.molgenis.model.elements.Entity>();
					List<String> entityNames = new ArrayList<String>();
					//TODO Use or Loose
					//HashMap<String, Vector<org.molgenis.model.elements.Field>> selectedDataTypes = new HashMap<String, Vector<org.molgenis.model.elements.Field>>();
					for (String dt : this.model.getDataTypes())
					{
						if (request.getString("dt_" + dt) != null)
						{
					
							org.molgenis.model.elements.Entity entity = metadb.getEntity(dt);
							entities.add(entity);
							entityNames.add(entity.getName());

						}
					}

					// help finding out if XREFs are valid by pointing towards
					// ancestors which might be part of the import list
					ArrayList<String> uniqueAncestorsOfEntities = new ArrayList<String>();

					// check every entity in the import selection
					for (org.molgenis.model.elements.Entity entity : entities)
					{
						// get the ancestors for this entity
						for (org.molgenis.model.elements.Entity ancestor : entity.getAllAncestors())
						{
							if (!uniqueAncestorsOfEntities.contains(ancestor.getName()))
							{
								uniqueAncestorsOfEntities.add(ancestor.getName());
							}
						}
					}
					
					this.model.setUniqueAncestorsOfEntities(uniqueAncestorsOfEntities);
					
					MolgenisModel.sortEntitiesByDependency(entities, metadb);

					this.model.setEntities(entities);
					this.model.setEntityNames(entityNames);

					this.model.setExampleCsvs(Helper.getExampleCSVs(entities));

				}else if(action.startsWith("upload_textarea_")){
					String entity = action.substring(16);
					String content = request.getString("textarea_"+entity);
					
					System.out.println("*** CONTENT: " + content);
					
				}else{
					throw new Exception("Unknown request action: " + action);
				}

				this.setMessages();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
			}
		}
	}

	public void clearMessage()
	{
		this.setMessages();
	}

	@Override
	public void reload(Database db)
	{

		try
		{
			if (model.getState() == null || model.getState().equals("screen1"))
			{

				List<String> dataTypes = new ArrayList<String>();
				ObservationElement o = new ObservationElement();
				List<ValueLabel> types = o.get__TypeOptions();
				for (ValueLabel vl : types)
				{
					String type = vl.getValue().toString();
					dataTypes.add(type);
				}
				this.model.setDataTypes(dataTypes);
				this.model.setState("screen1");
			}
			else if (model.getState().equals("screen2"))
			{
				List<Investigation> invList = db.find(Investigation.class);
				this.model.setInvestigations(invList);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
		}

	}

}
