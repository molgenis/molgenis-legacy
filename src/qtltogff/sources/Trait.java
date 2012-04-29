package qtltogff.sources;

public class Trait
{
	String name;
	Long bpPos;
	String chromosomeName;
	
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public Long getBpPos()
	{
		return bpPos;
	}
	public void setBpPos(Long bpPos)
	{
		this.bpPos = bpPos;
	}
	public String getChromosomeName()
	{
		return chromosomeName;
	}
	public void setChromosomeName(String chromosomeName)
	{
		this.chromosomeName = chromosomeName;
	}
	
}
