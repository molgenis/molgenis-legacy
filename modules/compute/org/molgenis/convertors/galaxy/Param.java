package org.molgenis.convertors.galaxy;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="param")
@XmlAccessorType(XmlAccessType.FIELD)
public class Param implements Input
{
	// <param format="interval" name="input1" type="data"
	// label="Choose Intervals">
	// <param name="table_names" type="drill_down" display="checkbox"
	// hierarchy="recurse" multiple="true" label="Choose Tables to Use"
	// help="Selecting no tables will result in using all tables."
	// from_file="annotation_profiler_options.xml"/>

	@XmlAttribute
	String format;

	@XmlAttribute
	String name;

	@XmlAttribute
	String type;
	
	@XmlAttribute
	String size;

	@XmlAttribute(name="label")
	String labelAttribute;
	@XmlElement(name="label")
	String label;

	@XmlAttribute()
	String display;

	@XmlAttribute
	String hierarchy;

	@XmlAttribute
	Boolean multiple;
	
	@XmlAttribute
	Boolean numerical;
	
	@XmlAttribute
	Boolean optional;

	@XmlAttribute
	String help;

	@XmlAttribute
	String value;
	
	@XmlAttribute
	String from_file;
	
	@XmlElement(name="option")
	List<Option> options = new ArrayList<Option>();
	
	@XmlElement
	Validator validator;
	
	@XmlElement
	Column column;
	
	@XmlElement
	ParamConditional conditional;
	
	@XmlElement(name="options")
	ParamOptions dynamicOptions;
	
	@XmlAttribute
	String data_ref;
	
	public String getFormat()
	{
		return format;
	}

	public void setFormat(String format)
	{
		this.format = format;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	//maps to both label attribute and <label element
	public String getLabel()
	{
		if(label == null) return labelAttribute;
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	public String getDisplay()
	{
		return display;
	}

	public void setDisplay(String display)
	{
		this.display = display;
	}

	public String getHierarchy()
	{
		return hierarchy;
	}

	public void setHierarchy(String hierarchy)
	{
		this.hierarchy = hierarchy;
	}

	public Boolean getMultiple()
	{
		return multiple;
	}

	public void setMultiple(Boolean multiple)
	{
		this.multiple = multiple;
	}

	public String getHelp()
	{
		return help;
	}

	public void setHelp(String help)
	{
		this.help = help;
	}

	public String getFrom_file()
	{
		return from_file;
	}

	public void setFrom_file(String from_file)
	{
		this.from_file = from_file;
	}
	
	public String toString()
	{
		String result = "";
		for(Option o: options) result += "\n\t"+o;
		if(validator != null) result +="\n\t"+validator.toString();
		if(result != "") result +="\n";
		if(dynamicOptions != null) result+=dynamicOptions.toString().replace("\n","\n\t");
		return String.format("Param(name='%s', type='%s', label='%s', numerical='%s', optional='%s', format='%s', size='%s', data_ref='%s', multiple='%s', help='%s', from_file='%s', value='%s', hierarchy='%s'%s)", name, type, getLabel(), numerical, optional, format, size, data_ref, multiple, help, from_file, value,hierarchy,result);
	}

	//TODO: Danny Eclipse tells me this is wrong: We should extend the XMLAdapter, Type it and add 
	//the Marshal and unMarshal function implementations: http://weblogs.java.net/blog/kohsuke/archive/2005/09/using_jaxb_20s.html
	public static class ParamLabelAdapter implements XmlJavaTypeAdapter
	{

		@Override
		public Class<?> type()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Class<? extends XmlAdapter<?,?>> value()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Class<? extends Annotation> annotationType()
		{
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
