package org.molgenis.mutation.vo;

import org.molgenis.mutation.Exon;

public class ExonVO extends Exon
{

	private static final long serialVersionUID = 5133514447846461160L;
	private Integer numFullAminoAcids;
	private Integer numPartAminoAcids;
	private String nuclSequence;
	private String aaSequence;
	private String nuclSequenceFlankLeft;
	private String nuclSequenceFlankRight;
	
	public Integer getNumFullAminoAcids() {
		return numFullAminoAcids;
	}
	public void setNumFullAminoAcids(Integer numFullAminoAcids) {
		this.numFullAminoAcids = numFullAminoAcids;
	}
	public Integer getNumPartAminoAcids() {
		return numPartAminoAcids;
	}
	public void setNumPartAminoAcids(Integer numPartAminoAcids) {
		this.numPartAminoAcids = numPartAminoAcids;
	}
	public String getNuclSequence() {
		return nuclSequence;
	}
	public void setNuclSequence(String nuclSequence) {
		this.nuclSequence = nuclSequence;
	}
	public String getAaSequence() {
		return aaSequence;
	}
	public void setAaSequence(String aaSequence) {
		this.aaSequence = aaSequence;
	}
	public String getNuclSequenceFlankLeft() {
		return nuclSequenceFlankLeft;
	}
	public void setNuclSequenceFlankLeft(String nuclSequenceFlankLeft) {
		this.nuclSequenceFlankLeft = nuclSequenceFlankLeft;
	}
	public String getNuclSequenceFlankRight() {
		return nuclSequenceFlankRight;
	}
	public void setNuclSequenceFlankRight(String nuclSequenceFlankRight) {
		this.nuclSequenceFlankRight = nuclSequenceFlankRight;
	}
}
