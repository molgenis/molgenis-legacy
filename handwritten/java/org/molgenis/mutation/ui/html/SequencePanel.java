package org.molgenis.mutation.ui.html;

import java.util.Formatter;
import java.util.List;

import org.apache.commons.lang.text.StrBuilder;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.mutation.Exon;
import org.molgenis.mutation.vo.ExonSummaryVO;
import org.molgenis.mutation.vo.MutationSummaryVO;

/*
 * A panel that prints a genomic sequence based on exons and mutations
 */
public class SequencePanel extends HtmlInput
{
	private ExonSummaryVO exonSummaryVO;
	private List<MutationSummaryVO> mutationSummaryVOs;
	private String screenName = "bla";

	public SequencePanel()
	{
		this(null, null);
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

		result.append(exonSummaryVO.getNuclSequenceFlankLeft());
		result.append("<span class=\"seq\">");
		int startPos = exonSummaryVO.getExon().getGdna_Position();
		int endPos   = ("R".equals(exonSummaryVO.getOrientation()) ?
						startPos - exonSummaryVO.getExon().getLength() :
						startPos + exonSummaryVO.getExon().getLength());
		for (int i = startPos; i > endPos; i--)
		{
			boolean hasMutation = false;
			for (MutationSummaryVO mutationSummaryVO : mutationSummaryVOs)
			{
				String tooltip = mutationSummaryVO.getNiceNotation();
				if (mutationSummaryVO.getMutation().getGdna_Position() == i)
				{
					hasMutation = true;
					result.append("<span class=\"mut\">");
					result.append("<a class=\"mut\" href=\"molgenis.do?__target=" + screenName + "&select=" + screenName + "&__action=showMutation&mid=" + mutationSummaryVO.getMutation().getIdentifier() + "#results\" alt=\"" + tooltip + "\" title=\"" + tooltip + "\">");
					break;
				}
			}
			if ("R".equals(exonSummaryVO.getOrientation()))
				result.append(exonSummaryVO.getNuclSequence().substring(startPos - i, startPos - i + 1)); //${exonSummaryVO.nuclSequence?substring(exonSummaryVO.exon.getGdna_Position() - i, exonSummaryVO.exon.getGdna_Position() - i + 1)}
			else
				result.append(exonSummaryVO.getNuclSequence().substring(i - startPos, i - startPos + 1));

			if (hasMutation)
			{
				result.append("</a></span>");
			}
		}
		result.append("</span>");
		result.appendln(exonSummaryVO.getNuclSequenceFlankRight());

		if (!exonSummaryVO.getExon().getIsIntron())
		{
			// print aa sequence
			result.appendPadding(exonSummaryVO.getNuclSequenceFlankLeft().length(), ' ');
			result.append("<span class=\"seq\">");
			result.append(exonSummaryVO.getAaSequence());
			result.appendln("</span>");
			
			// print base numbers
			result.appendPadding(exonSummaryVO.getNuclSequenceFlankLeft().length(), ' ');
			result.append("<span>");
			result.append(this.printBaseNumbers(exonSummaryVO.getExon()));
			result.appendln("</span>");
		}

		result.appendln("</pre>\n");
		result.appendln("</div>\n");

		return result.toString();
	}
	
	public void setExonSummaryVO(ExonSummaryVO exonSummaryVO)
	{
		this.exonSummaryVO = exonSummaryVO;
	}

	public void setMutationSummaryVOs(List<MutationSummaryVO> mutationSummaryVOs)
	{
		this.mutationSummaryVOs = mutationSummaryVOs;
	}

	public void setScreenName(String screenName)
	{
		this.screenName = screenName;
	}

	private String printBaseNumbers(Exon exon)
	{
		//<#list exon.cdna_position..exon.cdna_position + exon.length as i>
		StringBuffer buf = new StringBuffer();
		
		for (int i = exon.getCdna_Position(); i < exon.getCdna_Position() + exon.getLength(); i++)
			if (i % 10 == 0)
				buf.append(new Formatter().format("%4s", i).toString());
			else if (i % 10 < 7) // (i % 10 > 3 || i < 4)
				buf.append(" "); //new Integer(i % 10).toString(); //" ";
			//else
				//return "";
		return buf.toString();
	}
}
