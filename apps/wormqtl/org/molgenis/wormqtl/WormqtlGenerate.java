package org.molgenis.wormqtl;


import java.io.File;

import org.molgenis.Molgenis;
import org.molgenis.util.TarGz;

public class WormqtlGenerate
{
	public static void main(String[] args) throws Exception
	{
		try
		{
			TarGz.recursiveDeleteContentIgnoreSvn(new File("hsqldb"));
			new Molgenis("apps/wormqtl/org/molgenis/wormqtl/wormqtl.properties").generate();
			
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
