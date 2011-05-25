package org.molgenis.mutation.ui.html;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.text.StrBuilder;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.mutation.Exon;
import org.molgenis.mutation.vo.ProteinDomainSummaryVO;

/*
 * A panel that prints clickable exon-intron boxes
 */
public class ExonIntronPanel extends HtmlInput
{
	private List<Exon> exons     = new ArrayList<Exon>();
	private boolean showNames    = true;
	private boolean showExons    = true;
	private boolean showIntrons  = true;
	private boolean showPosition = true;
	private String screenName    = "";

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

			for (Exon exon : exons)
			{
				result.appendln("<td id=\"exon" + exon.getId() + "\" width=\"" + exon.getLength() + "px\" align=\"center\">" + exon.getName() + "</td>");
			}
	
			result.appendln("</tr>");
		}
		
		// second row: boxes
		result.appendln("<tr>");
		
		for (Exon exon : exons)
		{
			String url   = "molgenis.do?__target=" + this.screenName + "&select=" + this.screenName + "&__action=showExon&exon_id=" + exon.getId() + "#results";
			String title = "Go to " + exon.getName();

			if (exon.getIsIntron())
			{
				if (this.showIntrons)
				{
					result.appendln("<td>");
					result.append("<a href=\"" + url + "\" alt=\"[]\" title=\"" + title + "\"><img src=\"res/img/col7a1/intron.png\" width=\"" + exon.getLength() + "px\" height=\"30px\"/></a>");
					result.appendln("</td>");
				}
			}
			else
			{
				if (this.showExons)
				{
					result.appendln("<td>");
					result.append("<div class=\"pd" + exon.getProteinDomain_Id().get(0) + "\" style=\"display: block; width: " + exon.getLength() + "px; height: 26px; border-width:2px; border-style:solid;\">");
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
			
			for (Exon exon : exons)
			{
				result.appendln("<td width=\"" + exon.getLength() + "px\" align=\"left\">" + (!exon.getIsIntron() ? "<span style=\"font-size:6pt;\">" + exon.getCdna_Position() + "</span>" : "") + "</td>");
			}
	
			result.appendln("</tr>");
		}

		result.appendln("</table>");
		result.appendln("</div>");

		return result.toString();
	}

	public void setExons(List<Exon> exons)
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

	public void setScreenName(String screenName)
	{
		this.screenName = screenName;
	}
}
