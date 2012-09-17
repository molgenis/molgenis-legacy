package org.molgenis.mutation.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.molgenis.core.dto.PublicationDTO;
import org.molgenis.pheno.dto.ObservedValueDTO;

public class PatientSummaryDTO implements Comparable<PatientSummaryDTO>, Serializable
{
	/* The serial version UID of this class. Needed for serialization. */
	private static final long serialVersionUID = -8983436185205230071L;

	private Integer patientId;
	private String patientIdentifier;
	private String patientName;
	private String patientLocalId;
	private String patientConsent;
	private List<VariantDTO> variantDTOList;
	private Integer gdnaStart; // for sorting
	private Integer exonNumber; //for sorting
	private String variantComment;
	private Integer phenotypeId;
	private String phenotypeMajor;
	private String phenotypeSub;
	private java.util.Date submissionDate;
	private String submitterDepartment;
	private String submitterInstitute;
	private String submitterCity;
	private String submitterCountry;
	private List<PublicationDTO> publicationDTOList;
	private List<ObservedValueDTO> observedValueDTOList;
	private String pubmedURL;

	public PatientSummaryDTO()
	{
		this.init();
	}

	private void init()
	{
		this.patientId = 0;
		this.patientIdentifier = "";
		this.patientName = "";
		this.patientLocalId = "";
		this.patientConsent = "";
		this.variantDTOList = new ArrayList<VariantDTO>();
		this.gdnaStart = 0;
		this.exonNumber = 0;
		this.variantComment = "";
		this.phenotypeId = 0;
		this.phenotypeMajor = "";
		this.phenotypeSub = "";
		this.submissionDate = new Date();
		this.submitterDepartment = "";
		this.submitterInstitute = "";
		this.submitterCity = "";
		this.submitterCountry = "";
		this.publicationDTOList = new ArrayList<PublicationDTO>();
		this.observedValueDTOList = new ArrayList<ObservedValueDTO>();
		this.pubmedURL = "";
	}

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

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getPatientLocalId() {
		return patientLocalId;
	}

	public void setPatientLocalId(String patientLocalId) {
		this.patientLocalId = patientLocalId;
	}

	public String getPatientConsent() {
		return patientConsent;
	}

	public void setPatientConsent(String patientConsent) {
		this.patientConsent = patientConsent;
	}

	public List<VariantDTO> getVariantDTOList() {
		return variantDTOList;
	}

	public void setVariantDTOList(List<VariantDTO> variantDTOList) {
		this.variantDTOList = variantDTOList;
	}

	public Integer getGdnaStart() {
		return gdnaStart;
	}

	public void setGdnaStart(Integer gdnaStart) {
		this.gdnaStart = gdnaStart;
	}

	public Integer getExonNumber() {
		return exonNumber;
	}

	public void setExonNumber(Integer exonNumber) {
		this.exonNumber = exonNumber;
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

	public List<PublicationDTO> getPublicationDTOList() {
		return publicationDTOList;
	}

	public void setPublicationDTOList(List<PublicationDTO> publicationDTOList) {
		this.publicationDTOList = publicationDTOList;
	}

	public String getPubmedURL() {
	    return pubmedURL;
	}
	public void setPubmedURL(String pubmedURL) {
	    this.pubmedURL = pubmedURL;
	}

	public List<ObservedValueDTO> getObservedValueDTOList() {
		return observedValueDTOList;
	}

	public void setObservedValueDTOList(List<ObservedValueDTO> observedValueDTOList) {
		this.observedValueDTOList = observedValueDTOList;
	}
/*
	@Override
	public int compareTo(PatientSummaryDTO o)
	{
		if (CollectionUtils.isEmpty(this.getPublicationDTOList()) || CollectionUtils.isEmpty(o.getPublicationDTOList()))
			return 0;

		if (StringUtils.isEmpty(this.getPublicationDTOList().get(0).getYear()) || StringUtils.isEmpty(o.getPublicationDTOList().get(0).getYear()))
			return 0;

		Integer year1 = Integer.parseInt(this.getPublicationDTOList().get(0).getYear());
		Integer year2 = Integer.parseInt(o.getPublicationDTOList().get(0).getYear());
		
		return year2.compareTo(year1);
	}
*/

	@Override
	public int compareTo(PatientSummaryDTO o)
	{
		if (CollectionUtils.isEmpty(this.getVariantDTOList()))
			return -1;
		if (CollectionUtils.isEmpty(o.getVariantDTOList()))
			return 1;

		Integer gdnaPosition1 = this.getVariantDTOList().get(0).getGdnaStart();
		Integer gdnaPosition2 = o.getVariantDTOList().get(0).getGdnaStart();
			
		if (gdnaPosition1.compareTo(gdnaPosition2) == 0)
		{
			if (this.getVariantDTOList().size() == 1)
				return -1;
			else if (o.getVariantDTOList().size() == 1)
				return 1;
			else
			{
				gdnaPosition1 = this.getVariantDTOList().get(1).getGdnaStart();
				gdnaPosition2 = o.getVariantDTOList().get(1).getGdnaStart();
			}
		}
		return gdnaPosition1.compareTo(gdnaPosition2) * -1; // gDNA position is descending
	}
}
