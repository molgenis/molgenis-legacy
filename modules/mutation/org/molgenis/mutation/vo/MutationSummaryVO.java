package org.molgenis.mutation.vo;

import java.io.Serializable;
import java.util.List;

import org.molgenis.core.vo.PublicationVO;

public class MutationSummaryVO implements Serializable
{
	private static final long serialVersionUID = 6822471461546986166L;
	private Integer id;
	private String identifier;
	private String cdnaNotation;
	private Integer cdnaPosition;
	private String gdnaNotation;
	private Integer gdnaPosition;
	private String aaNotation;
	private Integer aaPosition;
	private String codonChange;
	private Integer exonId;
	private Integer exonNumber;
	private String exonName;
	private List<String> proteinDomainNameList;
	private String consequence;
	private String type;
	private String inheritance;
	private Boolean reportedSNP;
	private String pathogenicity;
	private List<PatientSummaryVO> patientSummaryVOList;
	private List<String> phenotypeNameList;
	private String pubmedURL;
	private List<PublicationVO> publicationVOList;
	private String niceNotation;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getCdnaNotation() {
		return cdnaNotation;
	}
	public void setCdnaNotation(String cdnaNotation) {
		this.cdnaNotation = cdnaNotation;
	}
	public Integer getCdnaPosition() {
		return cdnaPosition;
	}
	public void setCdnaPosition(Integer cdnaPosition) {
		this.cdnaPosition = cdnaPosition;
	}
	public String getGdnaNotation() {
		return gdnaNotation;
	}
	public void setGdnaNotation(String gdnaNotation) {
		this.gdnaNotation = gdnaNotation;
	}
	public Integer getGdnaPosition() {
		return gdnaPosition;
	}
	public void setGdnaPosition(Integer gdnaPosition) {
		this.gdnaPosition = gdnaPosition;
	}
	public String getAaNotation() {
		return aaNotation;
	}
	public void setAaNotation(String aaNotation) {
		this.aaNotation = aaNotation;
	}
	public Integer getAaPosition() {
		return aaPosition;
	}
	public void setAaPosition(Integer aaPosition) {
		this.aaPosition = aaPosition;
	}
	public Integer getExonId() {
		return exonId;
	}
	public void setExonId(Integer exonId) {
		this.exonId = exonId;
	}
	public Integer getExonNumber() {
		return exonNumber;
	}
	public void setExonNumber(Integer exonNumber) {
		this.exonNumber = exonNumber;
	}
	public String getExonName() {
		return exonName;
	}
	public void setExonName(String exonName) {
		this.exonName = exonName;
	}
	public List<String> getProteinDomainNameList() {
		return proteinDomainNameList;
	}
	public void setProteinDomainNameList(List<String> proteinDomainNameList) {
		this.proteinDomainNameList = proteinDomainNameList;
	}
	public String getConsequence() {
		return consequence;
	}
	public void setConsequence(String consequence) {
		this.consequence = consequence;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getInheritance() {
		return inheritance;
	}
	public void setInheritance(String inheritance) {
		this.inheritance = inheritance;
	}
	public Boolean getReportedSNP() {
		return reportedSNP;
	}
	public void setReportedSNP(Boolean reportedSNP) {
		this.reportedSNP = reportedSNP;
	}
	public String getPathogenicity() {
		return pathogenicity;
	}
	public void setPathogenicity(String pathogenicity) {
		this.pathogenicity = pathogenicity;
	}
	public List<PatientSummaryVO> getPatientSummaryVOList() {
		return patientSummaryVOList;
	}
	public void setPatientSummaryVOList(List<PatientSummaryVO> patientSummaryVOList) {
		this.patientSummaryVOList = patientSummaryVOList;
	}
	public List<String> getPhenotypeNameList() {
		return phenotypeNameList;
	}
	public void setPhenotypeNameList(List<String> phenotypeNameList) {
		this.phenotypeNameList = phenotypeNameList;
	}
	public List<PublicationVO> getPublicationVOList() {
		return publicationVOList;
	}
	public void setPublicationVOList(List<PublicationVO> publicationVOList) {
		this.publicationVOList = publicationVOList;
	}
	public String getNiceNotation() {
		return niceNotation;
	}
	public void setNiceNotation(String niceNotation) {
		this.niceNotation = niceNotation;
	}
	public String getCodonChange() {
		return codonChange;
	}
	public void setCodonChange(String codonChange) {
		this.codonChange = codonChange;
	}
	public String getPubmedURL() {
	    return pubmedURL;
	}
	public void setPubmedURL(String pubmedURL) {
	    this.pubmedURL = pubmedURL;
	}
}
