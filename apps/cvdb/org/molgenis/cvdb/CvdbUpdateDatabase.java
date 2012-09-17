package org.molgenis.cvdb;
import org.molgenis.Molgenis;

//import cmdline.CmdLineException;

public class CvdbUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("handwritten/apps/org/molgenis/cvdb/cvdb.properties").updateDb(false);
	}
}
