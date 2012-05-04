/**
 * File: invengine.screen.form.SelectInput <br>
 * Copyright: Inventory 2000-2006, GBIC 2005, all rights reserved <br>
 * Changelog:
 * <ul>
 * <li>2006-03-07, 1.0.0, DI Matthijssen
 * <li>2006-05-14; 1.1.0; MA Swertz integration into Inveninge (and major
 * rewrite)
 * <li>2006-05-14; 1.2ƒ.0; RA Scheltema major rewrite + cleanup
 * </ul>
 */

package org.molgenis.framework.ui.html;

// jdk
import java.text.ParseException;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

/**
 * Input for cross-reference (xref) entities in a MOLGENIS database. Data will
 * be shown as selection box. Use xrefEntity to specifiy what entity provides
 * the values for selection. Use xrefField to define which entity field to use
 * for the values. Use xrefLabels to select which field(s) should be shown as
 * labels to the user (optional).
 */
public class XrefInput<E extends Entity> extends AbstractRefInput<E>
{
	public static class Builder<E extends Entity> extends AbstractRefInput.Builder<E> {
		public Builder(Class<E> xrefEntity) {
			super(xrefEntity, (E)null);
		}
		
		public Builder(final String name, Class<E> xrefEntity, E value) {
			super(xrefEntity, value);
		}
		
		public Builder(String name, E object)
		{
			super((Class<E>) object.getClass(), object);
		}

		public XrefInput<E> build() {
			final XrefInput<E> xrefInput = new XrefInput<E>(this, UUID.randomUUID().toString());
			return xrefInput;
		}
		
		public XrefInput<E> build(String id) {
			final XrefInput<E> xrefInput = new XrefInput<E>(this, id);
			return xrefInput;
		}
	}

	private XrefInput(final Builder<E> builder, final String id) {
		super(builder, id);
	}	
	
	protected XrefInput() {
		super();
	}
	
	/** Minimal constructor */
	@Deprecated
	public XrefInput(String name,
			Class<? extends Entity> xrefEntityClass, E value)
	{
		super(name, xrefEntityClass, value);
		setXrefEntity(xrefEntityClass);
	}

	/** Alternative minimal constructor using an entity class to configure all. */
	@Deprecated
	public XrefInput(String name, Class<E> xrefEntityClass)
	{
		super(name, xrefEntityClass, null);
		setXrefEntity(xrefEntity);
	}

	/**
	 * Alternative minimal constructor using entity name
	 * 
	 * @throws ClassNotFoundException
	 * @throws ClassNotFoundException
	 * @throws ClassNotFoundException
	 */
//	@Deprecated
//	public XrefInput(String name, String entityClassname) throws HtmlInputException, ClassNotFoundException
//	{
//		super(name, null);
//		super.setXrefEntity(getXref)
//	}

	/** Complete constructor */
	@Deprecated
	public XrefInput(String name, String label, E value, Boolean nillable,
			Boolean readonly, String description,
			Class<? extends Entity> xrefEntityClass)
	{
		super(name, xrefEntityClass, label, value, nillable, readonly, description);
		setXrefEntity(xrefEntityClass);
	}

//	/** Alternative complete constructor using String name of entityClass */
//	@Deprecated
//	public XrefInput(String name, String label, Entity value, Boolean nillable,
//			Boolean readonly, String description, String xrefEntityClass)
//			throws HtmlInputException
//	{
//		super(name, label, value, nillable, readonly, description, xrefEntityClass);
//	}

	/**
	 * Constructor taking parameters from tuple
	 * 
	 * @throws HtmlInputException
	 */
	public XrefInput(Tuple t) throws HtmlInputException
	{
		super(t);
	}

//	protected XrefInput()
//	{
//	}

	

	@Override
	/**
	 * Returns the label of the selected value.
	 */
	public String getValue()
	{
		if (getObject() != null) 
			return this.getObject().getLabelValue();
		return StringUtils.EMPTY;
	}

	@Override
	public String toHtml(Tuple params) throws ParseException,
			HtmlInputException
	{
		return new XrefInput(params).render();
	}
	
	public void set(Tuple t) throws HtmlInputException
	{
		super.set(t);
		if (t.isNull(XREF_ENTITY)) throw new HtmlInputException(
				"parameter " + XREF_ENTITY + " cannot be null");
		else
			this.setXrefEntity(t.getString(XREF_ENTITY));
	}

	@SuppressWarnings("unchecked")
	@Deprecated
	public XrefInput(String name, String entityClassname)
			throws HtmlInputException, ClassNotFoundException
	{
		this(name, (Class<E>) Class.forName(entityClassname), null);
	}
	
	@Override
	protected String renderOptions()
	{
		final StringBuilder options = new StringBuilder();
		options.append(String.format("\t<option selected value=\"%s\">%s</option>\n", getObject().getIdValue(), this.getValue()));
		return options.toString();
	}

	@Override
	protected String getHtmlRefType()
	{
		return "search";
	}
}
