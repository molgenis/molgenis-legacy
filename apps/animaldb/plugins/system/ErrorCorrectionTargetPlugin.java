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
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
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
				List<ObservedValue> valRemovalList = new ArrayList<ObservedValue>();
				List<DeletedObservedValue> valAddList = new ArrayList<DeletedObservedValue>();
				
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
						
						List<ObservedValue> valList = db.query(ObservedValue.class).eq(ObservedValue.TARGET, tgt.getId()).
								or().eq(ObservedValue.RELATION, tgt.getId()).find();
						valRemovalList.addAll(valList);
						for (ObservedValue val : valList) {
							DeletedObservedValue valDel = new DeletedObservedValue();
							valDel.setEndtime(val.getEndtime());
							valDel.setFeature_Id(val.getFeature_Id());
							valDel.setInvestigation_Id(val.getInvestigation_Id());
							valDel.setOntologyReference_Id(val.getOntologyReference_Id());
							valDel.setProtocolApplication_Id(val.getProtocolApplication_Id());
							valDel.setTime(val.getTime());
							valDel.setValue(val.getValue());
							if (val.getTarget_Id().intValue() == tgt.getId().intValue()) {
								// Attach to deleted Target
								valDel.setDeletedTarget_Name(delTgt.getName());
								valDel.setTarget_Id(1); // Hack because override making Target field nillable doesn't seem to work! TODO: solve.
								valDel.setRelation_Id(val.getRelation_Id()); // Relation we can keep as-is because this target wasn't deleted
							} else {
								// Attach to deleted Relation
								valDel.setDeletedRelation_Name(delTgt.getName());
								valDel.setTarget_Id(val.getTarget_Id()); // Target we can keep as-is because this target wasn't deleted
							}
							valDel.setDeletionTime(now);
							valDel.setDeletedBy_Id(this.getLogin().getUserId());
							valAddList.add(valDel);
						}
					}
				}
				db.remove(valRemovalList);
				db.remove(removalList);
				db.add(addList);
				db.add(valAddList);
			}
			
			if (action.equals("undeleteTargets")) {
				List<DeletedObservationTarget> removalList = new ArrayList<DeletedObservationTarget>();
				List<ObservationTarget> addList = new ArrayList<ObservationTarget>();
				List<DeletedObservedValue> valRemovalList = new ArrayList<DeletedObservedValue>();
				List<ObservedValue> valAddList = new ArrayList<ObservedValue>();
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
						
						Query<DeletedObservedValue> q = db.query(DeletedObservedValue.class);
						QueryRule qrTarget = new QueryRule(DeletedObservedValue.DELETEDTARGET, Operator.EQUALS, tgt.getId());
						QueryRule qrRelation = new QueryRule(DeletedObservedValue.DELETEDRELATION, Operator.EQUALS, tgt.getId());
						q.addRules(new QueryRule(qrTarget, new QueryRule(Operator.OR), qrRelation));
						java.sql.Date targetDeletionTime = new java.sql.Date(tgt.getDeletionTime().getTime());
						q.addRules(new QueryRule(DeletedObservedValue.DELETIONTIME, Operator.EQUALS, targetDeletionTime));
						List<DeletedObservedValue> valList = q.find();
						valRemovalList.addAll(valList);
						for (DeletedObservedValue val : valList) {
							ObservedValue addVal = new ObservedValue();
							addVal.setEndtime(val.getEndtime());
							addVal.setFeature_Id(val.getFeature_Id());
							addVal.setInvestigation_Id(val.getInvestigation_Id());
							addVal.setOntologyReference_Id(val.getOntologyReference_Id());
							addVal.setProtocolApplication_Id(val.getProtocolApplication_Id());
							addVal.setTime(val.getTime());
							addVal.setValue(val.getValue());
							if (val.getTarget_Id().intValue() == tgt.getId().intValue()) {
								// Attach to undeleted Target
								addVal.setTarget_Name(addTgt.getName());
								addVal.setRelation_Id(val.getRelation_Id()); // Relation we can keep as-is because it wasn't (un)deleted
							} else {
								// Attach to undeleted Relation
								addVal.setRelation_Name(addTgt.getName());
								addVal.setTarget_Id(val.getTarget_Id()); // Target we can keep as-is because it wasn't (un)deleted
							}
							valAddList.add(addVal);
						}
					}
				}
				db.remove(valRemovalList);
				db.remove(removalList);
				db.add(addList);
				db.add(valAddList);
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
