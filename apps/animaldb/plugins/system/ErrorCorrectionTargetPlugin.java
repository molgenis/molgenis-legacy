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

import org.molgenis.animaldb.DeletedObservationTarget;
import org.molgenis.animaldb.DeletedObservedValue;
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
	private List<DeletedObservationTarget> deletedTargetList = new ArrayList<DeletedObservationTarget>();
	
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
				List<ObservationTarget> removalList = new ArrayList<ObservationTarget>();
				List<DeletedObservationTarget> addList = new ArrayList<DeletedObservationTarget>();
				for (int i = 0; i < targetList.size(); i++) {
					if (request.getBool(Integer.toString(i)) != null) {
						ObservationTarget tgt = targetList.get(i);
						removalList.add(tgt);
						
						DeletedObservationTarget delTgt = new DeletedObservationTarget();
						delTgt.setDescription(tgt.getDescription());
						delTgt.setInvestigation_Id(tgt.getInvestigation_Id());
						delTgt.setName(tgt.getName());
						delTgt.setOntologyReference_Id(tgt.getOntologyReference_Id());
						delTgt.setCanRead_Id(tgt.getCanRead_Id());
						delTgt.setCanWrite_Id(tgt.getCanWrite_Id());
						delTgt.setOwns_Id(tgt.getOwns_Id());
						delTgt.setDeletionTime(now);
						delTgt.setDeletedBy_Id(this.getLogin().getUserId());
						addList.add(delTgt);
						
						deleteValuesForTarget(db, tgt.getId());
					}
				}
				db.remove(removalList);
				db.add(addList);
			}
			
			if (action.equals("undeleteTargets")) {
				List<DeletedObservationTarget> removalList = new ArrayList<DeletedObservationTarget>();
				List<ObservationTarget> addList = new ArrayList<ObservationTarget>();
				for (int i = 0; i < deletedTargetList.size(); i++) {
					if (request.getBool(Integer.toString(i)) != null) {
						DeletedObservationTarget tgt = deletedTargetList.get(i);
						removalList.add(tgt);
						
						ObservationTarget addTgt = new ObservationTarget();
						addTgt.setDescription(tgt.getDescription());
						addTgt.setInvestigation_Id(tgt.getInvestigation_Id());
						addTgt.setName(tgt.getName());
						addTgt.setOntologyReference_Id(tgt.getOntologyReference_Id());
						addTgt.setCanRead_Id(tgt.getCanRead_Id());
						addTgt.setCanWrite_Id(tgt.getCanWrite_Id());
						addTgt.setOwns_Id(tgt.getOwns_Id());
						addList.add(addTgt);
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

	private void deleteValuesForTarget(Database db, int animalId) throws DatabaseException
	{
		Date now = new Date();
		List<ObservedValue> valList = db.query(ObservedValue.class).eq(ObservedValue.TARGET, animalId).
				or().eq(ObservedValue.RELATION, animalId).find();
		db.remove(valList);
		
		List<DeletedObservedValue> valDelList = new ArrayList<DeletedObservedValue>();
		for (ObservedValue val : valList) {
			DeletedObservedValue valDel = new DeletedObservedValue();
			valDel.setEndtime(val.getEndtime());
			valDel.setFeature_Id(val.getFeature_Id());
			valDel.setInvestigation_Id(val.getInvestigation_Id());
			valDel.setOntologyReference_Id(val.getOntologyReference_Id());
			valDel.setProtocolApplication_Id(val.getProtocolApplication_Id());
			valDel.setRelation_Id(val.getRelation_Id());
			//valDel.setTarget_Id(val.getTarget_Id()); TODO: solve how to retain link between ObsVal and ObsTgt!
			valDel.setTime(val.getTime());
			valDel.setValue(val.getValue());
			valDel.setDeletionTime(now);
			valDel.setDeletedBy_Id(this.getLogin().getUserId());
			valDelList.add(valDel);
		}
		db.add(valDelList);
	}

	@Override
	public void reload(Database db)
	{
		try
		{
			this.targetList = db.query(ObservationTarget.class).find();
			this.deletedTargetList = db.query(DeletedObservationTarget.class).find();
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

	public List<DeletedObservationTarget> getDeletedTargetList()
	{
		return deletedTargetList;
	}

	public void setDeletedValueList(List<DeletedObservationTarget> deletedTargetList)
	{
		this.deletedTargetList = deletedTargetList;
	}
	
}
