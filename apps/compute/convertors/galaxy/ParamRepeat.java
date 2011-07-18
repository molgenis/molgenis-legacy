package convertors.galaxy;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "repeat")
@XmlAccessorType(XmlAccessType.FIELD)
public class ParamRepeat implements Input
{
	@XmlAttribute
	String name;

	@XmlAttribute
	String title;

	@XmlElementRefs(
	{ @XmlElementRef(name = "param", type = Param.class),
			@XmlElementRef(name = "conditional", type = ParamConditional.class) })
	List<Input> inputs = new ArrayList<Input>();

	public String toString()
	{
		String inputs_string = "";
		for(Input i: inputs) inputs_string+="\n\t"+i.toString();
		return String.format("ParamRepeat(name='%s',title='%s'%s)",name,title,inputs_string);
	}
}
