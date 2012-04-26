package org.molgenis.pheno.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ObservedValueDTO implements Serializable
{
	/* The serial version UID of this class. Needed for serialization. */
	private static final long serialVersionUID = 5023457428783817292L;

	private Integer observedValueId;
	private Integer protocolApplicationId;
	private String protocolApplicationName;
	private Date protocolApplicationTime;
	private List<String> performerNameList;
	private Integer protocolId;
	private FeatureDTO featureDTO;
	private Integer targetId;
	private String targetName;
	private String value;

	public Integer getObservedValueId() {
		return observedValueId;
	}
	public void setObservedValueId(Integer observedValueId) {
		this.observedValueId = observedValueId;
	}
	public Integer getProtocolApplicationId() {
		return protocolApplicationId;
	}
	public void setProtocolApplicationId(Integer protocolApplicationId) {
		this.protocolApplicationId = protocolApplicationId;
	}
	public String getProtocolApplicationName() {
		return protocolApplicationName;
	}
	public void setProtocolApplicationName(String protocolApplicationName) {
		this.protocolApplicationName = protocolApplicationName;
	}
	public Date getProtocolApplicationTime() {
		return protocolApplicationTime;
	}
	public void setProtocolApplicationTime(Date protocolApplicationTime) {
		this.protocolApplicationTime = protocolApplicationTime;
	}
	public List<String> getPerformerNameList() {
		return performerNameList;
	}
	public void setPerformerNameList(List<String> performerNameList) {
		this.performerNameList = performerNameList;
	}
	public Integer getProtocolId() {
		return protocolId;
	}
	public void setProtocolId(Integer protocolId) {
		this.protocolId = protocolId;
	}
	public FeatureDTO getFeatureDTO() {
		return featureDTO;
	}
	public void setFeatureDTO(FeatureDTO featureDTO) {
		this.featureDTO = featureDTO;
	}
	public Integer getTargetId() {
		return targetId;
	}
	public void setTargetId(Integer targetId) {
		this.targetId = targetId;
	}
	public String getTargetName() {
		return targetName;
	}
	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
