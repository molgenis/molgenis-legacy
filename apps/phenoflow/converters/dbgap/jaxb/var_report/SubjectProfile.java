package converters.dbgap.jaxb.var_report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "subject_profile")
@XmlAccessorType(XmlAccessType.FIELD)
public class SubjectProfile
{
	@XmlElement(name="case_control")
	List<CaseControl> case_control = new ArrayList<CaseControl>();

	@XmlElement
	List<Sex> sex = new ArrayList<Sex>();

	public String toString()
	{
		String case_control_string = "";
		for(CaseControl c: case_control) case_control_string += c.toString();
		
		String sex_string = "";
		for(Sex s: sex) sex_string += s.toString();
		
		return String.format("SubjectProfile(" +
				"\n\tcase_control=%s," +
				"\n\tsex=%s" +
				"\n)", case_control_string, sex_string);
	}
}
