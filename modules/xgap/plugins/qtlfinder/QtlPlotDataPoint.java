package plugins.qtlfinder;

public class QtlPlotDataPoint
{
	double lodScore;
	long bpLoc;
	String chromosome;
	
	public QtlPlotDataPoint(double lodScore, long bpLoc, String chromosome)
	{
		super();
		this.lodScore = lodScore;
		this.bpLoc = bpLoc;
		this.chromosome = chromosome;
	}

	public double getLodScore()
	{
		return lodScore;
	}

	public void setLodScore(double lodScore)
	{
		this.lodScore = lodScore;
	}

	public long getBpLoc()
	{
		return bpLoc;
	}

	public void setBpLoc(long bpLoc)
	{
		this.bpLoc = bpLoc;
	}

	public String getChromosome()
	{
		return chromosome;
	}

	public void setChromosome(String chromosome)
	{
		this.chromosome = chromosome;
	}
	
}
