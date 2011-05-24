package org.molgenis.mutation.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.molgenis.auth.MolgenisUser;
import org.molgenis.core.Publication;
import org.molgenis.mutation.E_M;
import org.molgenis.mutation.I_F;
import org.molgenis.mutation.Mutation;
import org.molgenis.mutation.MutationPhenotype;
import org.molgenis.mutation.Patient;
import org.molgenis.mutation.PhenotypeDetails;
import org.molgenis.submission.Submission;

public class PatientSummaryVO implements Serializable
{
	private static final long serialVersionUID = -8983436185205230071L;
	private Patient patient;
	private Mutation mutation1;
	private Mutation mutation2;
	private MutationPhenotype phenotype;
	private PhenotypeDetails phenotypeDetails;
	private String pubmedURL;
	private List<Publication> publications;
	private Submission submission;
	private MolgenisUser submitter;
	private I_F if_;
	private E_M em_;
	private List<String> material;

	public PatientSummaryVO()
	{
		this.mutation1        = new Mutation();
		this.mutation2        = new Mutation();
		this.patient          = new Patient();
		this.phenotype        = new MutationPhenotype();
		this.phenotypeDetails = new PhenotypeDetails();
		this.publications     = new ArrayList<Publication>();
		this.submission       = new Submission();
		this.submitter        = new MolgenisUser();
		this.if_              = new I_F();
		this.em_              = new E_M();
	}

	public Patient getPatient() {
		return patient;
	}
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	public Mutation getMutation1() {
		return mutation1;
	}
	public void setMutation1(Mutation mutation1) {
		this.mutation1 = mutation1;
	}
	public Mutation getMutation2() {
		return mutation2;
	}
	public void setMutation2(Mutation mutation2) {
		this.mutation2 = mutation2;
	}
	public MutationPhenotype getPhenotype() {
		return phenotype;
	}
	public void setPhenotype(MutationPhenotype phenotype) {
		this.phenotype = phenotype;
	}
	public PhenotypeDetails getPhenotypeDetails() {
		return phenotypeDetails;
	}
	public void setPhenotypeDetails(PhenotypeDetails phenotypeDetails) {
		this.phenotypeDetails = phenotypeDetails;
	}
	public String getPubmedURL() {
	    return pubmedURL;
	}
	public void setPubmedURL(String pubmedURL) {
	    this.pubmedURL = pubmedURL;
	}
	public List<Publication> getPublications() {
		return publications;
	}
	public void setPublications(List<Publication> publications) {
		this.publications = publications;
	}
	public Submission getSubmission() {
		return submission;
	}
	public void setSubmission(Submission submission) {
		this.submission = submission;
	}
	public MolgenisUser getSubmitter() {
		return submitter;
	}
	public void setSubmitter(MolgenisUser submitter) {
		this.submitter = submitter;
	}
	public I_F getIf_() {
		return if_;
	}
	public void setIf_(I_F if_) {
		this.if_ = if_;
	}
	public E_M getEm_() {
		return em_;
	}
	public void setEm_(E_M em_) {
		this.em_ = em_;
	}
	public List<String> getMaterial() {
		return material;
	}
	public void setMaterial(List<String> material) {
		this.material = material;
	}

	public String toString()
	{
		return
		"Patient No: " + this.getPatient().getNumber() + "\n" +
		"Mutation1: " + (this.getMutation1() != null ? this.getMutation1().toString() : "") + "\n" +
		"Mutation2: " + (this.getMutation2() != null ? this.getMutation2().toString() : "") + "\n" +
		"Phenotype: " + this.getPhenotype().toString() + "\n" +
		"Pubmed ID: " + (CollectionUtils.isNotEmpty(this.getPublications()) ? this.getPublications().get(0).getPubmedID_Name() : "") + "\n" +
//		"Article: " + (CollectionUtils.isNotEmpty(this.getPublications()) ? this.getPublications().get(0).getPdf() : "") + "\n" +
		"Age: " + this.getPatient().getAge() + "\n" +
		"Deceased?: " + this.getPatient().getDeceased() + "\n" +
		"Consent: " + this.getPatient().getConsent() + "\n" +
		"Details: " + (this.getPhenotypeDetails() != null ? this.getPhenotypeDetails().toString() : "") + "\n" +
		"IF: " + (this.getIf_() != null ? this.getIf_().toString() : "") + "\n" +
		"EM: " + (this.getEm_() != null ? this.getEm_().toString() : "") + "\n";
	}
}
