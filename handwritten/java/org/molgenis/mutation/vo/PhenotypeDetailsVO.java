package org.molgenis.mutation.vo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class PhenotypeDetailsVO implements Serializable
{
	private static final long serialVersionUID = 1L;

	private Integer patientId;
	private String patientIdentifier;
	private String protocolName;
	// (protocolName, List<ObservedValueVO>)
	private HashMap<String, List<ObservedValueVO>> observedValues = new HashMap<String, List<ObservedValueVO>>();

	public Integer getPatientId() {
		return patientId;
	}
	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}
	public String getPatientIdentifier() {
		return patientIdentifier;
	}
	public void setPatientIdentifier(String patientIdentifier) {
		this.patientIdentifier = patientIdentifier;
	}
	public String getProtocolName() {
		return protocolName;
	}
	public void setProtocolName(String protocolName) {
		this.protocolName = protocolName;
	}
	public HashMap<String, List<ObservedValueVO>> getObservedValues() {
		return observedValues;
	}
	public void setObservedValues(HashMap<String, List<ObservedValueVO>> featureValues) {
		this.observedValues = featureValues;
	}
}
