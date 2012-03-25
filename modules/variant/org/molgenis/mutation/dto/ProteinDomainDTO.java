package org.molgenis.mutation.dto;

import java.io.Serializable;
import java.util.List;


public class ProteinDomainDTO implements Serializable
{
	/* The serial version UID of this class. Needed for serialization. */
	private static final long serialVersionUID = -4365982338471188950L;

	private Integer domainId;
	private String domainName;
	private Integer gdnaStart;
	private Integer gdnaEnd;
	private String orientation;
	private List<ExonDTO> exonDTOList;

	public Integer getDomainId() {
		return domainId;
	}
	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	public Integer getGdnaStart() {
		return gdnaStart;
	}
	public void setGdnaStart(Integer gdnaStart) {
		this.gdnaStart = gdnaStart;
	}
	public Integer getGdnaEnd() {
		return gdnaEnd;
	}
	public void setGdnaEnd(Integer gdnaEnd) {
		this.gdnaEnd = gdnaEnd;
	}
	public String getOrientation() {
		return orientation;
	}
	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}
	public List<ExonDTO> getExonDTOList() {
		return exonDTOList;
	}
	public void setExonDTOList(List<ExonDTO> exonDTOList) {
		this.exonDTOList = exonDTOList;
	}
}
