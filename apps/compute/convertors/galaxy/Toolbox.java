package convertors.galaxy;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "toolbox")
@XmlAccessorType(XmlAccessType.FIELD)
public class Toolbox
{
	@XmlElement(name="section")
	List<Section> sections = new ArrayList<Section>();	
	
	public List<Section> getSections()
	{
		return sections;
	}

	public void setSections(List<Section> sections)
	{
		this.sections = sections;
	}

	public String toString()
	{
		String result = "";
		for(Section s: sections) result += "\t"+s.toString().replace("\n", "\n\t")+"\n";
		return String.format("Toolbox(\n%s)",result);
	}
}
