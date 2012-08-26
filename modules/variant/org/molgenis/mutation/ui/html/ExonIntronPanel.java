package org.molgenis.mutation.ui.html;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrBuilder;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.mutation.dto.ExonDTO;

/*
 * A panel that prints clickable exon-intron boxes
 */
public class ExonIntronPanel extends HtmlInput implements Serializable
{
	/* The serial version UID of this class. Needed for serialization. */
	private static final long serialVersionUID = 929100596754279741L;

	//TODO: make SCALE_FACTOR customizable
//	private final double SCALE_FACTOR          = 0.1;
	private final double SCALE_FACTOR          = 1;
	private List<ExonDTO> exonDTOList          = new ArrayList<ExonDTO>();
	private boolean showNames                  = true;
	private boolean showExons                  = true;
	private boolean showIntrons                = true;
	private boolean showPosition               = true;
	private String baseUrl                     = "";

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

			for (ExonDTO exonDTO : exonDTOList)
			{
				result.appendln("<td id=\"exon" + exonDTO.getId() + "\" width=\"" + exonDTO.getLength() * SCALE_FACTOR + "px\" align=\"center\">" + exonDTO.getName() + "</td>");
			}
	
			result.appendln("</tr>");
		}
		
		// second row: boxes
		result.appendln("<tr>");
		
		for (ExonDTO exonDTO : exonDTOList)
		{
			String url   = this.baseUrl;
			url = StringUtils.replace(url, "exon_id=", "exon_id=" + exonDTO.getId());
			String title = "Go to " + exonDTO.getName();

			if (exonDTO.getIsIntron())
			{
				if (this.showIntrons)
				{
					result.appendln("<td>");
					result.append("<a href=\"" + url + "\" alt=\"[]\" title=\"" + title + "\"><img src=\"res/img/col7a1/intron.png\" width=\"" + exonDTO.getLength() * SCALE_FACTOR + "px\" height=\"30px\"/></a>");
					result.appendln("</td>");
				}
			}
			else
			{
				if (this.showExons)
				{
					result.appendln("<td>");
					result.append("<div class=\"pd" + exonDTO.getDomainId().get(0).intValue() + "\" style=\"display: block; width: " + exonDTO.getLength() * SCALE_FACTOR + "px; height: 26px; border-width:2px; border-style:solid;\">");
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
			
			for (ExonDTO exonSummaryVO : exonDTOList)
			{
				result.appendln("<td width=\"" + exonSummaryVO.getLength() * SCALE_FACTOR + "px\" align=\"left\">" + (!exonSummaryVO.getIsIntron() ? "<span style=\"font-size:6pt;\">" + exonSummaryVO.getCdnaStart() + "</span>" : "") + "</td>");
			}
	
			result.appendln("</tr>");
		}

		result.appendln("</table>");
		result.appendln("</div>");

		return result.toString();
	}

	public void setExons(List<ExonDTO> exons)
	{
		this.exonDTOList = exons;
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
