package org.molgenis.mutation.ui.html;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrBuilder;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.mutation.Exon;
import org.molgenis.mutation.vo.ProteinDomainSummaryVO;

/*
 * A panel that prints a protein domain with clickable exons inside
 */
public class ProteinDomainPanel extends HtmlInput
{
	private ProteinDomainSummaryVO proteinDomainSummaryVO;
	private String baseUrl = "";

	public ProteinDomainPanel()
	{
		this("", "");
	}
	
	public ProteinDomainPanel(String name, String label)
	{
		super(name, label);
		this.setLabel(label);
	}
	
	@Override
	public String toHtml()
	{
		StrBuilder result = new StrBuilder();

		List<Exon> exons  = this.proteinDomainSummaryVO.getExons();

		result.appendln("<div style=\"overflow: hidden; margin-right: 10px; padding-top: 10px; padding-bottom: 10px;\">");
		result.appendln("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
		result.appendln("<tr>");
		result.appendln("<td align=\"center\" valign=\"bottom\" colspan=\"" + exons.size() + "\">" +  this.proteinDomainSummaryVO.getProteinDomain().getName() + "</td>");
		result.appendln("</tr>");
		result.appendln("<tr>");
		for (Exon exon : exons)
		{
			int width = exon.getLength() / 10;
			result.appendln("<td>");
			result.appendln("<div class=\"pd" + this.proteinDomainSummaryVO.getProteinDomain().getId() + "\" style=\"display: block; width: " + width + "px; height: 26px;\">");
			String url = this.baseUrl;
			url = StringUtils.replace(url, "domain_id=", "domain_id=" + proteinDomainSummaryVO.getProteinDomain().getId());
			url = StringUtils.replace(url, "#exon", "#exon" + exon.getId());
			result.appendln("<a class=\"clickable_block\" href=\"" + url + "\" alt=\"" + exon.getName() + "\" title=\"" + exon.getName() + "\"></a>");
			result.appendln("</div>");
			result.appendln("</td>");
		}
		result.appendln("</tr>");
		result.appendln("</table>");
		result.appendln("</div>");

		return result.toString();
	}

	public void setProteinDomainSummaryVO(ProteinDomainSummaryVO proteinDomainSummaryVO)
	{
		this.proteinDomainSummaryVO = proteinDomainSummaryVO;
	}

	public void setBaseUrl(String baseUrl)
	{
		this.baseUrl = baseUrl;
	}

}
