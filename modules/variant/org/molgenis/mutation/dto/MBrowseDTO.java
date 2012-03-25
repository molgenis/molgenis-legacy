package org.molgenis.mutation.dto;

import java.io.Serializable;
import java.util.List;

import org.molgenis.mutation.ui.html.ExonIntronPanel;
import org.molgenis.mutation.ui.html.GenePanel;
import org.molgenis.mutation.ui.html.ProteinDomainPanel;
import org.molgenis.mutation.ui.html.SequencePanel;

public class MBrowseDTO implements Serializable
{
	private static final long serialVersionUID = -8585452283787495693L;
	List<ProteinDomainDTO> proteinDomainList;
	ProteinDomainDTO proteinDomainSummaryVO;
	List<ExonDTO> exonList;
	ExonDTO exonSummaryVO;
	GenePanel genePanel                   = new GenePanel();
	ProteinDomainPanel proteinDomainPanel = new ProteinDomainPanel();
	ExonIntronPanel exonIntronPanel       = new ExonIntronPanel();
	SequencePanel sequencePanel           = new SequencePanel();

	public List<ProteinDomainDTO> getProteinDomainList() {
		return proteinDomainList;
	}
	public void setProteinDomainList(List<ProteinDomainDTO> proteinDomainList) {
		this.proteinDomainList = proteinDomainList;
	}
	public ProteinDomainDTO getProteinDomainSummaryVO() {
		return proteinDomainSummaryVO;
	}
	public void setProteinDomainSummaryVO(
			ProteinDomainDTO proteinDomainSummaryVO) {
		this.proteinDomainSummaryVO = proteinDomainSummaryVO;
	}
	public List<ExonDTO> getExonList() {
		return exonList;
	}
	public void setExonList(List<ExonDTO> exonList) {
		this.exonList = exonList;
	}
	public ExonDTO getExonSummaryVO() {
		return exonSummaryVO;
	}
	public void setExonSummaryVO(ExonDTO exonSummaryVO) {
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
