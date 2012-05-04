/**
 * File: invengine.screen.form.SelectInput <br>
 * Copyright: Inventory 2000-2006, GBIC 2005, all rights reserved <br>
 * Changelog:
 * <ul>
 * <li>2006-03-07, 1.0.0, DI Matthijssen
 * <li>2006-05-14; 1.1.0; MA Swertz integration into Inveninge (and major
 * rewrite)
 * <li>2006-05-14; 1.2.0; RA Scheltema major rewrite + cleanup
 * </ul>
 */

package org.molgenis.framework.ui.html;

import java.util.List;
import java.util.UUID;

import org.molgenis.framework.ui.html.XrefInput.Builder;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

/**
 * Input for many-to-many cross-references (xref) to choose data entities from
 * the database. Selectable data items will be shown as selection box and are
 * loaded dynamically via an 'ajax' service.
 */
public class MrefInput<E extends Entity> extends AbstractRefInput<List<E>>
{
	public static class Builder<E extends Entity> extends AbstractRefInput.Builder<List<E>> {
		public Builder(Class<E> mrefEntity) {
			super(mrefEntity, (List<E>)null);
		}
		
		public Builder(final String name, Class<E> mrefEntity, List<E> value) {
			super(mrefEntity, value);
		}
		
		public Builder(String name, List<E> object)
		{
			super((Class<E>) object.getClass(), object);
		}

		public MrefInput<E> build() {
			final MrefInput<E> mrefInput = new MrefInput<E>(this, UUID.randomUUID().toString());
			return mrefInput;
		}
		
		public MrefInput<E> build(String id) {
			final MrefInput<E> mrefInput = new MrefInput<E>(this, id);
			return mrefInput;
		}
	}
	
	private MrefInput(final Builder<E> builder, final String id) {
		super(builder, id);
	}
	/** Minimal constructor */
	@Deprecated
	public MrefInput(String name, Class<? extends Entity> xrefEntityClass,
			List<E> dummyList)
	{
		super(name, xrefEntityClass, dummyList);
		setXrefEntity(xrefEntityClass);
	}

	/**
	 * Alternative minimal constructor using an entity object instance to
	 * configure all.
	 */
	@Deprecated
	public MrefInput(String name, List<E> objects)
	{
		this(name, objects.get(0).getClass(), objects);
		setXrefField(name);
	}

	/** Alternative minimal constructor using an entity class to configure all. */
	@Deprecated
	public MrefInput(String name, Class<? extends Entity> xrefEntityClass)
	{
		super(name, xrefEntityClass, null);
		setXrefEntity(xrefEntityClass);
	}

	/**
	 * Alternative minimal constructor using entity name
	 * 
	 * @throws HtmlInputException
	 * 
	 * @throws ClassNotFoundException
	 * @throws ClassNotFoundException
	 */
//	@Deprecated
//	public MrefInput(String name, String entityName) throws HtmlInputException
//	{
//		super(name, entityName);
//	}

	/** Complete constructor */
	@Deprecated
	public MrefInput(String name, String label, List<E> values,
			Boolean nillable, Boolean readonly, String description,
			Class<? extends Entity> xrefEntityClass)
	{
		super(name, xrefEntityClass, label, values, nillable, readonly, description);
		setXrefEntity(xrefEntityClass);
	}

	/**
	 * Alternative complete constructor using String name of entityClass
	 * 
	 * @throws HtmlInputException
	 * @throws ClassNotFoundException 
	 */
	@Deprecated
	public MrefInput(String name, String label, List<E> values,
			Boolean nillable, Boolean readonly, String description,
			String xrefEntityClass) throws HtmlInputException, ClassNotFoundException
	{
		super(name, (Class<? extends Entity>) Class.forName(xrefEntityClass) ,label, values, nillable, readonly, description);
		setXrefEntity(xrefEntityClass);
	}

	/**
	 * Constructor taking parameters from tuple
	 * 
	 * @throws HtmlInputException
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public MrefInput(Tuple t) throws HtmlInputException
	{	
		super(t);
	}

	@Deprecated
	protected MrefInput()
	{
		super();
	}

	@Override
	/**
	 * Note, this returns the labels of the selected values.
	 */
	public String getValue()
	{
		String result = "";
		for (Entity value : getObject())
		{
			if (result.toString().equals("")) result += value.getLabelValue();
			else
				result += ", " + value.getLabelValue();
		}
		return result;
	}


	@Override
	public String toHtml(Tuple params) throws HtmlInputException
	{
		return new MrefInput(params).render();
	}

	@Override
	protected String renderOptions()
	{
		final String option = "\t<option selected value=\"%s\">%s</option>\n";
		
		final StringBuilder result = new StringBuilder();
		for (Entity value : getObject())
		{
			result.append(
					String.format(option, value.getIdValue(), value.getLabelValue())
			);
		}
		
		return result.toString(); 
	}

	@Override
	protected String getHtmlRefType()
	{
		return "multiple";
	}

}
