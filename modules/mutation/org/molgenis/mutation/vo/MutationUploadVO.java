package org.molgenis.mutation.vo;

import java.io.Serializable;

import org.molgenis.mutation.Exon;
import org.molgenis.mutation.Mutation;
import org.molgenis.mutation.MutationGene;
import org.molgenis.util.ValueLabel;

public class MutationUploadVO implements Serializable
{
	private static final long serialVersionUID = 1070160510535611854L;
	//TODO: Danny: Use or loose
	/*private static final transient Logger logger = Logger.getLogger(MutationUploadVO.class.getSimpleName());*/
	private Mutation mutation;
	private Integer exonId;
	private Boolean exonIsIntron;
	private Integer geneBpStart;
	private String geneOrientation;
	private String geneSeq;
	private String geneSymbol;
	// Values that are calculated but not stored in the db
	private String refseq;
	private String nt;
	private String codon;
	private String aa;
	private String aachange;
	
	public MutationUploadVO()
	{
		this.mutation = new Mutation();
	}

	public Mutation getMutation() {
		return mutation;
	}
	public void setMutation(Mutation mutation) {
		this.mutation = mutation;
	}
	public Integer getExonId() {
		return exonId;
	}

	public void setExonId(Integer exonId) {
		this.exonId = exonId;
	}

	public Boolean getExonIsIntron() {
		return exonIsIntron;
	}

	public void setExonIsIntron(Boolean exonIsIntron) {
		this.exonIsIntron = exonIsIntron;
	}

	public Integer getGeneBpStart() {
		return geneBpStart;
	}

	public void setGeneBpStart(Integer geneBpStart) {
		this.geneBpStart = geneBpStart;
	}

	public String getGeneOrientation() {
		return geneOrientation;
	}

	public void setGeneOrientation(String geneOrientation) {
		this.geneOrientation = geneOrientation;
	}

	public String getGeneSeq() {
		return geneSeq;
	}

	public void setGeneSeq(String geneSeq) {
		this.geneSeq = geneSeq;
	}
	public String getGeneSymbol() {
		return geneSymbol;
	}

	public void setGeneSymbol(String geneSymbol) {
		this.geneSymbol = geneSymbol;
	}

	public String getRefseq() {
		return refseq;
	}
	public void setRefseq(String refseq) {
		this.refseq = refseq;
	}
	public String getNt() {
		return nt;
	}
	public void setNt(String nt) {
		this.nt = nt;
	}
	public String getCodon() {
		return codon;
	}
	public void setCodon(String codon) {
		this.codon = codon;
	}
	public String getAa() {
		return aa;
	}
	public void setAa(String aa) {
		this.aa = aa;
	}
	public String getAachange() {
		return aachange;
	}
	public void setAachange(String aachange) {
		this.aachange = aachange;
	}
	public java.util.List<ValueLabel>  getEventOptions()
	{
		return new Mutation().getEventOptions();
	}
	public java.util.List<ValueLabel> getConsequenceOptions()
	{
		return new Mutation().getConsequenceOptions();
	}
	public java.util.List<ValueLabel> getInheritanceOptions()
	{
		return new Mutation().getInheritanceOptions();
	}

	public Mutation toMutation()
	{
		this.mutation.setExon(this.exonId);
//		this.mutation.setGene(this.gene);
		return this.mutation;
	}
	
	public String toString()
	{
		return
		"Gene: " + this.getGeneSymbol() + "\n" +
		"Position: " + this.getMutation().getMutationPosition() + "\n" +
		"Nucleotide: " + this.getNt() + "\n" +
		"Event: " + this.getMutation().getEvent() + "\n" +
		"Conserved AA: " + this.getMutation().getConservedAA() + "\n" +
		"Splicing: " + this.getMutation().getEffectOnSplicing() + "\n" +
		"Founder mut: " + this.getMutation().getFounderMutation() + "\n" +
		"Population: " + this.getMutation().getPopulation() + "\n" +
		"SNP?: " + this.getMutation().getReportedSNP() + "\n" +
		"Inheritance: " + this.getMutation().getInheritance() + "\n" +
		"NT change: " + this.getMutation().getNtchange() + "\n" +
		"Codon: " + this.getCodon() + "\n" +
		"cDNA not: " + this.getMutation().getCdna_Notation() + "\n" +
		"gDNA not: " + this.getMutation().getGdna_Notation() + "\n" +
		"AA not: " + this.getMutation().getAa_Notation() + "\n" +
		"Consequence: " + this.getMutation().getConsequence() + "\n" +
		"Type: " + this.getMutation().getType() + "\n";
	}
}