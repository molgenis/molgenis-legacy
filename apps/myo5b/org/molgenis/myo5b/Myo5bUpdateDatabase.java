package org.molgenis.myo5b;
import org.molgenis.Molgenis;

//import cmdline.CmdLineException;

public class Myo5bUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/myo5b/org/molgenis/myo5b/myo5b.properties").updateDb(true);
	}
}
