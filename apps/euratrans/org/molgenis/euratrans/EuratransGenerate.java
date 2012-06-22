package org.molgenis.euratrans;


import java.io.File;

import org.molgenis.Molgenis;
import org.molgenis.util.TarGz;

public class EuratransGenerate
{
	public static void main(String[] args) throws Exception
	{
		try
		{
			TarGz.recursiveDeleteContentIgnoreSvn(new File("hsqldb"));
			new Molgenis("apps/euratrans/org/molgenis/euratrans/euratrans.properties").generate();
			
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
