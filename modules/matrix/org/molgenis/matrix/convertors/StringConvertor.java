package org.molgenis.matrix.convertors;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.regex.Pattern;

public class StringConvertor implements ValueConvertor<String>
{
	private int stringLength = 255;
	private Pattern nullCharPattern =  null;
	
	public StringConvertor()
	{
		
	}
	
	@Override
	public String read(String value)
	{
		return value;
	}

	@Override
	public String write(String value)
	{
		return value;
	}

	@Override
	public Class<String> getValueType()
	{
		return String.class;
	}

	@Override
	public String read(RandomAccessFile raf) throws IOException
	{
		byte[] string = new byte[stringLength];
		raf.read(string);

		String result = new String(string);

		if (this.getNullCharPattern().matcher(result).matches())
		{
			result = "";
		}
		return result;
	}
	
	public Pattern getNullCharPattern()
	{
		return nullCharPattern;
	}
}
