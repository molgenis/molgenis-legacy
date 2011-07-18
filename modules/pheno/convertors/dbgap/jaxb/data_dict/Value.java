package convertors.dbgap.jaxb.data_dict;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
public class Value
{
	@XmlAttribute
	public String code;
	@XmlValue
	public String value;
}
