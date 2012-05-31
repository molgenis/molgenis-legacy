package org.molgenis.hemodb;


import java.io.File;

import org.molgenis.Molgenis;
import org.molgenis.util.TarGz;

public class HemodbGenerate
{
	public static void main(String[] args) throws Exception
	{
		try
		{
			//TarGz.recursiveDeleteContentIgnoreSvn(new File("hsqldb"));
			new Molgenis("apps/hemodb/org/molgenis/hemodb/hemodb.properties").generate();
			
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
