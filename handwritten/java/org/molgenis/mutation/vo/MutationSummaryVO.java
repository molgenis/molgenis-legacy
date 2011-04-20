package org.molgenis.mutation.vo;

import java.io.Serializable;
import java.util.List;

import org.molgenis.core.Publication;
import org.molgenis.mutation.Mutation;
import org.molgenis.mutation.MutationPhenotype;
import org.molgenis.mutation.ProteinDomain;

public class MutationSummaryVO implements Serializable
{
	private static final long serialVersionUID = 6822471461546986166L;
	private Mutation mutation;
	private String niceNotation;
	private String codonChange;
	private List<PatientSummaryVO> patients;
	private List<MutationPhenotype> phenotypes;
	private List<Publication> publications;
	private ProteinDomain proteinDomain;
	private Mutation firstMutation;
	private Mutation prevMutation;
	private Mutation nextMutation;
	private Mutation lastMutation;
	private List<Mutation> positionMutations;
	private List<Mutation> codonMutations;

	public Mutation getMutation() {
		return mutation;
	}
	public void setMutation(Mutation mutation) {
		this.mutation = mutation;
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
	public List<PatientSummaryVO> getPatients() {
		return patients;
	}
	public void setPatients(List<PatientSummaryVO> patients) {
		this.patients = patients;
	}
	public List<MutationPhenotype> getPhenotypes() {
		return phenotypes;
	}
	public void setPhenotypes(List<MutationPhenotype> phenotypes) {
		this.phenotypes = phenotypes;
	}
	public List<Publication> getPublications() {
		return publications;
	}
	public void setPublications(List<Publication> publications) {
		this.publications = publications;
	}
	public ProteinDomain getProteinDomain() {
		return proteinDomain;
	}
	public void setProteinDomain(ProteinDomain proteinDomain) {
		this.proteinDomain = proteinDomain;
	}
	public Mutation getFirstMutation() {
		return firstMutation;
	}
	public void setFirstMutation(Mutation firstMutation) {
		this.firstMutation = firstMutation;
	}
	public Mutation getPrevMutation() {
		return prevMutation;
	}
	public void setPrevMutation(Mutation prevMutation) {
		this.prevMutation = prevMutation;
	}
	public Mutation getNextMutation() {
		return nextMutation;
	}
	public void setNextMutation(Mutation nextMutation) {
		this.nextMutation = nextMutation;
	}
	public Mutation getLastMutation() {
		return lastMutation;
	}
	public void setLastMutation(Mutation lastMutation) {
		this.lastMutation = lastMutation;
	}
	public List<Mutation> getPositionMutations() {
		return positionMutations;
	}
	public void setPositionMutations(List<Mutation> positionMutations) {
		this.positionMutations = positionMutations;
	}
	public List<Mutation> getCodonMutations() {
		return codonMutations;
	}
	public void setCodonMutations(List<Mutation> codonMutations) {
		this.codonMutations = codonMutations;
	}
}
