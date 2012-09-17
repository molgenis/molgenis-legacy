package org.molgenis.convertors.galaxy;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Filter
{
	@XmlAttribute
	String type;
	
	@XmlAttribute
	String name;
	
	@XmlAttribute
	String value;
	
	@XmlAttribute
	String key;
	
	@XmlAttribute
	String column;
	
	@XmlAttribute
	String ref; //reference to a param
	
	public String toString()
	{
		return String.format("Filter(type='%s' name='%s' value='%s' column='%s', key='%s', ref='%s')", type, name, value, column,key, ref);
	}
}
