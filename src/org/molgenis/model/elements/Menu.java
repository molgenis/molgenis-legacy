/**
 * File: invengine_generate/meta/Entity.java <br>
 * Copyright: Inventory 2000-2006, GBIC 2005, all rights reserved <br>
 * Changelog:
 * <ul>
 * <li>2005-12-12; 1.0.0; RA Scheltema; Creation.
 * <li>2005-01-11; 1.0.0; RA Scheltema Added documentation.
 * </ul>
 */

package org.molgenis.model.elements;

import org.molgenis.model.MolgenisModelException;


// jdk

/**
 * Describes a menu-element in the user-interface.
 * 
 * @author RA Scheltema
 * @version 1.0.0
 */
public class Menu extends UISchema
{
	public enum Position
	{
		TOP_LEFT("top_left"), TOP_RIGHT("top_right"), LEFT("left"), DEFAULT("default");

		private String tag;

		private Position(String tag)
		{
			this.tag = tag;
		}

		public String toString()
		{
			return this.tag;
		}
		
		public static Position getPosition(String position) throws MolgenisModelException
		{
			String options = "";
			for(Position p: Position.values())
			{
				if(p.toString().equalsIgnoreCase(position)) return p;
				options += p.toString() +", ";
			}
			throw new MolgenisModelException("position='"+position+"' is UNKNOWN for menu. Valid options: "+options);
		}
	};

	// constructor(s)
	/**
	 * 
	 */
	public Menu(String name, UISchema parent)
	{
		super(name, parent);
	}
	
	public Position getPosition()
	{
		return position;
	}

	public void setPosition(Position position)
	{
		this.position = position;
	}

	//
	/**
	 * 
	 */
	public Type getType()
	{
		return Type.MENU;
	}

	// local variables
	/** Used for serialization purposes. */
	static final long serialVersionUID = -1842653490799425686L;
	
	private Position position = Position.DEFAULT;
}
