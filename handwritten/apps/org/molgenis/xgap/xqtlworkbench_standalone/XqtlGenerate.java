package org.molgenis.xgap.xqtlworkbench_standalone;


import java.io.File;

import org.molgenis.Molgenis;
import org.molgenis.generators.Generator;
import org.molgenis.generators.cpp.CPPCassette;

public class XqtlGenerate
{
	
	  static public boolean deleteDirectory(File path) {
		    if( path.exists() ) {
		      File[] files = path.listFiles();
		      for(int i=0; i<files.length; i++) {
		         if(files[i].isDirectory()) {
		           deleteDirectory(files[i]);
		         }
		         else {
		           files[i].delete();
		         }
		      }
		    }
		    return( path.delete() );
		  }
	  
	public static void main(String[] args) throws Exception
	{
		try
		{
			deleteDirectory(new File("hsqldb"));
			new Molgenis("handwritten/apps/org/molgenis/xgap/xqtlworkbench_standalone/xqtl.properties").generate();
			
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
