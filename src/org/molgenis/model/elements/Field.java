/**
 * File: invengine_generate/meta/Field.java <br>
 * Copyright: Inventory 2000-2006, GBIC 2005, all rights reserved <br>
 * Changelog:
 * <ul>
 * <li>2005-12-06; 1.0.0; RA Scheltema; Creation.
 * <li> 2006-01-11; 1.0.0; RA Scheltema; Added documentation.
 * </ul>
 */

package org.molgenis.model.elements;

// jdk
import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.generators.GeneratorHelper;
import org.molgenis.model.MolgenisModelException;
import org.molgenis.util.SimpleTree;
import org.molgenis.util.Tree;

// invengine

/**
 * Describes a field in an entity.
 * 
 * @author RA Scheltema
 * @author MA Swertz
 * @version 1.0.0
 */
public class Field implements Serializable
{
	public final static String TYPE_FIELD = "__Type";
	public final transient Logger logger = Logger.getLogger(Field.class);

	/**
	 * Description of the different types of a field.
	 */
	public static enum Type
	{
		/** Ontology type */
		ONTOLOGY("ontology", "%s"),
		/** The type is unknown, this case should raise an exception. */
		UNKNOWN("unknown", ""),
		/** The type is a simple boolean. */
		BOOL("bool", "%d"),
		/** The type is a simple integer. */
		INT("int", "%d"),
		/** The type is a decimal value. */
		LONG("long", "%d"),
		/** The type is a decimal value. */
		DECIMAL("decimal", "%.20g"),
		/**
		 * The type is a variable character string. More information can be
		 * found with the appropriate functions.
		 */
		STRING("string", "%s"),
		/** fixed length */
		CHAR("char", "%s"),
		/** The type is free-text. The length of the string is not defined. */
		TEXT("text", "%s"),
		/** The type is a date-field. */
		DATE("date", "%s"),
		/** */
		DATETIME("datetime", "%s"),
		/**
		 * The type of the field is user, which basically references a hidden
		 * table.
		 */
		USER("user", "%s"),
		/** The type of the field is file. */
		FILE("file", "%s"),
		/** special type of file, namely images */
		IMAGE("image", "%s"),
		/** */
		ENUM("enum", "%s"),
		/** Reference to another table, which can contain only 1 value. */
		XREF_SINGLE("xref", ""),
		/** Reference to another table, which can contain multiple values. */
		XREF_MULTIPLE("mref", ""),
		/** hyperlink */
		HYPERLINK("hyperlink", "%s"),
		/** Nucleotide sequence */
		NSEQUENCE("nsequence", "%s"),
		/** Nucleotide sequence */
		ONOFF("onoff", "%d"),
		/** List of values */
		LIST("list", "%s"),
		/** Hexadecimal values, now treated as strings*/
		HEXA("hexa","%s");

		// access
		/**
		 * The standard constructor, which binds a string to the
		 * enumeration-type.
		 */
		private Type(String tag, String format_type)
		{
			this.tag = tag;
			this.format_type = format_type;
		}

		public String toString()
		{
			return this.tag;
		}

		/**
		 * With this method the enumeration-type can be found based on the given
		 * int conforming to java.sql.Types
		 * 
		 * @param type
		 *            The string-representation of the type.
		 * @return The enumeration-type.
		 */
		public static Type getType(int type)
		{
			switch (type)
			{
				case Types.CHAR:
					return CHAR;
				case Types.BOOLEAN:
					return BOOL;
				case Types.BIT:
					return BOOL;
				case Types.INTEGER:
					return INT;
				case Types.BIGINT:
					return LONG;
				case Types.DOUBLE:
					return DECIMAL;
				case Types.VARCHAR:
					return STRING;
				case Types.BLOB:
					return TEXT;
				case Types.DATE:
					return DATE;
				case Types.TIME:
					return DATETIME;
				case Types.TIMESTAMP:
					return DATETIME;
				default:
					return UNKNOWN;
			}

			/*
			 * public static final int ARRAY 2003 public static final int BIGINT
			 * -5 public static final int BINARY -2 public static final int BIT
			 * -7 public static final int BLOB 2004 public static final int
			 * BOOLEAN 16 public static final int CHAR 1 public static final int
			 * CLOB 2005 public static final int DATALINK 70 public static final
			 * int DATE 91 public static final int DECIMAL 3 public static final
			 * int DISTINCT 2001 public static final int DOUBLE 8 public static
			 * final int FLOAT 6 public static final int INTEGER 4 public static
			 * final int JAVA_OBJECT 2000 public static final int LONGNVARCHAR
			 * -16 public static final int LONGVARBINARY -4 public static final
			 * int LONGVARCHAR -1 public static final int NCHAR -15 public
			 * static final int NCLOB 2011 public static final int NULL 0 public
			 * static final int NUMERIC 2 public static final int NVARCHAR -9
			 * public static final int OTHER 1111 public static final int REAL 7
			 * public static final int REF 2006 public static final int ROWID -8
			 * public static final int SMALLINT 5 public static final int SQLXML
			 * 2009 public static final int STRUCT 2002 public static final int
			 * TIME 92 public static final int TIMESTAMP 93 public static final
			 * int TINYINT -6 public static final int VARBINARY -3 public static
			 * final int VARCHAR 12
			 */
		}

		/**
		 * With this method the enumeration-type can be found based on the given
		 * string.
		 * 
		 * @param tag
		 *            The string-representation of the tag.
		 * @return The enumeration-type.
		 */
		public static Type getType(String tag)
		{
			if (tag.equals(BOOL.tag)) return BOOL;
			else if (tag.equals(INT.tag)) return INT;
			else if (tag.equals(LONG.tag)) return LONG;
			else if (tag.equals(DECIMAL.tag)) return DECIMAL;
			else if (tag.equals(STRING.tag)) return STRING;
			else if (tag.equals(TEXT.tag)) return TEXT;
			else if (tag.equals(DATE.tag)) return DATE;
			else if (tag.equals(DATETIME.tag)) return DATETIME;
			else if (tag.equals(USER.tag)) return USER;
			else if (tag.equals(FILE.tag)) return FILE;
			else if (tag.equals(IMAGE.tag)) return IMAGE;
			else if (tag.equals(ENUM.tag)) return ENUM;
			else if (tag.equals(XREF_SINGLE.tag)) return XREF_SINGLE;
			else if (tag.equals(XREF_MULTIPLE.tag)) return XREF_MULTIPLE;
			else if (tag.equals(HYPERLINK.tag)) return HYPERLINK;
			else if (tag.equals(NSEQUENCE.tag)) return NSEQUENCE;
			else if (tag.equals(ONOFF.tag)) return ONOFF;
			else if (tag.equals(LIST.tag)) return LIST;
			else if (tag.equals(HEXA.tag)) return HEXA;
			else
				return UNKNOWN;
		}

		/** The string-representation of the enumeration-type. */
		public final String tag;
		/** */
		public final String format_type;
	};

	/**
	 * 
	 */
	public class XRefLabel
	{
		/**
		 */
		public Vector<String> getFields()
		{
			return fields;
		}

		/**
		 */
		public String getFormat()
		{
			return format;
		}

		/**
		 */
		public String toString()
		{
			return "XRefLabel: " + String.format(format, fields.toArray());
		}

		/** */
		public Vector<String> fields = new Vector<String>();
		/** */
		public String format;
	};

	/** Fixed value used for determining the not-set value for the varchar. */
	public static final int LENGTH_NOT_SET = 0;

	/**
	 * Empty constructor
	 */
	public Field(Entity parent, String name, Type type)
	{
		this(parent, type, name, name, false, false, false, null);
	}

	// constructor(s)
	/**
	 * Standard constructor, which sets all the common variables for a field.
	 * Extra fields can be set with the appropriate access methods.
	 * 
	 * @param type
	 *            The type of the field.
	 * @param name
	 *            The name of the field, which needs to be unique for the
	 *            entity.
	 * @param label
	 *            The label of the field, which is used for the user interface.
	 * @param auto
	 *            Indicates whether this field needs to assigned a value by the
	 *            database.
	 * @param nillable
	 *            Indicates whether this field can have the value NULL in the
	 *            database.
	 * @param readonly
	 *            Indicates whether this field is readonly.
	 */
	public Field(Entity parent, Type type, String name, String label, boolean auto, boolean nillable, boolean readonly,
			String default_value)
	{
		this.entity = parent;

		// global
		this.type = type;

		this.name = name;
		this.label = label;
		this.auto = auto;
		this.nillable = nillable;
		this.readonly = readonly;
		this.default_value = default_value;
		this.description = "";
		this.default_code = "";

		// varchar
		this.varchar_length = LENGTH_NOT_SET;

		// xref
		this.xref_table = "";
		this.xref_field = "";
		this.xref_labels = new ArrayList<String>();

		//
		this.system = false;
		this.user_data = null;
	}

	/**
	 * copy-constructor
	 */
	public Field(Field field)
	{
		this.auto = field.auto;
		this.default_code = field.default_code;
		this.default_value = field.default_value;
		this.description = field.description;
		this.entity = field.entity;
		this.enum_options = field.enum_options;
		this.filter = field.filter;
		this.filterfield = field.filterfield;
		this.filtertype = field.filtertype;
		this.filtervalue = field.filtervalue;
		this.hidden = field.hidden;
		this.label = field.label;
		this.mref_name = field.mref_name;
		this.mref_localid = field.mref_localid;
		this.mref_remoteid = field.mref_remoteid;
		this.name = field.name;
		this.nillable = field.nillable;
		this.readonly = field.readonly;
		this.system = field.system;
		this.type = field.type;
		this.user_data = field.user_data;
		this.varchar_length = field.varchar_length;
		this.xref_field = field.xref_field;
		this.xref_labels = field.xref_labels;
		this.xref_table = field.xref_table;
	}

	// global access methods
	/**
	 * 
	 */
	@Deprecated
	public Entity getParent()
	{
		return entity;
	}

	public Entity getEntity()
	{
		return entity;
	}

	/**
	 * This method returns the type of this field.
	 * 
	 * @return The type of this field.
	 */
	public Type getType()
	{
		return this.type;
	}

	/**
	 * @param type
	 */
	public void setType(Type type)
	{
		this.type = type;
	}

	public String getFormatString()
	{
		if (type == Type.XREF_SINGLE || type == Type.XREF_MULTIPLE)
		{
			try
			{
				DBSchema root = entity.getRoot();

				return this.getXrefField().getFormatString();
			}
			catch (Exception e)
			{
				;
			}
		}

		return type.format_type;
	}

	/**
	 * This method returns the name of this field.
	 * 
	 * @return The name of this field.
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * 
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * This method returns the label of this field.
	 * 
	 * @return The label of this field.
	 */
	public String getLabel()
	{
		if (label == null) return getName();
		return this.label;
	}

	/**
	 * 
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}

	public void setAuto(boolean auto)
	{
		this.auto = auto;
	}

	/**
	 * Returns whether this field is auto-assigned by the database.
	 * 
	 * @return True when this field is auto-assigned, false otherwise.
	 */
	public boolean isAuto()
	{
		return this.auto;
	}

	/**
	 * Returns whether this field can be NULL in the database.
	 * 
	 * @return True when this field can be NULL, false otherwise.
	 */
	public boolean isNillable()
	{
		return this.nillable;
	}

	public void setReadonly(boolean readonly)
	{
		this.readonly = readonly;
	}

	public boolean isHidden()
	{
		return hidden;
	}

	public void setHidden(boolean hidden)
	{
		this.hidden = hidden;
	}

	/**
	 * Returns whether this field is read-only in the database.
	 * 
	 * @return True when this field is read-only, false otherwise.
	 */
	public boolean isReadOnly()
	{
		return this.readonly;
	}

	/**
	 * Returns whether this field is a system-field. When it is a system-field,
	 * it will not be displayed in the user-interface.
	 * 
	 * @return True when this field is a system-field, false otherwise.
	 */
	public boolean isSystem()
	{
		return this.system;
	}

	/**
	 * With this set-function the system-property can be set.
	 * 
	 * @param s
	 *            The system boolean.
	 */
	public void setSystem(boolean s)
	{
		this.system = s;
	}

	/**
	 * Returns whether this field is locally available in the table, or whether
	 * it is located in another table (for example link-table).
	 * 
	 * @return Whether this field is located in the table
	 */
	public boolean isLocal()
	{
		return this.type != Type.XREF_MULTIPLE;
	}

	/**
	 * @throws MolgenisModelException
	 * 
	 */
	public boolean isCyclic() throws MolgenisModelException
	{
		if (this.type != Type.XREF_SINGLE) return false;

		if (xref_table.equals(this.name)) return true;

		DBSchema root = entity.getRoot();
		Entity e = (Entity) root.get(xref_table);
		for (Field field : e.getAllFields())
		{
			if (field.type != Type.XREF_SINGLE) continue;

			if (field.xref_table.equals(this.name)) return true;
		}

		return false;
	}

	/**
	 * Returns whether this field is a xref.
	 * 
	 * @return Whether this field is a xref.
	 */
	public boolean isXRef()
	{
		return type == Type.XREF_MULTIPLE || type == Type.XREF_SINGLE;
	}

	/**
	 * Returns the value the database should set for the field when there is no
	 * value set.
	 * 
	 * @return The default-value.
	 */
	public String getDefaultValue()
	{
		return this.default_value;
	}

	public void setDevaultValue(String value)
	{
		this.default_value = value;
	}

	/**
	 * Returns the description of the entity.
	 * 
	 * @return The description.
	 */
	public String getDescription()
	{
		return this.description;
	}

	/**
	 * Sets the description of this entity.
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	// enum access methods
	/**
	 * 
	 */
	public void setEnumOptions(Vector<String> options) // throws Exception
	{
		// if (this.type != Type.ENUM)
		// {
		// throw new Exception("Field is not a ENUM, so options cannot be
		// set.");
		// }
		// if (options.size() == 0)
		// {
		// throw new Exception("Enum must have at least one option");
		// }

		this.enum_options = options;
	}

	/**
	 * 
	 */
	public Vector<String> getEnumOptions() throws MolgenisModelException
	{
		if (this.type != Type.ENUM)
		{
			throw new MolgenisModelException("Field is not a ENUM, so options cannot be set.");
		}

		return this.enum_options;
	}

	// varchar access methods
	/**
	 * When this field is of type Type.VARCHAR, this method sets the maximum
	 * length the varchar can be. When this field is not of type Type.VARCHAR,
	 * this method raises an exception.
	 * 
	 * @param length
	 *            The maximum length the varchar field can be.
	 * @throws Exception
	 *             When the field is not of type Type.VARCHAR.
	 */
	public void setVarCharLength(int length) // throws Exception
	{
		// if (this.type != Type.VARCHAR)
		// {
		// / throw new Exception("Field is not a VARCHAR, so length cannot be
		// set.");
		// }

		this.varchar_length = length;
	}

	/**
	 * When this field is of type Type.VARCHAR, this method returns the maximum
	 * length the varchar can be. When this field is not of type Type.VARCHAR,
	 * this method raises an exception.
	 * 
	 * @return The maximum length the varchar field can be.
	 * @throws Exception
	 *             When the field is not of type Type.VARCHAR.
	 */
	public int getVarCharLength() throws MolgenisModelException
	{
		if (this.type != Type.STRING && this.type != Type.CHAR)
		{
			throw new MolgenisModelException("Field is not a VARCHAR, so length cannot be retrieved.");
		}

		return this.varchar_length;
	}

	// xref access methods
	public void setXRefEntity(String xref_entity)
	{
		this.xref_table = xref_entity;
	}

	/**
	 * With this method all the additional information for this xref-field can
	 * be set. When this field is not of type Type.XREF_SINGLE or
	 * Type.XREF_MULTIPLE an exception is raised.
	 * 
	 * @param entity
	 *            The entity this field references.
	 * @param field
	 *            The field of the entity this field references.
	 * @param label
	 *            The label of this xref.
	 * @throws Exception
	 *             When this field is not of type Type.XREF_SINGLE or
	 *             Type.XREF_MULTIPLE
	 */
	public void setXRefVariables(String entity, String field, List<String> labels) // throws
	// Exception
	{
		// if (this.type != Type.XREF_SINGLE && this.type != Type.XREF_MULTIPLE)
		// {
		// throw new Exception("Field is not a XREF, so xref-variables cannot be
		// set.");
		// }

		this.xref_table = entity;
		this.xref_field = field;
		this.xref_labels = labels;
	}

	/**
	 * Returns the name of the entity this field is referencing to. When this
	 * field is not of type Type.XREF_SINGLE or Type.XREF_MULTIPLE an exception
	 * is raised.
	 * 
	 * @return The name of the entity this field is referencing.
	 * @throws Exception
	 *             When this field is not of type Type.XREF_SINGLE or
	 *             Type.XREF_MULTIPLE
	 */
	public Entity getXrefEntity() throws MolgenisModelException
	{
		Entity e = this.getEntity().getModel().getEntity(this.getXrefEntityName());
		if( e == null){
			throw new MolgenisModelException("Xref entity '" + this.getXrefEntityName() +"' not part of model.");
		}
		return e;
	}

	public String getXrefEntityName() throws MolgenisModelException
	{
		if (this.type != Type.XREF_SINGLE && this.type != Type.XREF_MULTIPLE)
		{
			throw new MolgenisModelException("Field '" + this.getEntity().getName() + "." + this.getName()
					+ "' is not a XREF, so xref-table cannot be retrieved.");
		}

		return this.xref_table;

	}

	/**
	 * Returns the name of the field of the entity this field is referencing to.
	 * When this field is not of type Type.XREF_SINGLE or Type.XREF_MULTIPLE an
	 * exception is raised.
	 * 
	 * @return The name of the field of the entity this field is referencing.
	 * @throws Exception
	 * @throws Exception
	 *             When this field is not of type Type.XREF_SINGLE or
	 *             Type.XREF_MULTIPLE
	 */
	public Field getXrefField() throws MolgenisModelException // throws
	// Exception
	{
		if (this.type != Type.XREF_SINGLE && this.type != Type.XREF_MULTIPLE)
		{
			throw new MolgenisModelException("Field is not a XREF, so xref-field cannot be retrieved.");
		}

		Field result = this.getXrefEntity().getAllField(this.getXrefFieldName());
		if (result == null)
		{
			System.out.println("xref_field is not known for field " + getEntity().getName() + "." + getName());
		}
		return result;
	}

	public String getXrefFieldName()
	{
		return this.xref_field;
	}

	/**
	 * Returns the label of this reference. When this field is not of type
	 * Type.XREF_SINGLE or Type.XREF_MULTIPLE an exception is raised.
	 * 
	 * @return The label of this reference.
	 * @throws MolgenisModelException
	 * @throws Exception
	 *             When this field is not of type Type.XREF_SINGLE or
	 *             Type.XREF_MULTIPLE
	 */
	public List<String> getXrefLabelNames() throws MolgenisModelException
	{
		// label name = replace '.' and replace entity name if label entity ==
		// xref_entity
		List<String> label_names = new ArrayList<String>();
		for (String label : this.getXrefLabelsTemp())
			label_names.add(label.replace(".", "_").replace(this.getXrefEntity() + "_", ""));
		return label_names;
	}

	public Tree getXrefLabelTree() throws MolgenisModelException
	{
		List<String> labels = new ArrayList<String>();
		for (String label : this.getXrefLabelNames())
			labels.add(getName() + "_" + label);

		Tree root = new SimpleTree(getName(), null);
		root.setValue(this);
		this.getXrefLabelTree(labels, root);
		return root;
	}

	/**
	 * Creates a tree with leafs that match labels and nodes that match
	 * entities. xref fields will result in sub trees.
	 * 
	 * @param labels
	 *            to be matched
	 * @param path
	 *            so far in the tree to allow for recursion
	 * @return tree of paths matching labels.
	 * @throws MolgenisModelException
	 */
	protected void getXrefLabelTree(List<String> labels, Tree parent) throws MolgenisModelException
	{
		for (Field f : this.getXrefEntity().getAllFields())
		{
			String name = parent.getName() + "_" + f.getName();

			if (!f.getType().equals(Field.Type.XREF_SINGLE) && !f.getType().equals(Field.Type.XREF_MULTIPLE))
			{
				if (labels.contains(name))
				{
					Tree leaf = new SimpleTree(name, parent);
					leaf.setValue(f);
					// break;
				}
			}
		}

		for (Field f : this.getXrefEntity().getAllFields())
		{
			String name = parent.getName() + "_" + f.getName();
			
			if (f.getType().equals(Field.Type.XREF_SINGLE))
			{
				// check for cyclic relations
				// FIXME check for indirect cyclic relations or limit nesting
				// arbitrarily
				if (!f.getXrefEntity().equals(this.getXrefEntity()))
				{

					Tree<Tree> node = new SimpleTree(name, null);
					// get fields from subtree
					f.getXrefLabelTree(labels, node);
					// only attach the node if it leads to a label
					for (Tree child : node.getAllChildren())
					{
						if (labels.contains(child.getName()))
						{
							node.setParent(parent);
							node.setValue(f);
							break;
						}
					}
				}
			}
		}
	}

	public List<Field> getXrefLabelPath(String label) throws MolgenisModelException, DatabaseException
	{
		return this.allPossibleXrefLabels().get(label);
	}

	public List<Field> getXrefLabels() throws DatabaseException, MolgenisModelException
	{
		List<Field> result = new ArrayList<Field>();

		for (String label : getXrefLabelNames())
		{
			// absolute name
			if (label.contains("."))
			{
				result.add(this.getEntity().getModel().findField(label));
			}

			// path through xref to another field, path separated by _
			// caveat is fieldnames with '_' in the name
			// solution is to match against all possible xref_label candidates
			else if (label.contains("_"))
			{
				// match agains all known labels
				Map<String, List<Field>> candidates = this.allPossibleXrefLabels();
				for (String test : candidates.keySet())
				{
					if (test.equals(label))
					{
						result.add(candidates.get(test).get(candidates.get(test).size() - 1));
					}
				}

				// e.g., Sample.experiment, is an xref itself, can be cascade of
				// xrefs actually
				// Field xrefField = new
				// Field(this.getEntity().getModel().findField(
				// this.getXrefEntity().getName() + "." + label.split("_")[0]));
				// xrefField.setXrefLabelNames(Arrays.asList(new String[]
				// { label.split("_")[1] }));

				// result.add(xrefField);

				// System.out.println(result.get(result.size()-1));
			}

			// local name
			else
			{
				Field target = this.getEntity().getModel().findField(this.getXrefEntity().getName() + "." + label);
				result.add(new Field(target));
			}
			//System.out.println(result);
		}
		return result;
	}

	public List<String> getXrefLabelsTemp() throws MolgenisModelException
	{
		if (xref_labels == null || xref_labels.size() == 0)
		{
			if (this.getXrefEntity() == null) throw new MolgenisModelException("Cannot find xref_entity='"
					+ getXrefEntityName() + "' for " + getEntity().getName() + "." + getName());
			if (this.getXrefEntity().getXrefLabels() != null) return this.getXrefEntity().getXrefLabels();
			else
				return Arrays.asList(new String[]
				{ this.xref_field });
		}
		return xref_labels;
	}

	/**
	 * Gets the name of the link-table when this field is a XREF_MULTIPLE. When
	 * this field is not of type Type.XREF_MULTIPLE an exception is raised.
	 * 
	 * @return The name of the linktable.
	 * @throws Exception
	 *             When this field is not of type Type.XREF_MULTIPLE
	 */
	public String getMrefName()
	{
		// if (this.type != Type.XREF_MULTIPLE)
		// {
		// throw new Exception("Field is not a XREF with relation MULTIPLE, so
		// xref-linktable cannot be retrieved.");
		// }

		return this.mref_name;
	}

	/**
	 * Sets the name of the link-table when this field is a XREF_MULTIPLE. When
	 * this field is not of type Type.XREF_MULTIPLE an exception is raised.
	 * 
	 * @param linktable
	 *            The name of the linktable.
	 * @throws Exception
	 *             When this field is not of type Type.XREF_MULTIPLE
	 */
	public void setMrefName(String linktable)
	{
		// if (this.type != Type.XREF_MULTIPLE)
		// {
		// throw new Exception("Field is not a XREF with relation MULTIPLE, so
		// xref-linktable cannot be set.");
		// }

		this.mref_name = linktable;
	}

	public String getMrefLocalid()
	{
		return mref_localid;
	}

	public void setMrefLocalid(String mref_localid)
	{
		this.mref_localid = mref_localid;
	}

	public String getMrefRemoteid()
	{
		return mref_remoteid;
	}

	public void setMrefRemoteid(String mref_remoteid)
	{
		this.mref_remoteid = mref_remoteid;
	}

	//
	/**
	 * 
	 */
	public void setUserData(Object obj)
	{
		user_data = obj;
	}

	/**
	 * 
	 */
	public Object getUserData()
	{
		return user_data;
	}

	public String getDefaultCode()
	{
		return default_code;
	}

	public void setDefaultCode(String default_code)
	{
		this.default_code = default_code;
	}

	// Object overloads
	/**
	 * Returns a string representation of the Field.
	 * 
	 * @return The string-representation.
	 */
	public String toString()
	{
		String str = "Field(";

		// entity
		str += "entity=" + entity.getName();

		// name/label
		str += ", name=" + name;

		// type
		str += ", type=" + type.tag;
		if (type == Field.Type.STRING || type == Field.Type.CHAR) str += "[" + varchar_length + "]";
		else if (type == Field.Type.XREF_SINGLE || type == Field.Type.XREF_MULTIPLE) str += "[" + xref_table + "->"
				+ xref_field + "]";
		if (type == Field.Type.XREF_MULTIPLE) str += ", mref_name=" + this.mref_name + ", mref_localid="
				+ this.mref_localid + ", mref_remoteid=" + this.mref_remoteid;
		if (type == Field.Type.XREF_SINGLE || type == Field.Type.XREF_MULTIPLE) str += ", xref_label="
				+ new GeneratorHelper(null).toCsv(this.xref_labels);

		// settings
		str += ", auto=" + auto;
		str += ", nillable=" + nillable;
		str += ", readonly=" + readonly;

		// default
		str += ", default=" + default_value;

		if (this.enum_options != null) str += ", enum_options=" + this.enum_options;

		// closure
		str += ")";

		return str;
	}

	/**
	 * Indicates whether some other object is "equal to" this one.
	 * 
	 * @param obj
	 *            The reference object with which to compare.
	 * @return True if this object is the same as the obj argument, false
	 *         otherwise.
	 */
	public boolean equals(Object obj)
	{
		if (obj != null && obj instanceof Field)
		{
			return name.equals(((Field) obj).getName());
		}

		return false;
	}

	/**
	 * Returns a hash code value for the Field. This hash-code is used for quick
	 * searching in a vector of fields.
	 * 
	 * @return The hash-value for this field.
	 */
	public int hashCode()
	{
		return this.name.hashCode();
	}

	// member variables
	/** */
	private Entity entity;

	/** The type of this field. */
	private Type type;
	/**
	 * The name of this field, which needs to be unique for the associated
	 * entity.
	 */
	private String name;
	/** The label of this field, which is used for the user interface. */
	private String label;
	/** Whether this field is auto-assigned by the database. */
	private boolean auto;
	/** Whether this field can be NULL in the database. */
	private boolean nillable;
	/** Whether this field is read-only. */
	private boolean hidden;
	/** Whether this field is hidden. */
	private boolean readonly;
	/**
	 * The string that should be set as the default value (is passed to the
	 * database ...)
	 */
	private String default_value = null;
	/** A short description of this field. */
	private String description;

	private String default_code;

	/** When this field a of type Type.ENUM, this vector contains the options */
	private Vector<String> enum_options;

	/**
	 * When this field is of type Type.VARCHAR, this indicates the maximum
	 * length of the string.
	 */
	private int varchar_length;

	/**
	 * When this field is of type Type.XREF_SINGLE or Type.XREF_MULTIPLE, this
	 * is the name of the entity it is referencing.
	 */
	private String xref_table;
	/**
	 * When this field is of type Type.XREF_SINGLE or Type.XREF_MULTIPLE, this
	 * is the name of the field of the entity it is referencing.
	 */
	private String xref_field;
	/**
	 * When this field is of type Type.XREF_SINGLE or Type.XREF_MULTIPLE, this
	 * is the label of the reference.
	 */
	private List<String> xref_labels;
	/**
	 * Boolean to indicate cascading delete
	 */
	private boolean xref_cascade = false;
	/**
	 * When this field is of type Type.XREF_MULTIPLE, this is the name of the
	 * link-table.
	 */
	private String mref_name;

	private String mref_localid;

	private String mref_remoteid;

	private boolean filter;
	private String filtertype;
	private String filterfield;
	private String filtervalue;

	/** */
	private boolean system;
	/** Contains a pointer to some user-data. */
	private Object user_data;

	/** Used for serialization purposes. */
	private static final long serialVersionUID = -1879739243713730190L;

	public boolean hasFilter()
	{
		return filter;
	}

	public void setFilter(boolean filter)
	{
		this.filter = filter;
	}

	public String getFilterfield()
	{
		return filterfield;
	}

	public void setFilterfield(String filterfield)
	{
		this.filterfield = filterfield;
	}

	public String getFiltertype()
	{
		return filtertype;
	}

	public void setFiltertype(String filtertype)
	{
		this.filtertype = filtertype;
	}

	public String getFiltervalue()
	{
		return filtervalue;
	}

	public void setNillable(boolean nillable)
	{
		this.nillable = nillable;
	}

	public void setFiltervalue(String filtervalue)
	{
		this.filtervalue = filtervalue;
	}

	public void setEntity(Entity entity)
	{
		this.entity = entity;
	}

	public void setXrefField(String xrefField)
	{
		this.xref_field = xrefField;

	}

	public void setXrefLabelNames(List<String> labelNames)
	{
		this.xref_labels = labelNames;

	}

	public Map<String, List<Field>> allPossibleXrefLabels() throws MolgenisModelException, DatabaseException
	{
		if (!this.getType().equals(Field.Type.XREF_SINGLE) && !this.getType().equals(Field.Type.XREF_MULTIPLE)) throw new MolgenisModelException(
				"asking xref labels for non-xref field");

		Map<String, List<Field>> result = new LinkedHashMap<String, List<Field>>();
		for (Unique key : getXrefEntity().getAllKeys())
			// get all except primary key
			// if (!key.equals(getXrefEntity().getAllKeys().firstElement()))
			// {
			for (Field f : key.getFields())
			{
				if (f.getType().equals(Field.Type.XREF_SINGLE) || f.getType().equals(Field.Type.XREF_MULTIPLE))
				{
					f = getXrefEntity().getAllField(f.getName());

					Map<String, List<Field>> subpaths = f.allPossibleXrefLabels();
					for (Entry<String, List<Field>> pair : subpaths.entrySet())
					{
						List<Field> path = pair.getValue();
						path.add(0, f);
						String label = f.getName() + "_" + pair.getKey();
						result.put(label, path);

						// if
						// (!f.getEntity().getName().equals(getXrefEntity().getName()))
						// {
						//
						// System.out.println("PATH FOR " +
						// this.getEntity().getName() + "." + this.getName()
						// + "=" + this.getName() + "_" + label + " " + "field="
						// + f.getEntity().getName()
						// + " " + getXrefEntity().getName());
						// System.out.print(this.getEntity().getName() + "." +
						// this.getName());
						// for (Field pathField : path)
						// {
						// System.out
						// .print("->" + pathField.getEntity().getName() + "." +
						// pathField.getName());
						// }
						// System.out.println();
						// }
						//
						// else
						// {
						result.put(label, path);
						// }

					}
				}
				else
				{
					List<Field> path = new ArrayList<Field>();
					path.add(f);
					result.put(f.getName(), path);
				}
				// }
			}

		return result;
	}

	/**
	 * This method returns a map of all possible xref_labels based on the unique
	 * constraints
	 */
	// public static Map<String, List<Field>> allPossibleXrefLabels2(Field
	// xrefField) throws MolgenisModelException,
	// DatabaseException
	// {
	// // System.out.println("GENERATING xref_labels for " +
	// // xrefField.getEntity().getName()+"."+xrefField.getName());
	// Map<String, List<Field>> result = new LinkedHashMap<String,
	// List<Field>>();
	// // checking secondary keys
	// for (Unique unique : xrefField.getXrefEntity().getAllKeys())
	// if (unique != xrefField.getXrefEntity().getAllKeys().firstElement())
	// {
	// for (Field f : unique.getFields())
	// {
	// if (f.getType().equals(Field.Type.XREF_SINGLE))
	// {
	// // recurse to find subpaths
	// Map<String, List<Field>> subpaths = f.allPossibleXrefLabels();
	// for (String subpath : subpaths.keySet())
	// {
	// List<Field> path = subpaths.get(subpath);
	// path.add(0, f);
	// result.put(f.getName() + "_" + subpath, path);
	//
	// // System.out.println("FOUND PATH " + f.getName() +
	// // "_" + subpath + " for field " + xrefField);
	// System.out.print("FOUND " + xrefField.getEntity().getName() + "." +
	// xrefField.getName());
	// for (Field pathElement : result.get(f.getName() + "_" + subpath))
	// {
	// System.out
	// .print("->" + pathElement.getEntity().getName() + "." +
	// pathElement.getName());
	// }
	// System.out.println();
	// }
	// }
	// else if (!f.getType().equals(Field.Type.XREF_MULTIPLE))
	// {
	// List<Field> path = new ArrayList<Field>();
	// path.add(f);
	// result.put(f.getName(), path);
	// }
	// }
	// }
	// return result;
	// }

	/**
	 * Helper method to find labels within the same entity that point to the
	 * same endpoint. E.g. suppose fields protocol_investigation_name maps to
	 * same entity as investigatio_name
	 * 
	 * @param xrefField
	 * @return
	 * @throws MolgenisModelException
	 * @throws DatabaseException
	 */
	public List<String> labelsToSameEndpoint(String xref_label) throws MolgenisModelException, DatabaseException
	{
		List<String> result = new ArrayList<String>();

		// get the endpoint, if any
		List<Field> pathToEndpoint = this.allPossibleXrefLabels().get(xref_label);
		if (pathToEndpoint == null)
		{
			String knownLabels = "";
			for (String label : this.allPossibleXrefLabels().keySet())
				knownLabels += ", label";
			throw new MolgenisModelException("xref_label '" + xref_label + "'unknown for field "
					+ this.getEntity().getName() + "." + this.getName() + ". Known labels are " + knownLabels);
		}
		else
		{
			Field endpoint = pathToEndpoint.get(pathToEndpoint.size() - 1);
			//logger.debug("FINDING OTHER ENDS FOR xref_entity=" + getEntity().getName() + " xref_field="
			//		+ getName() + " xref_label=" + xref_label + " POINTING TO " + endpoint);
			//for (Field f : pathToEndpoint)
			//	System.out.println(f);
			for (Field otherField : getEntity().getAllFields())
			{
				// check the the other xref fields
				if (otherField.getType().equals(Field.Type.XREF_SINGLE) && otherField != this)
				{
					// check all the labels of this other field
					Map<String, List<Field>> all_xref_labels = otherField.allPossibleXrefLabels();
					for (String other_label : all_xref_labels.keySet())
					{
						// check endpoint
						List<Field> pathToOtherEndpoint = all_xref_labels.get(other_label);
						Field otherEndPoint = pathToOtherEndpoint.get(pathToOtherEndpoint.size() - 1);

						if (endpoint.getName().equals(otherEndPoint.getName())
								&& endpoint.getEntity().getName().equals(otherEndPoint.getEntity().getName()))
						{
							logger.debug("FOUND " + otherEndPoint.getEntity().getName() + "."
									+ otherEndPoint.getName() + " EQUALS " + endpoint.getEntity().getName() + "."
									+ endpoint.getName());
							result.add(otherField.getName().toLowerCase() + "_" + other_label);
						}
					}
				}
			}
		}

		return result;
	}

	public Integer getLength() throws MolgenisModelException
	{
		if (this.getType().equals(Field.Type.STRING)) return this.getVarCharLength();
		return null;
	}

	public synchronized boolean isXrefCascade()
	{
		return xref_cascade;
	}

	public synchronized void setXrefCascade(boolean xrefCascade)
	{
		xref_cascade = xrefCascade;
	}
}
