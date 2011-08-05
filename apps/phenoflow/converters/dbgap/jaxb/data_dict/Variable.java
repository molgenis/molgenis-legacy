package converters.dbgap.jaxb.data_dict;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;


@XmlAccessorType(XmlAccessType.FIELD)
public class Variable
{
	@XmlAttribute
	public String id;
	@XmlElement
	public String name;
	@XmlElement
	public String description;
	@XmlElement
	public String unit;
	@XmlElement
	public String logical_min;
	@XmlElement
	public String logical_max;
	@XmlElement
	public String type;
	@XmlElement(name="value")
	public List<Value> values = new ArrayList<Value>();
}
