package org.molgenis.xgap.other.xqtl_gids;


import java.io.File;

import org.apache.commons.io.FileUtils;
import org.molgenis.Molgenis;
import org.molgenis.util.TarGz;

public class XqtlGidsGenerate
{

	  
	public static void main(String[] args) throws Exception
	{
		try
		{
			FileUtils.deleteDirectory(new File("hsqldb"));
			new Molgenis("modules/xgap/org/molgenis/xgap/other/xqtl_gids/xqtl_gids.properties").generate();
			
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
