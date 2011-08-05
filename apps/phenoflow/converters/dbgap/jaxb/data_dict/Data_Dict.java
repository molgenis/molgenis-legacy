package converters.dbgap.jaxb.data_dict;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="data_table")
@XmlAccessorType(XmlAccessType.FIELD)
public class Data_Dict
{
	@XmlAttribute
	public String id;
	
	@XmlElement(name="variable")
	public List<Variable> variables = new ArrayList<Variable>();

	//metadata not parsed out of file
	public String study_id;
	
	public String study_version;

	public String description;

	public URL url;

	public String version;


}
