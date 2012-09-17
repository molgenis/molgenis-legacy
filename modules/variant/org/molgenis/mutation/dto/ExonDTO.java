package org.molgenis.mutation.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExonDTO implements Comparable<ExonDTO>, Serializable
{
	/* The serial version UID of this class. Needed for serialization. */
	private static final long serialVersionUID = 419557906940623900L;
	private Integer id;
	private String name;
	private Boolean isIntron;
	private Integer length;
	private Integer cdnaStart;
	private Integer cdnaEnd;
	private Integer gdnaStart;
	private Integer gdnaEnd;
	private List<Integer> domainId;
	private List<String> domainName;
	private String orientation;
	private Integer numFullAminoAcids;
	private Integer numPartAminoAcids;
	private Integer numGlyXYRepeats;
	private Boolean multiple3Nucl;
	private String nuclSequenceFlankLeft;
	private String nuclSequenceFlankRight;
	private String nuclSequence;
	private String aaSequence;

	public ExonDTO()
	{
		this.init();
	}
	
	private void init()
	{
		this.id = 0;
		this.name = "";
		this.isIntron = false;
		this.length = 0;
		this.cdnaStart = 0;
		this.cdnaEnd = 0;
		this.gdnaStart = 0;
		this.gdnaEnd = 0;
		this.domainId = new ArrayList<Integer>();
		this.domainName = new ArrayList<String>();
		this.orientation = "F";
		this.numFullAminoAcids = 0;
		this.numPartAminoAcids = 0;
		this.numGlyXYRepeats = 0;
		this.multiple3Nucl = false;
		this.nuclSequenceFlankLeft = "";
		this.nuclSequenceFlankRight = "";
		this.nuclSequence = "";
		this.aaSequence = "";
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
	public List<Integer> getDomainId() {
		return domainId;
	}
	public void setDomainId(List<Integer> domainId) {
		this.domainId = domainId;
	}
	public List<String> getDomainName() {
		return domainName;
	}
	public void setDomainName(List<String> domainName) {
		this.domainName = domainName;
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

	@Override
	public int compareTo(ExonDTO exonDTO)
	{
		if ("F".equals(this.orientation))
		{
			return this.getGdnaStart().compareTo(exonDTO.getGdnaStart());
		}
		else
		{
			return -1 * this.getGdnaEnd().compareTo(exonDTO.getGdnaEnd());
		}
	}
}
