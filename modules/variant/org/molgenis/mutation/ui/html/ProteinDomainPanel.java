package org.molgenis.mutation.ui.html;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrBuilder;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.mutation.dto.ExonDTO;
import org.molgenis.mutation.dto.ProteinDomainDTO;

/*
 * A panel that prints a protein domain with clickable exons inside
 */
public class ProteinDomainPanel extends HtmlInput implements Serializable
{
	/* The serial version UID of this class. Needed for serialization. */
	private static final long serialVersionUID = -3632595043556432831L;

	//TODO: make SCALE_FACTOR customizable
//	private final double SCALE_FACTOR          = 0.003;
	private final double SCALE_FACTOR          = 0.1;
	private ProteinDomainDTO proteinDomainDTO;
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

		List<ExonDTO> exonDTOs = this.proteinDomainDTO.getExonDTOList();

		result.appendln("<div style=\"overflow: hidden; margin-right: 10px; padding-top: 10px; padding-bottom: 10px;\">");
		result.appendln("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
		result.appendln("<tr>");
		result.appendln("<td align=\"center\" valign=\"bottom\" colspan=\"" + exonDTOs.size() + "\">" +  this.proteinDomainDTO.getDomainName() + "</td>");
		result.appendln("</tr>");
		result.appendln("<tr>");
		for (ExonDTO exonDTO : exonDTOs)
		{
			int width = new Double(exonDTO.getLength() * this.SCALE_FACTOR).intValue();
			result.appendln("<td>");
			result.appendln("<div class=\"pd" + this.proteinDomainDTO.getDomainId() + "\" style=\"display: block; width: " + width + "px; height: 26px;\">");
			String url = this.baseUrl;
			url = StringUtils.replace(url, "domain_id=", "domain_id=" + proteinDomainDTO.getDomainId());
			url = StringUtils.replace(url, "#exon", "#exon" + exonDTO.getId());
			result.appendln("<a class=\"clickable_block\" href=\"" + url + "\" alt=\"" + exonDTO.getName() + "\" title=\"" + exonDTO.getName() + "\"></a>");
			result.appendln("</div>");
			result.appendln("</td>");
		}
		result.appendln("</tr>");
		result.appendln("</table>");
		result.appendln("</div>");

		return result.toString();
	}

	public void setProteinDomainDTO(ProteinDomainDTO proteinDomainDTO)
	{
		this.proteinDomainDTO = proteinDomainDTO;
	}

	public void setBaseUrl(String baseUrl)
	{
		this.baseUrl = baseUrl;
	}

}
