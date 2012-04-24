package org.molgenis.pheno.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.molgenis.pheno.dto.ObservedValueDTO;

public class IndividualDTO implements Serializable
{
	/* The serial version UID of this class. Needed for serialization. */
	private static final long serialVersionUID = 7524289064806520907L;

	private Integer individualId;
	private List<ProtocolDTO> protocolList = new ArrayList<ProtocolDTO>();
//	private String protocolName;
	// (protocolName, List<ObservedValueVO>)
	private HashMap<String, HashMap<String, List<ObservedValueDTO>>> observedValues = new HashMap<String, HashMap<String, List<ObservedValueDTO>>>();
	private List<ObservedValueDTO> observedValueDTOList = new ArrayList<ObservedValueDTO>();

	public Integer getIndividualId() {
		return individualId;
	}
	public void setIndividualId(Integer individualId) {
		this.individualId = individualId;
	}
	public List<ProtocolDTO> getProtocolList() {
		return protocolList;
	}
	public void setProtocolList(List<ProtocolDTO> protocolList) {
		this.protocolList = protocolList;
	}
//	public String getProtocolName() {
//		return protocolName;
//	}
//	public void setProtocolName(String protocolName) {
//		this.protocolName = protocolName;
//	}
	public HashMap<String, HashMap<String, List<ObservedValueDTO>>> getObservedValues() {
		return observedValues;
	}
	public void setObservedValues(HashMap<String, HashMap<String, List<ObservedValueDTO>>> featureValues) {
		this.observedValues = featureValues;
	}
	public List<ObservedValueDTO> getObservedValueDTOList() {
		return observedValueDTOList;
	}
	public void setObservedValueDTOList(List<ObservedValueDTO> observedValueDTOList) {
		this.observedValueDTOList = observedValueDTOList;
	}
}
