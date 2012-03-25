package org.molgenis.mutation.dto;

import java.io.Serializable;
import java.util.List;

import org.molgenis.pheno.dto.ObservedValueDTO;

public class PatientUploadDTO implements Serializable
{
	// The serial version UID of this class. Needed for serialization.
	private static final long serialVersionUID = -2653357869259223062L;

	private String patientLocalId;
	private String patientName;
	private Integer submissionId;
	private List<String> variantCdnaNotationList;
	private List<ObservedValueDTO> observedValueDTOList;
	private List<String> pubmedStringList;

	public String getPatientLocalId() {
		return patientLocalId;
	}
	public void setPatientLocalId(String patientLocalId) {
		this.patientLocalId = patientLocalId;
	}
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	public Integer getSubmissionId() {
		return submissionId;
	}
	public void setSubmissionId(Integer submissionId) {
		this.submissionId = submissionId;
	}
	public List<String> getVariantCdnaNotationList() {
		return variantCdnaNotationList;
	}
	public void setVariantCdnaNotationList(List<String> variantCdnaNotationList) {
		this.variantCdnaNotationList = variantCdnaNotationList;
	}
	public List<ObservedValueDTO> getObservedValueDTOList() {
		return observedValueDTOList;
	}
	public void setObservedValueDTOList(List<ObservedValueDTO> observedValueDTOList) {
		this.observedValueDTOList = observedValueDTOList;
	}
	public List<String> getPubmedStringList() {
		return pubmedStringList;
	}
	public void setPubmedStringList(List<String> pubmedStringList) {
		this.pubmedStringList = pubmedStringList;
	}
}