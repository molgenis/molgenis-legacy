/* Date:        May 6, 2011
 * Template:	NewPluginModelGen.java.ftl
 * generator:   org.molgenis.generators.ui.NewPluginModelGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.mutation.ui.upload;

import org.molgenis.framework.ui.EasyPluginModel;
import org.molgenis.mutation.dto.MutationUploadDTO;
import org.molgenis.mutation.dto.PatientSummaryDTO;
import org.molgenis.mutation.ui.upload.form.BatchForm;
import org.molgenis.mutation.ui.upload.form.MutationForm;
import org.molgenis.mutation.ui.upload.form.PatientForm;

/**
 * UploadModel takes care of all state and it can have helper methods to query the database.
 * It should not contain layout or application logic which are solved in View and Controller.
 * @See org.molgenis.framework.ui.ScreenController for available services.
 */
public class UploadModel extends EasyPluginModel
{
	private static final long serialVersionUID = -352089187099258661L;

	private String action;
	private MutationUploadDTO mutationUploadVO;
	private PatientSummaryDTO patientSummaryVO;
	private int referer ; // referer for patient.mutation{1,2} => 1 or 2, 0 initially

	private BatchForm batchForm;
	private PatientForm patientForm;
	private MutationForm mutationForm;

	public UploadModel(Upload controller)
	{
		super(controller);
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public MutationUploadDTO getMutationUploadVO() {
		return mutationUploadVO;
	}

	public void setMutationUploadVO(MutationUploadDTO mutationUploadVO) {
		this.mutationUploadVO = mutationUploadVO;
	}

	public PatientSummaryDTO getPatientSummaryVO() {
		return patientSummaryVO;
	}

	public void setPatientSummaryVO(PatientSummaryDTO patientSummaryVO) {
		this.patientSummaryVO = patientSummaryVO;
	}

	public int getReferer() {
		return referer;
	}

	public void setReferer(int referer) {
		this.referer = referer;
	}

	public BatchForm getBatchForm() {
		return batchForm;
	}

	public void setBatchForm(BatchForm batchForm) {
		this.batchForm = batchForm;
	}

	public PatientForm getPatientForm() {
		return patientForm;
	}

	public void setPatientForm(PatientForm patientForm) {
		this.patientForm = patientForm;
	}

	public MutationForm getMutationForm() {
		return mutationForm;
	}

	public void setMutationForm(MutationForm mutationForm) {
		this.mutationForm = mutationForm;
	}
	
	
}
