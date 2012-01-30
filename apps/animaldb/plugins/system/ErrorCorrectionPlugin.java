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
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

//import commonservice.CommonService;

public class ErrorCorrectionPlugin extends PluginModel<Entity>
{
	private static final long serialVersionUID = -366762636959036651L;
	//private CommonService ct = CommonService.getInstance();
	private List<ObservedValue> valueList = new ArrayList<ObservedValue>();
	private List<ObservedValue> deletedValueList = new ArrayList<ObservedValue>();
	
	public ErrorCorrectionPlugin(String name, ScreenController<?> parent)
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
		return "plugins_system_ErrorCorrectionPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/system/ErrorCorrectionPlugin.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try
		{
			String action = request.getString("__action");
			Date now = new Date();
			
			if (action.equals("deleteValues")) {
				for (int i = 0; i < valueList.size(); i++) {
					if (request.getBool(Integer.toString(i)) != null) {
						valueList.get(i).setDeleted(true);
						valueList.get(i).setDeletionTime(now);
						valueList.get(i).setDeletedBy_Id(this.getLogin().getUserId());
					}
				}
				db.update(valueList);
			}
			
			if (action.equals("undeleteValues")) {
				for (int i = 0; i < valueList.size(); i++) {
					if (request.getBool(Integer.toString(i)) != null) {
						deletedValueList.get(i).setDeleted(false);
						deletedValueList.get(i).setDeletionTime(null);
						deletedValueList.get(i).setDeletedBy_Id(null);
					}
				}
				db.update(deletedValueList);
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
			this.valueList = db.query(ObservedValue.class).sortDESC(ObservedValue.TIME).
					eq(ObservedValue.DELETED, false).find();
			this.deletedValueList = db.query(ObservedValue.class).sortDESC(ObservedValue.TIME).
					eq(ObservedValue.DELETED, true).find();
		}
		catch (DatabaseException e)
		{
			this.setError("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public List<ObservedValue> getValueList()
	{
		return valueList;
	}

	public void setValueList(List<ObservedValue> valueList)
	{
		this.valueList = valueList;
	}

	public List<ObservedValue> getDeletedValueList()
	{
		return deletedValueList;
	}

	public void setDeletedValueList(List<ObservedValue> deletedValueList)
	{
		this.deletedValueList = deletedValueList;
	}
	
}
