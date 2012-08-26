package org.molgenis.xgap.other.xqtlworkbench_vanilla;
import org.molgenis.Molgenis;

public class XqtlWorkbenchUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("handwritten/apps/org/molgenis/xgap/xqtlworkbench/xqtlworkbench.properties").updateDb();
	}
}
