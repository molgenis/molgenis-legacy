package org.molgenis.mutation.vo;

import org.apache.regexp.RESyntaxException;
import org.molgenis.mutation.Exon;
import org.molgenis.mutation.Mutation;
import org.molgenis.mutation.MutationGene;
import org.molgenis.mutation.util.SequenceUtils;
import org.molgenis.util.ValueLabel;

public class MutationUploadVO
{
	//TODO: Danny: Use or loose
	/*private static final transient Logger logger = Logger.getLogger(MutationUploadVO.class.getSimpleName());*/
	private Mutation mutation;
	private Exon exon;
	private MutationGene gene;

	// Values that are calculated but not stored in the db
	private String refseq;
	private String nt;
	private String codon;
	private String aa;
	private String aachange;
	
	public MutationUploadVO()
	{
		this.mutation = new Mutation();
	}

	public Mutation getMutation() {
		return mutation;
	}
	public void setMutation(Mutation mutation) {
		this.mutation = mutation;
	}
	public Exon getExon() {
		return exon;
	}
	public void setExon(Exon exon) {
		this.exon = exon;
	}
	public MutationGene getGene() {
		return gene;
	}
	public void setGene(MutationGene gene) {
		this.gene = gene;
	}
	public String getRefseq() {
		return refseq;
	}
	public void setRefseq(String refseq) {
		this.refseq = refseq;
	}
	public String getNt() {
		return nt;
	}
	public void setNt(String nt) {
		this.nt = nt;
	}
	public String getCodon() {
		return codon;
	}
	public void setCodon(String codon) {
		this.codon = codon;
	}
	public String getAa() {
		return aa;
	}
	public void setAa(String aa) {
		this.aa = aa;
	}
	public String getAachange() {
		return aachange;
	}
	public void setAachange(String aachange) {
		this.aachange = aachange;
	}
	public java.util.List<ValueLabel>  getEventOptions()
	{
		return new Mutation().getEventOptions();
	}
	public java.util.List<ValueLabel> getConsequenceOptions()
	{
		return new Mutation().getConsequenceOptions();
	}
	public java.util.List<ValueLabel> getInheritanceOptions()
	{
		return new Mutation().getInheritanceOptions();
	}
	public java.util.List<ValueLabel> getTypeOptions()
	{
		return new Mutation().getTypeOptions();
	}

	public void assignNt(String nuclSequence, int mutationStart)
	{
		System.out.println(">>> assignNt: start: mutation==" + this.getMutation());
		Integer length = this.getMutation().getLength();
		System.out.println(">>> assignNt: length==" + length);

		if (length == null)
			length = 1;

		System.out.println(">>> assignNt: vor setNt, seq==" + nuclSequence);
		this.setNt(nuclSequence.substring(mutationStart, mutationStart + length).toUpperCase());
		System.out.println(">>> assignNt: nach setNt");
	}

	public void assignConsequence()
	{
		// default: missense, no effect on splicing
		this.getMutation().setConsequence("Missense codon");
		this.getMutation().setEffectOnSplicing(false);

		if (this.getExon().getIsIntron())
		{
			this.getMutation().setConsequence("Altered splicing -> premature termination codon");
			this.getMutation().setEffectOnSplicing(true);
		}
		else if (this.getMutation().getAa_Notation().indexOf("fsX") > -1 || this.getMutation().getAa_Notation().indexOf("Ter") > -1)
			this.getMutation().setConsequence("Premature termination codon");
		else if (this.getMutation().getAa_Position() != null && this.getMutation().getAa_Position() == 1)
			this.getMutation().setConsequence("No initiation of transcription/translation");
	}

	public void assignType()
	{
		if (this.getExon().getIsIntron())
			this.getMutation().setType("splice-site mutation");
		else if (this.getMutation().getAa_Notation().indexOf("fsX") > -1 || this.getMutation().getAa_Notation().indexOf("Ter") > -1)
			this.getMutation().setType("nonsense mutation");
		else if (this.getMutation().getEvent().equals("deletion"))
			if (this.getMutation().getLength() <= 20)
				if (this.getMutation().getAa_Notation().indexOf("fsX") > -1)
					this.getMutation().setType("small deletion frame-shift");
				else
					this.getMutation().setType("small deletion in-frame");
			else
				if (this.getMutation().getAa_Notation().indexOf("fsX") > -1)
					this.getMutation().setType("large deletion frame-shift");
				else
					this.getMutation().setType("large deletion in-frame");
		else if (this.getMutation().getEvent().equals("duplication"))
			if (this.getMutation().getLength() <= 20)
				if (this.getMutation().getAa_Notation().indexOf("fsX") > -1)
					this.getMutation().setType("small duplication frame-shift");
				else
					this.getMutation().setType("small duplication in-frame");
			else
				if (this.getMutation().getAa_Notation().indexOf("fsX") > -1)
					this.getMutation().setType("large duplication frame-shift");
				else
					this.getMutation().setType("large duplication in-frame");
		else if (this.getMutation().getEvent().equals("insertion"))
			if (this.getMutation().getLength() <= 20)
				if (this.getMutation().getAa_Notation().indexOf("fsX") > -1)
					this.getMutation().setType("small insertion frame-shift");
				else
					this.getMutation().setType("small insertion in-frame");
			else
				if (this.getMutation().getAa_Notation().indexOf("fsX") > -1)
					this.getMutation().setType("large insertion frame-shift");
				else
					this.getMutation().setType("large insertion in-frame");
		else
			this.getMutation().setType("missense mutation");
	}

	private String getNotation(String position) throws RESyntaxException
	{
		if (this.getMutation().getLength() == null)
			return "";

		String notation = position;

		if (this.getMutation().getEvent().equals("insertion"))
			notation += "_" + SequenceUtils.getAddedPosition(position, 1);
		else if (this.getMutation().getLength() > 1)
			notation += "_" + SequenceUtils.getAddedPosition(position, this.getMutation().getLength() - 1);

		if (this.getMutation().getEvent().equals("deletion"))
		{
			notation += "del";
			if (this.getMutation().getLength() <= 2)
				notation += this.getNt();
		}
		else if (this.getMutation().getEvent().equals("duplication"))
		{
			notation += "dup";
			if (this.getMutation().getLength() <= 2)
				notation += this.getNt();
		}
		else if (this.getMutation().getEvent().equals("insertion"))
			notation += "ins" + this.getMutation().getNtchange();
		else if (this.getMutation().getEvent().equals("point mutation"))
			notation += this.getNt() + ">" + this.getMutation().getNtchange();
		else if (this.getMutation().getEvent().equals("insertion/deletion"))
		{
			// deletion
			notation += "del";
			if (this.getMutation().getLength() <= 2)
				notation += this.getNt();
			//insertion
			notation += "ins" + this.getMutation().getNtchange();
		}
		return notation;
	}

	public void assignCdna_notation() throws RESyntaxException
	{
		if (this.getMutation().getLength() != null)
			this.getMutation().setCdna_Notation("c." + this.getNotation(this.getMutation().getPosition()));
		else
			this.getMutation().setCdna_Notation("");
	}

	public void assignGdna_notation() throws RESyntaxException
	{
		if (this.getMutation().getLength() != null)
			this.getMutation().setGdna_Notation("g." + this.getNotation(this.getMutation().getGdna_Position().toString()));
		else
			this.getMutation().setGdna_Notation("");
	}

	public void assignAa_notation(String trlMutSeq, int codonNum)
	{
		if (codonNum == 1)
			this.getMutation().setAa_Notation("p.0");
		else if (codonNum > 1)
		{
			this.getMutation().setAa_Notation("p." + this.getAa() + codonNum + this.getAachange());
			if (this.getMutation().getLength() % 3 != 0 && !this.getMutation().getEvent().equals("point mutation"))
			{
				this.getMutation().setAa_Notation(this.getMutation().getAa_Notation() + "fs");
				int terPos = SequenceUtils.indexOfCodon(trlMutSeq, "Ter", codonNum);

				if (terPos > -1)
					this.getMutation().setAa_Notation(this.getMutation().getAa_Notation() + "X" + (terPos - codonNum + 1)); // + " DEBUG: " + terPos + "-" + codonNum + ", "+ trlMutSeq); //StringUtils.substring(trlMutSeq, (codonNum - 1) * 3));
			}
		}
		else
			this.getMutation().setAa_Notation("");
	}

	public Mutation toMutation()
	{
		this.mutation.setExon(this.exon);
		this.mutation.setGene(this.gene);
		return this.mutation;
	}
	
	public String toString()
	{
		return
		"Gene: " + this.getGene().getId() + "\n" +
		"Position: " + this.getMutation().getPosition() + "\n" +
		"Nucleotide: " + this.getNt() + "\n" +
		"Event: " + this.getMutation().getEvent() + "\n" +
		"Conserved AA: " + this.getMutation().getConservedAA() + "\n" +
		"Splicing: " + this.getMutation().getEffectOnSplicing() + "\n" +
		"Founder mut: " + this.getMutation().getFounderMutation() + "\n" +
		"Population: " + this.getMutation().getPopulation() + "\n" +
		"SNP?: " + this.getMutation().getReportedSNP() + "\n" +
		"Inheritance: " + this.getMutation().getInheritance() + "\n" +
		"NT change: " + this.getMutation().getNtchange() + "\n" +
		"Codon: " + this.getCodon() + "\n" +
		"cDNA not: " + this.getMutation().getCdna_Notation() + "\n" +
		"gDNA not: " + this.getMutation().getGdna_Notation() + "\n" +
		"AA not: " + this.getMutation().getAa_Notation() + "\n" +
		"Consequence: " + this.getMutation().getConsequence() + "\n" +
		"Type: " + this.getMutation().getType() + "\n";
	}
}