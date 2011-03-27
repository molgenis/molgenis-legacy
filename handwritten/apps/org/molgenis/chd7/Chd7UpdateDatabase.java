package org.molgenis.chd7;
import org.molgenis.Molgenis;

//import cmdline.CmdLineException;

public class Chd7UpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("handwritten/apps/org/molgenis/chd7/chd7.properties").updateDb(true);
	}
}
