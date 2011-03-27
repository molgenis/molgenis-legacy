package convertors.galaxy;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "conditional")
@XmlAccessorType(XmlAccessType.FIELD)
public class ParamConditional extends Param
{
	@XmlElement
	Param param;
	
	@XmlElement
	String name;
	
	@XmlElement
	List<When> when = new ArrayList<When>();
	
	public String toString()
	{
		String result = "\n\t"+param.toString().replace("\n", "\n\t");
		for(When w: when)
		{
			result +="\n\t"+w.toString().replace("\n", "\n\t");
		}
		result += "\n";
		return String.format("ConditionalParam(%s)",result);
	}
}
