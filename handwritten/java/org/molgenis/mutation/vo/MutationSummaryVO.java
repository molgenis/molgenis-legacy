package org.molgenis.mutation.vo;

import java.io.Serializable;
import java.util.List;

import org.molgenis.core.Publication;
import org.molgenis.core.vo.PublicationVO;
import org.molgenis.mutation.Mutation;
import org.molgenis.mutation.MutationPhenotype;
import org.molgenis.mutation.ProteinDomain;

public class MutationSummaryVO implements Serializable
{
	private static final long serialVersionUID = 6822471461546986166L;
	private String identifier;
	private String cdnaNotation;
	private String gdnaNotation;
	private Integer gdnaPosition;
	private String aaNotation;
	private Integer aaPosition;
	private String codonChange;
	private Integer exonId;
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

//	// TODO: old
//	private Mutation mutation;
	private String niceNotation;
//	private String codonChange;
//	private List<PatientSummaryVO> patients;
//	private List<MutationPhenotype> phenotypes;
//	private String pubmedURL;
//	private List<Publication> publications;
//	private ProteinDomain proteinDomain;
//	private Mutation firstMutation;
//	private Mutation prevMutation;
//	private Mutation nextMutation;
//	private Mutation lastMutation;
	private List<MutationSummaryVO> positionMutations;
	private List<MutationSummaryVO> codonMutations;

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
//	public Mutation getMutation() {
//		return mutation;
//	}
//	public void setMutation(Mutation mutation) {
//		this.mutation = mutation;
//	}
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
//	public List<PatientSummaryVO> getPatients() {
//		return patients;
//	}
//	public void setPatients(List<PatientSummaryVO> patients) {
//		this.patients = patients;
//	}
//	public List<MutationPhenotype> getPhenotypes() {
//		return phenotypes;
//	}
//	public void setPhenotypes(List<MutationPhenotype> phenotypes) {
//		this.phenotypes = phenotypes;
//	}
	public String getPubmedURL() {
	    return pubmedURL;
	}
	public void setPubmedURL(String pubmedURL) {
	    this.pubmedURL = pubmedURL;
	}
//	public List<Publication> getPublications() {
//		return publications;
//	}
//	public void setPublications(List<Publication> publications) {
//		this.publications = publications;
//	}
//	public ProteinDomain getProteinDomain() {
//		return proteinDomain;
//	}
//	public void setProteinDomain(ProteinDomain proteinDomain) {
//		this.proteinDomain = proteinDomain;
//	}
//	public Mutation getFirstMutation() {
//		return firstMutation;
//	}
//	public void setFirstMutation(Mutation firstMutation) {
//		this.firstMutation = firstMutation;
//	}
//	public Mutation getPrevMutation() {
//		return prevMutation;
//	}
//	public void setPrevMutation(Mutation prevMutation) {
//		this.prevMutation = prevMutation;
//	}
//	public Mutation getNextMutation() {
//		return nextMutation;
//	}
//	public void setNextMutation(Mutation nextMutation) {
//		this.nextMutation = nextMutation;
//	}
//	public Mutation getLastMutation() {
//		return lastMutation;
//	}
//	public void setLastMutation(Mutation lastMutation) {
//		this.lastMutation = lastMutation;
//	}
	public List<MutationSummaryVO> getPositionMutations() {
		return positionMutations;
	}
	public void setPositionMutations(List<MutationSummaryVO> positionMutations) {
		this.positionMutations = positionMutations;
	}
	public List<MutationSummaryVO> getCodonMutations() {
		return codonMutations;
	}
	public void setCodonMutations(List<MutationSummaryVO> codonMutations) {
		this.codonMutations = codonMutations;
	}
}
