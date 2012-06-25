package org.molgenis.xgap.other.xqtl_tifn;


import java.io.File;

import org.molgenis.Molgenis;
import org.molgenis.util.TarGz;

public class XqtlTifnGenerate
{

	  
	public static void main(String[] args) throws Exception
	{
		try
		{
			TarGz.recursiveDeleteContentIgnoreSvn(new File("hsqldb"));
			new Molgenis("apps/xgap/org/molgenis/xgap/other/xqtl_tifn/xqtl_tifn.properties").generate();
			
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
