package org.molgenis.col7a1;
import org.molgenis.Molgenis;

//import cmdline.CmdLineException;

public class Col7a1UpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("handwritten/apps/org/molgenis/col7a1/col7a1.properties").updateDb();
	}
}
