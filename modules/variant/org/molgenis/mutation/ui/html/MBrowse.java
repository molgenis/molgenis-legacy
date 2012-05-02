package org.molgenis.mutation.ui.html;

import java.io.Serializable;
import java.util.List;

import org.molgenis.mutation.dto.ExonDTO;
import org.molgenis.mutation.dto.GeneDTO;
import org.molgenis.mutation.dto.MutationSummaryDTO;
import org.molgenis.mutation.dto.ProteinDomainDTO;

public class MBrowse implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String target;
	private List<ProteinDomainDTO> proteinDomainDTOList;
	private ProteinDomainDTO proteinDomainDTO;
	private List<MutationSummaryDTO> mutationSummaryDTOList;
	private String mutationPager;
	private GeneDTO geneDTO;
	private ExonDTO exonDTO;
	private List<ExonDTO> exonDTOList;
	private Boolean showNames;

	public MBrowse()
	{
		this.showNames = true;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public List<ProteinDomainDTO> getProteinDomainDTOList() {
		return proteinDomainDTOList;
	}

	public void setProteinDomainDTOList(List<ProteinDomainDTO> proteinDomainDTOList) {
		this.proteinDomainDTOList = proteinDomainDTOList;
	}

	public ProteinDomainDTO getProteinDomainDTO() {
		return proteinDomainDTO;
	}

	public void setProteinDomainDTO(ProteinDomainDTO proteinDomainDTO) {
		this.proteinDomainDTO = proteinDomainDTO;
	}

	public List<MutationSummaryDTO> getMutationSummaryDTOList() {
		return mutationSummaryDTOList;
	}

	public void setMutationSummaryDTOList(
			List<MutationSummaryDTO> mutationSummaryDTOList) {
		this.mutationSummaryDTOList = mutationSummaryDTOList;
	}

	public String getMutationPager() {
		return mutationPager;
	}

	public void setMutationPager(String mutationPager) {
		this.mutationPager = mutationPager;
	}

	public GeneDTO getGeneDTO() {
		return geneDTO;
	}

	public void setGeneDTO(GeneDTO geneDTO) {
		this.geneDTO = geneDTO;
	}

	public ExonDTO getExonDTO() {
		return exonDTO;
	}

	public void setExonDTO(ExonDTO exonDTO) {
		this.exonDTO = exonDTO;
	}

	public List<ExonDTO> getExonDTOList() {
		return exonDTOList;
	}

	public void setExonDTOList(List<ExonDTO> exonDTOList) {
		this.exonDTOList = exonDTOList;
	}

	public Boolean getShowNames() {
		return showNames;
	}

	public void setShowNames(Boolean showNames) {
		this.showNames = showNames;
	}

	public GenePanel createGenePanel()
	{
		GenePanel genePanel = new GenePanel();
		genePanel.setShowNames(this.showNames);
		genePanel.setProteinDomainSummaryVOList(this.getProteinDomainDTOList());
		genePanel.setBaseUrl("molgenis.do?__target=" + this.getTarget() + "&select=" + this.getTarget() + "&__action=showProteinDomain&domain_id=&snpbool=1#exon");

		return genePanel;
	}

	public ProteinDomainPanel createProteinDomainPanel()
	{
		ProteinDomainPanel proteinDomainPanel = new ProteinDomainPanel();
		proteinDomainPanel.setProteinDomainDTO(this.getProteinDomainDTO());
		proteinDomainPanel.setBaseUrl("molgenis.do?__target=" + this.getTarget() + "&select=" + this.getTarget() + "&__action=showProteinDomain&domain_id=&snpbool=1#exon");

		return proteinDomainPanel;
	}

	public ExonIntronPanel createExonIntronPanel()
	{
		ExonIntronPanel exonIntronPanel = new ExonIntronPanel();
		exonIntronPanel.setExons(this.getExonDTOList());
		exonIntronPanel.setShowIntrons(true);
		exonIntronPanel.setBaseUrl("molgenis.do?__target=" + this.getTarget() + "&select=" + this.getTarget() + "&__action=showExon&exon_id=#results");

		return exonIntronPanel;
	}

	public SequencePanel createSequencePanel()
	{
		SequencePanel sequencePanel = new SequencePanel();
		sequencePanel.setExonDTO(this.getExonDTO());
		sequencePanel.setMutationSummaryVOs(this.getMutationSummaryDTOList());
		sequencePanel.setBaseUrl("molgenis.do?__target=" + this.getTarget() + "&select=" + this.getTarget() + "&__action=showMutation&mid=#results");

		return sequencePanel;
	}
}
