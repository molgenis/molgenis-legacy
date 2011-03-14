package org.molgenis.framework.ui.html;

/**
 * Input for string data. Renders as a <code>textarea</code>.
 */
public class StringInput extends HtmlInput
{
	public StringInput(String name)
	{
		this(name,null);
	}
	
	public StringInput(String name, Object value)
	{
		super(name, value);
		width = 50;
		height = 1;
		this.setMinHeight(1);
		this.setMaxHeight(25);
	}

	public String toHtml()
	{
		String readonly = (this.isReadonly()) ? "readonly class=\"readonly\" " : ""; 

		if( this.isHidden() )
		{
			return "<input name=\""+this.getName()+"\"type=\"hidden\" value=\""+this.getValue()+"\"/>";
		}
		return "<textarea id=\""+this.getId()+"\" name=\""+this.getName()+"\"  "+ (this.getSize() != null && this.getSize() > 0 ? "onfocus=\"startcounter(this, "+getSize()+")\" onblur=\"endcounter()\"" : "") +" cols=\""+this.getWidth()+"\" rows=\""+this.getHeight()+"\" "+readonly+" >"+this.getValue()+"</textarea>" +
				"<script>showTextInput(document.getElementById('"+this.getId()+"'),"+this.getMinHeight()+","+this.getMaxHeight()+");</script>";
	}
	
	public int width;

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}
	
	public int height;

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}
	
	private int minHeight = 1;

	public int getMinHeight()
	{
		return minHeight;
	}

	public void setMinHeight(int minHeight)
	{
		this.minHeight = minHeight;
	}
	
	private int maxHeight = 25;

	public int getMaxHeight()
	{
		return maxHeight;
	}

	public void setMaxHeight(int maxHeight)
	{
		this.maxHeight = maxHeight;
	}
	
	
	
	
	
	
	
//	public StringInput(String name)
//	{
//		this(name, null);
//	}
//
//	public StringInput(String name, Object value)
//	{
//		super(name, value);
//	}
//
//	@Override
//	public String toHtml()
//	{
//		String classAtt = (this.getClazz() != null ? this.getClazz() : "");
//		classAtt += (this.isReadonly()) ? " readonly " : "";
//		if (!this.isNillable() && INJECT_JQUERY)
//		{
//			if(classAtt.length()>0) classAtt += " ";
//			classAtt += "required";
//		}
//		// 'disabled' doesn't send the value. We need the value if it is
//		// key...therefore we use 'readonly'.
//
//		if (this.isHidden())
//		{
//			return "<input type=\"hidden\" id=\"" + getId() + "\" name=\"" + getName() + "\" value=\"" + getValue()
//					+ "\" />";
//		}
//
//		String attributes = "";
//		if (INJECT_JQUERY && getSize() != null) attributes += " maxlength=\"" + getSize() + "\"";
//		if (INJECT_JQUERY && getStyle() != null) attributes += " style=\"" + getStyle() + "\"";
//
//		return "<input type=\"text\" id=\"" + getId() + "\" class=\"" + classAtt + "\" name=\"" + getName()
//				+ "\" value=\"" + getValue() + "\" " + attributes + tabIndex + " />";
//	}
}
