package org.molgenis.framework.ui.html;

/**
 * (Incomplete) Input for formatted sequences.
 */
public class NsequenceInput extends TextInput
{

	public NsequenceInput(String name, Object value)
	{
		super(name, value);
		super.setWidth(100);
		super.setHeight(6);
	}

	@Override
	public String getValue()
	{
		String value = "";
		if( getObject() == null )
			return value;
		
		value = getObject().toString();		
		String newvalue = "";
		
		for (int i = 0; i < value.length(); i+=80)
		{
			String line = (i+80 < value.length()) ? value.substring(i, i+80) : value.substring(i, value.length());			
			String newline = "";
			if (i < 10)
				newline += "&nbsp;";
			if (i < 100)
				newline += "&nbsp;";
			if (i < 1000)
				newline += "&nbsp;";
			newline += (i+1);
			
			for (int j = 0; j < line.length(); j+=10)
			{
				String part = (j+10 < line.length()) ? line.substring(j, j+10) : line.substring(j, line.length());
				newline += "&nbsp;" + part;				
			}
			newvalue += newline + "\n";
		}
		
		return newvalue;
	}
	
	public String getHtmlValue()
	{
		return "<span class=\"seqQual\">" + this.getValue().replace("\n","<br>") + "</span>";
	}

}
