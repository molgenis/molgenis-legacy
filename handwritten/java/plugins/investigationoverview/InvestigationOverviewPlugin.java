/* Date:        May 15, 2009
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.screen.PluginScreenJavaTemplateGen 3.3.0-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.investigationoverview;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.model.elements.DBSchema;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;
import org.molgenis.xgap.InvestigationFile;

import app.JDBCMetaDatabase;

public class InvestigationOverviewPlugin extends PluginModel<Entity>
{

	private static final long serialVersionUID = -7068554327138233108L;
	private InvestigationOverviewModel model = new InvestigationOverviewModel();

	public InvestigationOverviewModel getModel()
	{
		return model;
	}

	public InvestigationOverviewPlugin(String name, ScreenModel<Entity> parent)
	{
		super(name, parent);
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

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		System.out.println("*** handleRequest WRAPPER __action: " + request.getString("__action"));
		this.handleRequest(db, request, null);
	}

	public void handleRequest(Database db, Tuple request, PrintWriter out)
	{
		if (request.getString("__action") != null)
		{

			String action = request.getString("__action");

			System.out.println("*** handleRequest __action: " + request.getString("__action"));

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
			//TODO: Danny: Code smell ??? getparent().getparent() ?
			FormModel<Investigation> theParent = (FormModel<Investigation>) this.getParent().getParent();
			Investigation inv = ((Investigation) theParent.getRecords().get(0));

			this.model.setSelectedInv(inv);
			//TODO: Danny: old code ??? if yes please remove
			/*Map<String, Integer> counts = new HashMap<String, Integer>();*/
			QueryRule thisInv = new QueryRule("investigation", Operator.EQUALS, inv.getId());

			List<ObservationElement> ofList = db.find(ObservationElement.class, thisInv);

			// first make map of type + amount
			HashMap<String, Integer> annotationTypeAndNr = new HashMap<String, Integer>();
			for (ObservationElement of : ofList)
			{
				// System.out.println(of.get__Type() + " " + of.getName());
				if (!of.get__Type().equals("Data"))
				{
					if (annotationTypeAndNr.containsKey(of.get__Type()))
					{
						annotationTypeAndNr.put(of.get__Type(), annotationTypeAndNr.get(of.get__Type()) + 1);
					}
					else
					{
						annotationTypeAndNr.put(of.get__Type(), 1);
					}
				}

			}

			// merge type+amount and add hyperlink instead (note: hyperlink may
			// NOT actually match!
			HashMap<String, String> annotationWithLinks = new HashMap<String, String>();
			for (String key : annotationTypeAndNr.keySet())
			{
				annotationWithLinks.put(key + " (" + annotationTypeAndNr.get(key) + ")", "?select=" + key + "s");
			}

			this.model.setAnnotationList(annotationWithLinks);

			HashMap<String, String> expList = new HashMap<String, String>();
			List<Data> dataList = db.find(Data.class, thisInv);
			for (Data d : dataList)
			{
				// + " ("+d.getFeature_name()+" x "+d.getTarget_name()+")"
				expList.put(d.getName(),
						"?__target=Datas&__action=filter_set&__filter_attribute=Data_id&__filter_operator=EQUALS&__filter_value="
								+ d.getId());
			}
			this.model.setExpList(expList);

			HashMap<String, String> otherList = new HashMap<String, String>();

			List<InvestigationFile> ifList = db.find(InvestigationFile.class, thisInv);
			for (InvestigationFile invFile : ifList)
			{
				otherList.put(invFile.getName() + "." + invFile.getExtension(),
						"?__target=Files&__action=filter_set&__filter_attribute=File_id&__filter_operator=EQUALS&__filter_value="
								+ invFile.getId());

			}
			this.model.setOtherList(otherList);

			JDBCMetaDatabase metadb = new JDBCMetaDatabase();
			org.molgenis.model.elements.Entity entity = metadb.getEntity("ObservationElement");

			System.out.println("getting children of ObservationElement");
			for (DBSchema dbs : entity.getAllChildren())
			{
				System.out.println("CHILD: " + dbs.getName());
			}

			this.setMessages();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
		}
	}

	@Override
	public boolean isVisible()
	{
		return true;
	}
}
