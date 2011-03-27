package convertors.dbgap.jaxb.var_report;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Sex
{
	@XmlElement
	public String male;
	@XmlElement
	public String female;
	@XmlElement
	public String other;
	
	public String toString()
	{
		return String.format("Sex(male=%s, female=%s, other=%s)", male, female, other);
	}
}
