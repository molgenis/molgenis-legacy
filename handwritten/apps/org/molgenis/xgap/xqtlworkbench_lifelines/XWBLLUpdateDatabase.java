package org.molgenis.xgap.xqtlworkbench_lifelines;
import org.molgenis.Molgenis;

public class XWBLLUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("handwritten/apps/org/molgenis/xgap/xqtlworkbench_lifelines/xwbll.properties").updateDb(true);
	}
}
