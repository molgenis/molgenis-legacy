package org.molgenis.convertors.galaxy;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
public class Option
{
	// <option value="" selected="true">Discard</option>
	@XmlAttribute
	String value;

	@XmlAttribute
	Boolean selected = false;

	@XmlValue
	String label;
	
	public String toString()
	{
		return String.format("Option(value='%s' label='%s' selected='%s')",value,label,selected);
	}

}
