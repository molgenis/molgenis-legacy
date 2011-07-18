package convertors.galaxy;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlRootElement(name = "param")
@XmlSeeAlso({Param.class, ParamConditional.class})
@XmlAccessorType(XmlAccessType.FIELD)
public interface Input
{
}
