/**
 * @author Jessica Lundberg
 * @author Erik Roos
 * @date Feb 24, 2011
 * 
 * This class is the model for the ApplyProtocolPlugin
 */
package plugins.protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EditProtocolPluginModel {
	
	private List<Integer> featuresIdList = new ArrayList<Integer>();
	private List<Integer> targetsIdList = new ArrayList<Integer>();
	private List<String> targetList = new ArrayList<String>();
	private int protocolId;
	private int protocolApplicationId;
	private Map<Integer, String> targetMap;
	private boolean newProtocolApplication = false;
	private boolean timeInfo = false;
	
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

	public void setTargetMap(Map<Integer, String> targetMap) {
	    this.targetMap = targetMap;
	}

	public Map<Integer, String> getTargetMap() {
	    return targetMap;
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
}
