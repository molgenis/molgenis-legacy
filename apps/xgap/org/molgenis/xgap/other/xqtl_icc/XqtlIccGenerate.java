package org.molgenis.xgap.other.xqtl_icc;


import java.io.File;

import org.molgenis.Molgenis;
import org.molgenis.util.TarGz;

public class XqtlIccGenerate
{

	  
	public static void main(String[] args) throws Exception
	{
		try
		{
			TarGz.recursiveDeleteContentIgnoreSvn(new File("hsqldb"));
			new Molgenis("apps/xgap/org/molgenis/xgap/other/xqtl_icc/xqtl_icc.properties").generate();
			
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
