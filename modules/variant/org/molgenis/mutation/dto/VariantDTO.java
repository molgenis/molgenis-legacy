package org.molgenis.mutation.dto;

import java.io.Serializable;

public class VariantDTO implements Serializable
{
	/* The serial version UID of this class. Needed for serialization. */
	private static final long serialVersionUID = -3840582528830408695L;

	private Integer id;
	private String identifier;
	private String cdnaNotation;
	private Integer gdnaStart;
	private String aaNotation;
	private String pathogenicity;
	private Integer exonId;
	private String exonName;

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
	public String getPathogenicity() {
		return pathogenicity;
	}
	public void setPathogenicity(String pathogenicity) {
		this.pathogenicity = pathogenicity;
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
}
