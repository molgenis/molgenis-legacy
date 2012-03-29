package org.molgenis.mutation.util;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;
import org.molgenis.mutation.Exon;

public class SequenceUtils
{
	private static final transient Logger logger = Logger.getLogger(SequenceUtils.class.getSimpleName());
	public static enum AminoAcid
	{
		GCT ("A", "Ala"),
		GCC ("A", "Ala"),
		GCA ("A", "Ala"),
		GCG ("A", "Ala"),
		CGT ("R", "Arg"),
		CGC ("R", "Arg"),
		CGA ("R", "Arg"),
		CGG ("R", "Arg"),
		AGA ("R", "Arg"),
		AGG ("R", "Arg"),
		AAT ("N", "Asn"),
		AAC ("N", "Asn"),
		GAT ("D", "Asp"),
		GAC ("D", "Asp"),
		TGT ("C", "Cys"),
		TGC ("C", "Cys"),
		CAA ("Q", "Gln"),
		CAG ("Q", "Gln"),
		GAA ("E", "Glu"),
		GAG ("E", "Glu"),
		GGT ("G", "Gly"),
		GGC ("G", "Gly"),
		GGA ("G", "Gly"),
		GGG ("G", "Gly"),
		CAT ("H", "His"),
		CAC ("H", "His"),
		ATT ("I", "Ile"),
		ATC ("I", "Ile"),
		ATA ("I", "Ile"),
		TTA ("L", "Leu"),
		TTG ("L", "Leu"),
		CTT ("L", "Leu"),
		CTC ("L", "Leu"),
		CTA ("L", "Leu"),
		CTG ("L", "Leu"),
		AAA ("K", "Lys"),
		AAG ("K", "Lys"),
		ATG ("M", "Met"),
		TTT ("F", "Phe"),
		TTC ("F", "Phe"),
		CCT ("P", "Pro"),
		CCC ("P", "Pro"),
		CCA ("P", "Pro"),
		CCG ("P", "Pro"),
		TCT ("S", "Ser"),
		TCC ("S", "Ser"),
		TCA ("S", "Ser"),
		TCG ("S", "Ser"),
		AGT ("S", "Ser"),
		AGC ("S", "Ser"),
		TAA ("X", "Ter"),
		TAG ("X", "Ter"),
		TGA ("X", "Ter"),
		ACT ("T", "Thr"),
		ACC ("T", "Thr"),
		ACA ("T", "Thr"),
		ACG ("T", "Thr"),
		TGG ("W", "Trp"),
		TAT ("Y", "Tyr"),
		TAC ("Y", "Tyr"),
		GTT ("V", "Val"),
		GTC ("V", "Val"),
		GTA ("V", "Val"),
		GTG ("V", "Val");

		private final String code_1;
		private final String code_3;

		AminoAcid(String code_1, String code_3)
		{
			this.code_1 = code_1;
			this.code_3 = code_3;
		}

		public String getCode_1()
		{
			return this.code_1;
		}

		public String getCode_3()
		{
			return this.code_3;
		}
	}

	public static String getAminoAcid1(String codon)
	{
		for (AminoAcid aminoAcid : AminoAcid.values())
			if (aminoAcid.name().equals(codon))
				return aminoAcid.getCode_1();
		return "?";
	}
	
	public static String getAminoAcid3(String codon)
	{
		for (AminoAcid aminoAcid : AminoAcid.values())
			if (aminoAcid.name().equals(codon))
				return aminoAcid.getCode_3();
		return "???";
	}

	public static String toAminoAcid3(String code1)
	{
		for (AminoAcid aminoAcid : AminoAcid.values())
			if (aminoAcid.getCode_1().equals(code1))
				return aminoAcid.getCode_3();
		return "???";
	}

	/**
	 * Get the number of amino acids in given sequence, excluding the ones only partially present
	 * @param sequence
	 * @return Number of amino acids
	 * @throws Exception
	 */
	public static int getNumFullAminoAcids(String sequence)
	{
		String[] tokens = StringUtils.splitByCharacterTypeCamelCase(sequence);
		
		if (tokens.length < 1)
			return 0;

		int numFullAas  = tokens.length;
		if (tokens[0].length() < 3)
			numFullAas--;
		if (tokens[numFullAas - 1].length() < 3)
			numFullAas--;
		
		return numFullAas;
	}
	
	/**
	 * Get the number of amino acids in given sequence, including the ones only partially present
	 * @param sequence
	 * @return Number of amino acids
	 * @throws Exception
	 */
	public static Integer getNumPartAminoAcids(String sequence)
	{
		String[] tokens = StringUtils.splitByCharacterTypeCamelCase(sequence);
		
		return tokens.length;
	}
	
	/**
	 * Get the number of Gly-X-Y repeats, i.e. the number of GlyXxxYyy repeats.
	 * @param sequence
	 * @return number of Gly-X-Y repeats
	 */
	public static Integer getNumGlyXYRepeats(String sequence)
	{
		int numGly        = 0; // counter for total number of Gly in sequence

		for (int i = 0; i < sequence.length(); i++)
		{
			
			int localNumGly = 0;
			int glyPos      = StringUtils.indexOf(sequence, "Gly", i);
			while (glyPos + 8 < sequence.length())
			{
				localNumGly++;
				glyPos = glyPos + 9;
				if (!StringUtils.substring(sequence, glyPos, glyPos + 3).equals("Gly"))
					break;
			}
			if (localNumGly > numGly)
				numGly = localNumGly;
		}

		return numGly;
	}
	
	/**
	 * Splice sequence, i.e. remove intronic part which has to be in lowercase letters
	 * @param sequence
	 * @return spliced sequence
	 */
	public static String splice(String sequence)
	{
		String sequence2 = sequence;
		sequence2 = StringUtils.replace(sequence2, "a", "");
		sequence2 = StringUtils.replace(sequence2, "c", "");
		sequence2 = StringUtils.replace(sequence2, "g", "");
		sequence2 = StringUtils.replace(sequence2, "t", "");
		//TODO: Hardcoded removal of UTR of exon 1
		sequence2 = StringUtils.substring(sequence2, 108);
		
		return sequence2;
	}

	/**
	 * Get the amino acid sequence (translate) for a spliced nucleotide sequence
	 * @param nuclSequence
	 * @return amino acid sequence
	 */
	public static String translate(String nuclSequence)
	{
		String aaSequence = "";
		for (int i = 0; i < nuclSequence.length() - 1; i = i + 3)
		{
			String codon  = StringUtils.substring(nuclSequence, i, i + 3);
			aaSequence    = aaSequence + SequenceUtils.getAminoAcid3(codon);
		}
		return aaSequence;
	}
	
	/**
	 * Get index of first occurence of codon after given startCodonNum
	 * @param sequence
	 * @param startCodonNum
	 * @return codon number of first occurence, -1 if no match or null sequence input
	 */
	public static int indexOfCodon(String sequence, String codon, int startCodonNum)
	{
		if (sequence == null || codon == null)
			return -1;

		int codonPos = StringUtils.indexOf(sequence, codon, startCodonNum * 3) + 1; // NT pos starts at 1 not 0
		return SequenceUtils.getCodonNum(codonPos);
	}
	
	/**
	 * Get the nucleotide at the given position starting at 1
	 * @param sequence
	 * @param indexOf
	 * @return nucleotide
	 */
	public static String getNucleotide(String sequence, int indexOf)
	{
		return StringUtils.substring(sequence, indexOf - 1, indexOf);
	}
	
	/**
	 * Get number of codon at given position
	 * @param position
	 * @return codon number
	 */
	public static int getCodonNum(int position)
	{
		return new Double(Math.ceil(position / 3.0)).intValue();
	}

	/**
	 * Get the codon with the given number starting at 1
	 * @param sequence
	 * @param codonNum
	 * @return codon
	 */
	public static String getCodon(String sequence, int codonNum)
	{
		int start = codonNum * 3 - 3;
		int end   = start + 3; //2;
		logger.debug("getCodon: start==" + start + ", end==" + end);
		return StringUtils.substring(sequence, start, end);
		/*
		for (int i = 0; i < sequence.length(); i = i + 3)
			if ((i + 3) / 3 == codonNum)
				return sequence.substring(i, i + 3);
		return "";
		*/
	}
	
	/**
	 * Get the codon at the given position starting at 0
	 * @param sequence
	 * @param position
	 * @return codon
	 */
	public static String getCodonByPosition(String sequence, int position)
	{
		if (sequence == null)
			return "";

		return StringUtils.substring(sequence, position, position + 3);
	}

	/**
	 * Get the cDNA position. In case of an intronic position remove additive part (e.g. 1234+5 -> 1234)
	 * @param position
	 * @return cDNA position
	 * @throws RESyntaxException
	 */
	public static Integer getCDNAPosition(String position) throws RESyntaxException
	{
		RE reExon   = new RE("^(\\d+)$");
		RE reIntron = new RE("^(\\d+)([+-])(\\d+)$");
		
		if (reExon.match(position))
			return new Integer(reExon.getParen(1));
		else if (reIntron.match(position))
			return new Integer(reIntron.getParen(1));
		else
			throw new RESyntaxException("Invalid position notation: " + position);
	}
	
	/**
	 * Get position where 'add' has been added.
	 * @param position
	 * @param add
	 * @return Added position
	 * @throws RESyntaxException
	 */
	public static String getAddedPosition(String position, Integer add) throws RESyntaxException
	{
		RE reExon   = new RE("^(\\d+)$");
		RE reIntron = new RE("^(\\d+)([+-])(\\d+)$");
		
		if (reExon.match(position))
			return new Integer(new Integer(reExon.getParen(1)) + add).toString();
		else if (reIntron.match(position))
			if (reIntron.getParen(2).equals("-"))
				if (add < new Integer(reIntron.getParen(3)))
					return reIntron.getParen(1) + reIntron.getParen(2) + (new Integer(reIntron.getParen(3)).intValue() - add.intValue());
				else
					return String.valueOf(new Integer(reIntron.getParen(1)).intValue() - new Integer(reIntron.getParen(3)).intValue() + add.intValue());
			else
				return reIntron.getParen(1) + reIntron.getParen(2) + (new Integer(reIntron.getParen(3)).intValue() + add.intValue());
		else
			throw new RESyntaxException("Invalid position notation: " + position);
	}

	/**
	 * Get the gDNA position.
	 * @param position
	 * @param exon
	 * @return gDNA position
	 * @throws RESyntaxException
	 */
	public static Integer getGDNAPosition(String position, Exon exon, String orientation) throws RESyntaxException //Integer gDNAPositionStart, Integer cDNAPositionStart)
	{
		RE reExon   = new RE("^(\\d+)$");
		RE reIntron = new RE("^(\\d+)([+-])(\\d+)$");

		if (reExon.match(position))
			if ("R".equals(orientation))
				// exon.gDNA - (mutation.cDNA - exon.cDNA)
				return Math.abs(exon.getGdna_Position() - (Integer.valueOf(reExon.getParen(1)) - exon.getCdna_Position()));
			else
				// exon.gDNA + (mutation.cDNA - exon.cDNA)
				return Math.abs(exon.getGdna_Position() + (Integer.valueOf(reExon.getParen(1)) - exon.getCdna_Position()));
		else if (reIntron.match(position))
			if (reIntron.getParen(2).equals("+"))
				if ("R".equals(orientation))
					// intron.gdnaPos + 1 (back to exon) - difference ('+' means downstream)
					return Math.abs(exon.getGdna_Position() + 1 - Integer.valueOf(reIntron.getParen(3)));
				else
					// intron.gdnaPos - 1 (start at last position of exon) + difference ('+' means upstream)
					return Math.abs(exon.getGdna_Position() - 1 + Integer.valueOf(reIntron.getParen(3)));
			else
				if ("R".equals(orientation))
					// intron.gdnaPos - intron.length + difference ('-' means upstream)
					return Math.abs(exon.getGdna_Position() - exon.getLength() + Integer.valueOf(reIntron.getParen(3)));
				else
					// intron.gdnaPos - difference ('-' means downstream)
					return Math.abs(exon.getGdna_Position() - exon.getLength() + Integer.valueOf(reIntron.getParen(3)));
		else
			throw new RESyntaxException("Invalid position notation: " + position);
	}
	
	/**
	 * Get start position (in bases) of first triplet change in two sequences
	 * @param sequence1
	 * @param sequence2
	 * @return first triplet change position
	 */
	public static int getFirstTripletChange(String sequence1, String sequence2)
	{
		int changePos        = StringUtils.indexOfDifference(sequence1, sequence2) + 1;
		int tripletChangePos = changePos % 3 > 0 ? changePos % 3 : 3;
		int tripletStart     = changePos - tripletChangePos;
		return tripletStart;
	}
}