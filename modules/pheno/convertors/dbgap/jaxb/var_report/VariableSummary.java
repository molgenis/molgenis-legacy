package convertors.dbgap.jaxb.var_report;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class VariableSummary
{
	@XmlAttribute(name="id")
	public String id;
	@XmlAttribute(name="var_name")
	public String var_name;
	@XmlAttribute(name="calculated_type")
	public String calculated_type;
	@XmlElement(name="description")
	//description is redundant with Variable
	public String description;
	@XmlElement(name="total")
	public Summary total;
	@XmlElement(name="cases")
	public Summary cases;
	@XmlElement(name="controls")
	public Summary controls;
	
	public String toString()
	{
		return String.format("VariableSummary(" +
				"\n\tid=%s," +
				"\n\tvar_name=%s," +
				"\n\tcalculated_type=%s," +
				"\n\tdescription=%s," +
				"\n\ttotal=%s," +
				"\n\tcases=%s," +
				"\n\tcontrols=%s" +
				"\n)", id, var_name, calculated_type, description, 
				total != null ? total.toString().replace("\n","\n\t") : null, 
				cases != null ? cases.toString().replace("\n","\n\t") : null, 
				controls != null ? controls.toString().replace("\n","\n\t") : null);
	}
}
