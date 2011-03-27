package convertors.galaxy;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;

public class When
{
	@XmlAttribute
	String value;
	
	@XmlElementRefs(
	{ @XmlElementRef(name = "param", type = Param.class),
		@XmlElementRef(name = "repeat", type = ParamRepeat.class),
			@XmlElementRef(name = "conditional", type = ParamConditional.class) })

	List<Input> inputs = new ArrayList<Input>();
	
	public String toString()
	{
		String result = "";
		for(Input i: inputs) result += "\n\t"+i.toString().replace("\n", "\n\t");
		if(result != "") result += "\n";
		return String.format("When(value='%s'%s)", value,result);
	}
}
