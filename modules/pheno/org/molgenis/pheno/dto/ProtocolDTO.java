package org.molgenis.pheno.dto;

import java.io.Serializable;
import java.util.List;

public class ProtocolDTO implements Serializable
{
	/* The serial version UID of this class. Needed for serialization. */
	private static final long serialVersionUID = -5727025775906852427L;

	private Integer protocolId;
	private String protocolKey;
	private String protocolName;
	private List<FeatureDTO> featureDTOList;

	public Integer getProtocolId() {
		return protocolId;
	}
	public void setProtocolId(Integer protocolId) {
		this.protocolId = protocolId;
	}
	public String getProtocolKey() {
		return protocolKey;
	}
	public void setProtocolKey(String protocolKey) {
		this.protocolKey = protocolKey;
	}
	public String getProtocolName() {
		return protocolName;
	}
	public void setProtocolName(String protocolName) {
		this.protocolName = protocolName;
	}
	public List<FeatureDTO> getFeatureDTOList() {
		return featureDTOList;
	}
	public void setFeatureDTOList(List<FeatureDTO> featureDTOList) {
		this.featureDTOList = featureDTOList;
	}
}
