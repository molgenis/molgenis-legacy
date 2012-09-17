package org.molgenis.convertors.galaxy;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class TestParam
{
	@XmlAttribute
	String name;
	@XmlAttribute
	String value;
	


	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public String toString()
	{
		return String.format("Param(name='%s', value='%s')", name,value);
	}

}
