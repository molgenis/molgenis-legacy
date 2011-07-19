package convertors.galaxy;

import javax.xml.bind.annotation.XmlAttribute;

public class Column
{
	@XmlAttribute
	String name;
	
	@XmlAttribute
	String index;
	
	public String toString()
	{
		return String.format("Column(name='%s' index='%s')",name,index);
	}
}
