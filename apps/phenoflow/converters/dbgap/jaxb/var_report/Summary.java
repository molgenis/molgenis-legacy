package converters.dbgap.jaxb.var_report;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;


public class Summary
{
	@XmlElement(name="subject_profile")
	public List<SubjectProfile> subject_profile = new ArrayList<SubjectProfile>();
	
	@XmlElementWrapper(name="stats")
	@XmlElement(name="stat")
	public List<Stat> stats = new ArrayList<Stat>();
	
	public String toString()
	{
		String profiles_string = "";
		for(SubjectProfile sp: subject_profile)
		{
			profiles_string += sp.toString().replace("\n", "\n\t");
		}
		String stats_string = "";
		for(Stat s: stats)
		{
			stats_string += s.toString().replace("\n", "\n\t");
		}
		
		return String.format("Summary(" +
				"\n\tsubject_profiles=%s," +
				"\n\tstats=%s" +
				"\n)", profiles_string, stats_string);
	}
}
