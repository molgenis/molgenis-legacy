package qtltogff.sources;

public class Chromosome
{
	String name;
	String gffName;
	Long bpLength;
	Short orderNr;
	Long cumuBpDeductionAmount;
	
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getGffName()
	{
		return gffName;
	}
	public void setGffName(String gffName)
	{
		this.gffName = gffName;
	}
	public Long getBpLength()
	{
		return bpLength;
	}
	public void setBpLength(Long bpLength)
	{
		this.bpLength = bpLength;
	}
	public Short getOrderNr()
	{
		return orderNr;
	}
	public void setOrderNr(Short orderNr)
	{
		this.orderNr = orderNr;
	}
	public Long getCumuBpDeductionAmount() {
		return cumuBpDeductionAmount;
	}
	public void setCumuBpDeductionAmount(Long cumuBpDeductionAmount) {
		this.cumuBpDeductionAmount = cumuBpDeductionAmount;
	}
	
	
	
	
}
