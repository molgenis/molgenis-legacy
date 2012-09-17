package org.molgenis.mutation.dto;

import java.io.Serializable;

public class VariantDTO implements Serializable
{
	/* The serial version UID of this class. Needed for serialization. */
	private static final long serialVersionUID = -3840582528830408695L;

	private Integer id;
	private String identifier;
	private String cdnaNotation;
	private Integer cdnaStart;
	private Integer gdnaStart;
	private String aaNotation;
	private Integer aaStart;
	private Integer exonId;
	private String exonName;
	private String consequence;
	private String inheritance;
	private String observedValue;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
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
	public Integer getAaStart() {
		return aaStart;
	}
	public void setAaStart(Integer aaStart) {
		this.aaStart = aaStart;
	}
	public Integer getGdnaStart() {
		return gdnaStart;
	}
	public void setGdnaStart(Integer gdnaStart) {
		this.gdnaStart = gdnaStart;
	}
	public String getAaNotation() {
		return aaNotation;
	}
	public void setAaNotation(String aaNotation) {
		this.aaNotation = aaNotation;
	}
	public Integer getExonId() {
		return exonId;
	}
	public void setExonId(Integer exonId) {
		this.exonId = exonId;
	}
	public String getExonName() {
		return exonName;
	}
	public void setExonName(String exonName) {
		this.exonName = exonName;
	}
	public String getConsequence() {
		return consequence;
	}
	public void setConsequence(String consequence) {
		this.consequence = consequence;
	}
	public String getInheritance() {
		return inheritance;
	}
	public void setInheritance(String inheritance) {
		this.inheritance = inheritance;
	}
	public String getObservedValue() {
		return observedValue;
	}
	public void setObservedValue(String observedValue) {
		this.observedValue = observedValue;
	}
}
