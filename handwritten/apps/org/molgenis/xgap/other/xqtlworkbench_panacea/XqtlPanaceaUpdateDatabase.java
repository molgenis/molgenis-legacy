package org.molgenis.xgap.other.xqtlworkbench_panacea;
import org.molgenis.Molgenis;

public class XqtlPanaceaUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("handwritten/apps/org/molgenis/xgap/xqtlworkbench_panacea/xqtlpanacea.properties").updateDb(true);
	}
}
