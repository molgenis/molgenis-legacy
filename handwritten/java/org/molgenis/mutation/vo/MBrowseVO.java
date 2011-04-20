package org.molgenis.mutation.vo;

import java.io.Serializable;
import java.util.List;

import org.molgenis.mutation.Exon;

public class MBrowseVO implements Serializable
{
	private static final long serialVersionUID = -8585452283787495693L;
	List<ProteinDomainSummaryVO> proteinDomainList;
	ProteinDomainSummaryVO proteinDomainSummaryVO;
	List<Exon> exonList;
	ExonSummaryVO exonSummaryVO;

	public List<ProteinDomainSummaryVO> getProteinDomainList() {
		return proteinDomainList;
	}
	public void setProteinDomainList(List<ProteinDomainSummaryVO> proteinDomainList) {
		this.proteinDomainList = proteinDomainList;
	}
	public ProteinDomainSummaryVO getProteinDomainSummaryVO() {
		return proteinDomainSummaryVO;
	}
	public void setProteinDomainSummaryVO(
			ProteinDomainSummaryVO proteinDomainSummaryVO) {
		this.proteinDomainSummaryVO = proteinDomainSummaryVO;
	}
	public List<Exon> getExonList() {
		return exonList;
	}
	public void setExonList(List<Exon> exonList) {
		this.exonList = exonList;
	}
	public ExonSummaryVO getExonSummaryVO() {
		return exonSummaryVO;
	}
	public void setExonSummaryVO(ExonSummaryVO exonSummaryVO) {
		this.exonSummaryVO = exonSummaryVO;
	}
}
