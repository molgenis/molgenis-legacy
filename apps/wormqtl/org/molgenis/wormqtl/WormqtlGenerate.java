package org.molgenis.wormqtl;


import java.io.File;

import org.apache.commons.io.FileUtils;
import org.molgenis.Molgenis;
import org.molgenis.util.TarGz;

public class WormqtlGenerate
{
	public static void main(String[] args) throws Exception
	{
		try
		{
			FileUtils.deleteDirectory(new File("hsqldb"));
			new Molgenis("apps/wormqtl/org/molgenis/wormqtl/wormqtl.properties").generate();
			
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
