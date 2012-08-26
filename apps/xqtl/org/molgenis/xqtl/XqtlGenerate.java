package org.molgenis.xqtl;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.molgenis.Molgenis;

public class XqtlGenerate
{

	// static public boolean deleteDirectory(File path) {
	// if( path.exists() && !path.toString().contains(".svn") ) {
	// File[] files = path.listFiles();
	// for(int i=0; i<files.length; i++) {
	// if(files[i].isDirectory()) {
	// deleteDirectory(files[i]);
	// }
	// else {
	// files[i].delete();
	// }
	// }
	// }
	// return( path.delete() );
	// }

	public static void main(String[] args) throws Exception
	{
		try
		{
			FileUtils.deleteDirectory(new File("hsqldb"));
			new Molgenis("apps/xqtl/org/molgenis/xqtl/xqtl.properties").generate();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
