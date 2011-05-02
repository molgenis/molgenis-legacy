/**
 * File: invengine_generate/meta/Form.java <br>
 * Copyright: Inventory 2000-2006, GBIC 2005, all rights reserved <br>
 * Changelog:
 * <ul>
 * <li>2005-12-06; 1.0.0; RA Scheltema Creation.
 * </ul>
 */

package org.molgenis.model.elements;

// jdk

/**
 * 
 */
public class Plugin extends UISchema 
{
	// constructor(s)
	/**
	 * 
	 */
	public Plugin(String name, UISchema parent, String pluginType) 
	{
		super(name, parent);
		this.entity = null;
		this.pluginType = pluginType;
	}
	/**
	 * 
	 */
	public Type getType()
	{
		return Type.PLUGIN;
	}

	// access methods
	/**
	 * 
	 */
	public void setEntity(Entity entity)
	{
		this.entity = entity;
	}

	/**
	 * 
	 */
	public Entity getEntity()
	{
		return this.entity;
	}
	
	/**
	 * 
	 */
	public void setRecord(Record record)
	{
		this.record = record;
	}
	
	/**
	 * 
	 */
	public Record getRecord()
	{
		return this.record;
	}
	/**
	 * 
	 */
	public void setReadOnly(final boolean readonly)
	{
		this.readonly = readonly;
	}
	
	public String toString()
	{
		if(getRecord() != null){
			return String.format("Plugin(name=%s, entity=%s, group=%s)", getName(), getRecord().getName(), getGroup());
		}else{
			return String.format("Plugin(name=%s, group=%s)", getName(), getGroup());
		}
	}

	/**
	 * 
	 */
	public boolean getReadOnly()
	{
		return this.readonly;
	}
	
	public String getPluginType() {
		return pluginType;
	}

	public void setPluginType(String pluginType) {
		this.pluginType = pluginType;
	}
	
	/** */
	private Record record;
	/** */
	private Entity entity;
	/** */
	private String pluginType;
	/** */
	private boolean readonly;

	/** */
	private static final long serialVersionUID = -2642011592737487306L;
}
