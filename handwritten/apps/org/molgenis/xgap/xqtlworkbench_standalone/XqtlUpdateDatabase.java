package org.molgenis.xgap.xqtlworkbench_standalone;
import org.molgenis.Molgenis;

public class XqtlUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("handwritten/apps/org/molgenis/xgap/xqtlworkbench_standalone/xqtl.properties").updateDb(true);
	}
}
