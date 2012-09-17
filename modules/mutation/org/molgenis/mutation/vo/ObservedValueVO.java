package org.molgenis.mutation.vo;

import java.io.Serializable;

public class ObservedValueVO implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String featureName;
	private String targetName;
	private String value;

	public String getFeatureName() {
		return featureName;
	}
	public void setFeatureName(String featureName) {
		this.featureName = featureName;
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
