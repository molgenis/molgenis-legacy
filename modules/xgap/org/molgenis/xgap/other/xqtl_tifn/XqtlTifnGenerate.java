package org.molgenis.xgap.other.xqtl_tifn;


import java.io.File;

import org.apache.commons.io.FileUtils;
import org.molgenis.Molgenis;
import org.molgenis.util.TarGz;

public class XqtlTifnGenerate
{

	  
	public static void main(String[] args) throws Exception
	{
		try
		{
			FileUtils.deleteDirectory(new File("hsqldb"));
			new Molgenis("modules/xgap/org/molgenis/xgap/other/xqtl_tifn/xqtl_tifn.properties").generate();
			
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
