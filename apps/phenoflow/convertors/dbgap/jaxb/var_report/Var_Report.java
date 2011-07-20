package convertors.dbgap.jaxb.var_report;

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
public class Var_Report
{
	@XmlAttribute(name="dataset_id")
	public String dataset_id;
	
	@XmlAttribute(name="study_id")
	public String study_id;	
	
	@XmlAttribute(name="study_name")
	public String study_name;
	
	@XmlElement(name="variable")
	public List<VariableSummary> variables = new ArrayList<VariableSummary>();
	
	@XmlAttribute(name="study_version")
	public String study_version;
	
	@XmlAttribute(name="description")
	public String description;

	@XmlAttribute(name="url")
	public URL url;

	@XmlAttribute(name="version")
	public String version;

	//for debug purposes
	public String toString()
	{
		String variable_string = "";
		for(VariableSummary v: variables)
		{
			variable_string += "\n\t" + v.toString().replace("\n", "\n\t");
		}
		return String.format("Var_Report(" +
				"\n\tdataset_id=%s, " +
				"\n\tversion=%s, " +
				"\n\tstudy_id=%s, " +
				"\n\tstudy_name=%s, " +
				"\n\tstudy_version=%s, " +
				"\n\tdescription=%s, " +
				"\n\turl=%s" +
				"%s", dataset_id, version, study_id, study_name, study_version, description, url, variable_string);
	}
	
}
