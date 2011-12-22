package tritoplinklifelines.sources;

public class PseudoPheno
{
	double pheno;
	String pseudo;
	public double getPheno()
	{
		return pheno;
	}
	public void setPheno(double pheno)
	{
		this.pheno = pheno;
	}
	public String getPseudo()
	{
		return pseudo;
	}
	public void setPseudo(String pseudo)
	{
		this.pseudo = pseudo;
	}
	public PseudoPheno(double pheno, String pseudo)
	{
		super();
		this.pheno = pheno;
		this.pseudo = pseudo;
	}
	
	
}
