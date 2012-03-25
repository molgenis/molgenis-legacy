package org.molgenis.mutation.dto;

import java.io.Serializable;


public class MutationUploadDTO implements Serializable
{
	/* The serial version UID of this class. Needed for serialization. */
	private static final long serialVersionUID = 1070160510535611854L;
	
	private String aaNotation;
	private Integer aaStart;
	private Integer aaEnd;
	private String cdnaNotation;
	private Integer cdnaStart;
	private Integer cdnaEnd;
	private String codonChange;
	private String consequence;
	private Boolean effectOnSplicing;
	private String event;
	private String gdnaNotation;
	private Integer gdnaStart;
	private Integer gdnaEnd;
	private String identifier;
//	private String inheritance;
	private Integer length;
	private String mutationPosition;
	private String ntChange;
//	private String pathogenicity;
	private String type;
	
	private Integer exonId;
	private Boolean exonIsIntron;
	
	private GeneDTO geneDTO;

	// Values that are calculated but not stored in the db
//	private String refseq;
	private String nt;
//	private String codon;
	private String aa;
	private String aachange;
	
	public MutationUploadDTO()
	{
	}

	public String getAaNotation() {
		return aaNotation;
	}

	public void setAaNotation(String aaNotation) {
		this.aaNotation = aaNotation;
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

	public String getCdnaNotation() {
		return cdnaNotation;
	}

	public void setCdnaNotation(String cdnaNotation) {
		this.cdnaNotation = cdnaNotation;
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

	public String getCodonChange() {
		return codonChange;
	}

	public void setCodonChange(String codonChange) {
		this.codonChange = codonChange;
	}

	public String getConsequence() {
		return consequence;
	}

	public void setConsequence(String consequence) {
		this.consequence = consequence;
	}

	public Boolean getEffectOnSplicing() {
		return effectOnSplicing;
	}

	public void setEffectOnSplicing(Boolean effectOnSplicing) {
		this.effectOnSplicing = effectOnSplicing;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

//	public String getInheritance() {
//		return inheritance;
//	}
//
//	public void setInheritance(String inheritance) {
//		this.inheritance = inheritance;
//	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getGdnaNotation() {
		return gdnaNotation;
	}

	public void setGdnaNotation(String gdnaNotation) {
		this.gdnaNotation = gdnaNotation;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public String getMutationPosition() {
		return mutationPosition;
	}

	public void setMutationPosition(String mutationPosition) {
		this.mutationPosition = mutationPosition;
	}

	public String getNtChange() {
		return ntChange;
	}

	public void setNtChange(String ntChange) {
		this.ntChange = ntChange;
	}

//	public String getPathogenicity() {
//		return pathogenicity;
//	}
//
//	public void setPathogenicity(String pathogenicity) {
//		this.pathogenicity = pathogenicity;
//	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public GeneDTO getGeneDTO() {
		return geneDTO;
	}

	public void setGeneDTO(GeneDTO geneDTO) {
		this.geneDTO = geneDTO;
	}

//	public String getRefseq() {
//		return refseq;
//	}
//	public void setRefseq(String refseq) {
//		this.refseq = refseq;
//	}
	public String getNt() {
		return nt;
	}
	public void setNt(String nt) {
		this.nt = nt;
	}
//	public String getCodon() {
//		return codon;
//	}
//	public void setCodon(String codon) {
//		this.codon = codon;
//	}
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

//	public String toString()
//	{
//		return
//		"Gene: " + this.getGeneSymbol() + "\n" +
//		"Position: " + this.getMutation().getMutationPosition() + "\n" +
//		"Nucleotide: " + this.getNt() + "\n" +
//		"Event: " + this.getMutation().getEvent() + "\n" +
//		"Conserved AA: " + this.getMutation().getConservedAA() + "\n" +
//		"Splicing: " + this.getMutation().getEffectOnSplicing() + "\n" +
//		"Founder mut: " + this.getMutation().getFounderMutation() + "\n" +
//		"Population: " + this.getMutation().getPopulation() + "\n" +
//		"SNP?: " + this.getMutation().getReportedSNP() + "\n" +
//		"Inheritance: " + this.getMutation().getInheritance() + "\n" +
//		"NT change: " + this.getMutation().getNtchange() + "\n" +
//		"Codon: " + this.getCodon() + "\n" +
//		"cDNA not: " + this.getMutation().getCdna_Notation() + "\n" +
//		"gDNA not: " + this.getMutation().getGdna_Notation() + "\n" +
//		"AA not: " + this.getMutation().getAa_Notation() + "\n" +
//		"Consequence: " + this.getMutation().getConsequence() + "\n" +
//		"Type: " + this.getMutation().getType() + "\n";
//	}
}