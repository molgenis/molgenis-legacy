package org.molgenis.mutation.vo;

import java.io.Serializable;
import java.util.List;

import org.molgenis.mutation.Exon;
import org.molgenis.mutation.ProteinDomain;

public class ProteinDomainSummaryVO implements Serializable
{
	private static final long serialVersionUID = -4365982338471188950L;
	private ProteinDomain proteinDomain;
	private List<Exon> exons;
	private List<Exon> allExons;

	public ProteinDomain getProteinDomain() {
		return proteinDomain;
	}
	public void setProteinDomain(ProteinDomain proteinDomain) {
		this.proteinDomain = proteinDomain;
	}
	public List<Exon> getExons() {
		return exons;
	}
	public void setExons(List<Exon> exons) {
		this.exons = exons;
	}
	public List<Exon> getAllExons() {
		return allExons;
	}
	public void setAllExons(List<Exon> allExons) {
		this.allExons = allExons;
	}
}
