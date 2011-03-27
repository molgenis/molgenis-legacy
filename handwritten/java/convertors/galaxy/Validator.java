package convertors.galaxy;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Validator
{
	// <validator type="dataset_metadata_in_file"
	// filename="annotation_profiler_valid_builds.txt" metadata_name="dbkey"
	// metadata_column="0"
	// message="Profiling is not currently available for this species."/>

	@XmlAttribute
	String type;

	@XmlAttribute
	String filename;

	@XmlAttribute
	String metadata_name;
	
	@XmlAttribute
	String metadata_column;
	
	@XmlAttribute
	String message;
	
	@XmlAttribute
	String max;
	
	@XmlAttribute
	String size;
	
	public String toString()
	{
		return String.format("Validator(type='%s' filename='%s' message='%s' metadata_name='%s' metadata_column='%s' max='%s' size='%s')",
				type, filename, message, metadata_name, metadata_column, max, size);
	}
}
