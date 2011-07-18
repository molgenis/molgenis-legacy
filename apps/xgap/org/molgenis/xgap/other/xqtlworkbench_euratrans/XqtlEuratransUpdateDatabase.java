package org.molgenis.xgap.other.xqtlworkbench_euratrans;
import org.molgenis.Molgenis;

public class XqtlEuratransUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("handwritten/apps/org/molgenis/xgap/xqtlworkbench_euratrans/xqtleuratrans.properties").updateDb();
	}
}
