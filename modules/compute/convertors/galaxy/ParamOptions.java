package convertors.galaxy;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ParamOptions implements Input
{
	@XmlAttribute
	String from_file;
	
	@XmlAttribute
	String from_dataset;
	
	@XmlElement(name="column")
	List<Column> columns = new ArrayList<Column>();
	
	@XmlElement(name="filter")
	List<Filter> filters = new ArrayList<Filter>();
	
	@XmlElement
	Validator validator;
	
	@XmlAttribute
	String startswith; //seems to mark position in file to start reading in combination with from_file
	
	public String toString()
	{
		String columns_string = "";
		for(Column c: columns) columns_string +="\n\t"+c.toString();
		String filters_string = "";
		for(Filter f: filters) filters_string += "\n\t"+f.toString();
		String validators_string = "";
		if(validator != null) validators_string += "\n\t"+validator.toString();
		return String.format("Options(from_file='%s' from_dataset='%s' startswith='%s'%s%s%s", from_file, from_dataset, startswith, columns_string, filters_string, validators_string);
	}

}
