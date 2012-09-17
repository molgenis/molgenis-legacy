package org.molgenis.mutation.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.molgenis.core.dto.PublicationDTO;
import org.molgenis.pheno.dto.ObservedValueDTO;
import org.molgenis.pheno.dto.ProtocolDTO;

public class MutationSummaryDTO implements Comparable<MutationSummaryDTO>, Serializable
{
	private static final long serialVersionUID = 6822471461546986166L;
	private Integer id;
	private String identifier;
	private String cdnaNotation;
	private Integer cdnaStart;
	private Integer cdnaEnd;
	private String gdnaNotation;
	private Integer gdnaStart;
	private Integer gdnaEnd;
	private String mrnaNotation;
	private String aaNotation;
	private Integer aaStart;
	private Integer aaEnd;
	private String codonChange;
	private Integer exonId;
	private Integer exonNumber;
	private String exonName;
	private List<String> proteinDomainNameList;
	private String type;
	private String consequence;
	private String inheritance;
	private Boolean reportedSNP;
	private String pathogenicity;
	private List<PatientSummaryDTO> patientSummaryDTOList;
	private List<String> phenotypeNameList;
	private String pubmedURL;
	private List<PublicationDTO> publicationDTOList;
	private String niceNotation;
	private List<ProtocolDTO> protocolDTOList;
	private String observedValue;
	/* Hash for ObservedValues: Key: "Protocol" + id (because of freemarker issues), Value: List of ObservedValue's */
	private Map<String, List<ObservedValueDTO>> observedValueDTOHash;

	public MutationSummaryDTO()
	{
		this.init();
	}

	private void init()
	{
		this.aaEnd = 0;
		this.aaNotation = "p.?";
		this.aaStart = 0;
		this.cdnaEnd = 0;
		this.cdnaNotation = "c.?";
		this.cdnaStart = 0;
		this.codonChange = "";
		this.exonId = 0;
		this.exonName = "";
		this.exonNumber = 0;
		this.gdnaEnd = 0;
		this.gdnaNotation = "g.?";
		this.gdnaStart = 0;
		this.id = 0;
		this.identifier = "";
		this.consequence = "";
		this.inheritance = "";
		this.mrnaNotation = "r.?";
		this.niceNotation = "";
		this.pathogenicity = "";
		this.patientSummaryDTOList = new ArrayList<PatientSummaryDTO>();
		this.phenotypeNameList = new ArrayList<String>();
		this.proteinDomainNameList = new ArrayList<String>();
		this.publicationDTOList = new ArrayList<PublicationDTO>();
		this.pubmedURL = "";
		this.reportedSNP = false;
		this.type = "";
	}

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
	public String getGdnaNotation() {
		return gdnaNotation;
	}
	public void setGdnaNotation(String gdnaNotation) {
		this.gdnaNotation = gdnaNotation;
	}
	public String getAaNotation() {
		return aaNotation;
	}
	public void setAaNotation(String aaNotation) {
		this.aaNotation = aaNotation;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getConsequence() {
		return consequence;
	}

	public void setConsequence(String consequence) {
		this.consequence = consequence;
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
	public List<PatientSummaryDTO> getPatientSummaryDTOList() {
		return patientSummaryDTOList;
	}
	public void setPatientSummaryDTOList(List<PatientSummaryDTO> patientSummaryDTOList) {
		this.patientSummaryDTOList = patientSummaryDTOList;
	}
	public List<String> getPhenotypeNameList() {
		return phenotypeNameList;
	}
	public void setPhenotypeNameList(List<String> phenotypeNameList) {
		this.phenotypeNameList = phenotypeNameList;
	}
	public List<PublicationDTO> getPublicationDTOList() {
		return publicationDTOList;
	}
	public void setPublicationDTOList(List<PublicationDTO> publicationDTOList) {
		this.publicationDTOList = publicationDTOList;
	}
	public String getNiceNotation() {
		return niceNotation;
	}
	public void setNiceNotation(String niceNotation) {
		this.niceNotation = niceNotation;
	}
	public List<ProtocolDTO> getProtocolDTOList() {
		return protocolDTOList;
	}
	public void setProtocolDTOList(List<ProtocolDTO> protocolDTOList) {
		this.protocolDTOList = protocolDTOList;
	}
	public String getObservedValue() {
		return observedValue;
	}

	public void setObservedValue(String observedValue) {
		this.observedValue = observedValue;
	}

	public Map<String, List<ObservedValueDTO>> getObservedValueDTOHash() {
		return observedValueDTOHash;
	}
	public void setObservedValueDTOHash(
			Map<String, List<ObservedValueDTO>> observedValueDTOHash) {
		this.observedValueDTOHash = observedValueDTOHash;
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
	public Integer getCdnaStart() {
		return cdnaStart;
	}
	public void setCdnaStart(Integer cdnaStart) {
		this.cdnaStart = cdnaStart;
	}
	public Integer getCdnaEnd() {
		return cdnaEnd;
	}
	public void setCdnaEnd(Integer cdnaEnd) {
		this.cdnaEnd = cdnaEnd;
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
	public String getMrnaNotation() {
		return mrnaNotation;
	}
	public void setMrnaNotation(String mrnaNotation) {
		this.mrnaNotation = mrnaNotation;
	}
	public Integer getAaStart() {
		return aaStart;
	}
	public void setAaStart(Integer aaStart) {
		this.aaStart = aaStart;
	}
	public Integer getAaEnd() {
		return aaEnd;
	}
	public void setAaEnd(Integer aaEnd) {
		this.aaEnd = aaEnd;
	}

	@Override
	public int compareTo(MutationSummaryDTO mutationSummaryVO)
	{
		if (this.getCdnaStart() == null || mutationSummaryVO.getCdnaStart() == null)
			return 0;

		return this.getCdnaStart().compareTo(mutationSummaryVO.getCdnaStart());
	}
}
