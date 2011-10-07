package org.molgenis.xgap.other.xqtlworkbench_lifelines;
import org.molgenis.Molgenis;

public class XWBLLUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("org/molgenis/xgap/other/xqtlworkbench_lifelines/xwbll.properties").updateDb(false);
	}
}
