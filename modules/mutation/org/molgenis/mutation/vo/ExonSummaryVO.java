package org.molgenis.mutation.vo;

import java.io.Serializable;

import org.molgenis.mutation.Exon;

public class ExonSummaryVO implements Serializable
{
	private static final long serialVersionUID = 419557906940623900L;
	Exon exon;
	Integer id;
	String name;
	Boolean isIntron;
	Integer length;
	Integer cdnaPosition;
	Integer domainId;
	String orientation;
	Integer numFullAminoAcids;
	Integer numPartAminoAcids;
	Integer numGlyXYRepeats;
	Boolean multiple3Nucl;
	String nuclSequenceFlankLeft;
	String nuclSequenceFlankRight;
	String nuclSequence;
	String aaSequence;
	Exon firstExon;
	Exon prevExon;
	Exon nextExon;
	Exon lastExon;

	public Exon getExon() {
		return exon;
	}
	public void setExon(Exon exon) {
		this.exon = exon;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Boolean getIsIntron() {
		return isIntron;
	}
	public void setIsIntron(Boolean isIntron) {
		this.isIntron = isIntron;
	}
	public Integer getLength() {
		return length;
	}
	public void setLength(Integer length) {
		this.length = length;
	}
	public Integer getCdnaPosition() {
		return cdnaPosition;
	}
	public void setCdnaPosition(Integer cdnaPosition) {
		this.cdnaPosition = cdnaPosition;
	}
	public Integer getDomainId() {
		return domainId;
	}
	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}
	public String getOrientation() {
		return orientation;
	}
	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}
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
	public Integer getNumGlyXYRepeats() {
		return numGlyXYRepeats;
	}
	public void setNumGlyXYRepeats(Integer numGlyXYRepeats) {
		this.numGlyXYRepeats = numGlyXYRepeats;
	}
	public Boolean getMultiple3Nucl() {
		return multiple3Nucl;
	}
	public void setMultiple3Nucl(Boolean multiple3Nucl) {
		this.multiple3Nucl = multiple3Nucl;
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
	public Exon getFirstExon() {
		return firstExon;
	}
	public void setFirstExon(Exon firstExon) {
		this.firstExon = firstExon;
	}
	public Exon getPrevExon() {
		return prevExon;
	}
	public void setPrevExon(Exon prevExon) {
		this.prevExon = prevExon;
	}
	public Exon getNextExon() {
		return nextExon;
	}
	public void setNextExon(Exon nextExon) {
		this.nextExon = nextExon;
	}
	public Exon getLastExon() {
		return lastExon;
	}
	public void setLastExon(Exon lastExon) {
		this.lastExon = lastExon;
	}
}
