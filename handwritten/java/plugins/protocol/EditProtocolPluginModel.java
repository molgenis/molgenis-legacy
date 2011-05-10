/**
 * @author Jessica Lundberg
 * @author Erik Roos
 * @date Feb 24, 2011
 * 
 * This class is the model for the ApplyProtocolPlugin
 */
package plugins.protocol;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.DatabaseException;
import org.molgenis.pheno.Measurement;

import commonservice.CommonService;

@Deprecated
public class EditProtocolPluginModel {
	
	private List<Integer> featuresIdList = new ArrayList<Integer>();
	private List<Integer> targetsIdList = new ArrayList<Integer>();
	private int protocolId;
	private int protocolApplicationId;
	private boolean newProtocolApplication = false;
	private boolean timeInfo = false;
	private CommonService cs = CommonService.getInstance();
	
	public EditProtocolPluginModel() {
		
	}

	public void setFeaturesIdList(List<Integer> featuresIdList) {
		this.featuresIdList = featuresIdList;
	}

	public List<Integer> getFeaturesIdList() {
		return featuresIdList;
	}

	public void setTargetsIdList(List<Integer> targetsIdList) {
		this.targetsIdList = targetsIdList;
	}

	public List<Integer> getTargetsIdList() {
		return targetsIdList;
	}

	public void setProtocolId(int protocolId) {
	    this.protocolId = protocolId;
	}

	public int getProtocolId() {
	    return protocolId;
	}
	
	public void setProtocolApplicationId(int protAppInt) {
		this.protocolApplicationId = protAppInt;
	}
	
	public int getProtocolApplicationId() {
		return protocolApplicationId;
	}

	public void setNewProtocolApplication(boolean newProtocolApplication) {
		this.newProtocolApplication = newProtocolApplication;
	}

	public boolean isNewProtocolApplication() {
		return newProtocolApplication;
	}

	public void setTimeInfo(boolean timeInfo) {
		this.timeInfo = timeInfo;
	}

	public boolean isTimeInfo() {
		return timeInfo;
	}

	public List<Measurement> getFeaturesList() throws DatabaseException, ParseException {
		List<Measurement> returnList = new ArrayList<Measurement>();
		for (Integer id : featuresIdList) {
			returnList.add(cs.getMeasurementById(id));
		}
		return returnList;
	}
}
