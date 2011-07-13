package org.molgenis.mutation.vo;

import java.io.Serializable;
import java.util.List;

import org.molgenis.core.vo.PublicationVO;

public class PatientSummaryVO implements Serializable
{
	private static final long serialVersionUID = -8983436185205230071L;
	private String patientIdentifier;
	private String patientName;
	private String patientNumber;
	private String patientConsent;
	private String patientAge;
	private String patientGender;
	private String patientEthnicity;
	private String patientDeceased;
	private String patientDeathCause;
	private String patientMmp1Allele1;
	private String patientMmp1Allele2;
	private List<MutationSummaryVO> variantSummaryVOList;
	private String variantComment;
	private Integer phenotypeId;
	private String phenotypeMajor;
	private String phenotypeSub;
	private List<String> patientMaterialList;
	private java.util.Date submissionDate;
	private String submitterDepartment;
	private String submitterInstitute;
	private String submitterCity;
	private String submitterCountry;
	private List<PublicationVO> publicationVOList;
	private List<ObservedValueVO> observedValueVOList;
	
	//TODO: remove old code with persistent objects
//	private Patient patient;
//	private Mutation mutation1;
//	private Mutation mutation2;
//	private MutationPhenotype phenotype;
//	private PhenotypeDetails phenotypeDetails;
	private String pubmedURL;
//	private List<Publication> publications;
//	private Submission submission;
//	private MolgenisUser submitter;
//	private I_F if_;
//	private E_M em_;
//	private List<String> material;

	public PatientSummaryVO()
	{
//		this.mutation1        = new Mutation();
//		this.mutation2        = new Mutation();
//		this.patient          = new Patient();
//		this.phenotype        = new MutationPhenotype();
//		this.phenotypeDetails = new PhenotypeDetails();
//		this.publications     = new ArrayList<Publication>();
//		this.submission       = new Submission();
//		this.submitter        = new MolgenisUser();
//		this.if_              = new I_F();
//		this.em_              = new E_M();
	}

	public String getPatientIdentifier() {
		return patientIdentifier;
	}

	public void setPatientIdentifier(String patientIdentifier) {
		this.patientIdentifier = patientIdentifier;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getPatientNumber() {
		return patientNumber;
	}

	public void setPatientNumber(String patientNumber) {
		this.patientNumber = patientNumber;
	}

	public String getPatientConsent() {
		return patientConsent;
	}

	public void setPatientConsent(String patientConsent) {
		this.patientConsent = patientConsent;
	}

	public String getPatientAge() {
		return patientAge;
	}

	public void setPatientAge(String patientAge) {
		this.patientAge = patientAge;
	}

	public String getPatientGender() {
		return patientGender;
	}

	public void setPatientGender(String patientGender) {
		this.patientGender = patientGender;
	}

	public String getPatientEthnicity() {
		return patientEthnicity;
	}

	public void setPatientEthnicity(String patientEthnicity) {
		this.patientEthnicity = patientEthnicity;
	}

	public String getPatientDeceased() {
		return patientDeceased;
	}

	public void setPatientDeceased(String patientDeceased) {
		this.patientDeceased = patientDeceased;
	}

	public String getPatientDeathCause() {
		return patientDeathCause;
	}

	public void setPatientDeathCause(String patientDeathCause) {
		this.patientDeathCause = patientDeathCause;
	}

	public String getPatientMmp1Allele1() {
		return patientMmp1Allele1;
	}

	public void setPatientMmp1Allele1(String patientMmp1Allele1) {
		this.patientMmp1Allele1 = patientMmp1Allele1;
	}

	public String getPatientMmp1Allele2() {
		return patientMmp1Allele2;
	}

	public void setPatientMmp1Allele2(String patientMmp1Allele2) {
		this.patientMmp1Allele2 = patientMmp1Allele2;
	}

	public List<MutationSummaryVO> getVariantSummaryVOList() {
		return variantSummaryVOList;
	}

	public void setVariantSummaryVOList(List<MutationSummaryVO> variantSummaryVOList) {
		this.variantSummaryVOList = variantSummaryVOList;
	}

	public String getVariantComment() {
		return variantComment;
	}

	public void setVariantComment(String variantComment) {
		this.variantComment = variantComment;
	}

	public Integer getPhenotypeId() {
		return phenotypeId;
	}

	public void setPhenotypeId(Integer phenotypeId) {
		this.phenotypeId = phenotypeId;
	}

	public String getPhenotypeMajor() {
		return phenotypeMajor;
	}

	public void setPhenotypeMajor(String phenotypeMajor) {
		this.phenotypeMajor = phenotypeMajor;
	}

	public String getPhenotypeSub() {
		return phenotypeSub;
	}

	public void setPhenotypeSub(String phenotypeSub) {
		this.phenotypeSub = phenotypeSub;
	}

	public List<String> getPatientMaterialList() {
		return patientMaterialList;
	}

	public void setPatientMaterialList(List<String> patientMaterialList) {
		this.patientMaterialList = patientMaterialList;
	}

	public java.util.Date getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(java.util.Date submissionDate) {
		this.submissionDate = submissionDate;
	}

	public String getSubmitterDepartment() {
		return submitterDepartment;
	}

	public void setSubmitterDepartment(String submitterDepartment) {
		this.submitterDepartment = submitterDepartment;
	}

	public String getSubmitterInstitute() {
		return submitterInstitute;
	}

	public void setSubmitterInstitute(String submitterInstitute) {
		this.submitterInstitute = submitterInstitute;
	}

	public String getSubmitterCity() {
		return submitterCity;
	}

	public void setSubmitterCity(String submitterCity) {
		this.submitterCity = submitterCity;
	}

	public String getSubmitterCountry() {
		return submitterCountry;
	}

	public void setSubmitterCountry(String submitterCountry) {
		this.submitterCountry = submitterCountry;
	}

	public List<PublicationVO> getPublicationVOList() {
		return publicationVOList;
	}

	public void setPublicationVOList(List<PublicationVO> publicationVOList) {
		this.publicationVOList = publicationVOList;
	}

public List<ObservedValueVO> getObservedValueVOList() {
		return observedValueVOList;
	}

	public void setObservedValueVOList(List<ObservedValueVO> observedValueVOList) {
		this.observedValueVOList = observedValueVOList;
	}

	//	public Patient getPatient() {
//		return patient;
//	}
//	public void setPatient(Patient patient) {
//		this.patient = patient;
//	}
//	public Mutation getMutation1() {
//		return mutation1;
//	}
//	public void setMutation1(Mutation mutation1) {
//		this.mutation1 = mutation1;
//	}
//	public Mutation getMutation2() {
//		return mutation2;
//	}
//	public void setMutation2(Mutation mutation2) {
//		this.mutation2 = mutation2;
//	}
//	public MutationPhenotype getPhenotype() {
//		return phenotype;
//	}
//	public void setPhenotype(MutationPhenotype phenotype) {
//		this.phenotype = phenotype;
//	}
//	public PhenotypeDetails getPhenotypeDetails() {
//		return phenotypeDetails;
//	}
//	public void setPhenotypeDetails(PhenotypeDetails phenotypeDetails) {
//		this.phenotypeDetails = phenotypeDetails;
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
//	public Submission getSubmission() {
//		return submission;
//	}
//	public void setSubmission(Submission submission) {
//		this.submission = submission;
//	}
//	public MolgenisUser getSubmitter() {
//		return submitter;
//	}
//	public void setSubmitter(MolgenisUser submitter) {
//		this.submitter = submitter;
//	}
//	public I_F getIf_() {
//		return if_;
//	}
//	public void setIf_(I_F if_) {
//		this.if_ = if_;
//	}
//	public E_M getEm_() {
//		return em_;
//	}
//	public void setEm_(E_M em_) {
//		this.em_ = em_;
//	}
//	public List<String> getMaterial() {
//		return material;
//	}
//	public void setMaterial(List<String> material) {
//		this.material = material;
//	}

	public String toString()
	{
		return "";
//		"Patient No: " + this.getPatient().getNumber() + "\n" +
//		"Mutation1: " + (this.getMutation1() != null ? this.getMutation1().toString() : "") + "\n" +
//		"Mutation2: " + (this.getMutation2() != null ? this.getMutation2().toString() : "") + "\n" +
//		"Phenotype: " + this.getPhenotype().toString() + "\n" +
//		"Pubmed ID: " + (CollectionUtils.isNotEmpty(this.getPublications()) ? this.getPublications().get(0).getPubmedID_Name() : "") + "\n" +
////		"Article: " + (CollectionUtils.isNotEmpty(this.getPublications()) ? this.getPublications().get(0).getPdf() : "") + "\n" +
//		"Age: " + this.getPatient().getAge() + "\n" +
//		"Deceased?: " + this.getPatient().getDeceased() + "\n" +
//		"Consent: " + this.getPatient().getConsent() + "\n" +
//		"Details: " + (this.getPhenotypeDetails() != null ? this.getPhenotypeDetails().toString() : "") + "\n" +
//		"IF: " + (this.getIf_() != null ? this.getIf_().toString() : "") + "\n" +
//		"EM: " + (this.getEm_() != null ? this.getEm_().toString() : "") + "\n";
	}
}
