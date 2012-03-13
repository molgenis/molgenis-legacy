/* Date:        November 9, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.animaldb.plugins.system;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.animaldb.commonservice.CommonService;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;


public class CascadingDeleteAnimalsPlugin extends PluginModel<Entity>
{
	private static final long serialVersionUID = -366762636959036651L;
	private CommonService ct = CommonService.getInstance();
	private List<Integer> targetIdList;
	
	public CascadingDeleteAnimalsPlugin(String name, ScreenController<?> parent)
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
		return "org_molgenis_animaldb_plugins_system_CascadingDeleteAnimalsPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/animaldb/plugins/system/CascadingDeleteAnimalsPlugin.ftl";
	}
	
	// Target related methods:
	public List<Integer> getTargetIdList()
	{
		return targetIdList;
	}

	public void setTargetIdList(List<Integer> targetIdList)
	{
		this.targetIdList = targetIdList;
	}
	
	public String getTargetName(Integer id) {
		try {
			return ct.getObservationTargetLabel(id);
		} catch (Exception e) {
			return id.toString();
		}
	}

	public void removeValues(Database db, CommonService ct, int targetId, 
			List<ProtocolApplication> protocolApplicationList, List<Integer> investigationIds) throws DatabaseException {
		// Values related to the target itself
		Query<ObservedValue> q = db.query(ObservedValue.class);
		q.addRules(new QueryRule(ObservedValue.TARGET, Operator.EQUALS, targetId));
		q.addRules(new QueryRule(ObservedValue.INVESTIGATION, Operator.IN, investigationIds));
		List<ObservedValue> valueList = q.find();
		for (ObservedValue value : valueList) {
			int paId = value.getProtocolApplication_Id();
			try {
				ProtocolApplication pa = ct.getProtocolApplicationById(paId);
				if (!protocolApplicationList.contains(pa)) {
					protocolApplicationList.add(pa);
				}
			} catch (DatabaseException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		db.remove(valueList);
		// Values in which the target is linked to
		q = db.query(ObservedValue.class);
		q.addRules(new QueryRule(ObservedValue.RELATION, Operator.EQUALS, targetId));
		q.addRules(new QueryRule(ObservedValue.INVESTIGATION, Operator.IN, investigationIds));
		valueList = q.find();
		for (ObservedValue value : valueList) {
			int paId = value.getProtocolApplication_Id();
			try {
				ProtocolApplication pa = ct.getProtocolApplicationById(paId);
				if (!protocolApplicationList.contains(pa)) {
					protocolApplicationList.add(pa);
				}
			} catch (DatabaseException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		db.remove(valueList);
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try
		{
			int userId = this.getLogin().getUserId();
			List<Integer> investigationIds = ct.getWritableUserInvestigationIds(userId);
			
			String action = request.getString("__action");
			List<ProtocolApplication> protocolApplicationList = new ArrayList<ProtocolApplication>();
			
			if (action.equals("remove")) {
				
				List<?> targetsIdsAsObjectsList = request.getList("target");
				for (Object targetIdAsObject : targetsIdsAsObjectsList) {
					// Animal ID
					int targetId = Integer.parseInt((String)targetIdAsObject);
					removeValues(db, ct, targetId, protocolApplicationList, investigationIds);
					ObservationTarget target = ct.getObservationTargetById(targetId);
					db.remove(target);
				}
				for (ProtocolApplication pa : protocolApplicationList) {
					Query<ObservedValue> q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.PROTOCOLAPPLICATION, Operator.EQUALS, pa.getId()));
					q.addRules(new QueryRule(ObservedValue.INVESTIGATION, Operator.IN, investigationIds));
					List<ObservedValue> valueList = q.find();
					if (valueList.size() == 0) {
						// No values left in this application, so safe to remove
						db.remove(pa);
					}
				}
				
				this.getMessages().clear();
				this.getMessages().add(new ScreenMessage("Targets, values and protocol applications successfully removed", true));
			}
		
			if (action.equals("removeAllAnimals"))
			{
				List<Integer> animalIdList = ct.getAllObservationTargetIds("Individual", false, investigationIds);
				List<ObservationTarget> allAnimalList = ct.getObservationTargets(animalIdList);
				for (ObservationTarget animal : allAnimalList) {
					removeValues(db, ct, animal.getId(), protocolApplicationList, investigationIds);
					db.remove(animal);
				}
				db.remove(protocolApplicationList);
				
				this.getMessages().clear();
				this.getMessages().add(new ScreenMessage("All animals successfully removed", true));
			}
		} catch(Exception e) {
			this.getMessages().clear();
			if (e.getMessage() != null) {
				this.getMessages().add(new ScreenMessage("Error - targets not or partly removed", false));
			}
			e.printStackTrace();
		}
	}

	@Override
	public void reload(Database db)
	{
		ct.setDatabase(db);
		ct.makeObservationTargetNameMap(this.getLogin().getUserName(), false);

		try
		{
			List<Integer> investigationIds = ct.getWritableUserInvestigationIds(this.getLogin().getUserId());
			this.setTargetIdList(ct.getAllObservationTargetIds(null, false, investigationIds));
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage() != null) {
				this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			}
		}
	}
	
}
