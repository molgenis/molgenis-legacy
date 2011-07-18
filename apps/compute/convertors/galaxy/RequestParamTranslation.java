package convertors.galaxy;

import javax.xml.bind.annotation.XmlAttribute;

public class RequestParamTranslation
{
	@XmlAttribute
	String galaxy_name;

	@XmlAttribute
	String missing;

	@XmlAttribute
	String remote_name;

	public String toString()
	{
		return String.format("RequestParamTranslation(galaxy_name='%s' missing='%s' remote_name='%s')", galaxy_name,
				missing, remote_name);
	}
}
