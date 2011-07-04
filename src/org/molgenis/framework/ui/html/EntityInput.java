package org.molgenis.framework.ui.html;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.server.QueryRuleUtil;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

/** Superclass for inputs that use AJAX to retrieve entities from the database. */
public abstract class EntityInput<E> extends HtmlInput<E>
{
	public static final String XREF_ENTITY = "xref_entity";

	private String xrefEntity;
	private String xrefField;

	private List<QueryRule> xrefFilters = new ArrayList<QueryRule>();
	private List<String> xrefLabels = new ArrayList<String>();

	protected String error = null;

	public EntityInput(String name, Class<? extends Entity> xrefEntityClass,
			E value)
	{
		this(name, null, value, false, false, null, xrefEntityClass);

	}
	
	public EntityInput(String name, String label, E value,
			Boolean nillable, Boolean readonly, String description,
			String xrefEntityClass) throws HtmlInputException
	{
		this(name,label,value,nillable,readonly,description, getEntityClass(xrefEntityClass));
	}

	public EntityInput(String name, String label, E value,
			boolean nillable, boolean readonly, String description,
			Class<? extends Entity> xrefEntityClass)
	{
		super(name, label, value, nillable, readonly, description);
		this.setXrefEntity(xrefEntityClass);
 	}

	public EntityInput(Tuple t) throws HtmlInputException
	{
		set(t);
	}
	
	public void set(Tuple t) throws HtmlInputException
	{
		super.set(t);
		if (t.isNull(XREF_ENTITY)) throw new HtmlInputException(
				"parameter " + XREF_ENTITY + " cannot be null");
		else
			this.setXrefEntity(t.getString(XREF_ENTITY));
	}

	public EntityInput(String name, String entityName)
			throws HtmlInputException
	{
		this(name, getEntityClass(entityName), null);
	}

	@SuppressWarnings("unchecked")
	private static Class<? extends Entity> getEntityClass(String entityName)
			throws HtmlInputException
	{
		try
		{
			return (Class<? extends Entity>) Class.forName(entityName);
		}
		catch (Exception e)
		{
			throw new HtmlInputException("creation failed: entity_name='"
					+ entityName + "' not known. Did you include package names?");
		}

	}

	protected EntityInput()
	{
	}

	public EntityInput(String name, Class<? extends Entity> entityClass)
	{
		// TODO Auto-generated constructor stub
	}

	public String getXrefEntity()
	{
		return xrefEntity;
	}

	/**
	 * Set the entity where this xref should get its values from
	 * 
	 * @param xrefEntity
	 * @throws HtmlInputException 
	 */
	public <E extends Entity> void setXrefEntity(Class<E> xrefEntity)
	{
		try
		{
			Entity instance = xrefEntity.newInstance();
			this.xrefField = instance.getIdField();
			this.xrefLabels = instance.getLabelFields();
			this.xrefEntity = xrefEntity.getName();
		}
		catch (Exception e)
		{
			this.error = e.getMessage();
			e.printStackTrace();
		}
	}

	/**
	 * Set the entity where this xref should get its values from
	 * 
	 * @param xrefEntity
	 * @throws HtmlInputException 
	 */
	public void setXrefEntity(String xrefEntity) throws HtmlInputException
	{
		this.setXrefEntity(getEntityClass(xrefEntity));
	}

	public String getXrefField()
	{
		return xrefField;
	}

	/**
	 * Set the entity field (i.e. database column) that this xref should get its
	 * values from. For example 'id'.
	 * 
	 * @param xrefField
	 *            field name
	 */
	public void setXrefField(String xrefField)
	{
		this.xrefField = xrefField;
	}

	public List<String> getXrefLabels()
	{
		return xrefLabels;
	}

	/**
	 * Set the entity field (i.e. database column) that provides the values that
	 * should be shown to the user as options in the xref select box. For
	 * example 'name'.
	 * 
	 * @param xrefLabel
	 *            field name
	 */
	public void setXrefLabel(String xrefLabel)
	{
		assert (xrefLabel != null);
		this.xrefLabels.clear();
		this.xrefLabels.add(xrefLabel);
	}

	/**
	 * In case of entities with multiple column keys you can also have multiple
	 * labels concatenated together. For example 'investigation_name, name'.
	 * 
	 * @param xrefLabels
	 *            a list of field names
	 */
	public void setXrefLabels(List<String> xrefLabels)
	{
		assert (xrefLabels != null);
		this.xrefLabels = xrefLabels;
	}

	public List<QueryRule> getXrefFilters()
	{
		return xrefFilters;
	}

	public void setXrefFilters(List<QueryRule> xrefFilter)
	{
		this.xrefFilters = xrefFilter;
	}
	
	// returns filters as filter string
	public String getXrefFilterRESTString()
	{
		return QueryRuleUtil.toRESTstring(xrefFilters);
	}

}
