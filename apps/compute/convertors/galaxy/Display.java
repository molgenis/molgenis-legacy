package convertors.galaxy;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "display")
@XmlAccessorType(XmlAccessType.FIELD)
public class Display implements Input
{
	public String toString()
	{
		return "Display = todo: just take xhtml inline";
	}
}
