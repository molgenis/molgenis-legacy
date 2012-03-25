package org.molgenis.pheno.dto;

import java.io.Serializable;

public class FeatureDTO implements Serializable
{
	/* The serial version UID of this class. Needed for serialization. */
	private static final long serialVersionUID = 3282738498021071323L;

	private Integer featureId;
	private String featureKey;
	private String featureName;
	private String featureType;

	public Integer getFeatureId() {
		return featureId;
	}
	public void setFeatureId(Integer featureId) {
		this.featureId = featureId;
	}
	public String getFeatureKey() {
		return featureKey;
	}
	public void setFeatureKey(String featureKey) {
		this.featureKey = featureKey;
	}
	public String getFeatureName() {
		return featureName;
	}
	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}
	public String getFeatureType() {
		return featureType;
	}
	public void setFeatureType(String featureType) {
		this.featureType = featureType;
	}
}
