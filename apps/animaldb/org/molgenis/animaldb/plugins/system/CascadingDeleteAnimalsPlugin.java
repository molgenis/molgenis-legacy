/* Date:        November 9, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.animaldb.plugins.system;

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
	private List<String> targetNameList;
	
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
	public List<String> getTargetNameList()
	{
		return targetNameList;
	}

	public void setTargetNameList(List<String> targetNameList)
	{
		this.targetNameList = targetNameList;
	}
	
	public String getTargetName(Integer id) {
		try {
			return ct.getObservationTargetLabel(id);
		} catch (Exception e) {
			return id.toString();
		}
	}

	public void removeValues(Database db, CommonService ct, String targetName, 
			List<ProtocolApplication> protocolApplicationList, List<String> investigationNames) throws DatabaseException {
		// Values related to the target itself
		Query<ObservedValue> q = db.query(ObservedValue.class);
		q.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, targetName));
		q.addRules(new QueryRule(ObservedValue.INVESTIGATION_NAME, Operator.IN, investigationNames));
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
		q.addRules(new QueryRule(ObservedValue.RELATION_NAME, Operator.EQUALS, targetName));
		q.addRules(new QueryRule(ObservedValue.INVESTIGATION_NAME, Operator.IN, investigationNames));
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
			String userName = this.getLogin().getUserName();
			List<String> investigationNames = ct.getWritableUserInvestigationNames(userName);
			
			String action = request.getString("__action");
			List<ProtocolApplication> protocolApplicationList = new ArrayList<ProtocolApplication>();
			
			if (action.equals("remove")) {
				
				List<?> targetsNamesAsObjectsList = request.getList("target");
				for (Object targetNameAsObject : targetsNamesAsObjectsList) {
					String targetName = (String)targetNameAsObject;
					removeValues(db, ct, targetName, protocolApplicationList, investigationNames);
					ObservationTarget target = ct.getObservationTargetByName(targetName);
					db.remove(target);
				}
				for (ProtocolApplication pa : protocolApplicationList) {
					Query<ObservedValue> q = db.query(ObservedValue.class);
					q.addRules(new QueryRule(ObservedValue.PROTOCOLAPPLICATION, Operator.EQUALS, pa.getId()));
					q.addRules(new QueryRule(ObservedValue.INVESTIGATION_NAME, Operator.IN, investigationNames));
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
				List<String> animalNameList = ct.getAllObservationTargetNames("Individual", false, investigationNames);
				List<ObservationTarget> allAnimalList = ct.getObservationTargets(animalNameList);
				for (ObservationTarget animal : allAnimalList) {
					removeValues(db, ct, animal.getName(), protocolApplicationList, investigationNames);
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
			List<String> investigationNames = ct.getWritableUserInvestigationNames(this.getLogin().getUserName());
			this.setTargetNameList(ct.getAllObservationTargetNames(null, false, investigationNames));
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage() != null) {
				this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			}
		}
	}
	
}
