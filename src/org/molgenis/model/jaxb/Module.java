package org.molgenis.model.jaxb;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Module
{
	@XmlElement(name="entity")
	private List<Entity> entities = new ArrayList<Entity>();

	public synchronized List<Entity> getEntities()
	{
		return entities;
	}

	public synchronized void setEntities(List<Entity> entities)
	{
		this.entities = entities;
	}	
}
