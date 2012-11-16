/**
 * File: invengine_generate/meta/Entity.java <br>
 * Copyright: Inventory 2000-2006, GBIC 2005, all rights reserved <br>
 * Changelog:
 * <ul>
 * <li>2005-12-06; 1.0.0; RA Scheltema; Creation.
 * <li>2005-01-11; 1.0.0; RA Scheltema; Added documentation.
 * </ul>
 */

package org.molgenis.model.elements;

// imports
import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import org.molgenis.model.MolgenisModelException;

/**
 * This class describes a unique (combination of) field(s) associated with
 * entity.
 * 
 * @author RA Scheltema
 * @version 1.0.0
 */
public class Unique implements Serializable
{
	// member variables
	/** The entity this unique (key) is associated with */
	private Entity entity;

	/** The fields of the associated entity that make up this unique. */
	private List<String> fields;

	/**
	 * The unique applies only to this entity (under the hood, the constraint is
	 * than reduced to include 'type'
	 */
	private boolean subclass = false;

	/** Used for serialization purposes */
	private static final long serialVersionUID = -1201614213585052020L;

	/** A description of this constraint */
	private String description;

	// constructor(s)
	/**
	 * Constructor, which sets the associated entity and a single field as the
	 * unique.
	 * 
	 * @param entity
	 *            The entity this unique is associated with.
	 * @param field
	 *            The field that makes up this unique.
	 */
	public Unique(Entity entity, String fieldName, boolean subclass, String description)
	{
		this.entity = entity;
		this.fields = new Vector<String>();
		this.subclass = subclass;
		this.description = description;

		this.fields.add(fieldName);
	}

	/** Copy constructor */
	public Unique(Unique u)
	{
		this.description = u.description;
		this.entity = u.entity;
		this.fields = u.fields;
		this.subclass = u.subclass;
	}

	/**
	 * Constructor, which sets the associated entity and the list of fields as
	 * the unique.
	 * 
	 * @param entity
	 *            The entity this unique is associated with.
	 * @param fields
	 *            The fields that make up this unique.
	 */
	public Unique(Entity entity, List<String> fieldNames, boolean subclass, String description)
	{
		this.entity = entity;
		this.fields = fieldNames;
		this.subclass = subclass;
		this.description = description;
	}

	// access methods
	/**
	 * Returns a list of all the fields that make up this unique.
	 * 
	 * @return The fields that make up the unique.
	 * @throws MolgenisModelException
	 */
	public Vector<Field> getFields() throws MolgenisModelException
	{
		Vector<Field> result = new Vector<Field>();
		for (String fieldName : fields)
		{
			Field f = entity.getAllField(fieldName);
			if (f == null) throw new MolgenisModelException("Unknown unique field: " + this.getEntity().getName() + "."
					+ fieldName);
			result.add(f);
		}
		return result;
	}

	/**
	 * Returns the associated entity.
	 * 
	 * @return The entity associated with this unique.
	 */
	public Entity getEntity()
	{
		return this.entity;
	}

	// Object overloads
	@Override
	public String toString()
	{
		StringBuilder strBuilder = new StringBuilder("Unique(");

		for (String field : fields)
		{
			strBuilder.append(field).append(", ");
		}
		strBuilder.append(')');

		return strBuilder.toString();
	}

	/**
	 * Indicates whether some other object is "equal to" this one.
	 * 
	 * @param obj
	 *            The reference object with which to compare.
	 * @return True if this object is the same as the obj argument, false
	 *         otherwise.
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj != null && obj.getClass() == Unique.class)
		{
			return toString().equals(((Unique) obj).toString());
		}

		return false;
	}

	public boolean isSubclass()
	{
		return subclass;
	}

	public void setSubclass(boolean subclass)
	{
		this.subclass = subclass;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getDescription()
	{
		return description;
	}

	public void setEntity(Entity entity)
	{
		this.entity = entity;
	}
}
