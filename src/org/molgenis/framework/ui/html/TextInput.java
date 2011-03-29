/**
 * File: TextInput.java <br>
 * Copyright: Inventory 2000-2006, GBIC 2005, all rights reserved <br>
 * Changelog:
 * <ul>
 * <li>2006-03-08, 1.0.0, DI Matthijssen; Creation
 * <li>2006-05-14, 1.1.0, MA Swertz; Refectoring into Invengine.
 * </ul>
 * TODO look at the depreciated functions.
 */

package org.molgenis.framework.ui.html;

/**
 * Input for strings that renders as textarea.
 * Deprecated because merged with StringInput.
 */
public class TextInput extends StringInput
{
	public TextInput(String name)
	{
		this(name,null);
	}
	
	public TextInput(String name, Object value)
	{
		super(name, value);
		this.setMaxHeight(50);
		this.setMinHeight(3);
	}
	
//merged with StringInput

//	public String toHtml()
//	{
//		String readonly = (this.isReadonly()) ? "readonly class=\"readonly\" " : ""; 
//
//		if( this.isHidden() )
//		{
//			StringInput input = new StringInput(this.getName(), this.getValue());
//			input.setLabel(this.getLabel());
//			input.setDescription(this.getDescription());
//			input.setHidden(true);
//			return input.toHtml();
//		}
//		return "<textarea id=\""+this.getId()+"\" name=\""+this.getName()+"\" cols=\""+this.getWidth()+"\" rows=\""+this.getHeight()+"\" "+readonly+" >"+this.getValue()+"</textarea>" +
//				"<script>showTextInput(document.getElementById('"+this.getId()+"'),5,25);</script>";
//	}
//	
//	public int width;
//
//	public int getWidth()
//	{
//		return width;
//	}
//
//	public void setWidth(int width)
//	{
//		this.width = width;
//	}
//	
//	public int height;
//
//	public int getHeight()
//	{
//		return height;
//	}
//
//	public void setHeight(int height)
//	{
//		this.height = height;
//	}
}
