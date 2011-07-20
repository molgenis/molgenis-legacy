package org.molgenis.xgap.xqtlworkbench;


import java.io.File;

import org.molgenis.Molgenis;

public class XqtlGenerate
{
	
	  static public boolean deleteDirectory(File path) {
		  if( path.exists() && !path.toString().contains(".svn") ) {
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
			new Molgenis("apps/xgap/org/molgenis/xgap/xqtlworkbench/xqtl.properties").generate();
			
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
