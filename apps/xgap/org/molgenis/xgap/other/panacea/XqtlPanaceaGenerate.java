package org.molgenis.xgap.other.panacea;


import java.io.File;

import org.molgenis.Molgenis;
import org.molgenis.util.TarGz;

public class XqtlPanaceaGenerate
{
	public static void main(String[] args) throws Exception
	{
		try
		{
			TarGz.recursiveDeleteContentIgnoreSvn(new File("hsqldb"));
			new Molgenis("apps/xgap/org/molgenis/xgap/other/panacea/xqtlpanacea.properties").generate();
			
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
