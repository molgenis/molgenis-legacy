/**
 * File: invengine.screen.form.FileInput <br>
 * Copyright: Inventory 2000-2006, GBIC 2005, all rights reserved <br>
 * Changelog:
 * <ul>
 * <li> 2005-05-14; 1.0.0; MA Swertz Creation.
 * </ul>
 * TODO: make efficient.
 */
package org.molgenis.framework.ui.html;

/**
 * Input for upload of files.
 */
public class FileInput extends HtmlInput
{
	public static final String INPUT_CURRENT_DOWNLOAD = "__filename";
	public static final String ACTION_DOWNLOAD = "download";
	
	/** Entity name, needed for download */
	private String entityname;

	public FileInput(String name, Object value)
	{
		super(name, value);
	}

	@Override
	public String toHtml()
	{		
		//FIXME how to check not null file uploads
		this.setNillable(true);
		String readonly = ( isReadonly() ? " class=\"readonly\" readonly " : "");
		
		StringInput hidden = new StringInput(this.getName(), super.getValue());
		hidden.setLabel(this.getLabel());
		hidden.setDescription(this.getDescription());	
		hidden.setHidden(true);
		
		if (this.isHidden())
		{
			return hidden.toHtml();
		}
		
		return hidden.toHtml() + "<input type=\"file\" "+readonly+"name=\"filefor_" + getName() + "\" size=\"20\">" + getValue();	
	}
	
	/**
	 * {@inheritDoc}. Extended to show download button.
	 */
	public String getValue()
	{
		if(super.getValue() != "")
			return super.getValue()+"<input class=\"manbutton\" type=\"image\" src=\"generated-res/img/download.png\" alt=\"download\" onclick=\"this.form.__filename.value = '"+super.getValue()+"';this.form.__action.value='"+ACTION_DOWNLOAD+"'; return true;\"/>";
		return super.getValue();
	}

	/** Retrieve the name of the entity for wich a download has to be started
	 * 
	 * @return entity name
	 */
	public String getEntityname()
	{
		return entityname;
	}

	/**
	 * Set the entity for which this file can be downloaded/.
	 * @param entityname
	 */
	public void setEntityname( String entityname )
	{
		this.entityname = entityname;
	}
}
