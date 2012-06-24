package org.molgenis.tifn;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.molgenis.Molgenis;
import org.molgenis.util.TarGz;

public class TifnGenerate
{
	public static void main(String[] args) throws Exception
	{
		try
		{
			//TarGz.recursiveDeleteContentIgnoreSvn(new File("hsqldb"));
			FileUtils.deleteDirectory(new File("hsqldb"));
			new Molgenis("apps/tifn/org/molgenis/tifn/tifn.properties").generate();
			
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
