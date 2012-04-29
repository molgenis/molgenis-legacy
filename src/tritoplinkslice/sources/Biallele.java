package tritoplinkslice.sources;

public class Biallele
{
	private char allele1;
	private char allele2;

	public Biallele(char allele1, char allele2)
	{
		this.allele1 = allele1;
		this.allele2 = allele2;
	}

	public Biallele(String allele1, String allele2) throws Exception
	{
		if (allele1.length() != 1 || allele2.length() != 1) throw new Exception(
				"Inputs must have 1 character each (1 per allele)");
		this.allele1 = allele1.charAt(0);
		this.allele2 = allele2.charAt(0);
	}

	public Biallele(String allele) throws Exception
	{
		if (allele.length() != 2) throw new Exception(
				"Input must have 2 characters (allele 1 & 2)");
		this.allele1 = allele.charAt(0);
		this.allele2 = allele.charAt(1);
	}

	public char getAllele1()
	{
		return allele1;
	}

	public char getAllele2()
	{
		return allele2;
	}

	public String toString()
	{
		return allele1 + " " + allele2;
	}
}
