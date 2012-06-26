package org.molgenis.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesTuple extends SimpleTuple
{
	public PropertiesTuple(File f) throws FileNotFoundException, IOException
	{
		Properties p = new Properties();
		p.load(new FileInputStream(f));
		this.loadFromProperties(p);
	}
	
	public PropertiesTuple(Properties p)
	{
		this.loadFromProperties(p);
	}
	
	private void loadFromProperties(Properties p)
	{
		for(Object key: p.keySet())
		{
			this.set(key.toString(),p.get(key));
		}
	}
}
