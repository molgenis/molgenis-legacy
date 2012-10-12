package org.molgenis.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

public class PropertiesTuple extends SimpleTuple
{
	public PropertiesTuple(File f) throws FileNotFoundException, IOException
	{
		Properties p = new Properties();
		InputStream in = new FileInputStream(f);
		try
		{
			p.load(in);
		}
		finally
		{
			IOUtils.closeQuietly(in);
		}
		this.loadFromProperties(p);
	}

	public PropertiesTuple(Properties p)
	{
		this.loadFromProperties(p);
	}

	private void loadFromProperties(Properties p)
	{
		for (Object key : p.keySet())
		{
			this.set(key.toString(), p.get(key));
		}
	}
}
