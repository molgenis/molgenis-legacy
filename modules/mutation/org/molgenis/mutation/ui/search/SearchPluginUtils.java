package org.molgenis.mutation.ui.search;

import java.util.Formatter;

import org.molgenis.mutation.Exon;
import org.molgenis.mutation.Mutation;

public class SearchPluginUtils
{
	/**
	 * Print the base numbers in <pre> mode
	 * @param exon
	 * @return String with formatted numbers every ten bases
	 */
	public String printBaseNumbers(Exon exon)
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

	/**
	 * Print mutation mark ('|') in <pre> mode
	 * @param exon
	 * @param mutation
	 * @return String with mutation mark '|' formatted at correct position
	 */
	public String printMutationMark(Exon exon, Mutation mutation)
	{
		//<#list exon.cdna_position..mutation.position - 1 as i> </#list>|
		StringBuffer buf = new StringBuffer();

		for (int i = exon.getCdna_Position(); i < mutation.getCdna_Position(); i++)
		//for (int i = exon.getGdna_position(); i > mutation.getPosition(); i--)
			buf.append(" ");
		
		buf.append("|");
		
		return buf.toString();
	}
	
//	public Boolean isMutationPosition(Exon exon, Mutation mutation, Integer position)
//	{
//		//Integer mutationPos = Math.abs(exon.getGdna_position() - mutation.getPosition()) + exon.getCdna_position();
//		Integer mutationPos = Math.abs(exon.getGdna_position() - mutation.getGdna_position());
//		return mutationPos.equals(position);
//	}
//	
//	public Integer bla(Exon exon, Mutation mutation, Integer position)
//	{
//		return 0; //Math.abs(exon.getGdna_position() - mutation.getPosition()) + exon.getCdna_position();
//	}
}
