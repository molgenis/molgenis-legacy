package org.molgenis.mutation.ui.html;

import java.io.Serializable;
import java.util.Formatter;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrBuilder;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.mutation.dto.ExonDTO;
import org.molgenis.mutation.dto.MutationSummaryDTO;

/*
 * A panel that prints a genomic sequence based on exons and mutations
 */
public class SequencePanel extends HtmlInput implements Serializable
{
	private static final long serialVersionUID = 715300797597319262L;
	private ExonDTO exonDTO;
	private List<MutationSummaryDTO> mutationSummaryVOs;
	boolean showNuclSequence  = true;
	boolean showAaSequence    = true;
	boolean showBasePositions = true;
	private String baseUrl    = "";

	public SequencePanel()
	{
		this("", "");
	}
	
	public SequencePanel(String name, String label)
	{
		super(name, label);
		this.setLabel(label);
		this.setClazz("scrollable");
	}
	
	@Override
	/**
	 * Each input is rendered with a label and in its own div to enable scripting.
	 */
	public String toHtml()
	{
		StrBuilder result = new StrBuilder();

		result.appendln("<div class=\"" + this.getClazz() + "\">");
		result.appendln("<pre>");

		if (this.showNuclSequence)
		{
			result.append(this.exonDTO.getNuclSequenceFlankLeft());
			result.append("<span class=\"seq\">");
			int startPos = this.exonDTO.getGdnaStart();
			int endPos   = this.exonDTO.getGdnaEnd();
			for (int i = startPos;
					("R".equals(this.exonDTO.getOrientation()) ? i > endPos : i < endPos);
					i = ("R".equals(this.exonDTO.getOrientation()) ? i - 1 : i + 1))
			{
//				System.out.println(">>> SequencePanel: seqlen==" + exonDTO.getNuclSequence().length() + ", startPos==" + startPos + ", i==" + i);
				boolean hasMutation = false;
				for (MutationSummaryDTO mutationSummaryVO : mutationSummaryVOs)
				{
//					System.out.println(">>> SequencePanel: m.cdna==" + mutationSummaryVO.getCdnaNotation() + ", m.gdna=" + mutationSummaryVO.getGdnaNotation());
					String url     = this.baseUrl;
					url = StringUtils.replace(url, "mid=", "mid=" + mutationSummaryVO.getIdentifier());
					String tooltip = mutationSummaryVO.getNiceNotation();
					if (mutationSummaryVO.getGdnaStart() == i)
					{
						hasMutation = true;
						result.append("<span class=\"mut\">");
						result.append("<a class=\"mut\" href=\"" + url + "\" alt=\"" + tooltip + "\" title=\"" + tooltip + "\">");
						break;
					}
				}
				if ("R".equals(this.exonDTO.getOrientation()))
					result.append(this.exonDTO.getNuclSequence().substring(startPos - i, startPos - i + 1)); //${exonSummaryVO.nuclSequence?substring(exonSummaryVO.exon.getGdna_Position() - i, exonSummaryVO.exon.getGdna_Position() - i + 1)}
				else
					result.append(this.exonDTO.getNuclSequence().substring(i - startPos, i - startPos + 1));
	
				if (hasMutation)
				{
					result.append("</a></span>");
				}
			}
			result.append("</span>");
			result.appendln(this.exonDTO.getNuclSequenceFlankRight());
		}

		if (!this.exonDTO.getIsIntron())
		{
			// print aa sequence
			if (this.showAaSequence)
			{
				result.appendPadding(this.exonDTO.getNuclSequenceFlankLeft().length(), ' ');
				result.append("<span class=\"seq\">");
				result.append(this.exonDTO.getAaSequence());
				result.appendln("</span>");
			}

			// print base numbers
			if (this.showBasePositions)
			{
				result.appendPadding(this.exonDTO.getNuclSequenceFlankLeft().length(), ' ');
				result.append("<span>");
				result.append(this.printBasePositions(this.exonDTO));
				result.appendln("</span>");
			}
		}

		result.appendln("</pre>\n");
		result.appendln("</div>\n");

		return result.toString();
	}
	
	public void setExonDTO(ExonDTO exonDTO)
	{
		this.exonDTO = exonDTO;
	}

	public void setMutationSummaryVOs(List<MutationSummaryDTO> mutationSummaryVOs)
	{
		this.mutationSummaryVOs = mutationSummaryVOs;
	}

	public void setShowNuclSequence(boolean showNuclSequence)
	{
		this.showNuclSequence = showNuclSequence;
	}

	public void setShowAaSequence(boolean showAaSequence)
	{
		this.showAaSequence = showAaSequence;
	}

	public void setShowBasePositions(boolean showBasePositions)
	{
		this.showBasePositions = showBasePositions;
	}

	public void setBaseUrl(String baseUrl)
	{
		this.baseUrl = baseUrl;
	}

	private String printBasePositions(ExonDTO exonSummaryVO)
	{
		//<#list exon.cdna_position..exon.cdna_position + exon.length as i>
		StringBuffer buf = new StringBuffer();
		
		for (int i = exonSummaryVO.getCdnaStart(); i < exonSummaryVO.getCdnaEnd(); i++)
			if (i % 10 == 0)
				buf.append(new Formatter().format("%4s", i).toString());
			else if (i % 10 < 7) // (i % 10 > 3 || i < 4)
				buf.append(" "); //new Integer(i % 10).toString(); //" ";
			//else
				//return "";
		return buf.toString();
	}
}
