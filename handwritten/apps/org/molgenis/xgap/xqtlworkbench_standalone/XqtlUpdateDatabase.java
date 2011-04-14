package org.molgenis.xgap.xqtlworkbench_standalone;
import java.io.File;

import org.molgenis.Molgenis;

public class XqtlUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		XqtlGenerate.deleteDirectory(new File("hsqldb"));
		new Molgenis("handwritten/apps/org/molgenis/xgap/xqtlworkbench_standalone/xqtl.properties").updateDb(true);
	}
}
