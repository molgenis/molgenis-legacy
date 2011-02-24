package org.molgenis.framework.ui.html;

/**
 * The ActionInput defines action buttons.
 * When clicked, it will result in a new request(__action=&lt;name&gt;)
 */
public class ActionInput extends HtmlInput
{
	public enum Type
	{
		/** save current record */
		SAVE("Save"),
		/** cancel current action (closes popup window, refreshes opener window) */
		CANCEL("Cancel"),
		/** goto next screen */
		NEXT("Next"),
		// close current screen
		CLOSE("Close"),
		/** automatically close current dialogue */
		AUTOCLOSE("Autoclose"),
		/**
		 * Default: a custom action that requires you to set label, tooltip and
		 * javascript yourself
		 */
		CUSTOM("Set Label, Tooltip, and JavaScriptAction yourself");

		private Type(String tag)
		{
			this.tag = tag;
		}

		public final String tag;

		public String toString()
		{
			return tag;
		}
	}

	/** Path to an icon image */
	private String icon;

	/** Type of submit */
	private Type type;

	/** JavaScript action */
	private String JavaScriptAction;
	
	/** Text to display on button (normally "value") */
	private String buttonValue;

	// constructor(s)
	/**
	 * Default constructor, type is submit
	 */
	public ActionInput(String name)
	{
		this(name, Type.CUSTOM);
	}

	/**
	 * Constructor that sets action name and label.
	 * @param name
	 * @param label
	 */
	public ActionInput(String name, String label)
	{
		this(name, Type.CUSTOM);
		this.setLabel(label);
		this.setButtonValue(label); // override default button value (name) with label
	}
	
	/**
	 * Constructor that sets action name, label and button value (text to show on button).
	 * @param name
	 * @param label
	 */
	public ActionInput(String name, String label, String buttonValue)
	{
		this(name, label);
		this.setTooltip(buttonValue);
		this.setButtonValue(buttonValue); // override label as button value with explicit button value
	}

	/**
	 * Create a new instance of ActionInput.
	 * 
	 * @param name
	 *            name of the input.
	 * @param type
	 *            type of the input. @see Type
	 */
	public ActionInput(String name, Type type)
	{
		super(name, type);
		this.setType(type);
		this.setLabel(type.toString());
		this.setTooltip(type.toString());
		this.setButtonValue(name); // specific for action buttons
	}

	public ActionInput(Type select_target)
	{
		this(select_target.toString());
		this.setLabel(select_target.toString().replace("_", " "));
	}

	// HtmlInput overloads
	@Override
	public String toHtml()
	{
		StringBuffer input = new StringBuffer("");

		// TODO: apparantly this can be disabled.
		if (getIcon() != null)
		{
			input.append("<img class=\"edit_button\" src=\"" + getIcon()
					+ "\" alt=\"" + getLabel() + "\" onclick=\""
					+ getJavaScriptAction() + "\" title=\"" + this.getTooltip()
					+ "\" id=\"" + this.getId() + "\" style=\""
					+ this.getStyle() + "\" " + tabIndex + " />");
		}
		else
		{
			input.append("<input type=\"submit\" onclick=\""
					+ getJavaScriptAction() + "\" title=\"" + this.getTooltip()
					+ "\" id=\"" + this.getId() + "\"" + "value=\""
					+ this.getButtonValue() + "\" style=\"" + this.getStyle() + "\" "
					+ tabIndex + " />");
		}

		return input.toString();
	}

	// attribute methods
	/**
	 * Get the icon that is shown on this button.
	 */
	public String getIcon()
	{
		return icon;
	}

	/**
	 * Set the icon that should be shown on this button
	 * @param icon relative path from WebContent or classpath.
	 */
	public void setIcon(String icon)
	{
		this.icon = icon;
	}

	/**
	 * Javascript action for this button.
	 * 
	 * @return onClick javascript
	 */
	public String getJavaScriptAction()
	{
		if (JavaScriptAction == null)
		{
			if (this.type == Type.SAVE)
			{
				StringBuffer jScript = new StringBuffer();
				jScript
						.append("if( validateForm(molgenis_popup,molgenis_required) ) { if( window.opener.name == '' ){ window.opener.name = 'molgenis'+Math.random();} document.forms.molgenis_popup.target = window.opener.name; document.forms.molgenis_popup.submit(); window.close();} else return false;");
				return jScript.toString();
			}
			else if (this.type == Type.NEXT)
			{
				StringBuffer jScript = new StringBuffer();
				jScript
						.append("if( validateForm(molgenis_popup,molgenis_required) ) { if( window.opener.name == '' ){ window.opener.name = 'molgenis'+Math.random();} document.forms.molgenis_popup.__show.value='popup'; document.forms.molgenis_popup.submit();} else return false;");
				return jScript.toString();
			}
			else if (this.type == Type.CLOSE)
			{
				return "window.close();";
			}
			else if (this.type == Type.CUSTOM)
			{
				return "__action.value = \'" + getName() + "'; return true;";
			}
		}
		return JavaScriptAction;
	}

	/**
	 * Override default javascript 'onClick' action.
	 * 
	 * @param javaScriptAction
	 */
	public void setJavaScriptAction(String javaScriptAction)
	{
		JavaScriptAction = javaScriptAction;
	}

	/**
	 * The Type of this action
	 * @return type
	 * @see Type
	 */
	public Type getType()
	{
		return type;
	}

	/** Set the Type of this action, e.g. SAVE.*/
	public void setType(Type type)
	{
		this.type = type;
	}

	@Override
	public String getLabel()
	{
		if (super.getLabel() == super.getValue()) return getName();
		return super.getLabel();
	}
	
	public String getButtonValue()
	{
		return buttonValue;
	}
	
	public void setButtonValue(String buttonValue)
	{
		this.buttonValue = buttonValue;
	}
	
	/** Helper method to produce the html for the icon (&lt;img&gt;)*/
	public String getIconHtml()
	{
		// TODO Auto-generated method stub
		return "<img src=\"" + this.getIcon() + "\"/>";
	}

	/** Helper method to produce html for the clickable image*/
	public String toIconHtml()
	{
		return "<img class=\"edit_button\" src=\"" + getIcon() + "\" title=\""
				+ getLabel() + "\" onClick=\"" + this.getJavaScriptAction()
				+ "\">";
		// <img class="edit_button" src="generated-res/img/recordview.png"
		// title="view record" alt="edit${offset}"
		// onClick="setInput('${screen.name}_form','_self','','${screen.name}','recordview','iframe'); document.forms.${screen.name}_form.__offset.value='${offset}'; document.forms.${screen.name}_form.submit();">${readonly}</label>
	}

	/** Helper method to render this button as clickable link*/
	public String toLinkHtml()
	{
		return "<a title=\"" + this.getDescription() + "\" onclick=\""
				+ this.getJavaScriptAction() + "\">" + getLabel() + "</a>";
	}
}
