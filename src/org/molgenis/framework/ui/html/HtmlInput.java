package org.molgenis.framework.ui.html;

import java.text.ParseException;

import org.apache.commons.lang.StringEscapeUtils;
import org.molgenis.util.Tuple;

/**
 * HtmlInput is the base-class for all the 'toHtml' input classes.
 * 
 * TODO: parameterize value.
 * 
 * Implements TemplateDirectiveModel so it can be used as macro (if
 * toHtml(params) is implemented).
 * 
 */
public abstract class HtmlInput<E> implements Input<E>, HtmlRenderer
{
	public static final String NAME = "name";
	public static final String LABEL = "label";
	public static final String VALUE = "value";
	public static final String NILLABLE = "nillable";
	public static final String READONLY = "readonly";
	public static final String DESCRIPTION = "decription";

	/** Constant indicating use of JQUERY */
	public static boolean INJECT_JQUERY = true;

	/** The name of the input */
	private String name;

	/** The value of the input */
	private E value;

	/** The label of the input. Defaults to 'name'. */
	private String label;

	/**
	 * Flag indicating whether this input is readonly ( optional, default false
	 * )
	 */
	private boolean readonly;

	/** Flag indicating whether this input is hidden ( optional, default false ) */
	protected boolean hidden;

	/** String with a one-line description of the input ( optional ) */
	private String tooltip;

	/** The ID of this input. Defaults to 'name'. */
	private String id;

	/** The css class of this input. */
	private String clazz;

	/** variable for make-up */
	private String style;

	/** variable to validate size */
	private Integer size;

	/** for hyperlinks...??? */
	private String target = "";

	/** Description. Defaults to 'name'. */
	private String description;

	/** required form field */
	private boolean nillable = true;

	/** indicate if this input should be hidden in 'compact' view */
	private boolean collapse = false;

	protected String tabIndex = "";

	/**
	 * Standard constructor, which sets the name and the label for the
	 * html-input.
	 * 
	 * @param name
	 *            The name of the html-input.
	 * @param value
	 *            The value of the html-input.
	 */
	public HtmlInput(String name, E value)
	{
		assert (name != null);
		this.setName(name);
		this.setLabel(name);
		this.setDescription(name);
		this.setId(name);
		this.setValue(value);
	}

	public HtmlInput(String name, String label, E value, boolean nillable,
			boolean readonly, String description)
	{
		this(name, value);
		this.setLabel(label);
		this.setNillable(nillable);
		this.setReadonly(readonly);
		this.setDescription(description);
	}

	public HtmlInput(Tuple t) throws HtmlInputException
	{
		this.set(t);
	}

	@SuppressWarnings("unchecked")
	public void set(Tuple t) throws HtmlInputException
	{
		this.name = t.getString(NAME);
		this.label = t.getString(LABEL);
		this.value = (E)t.getObject(VALUE);
		if (t.getBool(NILLABLE) != null) this.nillable = t.getBool(NILLABLE);
		if (t.getBool(READONLY) != null) this.readonly = t.getBool(READONLY);
		this.description = t.getString(DESCRIPTION);

	}

	public HtmlInput()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * Implement this for generating the specific html-code for the html-input.
	 * All inheriting classes need to override this method in order to work in
	 * the same fashion.
	 * 
	 * @return The html-code for the html-input.
	 */
	public abstract String toHtml();

	public String getLabel()
	{
		if (label != null) return label;
		return name;
	}

	public void setLabel(String label)
	{
		assert (label != null);
		this.label = label;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	// TODO: This *needs* to be renamed to getValue()
	public E getObject()
	{
		return value;
	}

	// TODO: This *needs* to be renamed to getValueToString() or removed!!!
	public String getValue()
	{
		return getValue(true);
	}

	/**
	 * Get the value of the input as a String, optionally replacing special
	 * characters like \\n and &gt;
	 * 
	 * @param replaceSpechialChars
	 * @return
	 */
	public String getValue(boolean replaceSpechialChars)
	{
		if (getObject() == null)
		{
			return "";
		}

		// todo: why different from getHtmlValue()??
		if (replaceSpechialChars)
		{
			return getObject().toString().replace("\n", "<br>").replace("\r",
					"").replace(">", "&gt;").replace("<", "&lt;");
		}
		else
		{
			return getObject().toString();
		}
	}
	
	public void setValue(E value)
	{
		this.value = value;
	}

	public boolean isReadonly()
	{
		return readonly;
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

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getClazz()
	{
		return this.clazz;
	}

	public void setClazz(String clazz)
	{
		this.clazz = clazz;
	}

	public String getStyle()
	{
		return style;
	}

	public void setStyle(String style)
	{
		this.style = style;
	}

	public String getTooltip()
	{
		return tooltip;
	}

	public void setTooltip(String tooltip)
	{
		this.tooltip = tooltip;
	}

	public String getTarget()
	{
		return target.replace(".", "_");
	}

	public void setTarget(String target)
	{
		this.target = target.replace(".", "_");
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public boolean isNillable()
	{
		return nillable;
	}

	public void setNillable(boolean required)
	{
		this.nillable = required;
	}

	public String getHtmlValue(int maxLength)
	{
		return this.getHtmlValue().substring(0, 100);
	}

	public String getHtmlValue()
	{
		String value = null;
		value = this.getValue().replace("\n", "<br>").replace("\r", "")
				.replace(">", "&gt;").replace("<", "&lt;");
		return value;
	}

	public String getJavaScriptValue()
	{
		String value = StringEscapeUtils.escapeXml(StringEscapeUtils
				.escapeJavaScript(this.getValue()));
		return value;
	}

	public boolean isCollapse()
	{
		return collapse;
	}

	public void setCollapse(boolean collapse)
	{
		this.collapse = collapse;
	}

	public String toString()
	{
		return this.toHtml();
	}

	public String getHtml()
	{
		return toHtml();
	}

	public synchronized Integer getSize()
	{
		return size;
	}

	public synchronized void setSize(Integer size)
	{
		this.size = size;
	}

	public void setTabIndex(int tabidx)
	{
		tabIndex = " tabindex=" + Integer.toString(tabidx);
	}

	@Override
	public String render()
	{
		return this.toHtml();
	}

	public String getCustomHtmlHeaders()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Generic method to produce the inputs html by setting its parameters from
	 * tuple. This is used to create Freermarker macros like <@string name="id"
	 * /> Needs to be overriden to work.
	 * 
	 * @throws ParseException
	 * @throws HtmlInputException
	 */
	public String toHtml(Tuple params) throws ParseException,
			HtmlInputException
	{
		throw new UnsupportedOperationException();
	}
}
