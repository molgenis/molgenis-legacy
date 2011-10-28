package org.molgenis.mutation.ui.html;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrBuilder;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.mutation.Exon;
import org.molgenis.mutation.vo.ProteinDomainSummaryVO;

/*
 * A panel that prints clickable exon-intron boxes
 */
public class GenePanel extends HtmlInput
{
	private List<ProteinDomainSummaryVO> proteinDomainSummaryVOList = new ArrayList<ProteinDomainSummaryVO>();
	private String baseUrl                                          = "";

	@Override
	public String toHtml()
	{
		StrBuilder result = new StrBuilder();

		result.appendln("<div class=\"scrollable\">");
		result.appendln("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\"  width=\"1%\">");
		result.appendln("<tr>");

		// first row: names
		for (ProteinDomainSummaryVO proteinDomainSummaryVO : proteinDomainSummaryVOList)
		{
			List<Exon> exons = proteinDomainSummaryVO.getExons();

			if (exons.size() > 0)
			{
				result.appendln("<td align=\"center\" valign=\"bottom\" colspan=\"" + exons.size() + "\" width=\"1%\">" + proteinDomainSummaryVO.getProteinDomain().getName() + " (exon " + exons.get(0).getNumber() + " - " + exons.get(exons.size() - 1).getNumber() + ")</td>");
			}
		}

		result.appendln("</tr>");
		result.appendln("<tr>");

		// second row: boxes
		for (ProteinDomainSummaryVO proteinDomainSummaryVO : proteinDomainSummaryVOList)
		{
			for (Exon exon : proteinDomainSummaryVO.getExons())
			{
				result.append("<td align=\"left\">");
				result.append("<div class=\"pd" + proteinDomainSummaryVO.getProteinDomain().getId() + "\" style=\"display: block; width: " + exon.getLength() / 10 + "px; height: 26px;\">");
				String url = this.baseUrl;
				url = StringUtils.replace(url, "domain_id=", "domain_id=" + proteinDomainSummaryVO.getProteinDomain().getId());
				url = StringUtils.replace(url, "#exon", "#exon" + exon.getId());
				result.append("<a style=\"display: block; height: 100%; width: 100%;\" href=\"" + url + "\" alt=\"" + exon.getName() + "\" title=\"" + exon.getName() + "\"></a>");
				result.append("</div>");
				result.appendln("</td>");
			}
		}

		result.appendln("</tr>");
		result.appendln("</table>");
		result.appendln("</div>");

		return result.toString();
	}

	public List<ProteinDomainSummaryVO> getProteinDomainSummaryVOList() {
		return proteinDomainSummaryVOList;
	}

	public void setProteinDomainSummaryVOList(
			List<ProteinDomainSummaryVO> proteinDomainSummaryVOList) {
		this.proteinDomainSummaryVOList = proteinDomainSummaryVOList;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}


}
