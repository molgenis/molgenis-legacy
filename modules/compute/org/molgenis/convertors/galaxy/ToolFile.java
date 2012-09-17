package org.molgenis.convertors.galaxy;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class ToolFile
{
	@XmlAttribute
	String file;
	
	public String getFile()
	{
		return file;
	}
	public void setFile(String file)
	{
		this.file = file;
	}

	public String toString()
	{
		return "ToolFile(file="+getFile()+")";
	}
}
