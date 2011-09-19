package org.molgenis.convertors.galaxy;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Section
{
	@XmlAttribute
	String id;
	
	@XmlAttribute
	String name;
	
	@XmlElement(name="tool")
	List<ToolFile> toolFiles = new ArrayList<ToolFile>();
		
	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public List<ToolFile> getToolFiles()
	{
		return toolFiles;
	}

	public void setToolFiles(List<ToolFile> toolFiles)
	{
		this.toolFiles = toolFiles;
	}



	public String toString()
	{
		String result = "";
		for(ToolFile t: toolFiles)
		{
			result += "\n\t"+t.toString();
		}
		return String.format("Section(name=%s id=%s%s\n)",name,id,result);
	}
}
