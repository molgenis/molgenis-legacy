/* Date:        November 9, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

//import commonservice.CommonService;

public class ErrorCorrectionTargetPlugin extends PluginModel<Entity>
{
	private static final long serialVersionUID = -366762636959036651L;
	//private CommonService ct = CommonService.getInstance();
	private List<ObservationTarget> targetList = new ArrayList<ObservationTarget>();
	private List<ObservationTarget> deletedTargetList = new ArrayList<ObservationTarget>();
	
	public ErrorCorrectionTargetPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	public String getCustomHtmlHeaders()
    {
        return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">\n";
    }

	@Override
	public String getViewName()
	{
		return "plugins_system_ErrorCorrectionTargetPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/system/ErrorCorrectionTargetPlugin.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try
		{
			String action = request.getString("__action");
			Date now = new Date();
			
			if (action.equals("deleteTargets")) {
				for (int i = 0; i < targetList.size(); i++) {
					if (request.getBool(Integer.toString(i)) != null) {
						// TODO: flag values as deleted
						targetList.get(i).setDeleted(true);
						targetList.get(i).setDeletionTime(now);
						targetList.get(i).setDeletedBy_Id(this.getLogin().getUserId());
					}
				}
				db.update(targetList);
			}
			
			if (action.equals("undeleteTargets")) {
				for (int i = 0; i < deletedTargetList.size(); i++) {
					if (request.getBool(Integer.toString(i)) != null) {
						deletedTargetList.get(i).setDeleted(false);
						deletedTargetList.get(i).setDeletionTime(null);
						deletedTargetList.get(i).setDeletedBy_Id(null);
					}
				}
				db.update(deletedTargetList);
			}
			
		} catch(Exception e) {
			this.setError("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void reload(Database db)
	{
		try
		{
			this.targetList = db.query(ObservationTarget.class).eq(ObservedValue.DELETED, false).find();
			this.deletedTargetList = db.query(ObservationTarget.class).eq(ObservedValue.DELETED, true).find();
		}
		catch (DatabaseException e)
		{
			this.setError("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public List<ObservationTarget> getTargetList()
	{
		return targetList;
	}

	public void setValueList(List<ObservationTarget> targetList)
	{
		this.targetList = targetList;
	}

	public List<ObservationTarget> getDeletedTargetList()
	{
		return deletedTargetList;
	}

	public void setDeletedValueList(List<ObservationTarget> deletedTargetList)
	{
		this.deletedTargetList = deletedTargetList;
	}
	
}
