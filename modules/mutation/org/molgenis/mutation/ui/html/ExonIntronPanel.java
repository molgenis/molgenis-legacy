package org.molgenis.mutation.ui.html;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrBuilder;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.mutation.vo.ExonSummaryVO;

/*
 * A panel that prints clickable exon-intron boxes
 */
public class ExonIntronPanel extends HtmlInput
{
	private List<ExonSummaryVO> exons = new ArrayList<ExonSummaryVO>();
	private boolean showNames         = true;
	private boolean showExons         = true;
	private boolean showIntrons       = true;
	private boolean showPosition      = true;
	private String baseUrl            = "";

	@Override
	public String toHtml()
	{
		StrBuilder result = new StrBuilder();

		result.appendln("<div class=\"scrollable\">");
		result.appendln("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");

		// first row: names
		if (this.showNames)
		{
			result.appendln("<tr>");

			for (ExonSummaryVO exonSummaryVO : exons)
			{
				result.appendln("<td id=\"exon" + exonSummaryVO.getId() + "\" width=\"" + exonSummaryVO.getLength() + "px\" align=\"center\">" + exonSummaryVO.getName() + "</td>");
			}
	
			result.appendln("</tr>");
		}
		
		// second row: boxes
		result.appendln("<tr>");
		
		for (ExonSummaryVO exonSummaryVO : exons)
		{
			String url     = this.baseUrl;
			url = StringUtils.replace(url, "exon_id=", "exon_id=" + exonSummaryVO.getId());
			String title = "Go to " + exonSummaryVO.getName();

			if (exonSummaryVO.getIsIntron())
			{
				if (this.showIntrons)
				{
					result.appendln("<td>");
					result.append("<a href=\"" + url + "\" alt=\"[]\" title=\"" + title + "\"><img src=\"res/img/col7a1/intron.png\" width=\"" + exonSummaryVO.getLength() + "px\" height=\"30px\"/></a>");
					result.appendln("</td>");
				}
			}
			else
			{
				if (this.showExons)
				{
					result.appendln("<td>");
					result.append("<div class=\"pd" + exonSummaryVO.getDomainId() + "\" style=\"display: block; width: " + exonSummaryVO.getLength() + "px; height: 26px; border-width:2px; border-style:solid;\">");
					result.append("<a class=\"clickable_block\" href=\"" + url + "\" alt=\"[]\" title=\"" + title + "\"></a>");
					result.append("</div>");
					result.appendln("</td>");
				}
			}
		}

		result.appendln("</tr>");
		
		// third row: positions
		if (this.showPosition)
		{
			result.appendln("<tr>");
			
			for (ExonSummaryVO exonSummaryVO : exons)
			{
				result.appendln("<td width=\"" + exonSummaryVO.getLength() + "px\" align=\"left\">" + (!exonSummaryVO.getIsIntron() ? "<span style=\"font-size:6pt;\">" + exonSummaryVO.getCdnaPosition() + "</span>" : "") + "</td>");
			}
	
			result.appendln("</tr>");
		}

		result.appendln("</table>");
		result.appendln("</div>");

		return result.toString();
	}

	public void setExons(List<ExonSummaryVO> exons)
	{
		this.exons = exons;
	}

	public void setShowNames(boolean showNames) {
		this.showNames = showNames;
	}

	public void setShowExons(boolean showExons) {
		this.showExons = showExons;
	}

	public void setShowIntrons(boolean showIntrons)
	{
		this.showIntrons = showIntrons;
	}

	public void setShowPosition(boolean showPosition) {
		this.showPosition = showPosition;
	}

	public void setBaseUrl(String baseUrl)
	{
		this.baseUrl = baseUrl;
	}
}
