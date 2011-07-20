package convertors.dbgap.jaxb.var_report;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="case_control")
@XmlAccessorType(XmlAccessType.FIELD)
public class CaseControl
{
	@XmlElement(name="case")
	public String case_;
	@XmlElement(name="control")
	public String control;
	@XmlElement(name="other")
	public String other;
	
	public String toString()
	{
		return String.format("CaseControl(case=%s, control=%s, other=%s)", case_, control, other);
	}
}
