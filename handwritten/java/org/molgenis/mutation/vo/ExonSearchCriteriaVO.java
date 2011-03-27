package org.molgenis.mutation.vo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class ExonSearchCriteriaVO
{
	Integer cdnaPosition;
	Integer gdnaPosition;
	Integer exonId;
	String position;
	Integer number;
	Integer proteinDomainId;
	Boolean isIntron;

	public Integer getCdnaPosition() {
		return cdnaPosition;
	}
	public void setCdnaPosition(Integer cdnaPosition) {
		this.cdnaPosition = cdnaPosition;
	}
	public Integer getGdnaPosition() {
		return gdnaPosition;
	}
	public void setGdnaPosition(Integer gdnaPosition) {
		this.gdnaPosition = gdnaPosition;
	}
	public Integer getExonId() {
		return exonId;
	}
	public void setExonId(Integer exonId) {
		this.exonId = exonId;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	public Integer getProteinDomainId() {
		return proteinDomainId;
	}
	public void setProteinDomainId(Integer proteinDomainId) {
		this.proteinDomainId = proteinDomainId;
	}
	public Boolean getIsIntron() {
		return isIntron;
	}
	public void setIsIntron(Boolean isIntron) {
		this.isIntron = isIntron;
	}
	public String toString()
	{
		List<String> result = new ArrayList<String>();

		if (this.cdnaPosition != null)
			result.add("nucleotide number = '" + this.cdnaPosition + "'");
		if (this.gdnaPosition != null)
			result.add("genomic position = '" + this.gdnaPosition + "'");
		if (this.exonId != null)
			result.add("id = '" + this.exonId + "'");
		if (this.position != null)
			result.add("position = '" + this.position + "'");
		if (this.number != null)
			result.add("number = '" + this.number + "'");
		if (this.proteinDomainId != null)
			result.add("protein domain = '" + this.proteinDomainId + "'");
		if (this.isIntron != null)
			result.add("intron? = '" + this.isIntron + "'");

		return StringUtils.join(result, " or ");
	}
}
