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

import org.molgenis.animaldb.DeletedObservedValue;
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
	private List<DeletedObservedValue> deletedValueList = new ArrayList<DeletedObservedValue>();
	
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
				List<ObservedValue> removalList = new ArrayList<ObservedValue>();
				List<DeletedObservedValue> addList = new ArrayList<DeletedObservedValue>();
				for (int i = 0; i < valueList.size(); i++) {
					if (request.getBool(Integer.toString(i)) != null) {
						ObservedValue val = valueList.get(i);
						removalList.add(val);
						
						DeletedObservedValue delVal = new DeletedObservedValue();
						delVal.setEndtime(val.getEndtime());
						delVal.setFeature_Id(val.getFeature_Id());
						delVal.setInvestigation_Id(val.getInvestigation_Id());
						delVal.setOntologyReference_Id(val.getOntologyReference_Id());
						delVal.setProtocolApplication_Id(val.getProtocolApplication_Id());
						delVal.setRelation_Id(val.getRelation_Id());
						delVal.setTarget_Id(val.getTarget_Id());
						delVal.setTime(val.getTime());
						delVal.setValue(val.getValue());
						delVal.setDeletionTime(now);
						delVal.setDeletedBy_Id(this.getLogin().getUserId());
						addList.add(delVal);
					}
				}
				db.remove(removalList);
				db.add(addList);
			}
			
			if (action.equals("undeleteValues")) {
				List<DeletedObservedValue> removalList = new ArrayList<DeletedObservedValue>();
				List<ObservedValue> addList = new ArrayList<ObservedValue>();
				for (int i = 0; i < deletedValueList.size(); i++) {
					if (request.getBool(Integer.toString(i)) != null) {
						DeletedObservedValue val = deletedValueList.get(i);
						removalList.add(val);
						
						ObservedValue addVal = new ObservedValue();
						addVal.setEndtime(val.getEndtime());
						addVal.setFeature_Id(val.getFeature_Id());
						addVal.setInvestigation_Id(val.getInvestigation_Id());
						addVal.setOntologyReference_Id(val.getOntologyReference_Id());
						addVal.setProtocolApplication_Id(val.getProtocolApplication_Id());
						addVal.setRelation_Id(val.getRelation_Id());
						addVal.setTarget_Id(val.getTarget_Id());
						addVal.setTime(val.getTime());
						addVal.setValue(val.getValue());
						addList.add(addVal);
					}
				}
				db.remove(removalList);
				db.add(addList);
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
			this.valueList = db.query(ObservedValue.class).sortDESC(ObservedValue.TIME).find();
			this.deletedValueList = db.query(DeletedObservedValue.class).sortDESC(DeletedObservedValue.TIME).find();
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

	public List<DeletedObservedValue> getDeletedValueList()
	{
		return deletedValueList;
	}

	public void setDeletedValueList(List<DeletedObservedValue> deletedValueList)
	{
		this.deletedValueList = deletedValueList;
	}
	
}
