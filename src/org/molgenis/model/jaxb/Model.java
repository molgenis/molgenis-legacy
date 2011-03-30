package org.molgenis.model.jaxb;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "molgenis")
@XmlAccessorType(XmlAccessType.FIELD)
// so use fields bypassing get/set
public class Model
{
	@XmlAttribute
	private String name;

	@XmlAttribute
	private String label;

	@XmlAttribute
	private String version;

	@XmlElement(name = "entity")
	private List<Entity> entities = new ArrayList<Entity>();

	@XmlElement(name = "module")
	private List<Module> modules = new ArrayList<Module>();

	@XmlElement
	private List<Screen> screens = new ArrayList<Screen>();

	// GETTERS AND SETTERS
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	public List<Entity> getEntities()
	{
		return entities;
	}

	// added function addEntity to add entity to model
	public void addEntity(Entity e)
	{
		this.entities.add(e);
	}

	// added function addModule to add module to model
	public void addModule(Module e)
	{
		modules.add(e);
	}

	public void setEntities(List<Entity> entities)
	{
		this.entities = entities;
	}

	public Entity getEntity(String name)
	{
		for (Entity entity : entities)
		{
			if (entity.getName().toLowerCase().equals(name.toLowerCase())) return entity;
		}
		return null;
	}

	/**
	 * find entity across local entities, and the ones contained in modules
	 * 
	 * @param name
	 * @return
	 */
	public Entity findEntity(String name)
	{
		for (Entity entity : entities)
		{
			if (entity.getName().toLowerCase().equals(name.toLowerCase())) return entity;
		}

		for (Module module : modules)
		{
			for (Entity entity : module.getEntities())
			{
				if (entity.getName().toLowerCase().equals(name.toLowerCase())) return entity;
			}
		}
		return null;
	}

	public Module getModule(String name)
	{
		for (Module module : modules)
		{
			if (module.getName().toLowerCase().equals(name.toLowerCase())) return module;
		}

		return null;
	}

	public void removeModule(String name)
	{
		for (int i = 0; i < modules.size(); i++)
		{
			if (modules.get(i).getName().toLowerCase().equals(
					name.toLowerCase()))
			{
				modules.remove(i);
			}
		}
	}

	public synchronized List<Screen> getScreens()
	{
		return screens;
	}

	public synchronized void setScreens(List<Screen> screens)
	{
		this.screens = screens;
	}

	public synchronized List<Module> getModules()
	{
		return modules;
	}

	public synchronized void setModules(List<Module> modules)
	{
		this.modules = modules;
	}

	/**
	 * Find and remove an entity from either root or a module. If remove from
	 * module, report back its name.
	 * 
	 * @param string
	 * @return
	 */
	public String findRemoveEntity(String name)
	{
		for (int i = 0; i < entities.size(); i++)
		{
			if (entities.get(i).getName().toLowerCase().equals(
					name.toLowerCase()))
			{
				entities.remove(i);
				return null;
			}
		}
		for (Module module : modules)
		{
			for (int i = 0; i < module.getEntities().size(); i++)
			{
				if (module.getEntities().get(i).getName().toLowerCase().equals(
						name.toLowerCase()))
				{
					module.getEntities().remove(i);
					return module.getName();
				}
			}
		}
		return null;

	}

}
