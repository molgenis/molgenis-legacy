package org.molgenis.mutation.vo;

import java.io.Serializable;
import java.util.List;

import org.molgenis.mutation.ui.html.ExonIntronPanel;
import org.molgenis.mutation.ui.html.GenePanel;
import org.molgenis.mutation.ui.html.ProteinDomainPanel;
import org.molgenis.mutation.ui.html.SequencePanel;

public class MBrowseVO implements Serializable
{
	private static final long serialVersionUID = -8585452283787495693L;
	List<ProteinDomainSummaryVO> proteinDomainList;
	ProteinDomainSummaryVO proteinDomainSummaryVO;
	List<ExonSummaryVO> exonList;
	ExonSummaryVO exonSummaryVO;
	GenePanel genePanel                   = new GenePanel();
	ProteinDomainPanel proteinDomainPanel = new ProteinDomainPanel();
	ExonIntronPanel exonIntronPanel       = new ExonIntronPanel();
	SequencePanel sequencePanel           = new SequencePanel();

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
	public List<ExonSummaryVO> getExonList() {
		return exonList;
	}
	public void setExonList(List<ExonSummaryVO> exonList) {
		this.exonList = exonList;
	}
	public ExonSummaryVO getExonSummaryVO() {
		return exonSummaryVO;
	}
	public void setExonSummaryVO(ExonSummaryVO exonSummaryVO) {
		this.exonSummaryVO = exonSummaryVO;
	}
	public GenePanel getGenePanel() {
		return genePanel;
	}
	public void setGenePanel(GenePanel genePanel) {
		this.genePanel = genePanel;
	}
	public ProteinDomainPanel getProteinDomainPanel() {
		return proteinDomainPanel;
	}
	public void setProteinDomainPanel(ProteinDomainPanel proteinDomainPanel) {
		this.proteinDomainPanel = proteinDomainPanel;
	}
	public ExonIntronPanel getExonIntronPanel() {
		return exonIntronPanel;
	}
	public void setExonIntronPanel(ExonIntronPanel exonIntronPanel) {
		this.exonIntronPanel = exonIntronPanel;
	}
	public SequencePanel getSequencePanel() {
		return sequencePanel;
	}
	public void setSequencePanel(SequencePanel sequencePanel) {
		this.sequencePanel = sequencePanel;
	}
}
