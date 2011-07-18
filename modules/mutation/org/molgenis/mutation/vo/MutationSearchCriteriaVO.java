package org.molgenis.mutation.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class MutationSearchCriteriaVO implements Serializable
{
	private static final long serialVersionUID = -399485639945935744L;
	Integer cdnaPosition;
	Integer codonChangeNumber;
	Integer codonNumber;
	String consequence;
	Integer exonId;
	String exonName;
	Integer exonNumber;
	String mid;
	String inheritance;
	Integer mutationId;
	String pid;
	Integer phenotypeId;
	String phenotypeName;
	Integer proteinDomainId;
	String publication;
	Boolean reportedAsSNP;
	String searchTerm;
	String type;
	String variation;

	public Integer getCdnaPosition() {
		return cdnaPosition;
	}
	public void setCdnaPosition(Integer cdnaPosition) {
		this.cdnaPosition = cdnaPosition;
	}
	public Integer getCodonChangeNumber() {
		return codonChangeNumber;
	}
	public void setCodonChangeNumber(Integer codonChangeNumber) {
		this.codonChangeNumber = codonChangeNumber;
	}
	public Integer getCodonNumber() {
		return codonNumber;
	}
	public void setCodonNumber(Integer codonNumber) {
		this.codonNumber = codonNumber;
	}
	public String getConsequence() {
		return consequence;
	}
	public void setConsequence(String consequence) {
		this.consequence = consequence;
	}
	public Integer getExonId() {
		return exonId;
	}
	public void setExonId(Integer exonId) {
		this.exonId = exonId;
	}
	public String getExonName() {
		return exonName;
	}
	public void setExonName(String exonName) {
		this.exonName = exonName;
	}
	public Integer getExonNumber() {
		return exonNumber;
	}
	public void setExonNumber(Integer exonNumber) {
		this.exonNumber = exonNumber;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getInheritance() {
		return inheritance;
	}
	public void setInheritance(String inheritance) {
		this.inheritance = inheritance;
	}
	public Integer getMutationId() {
		return mutationId;
	}
	public void setMutationId(Integer mutationId) {
		this.mutationId = mutationId;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public Integer getPhenotypeId() {
		return phenotypeId;
	}
	public void setPhenotypeId(Integer phenotypeId) {
		this.phenotypeId = phenotypeId;
	}
	public String getPhenotypeName() {
		return phenotypeName;
	}
	public void setPhenotypeName(String phenotypeName) {
		this.phenotypeName = phenotypeName;
	}
	public Integer getProteinDomainId() {
		return proteinDomainId;
	}
	public void setProteinDomainId(Integer proteinDomainId) {
		this.proteinDomainId = proteinDomainId;
	}
	public String getPublication() {
		return publication;
	}
	public void setPublication(String publication) {
		this.publication = publication;
	}
	public Boolean getReportedAsSNP() {
		return reportedAsSNP;
	}
	public void setReportedAsSNP(Boolean reportedAsSNP) {
		this.reportedAsSNP = reportedAsSNP;
	}
	public String getSearchTerm() {
		return searchTerm;
	}
	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getVariation() {
		return variation;
	}
	public void setVariation(String variation) {
		this.variation = variation;
	}
	public String toString()
	{
		List<String> result = new ArrayList<String>();

		if (this.cdnaPosition != null)
			result.add("nucleotide number = '" + this.cdnaPosition + "'");
		if (this.codonNumber != null)
			result.add("codon number = '" + this.codonNumber + "'");
		if (this.codonChangeNumber != null)
			result.add("codon change number = '" + this.codonChangeNumber + "'");
		if (this.consequence != null)
			result.add("consequence = '" + this.consequence + "'");
		if (this.exonId != null)
			result.add("exon/intron = '" + this.exonId + "'");
		if (this.exonName != null)
			result.add("exon/intron = '" + this.exonName + "'");
		if (this.exonNumber != null)
			result.add("exon/intron = '" + this.exonNumber + "'");
		if (this.phenotypeId != null)
			result.add("phenotype = '" + this.phenotypeId + "'");
		if (this.phenotypeName != null)
			result.add("phenotype = '" + this.phenotypeName + "'");
		if (this.searchTerm != null)
			result.add("search term = '" + this.searchTerm + "'");
		if (this.type != null)
			result.add("mutation type = '" + this.type + "'");
		if (this.variation != null)
			result.add("variation = '" + this.variation + "'");

		return StringUtils.join(result, " and ");
	}
}
