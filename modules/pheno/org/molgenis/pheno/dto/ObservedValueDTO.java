package org.molgenis.pheno.dto;

import java.io.Serializable;

public class ObservedValueDTO implements Serializable
{
	/* The serial version UID of this class. Needed for serialization. */
	private static final long serialVersionUID = 5023457428783817292L;

	private Integer observedValueId;
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
