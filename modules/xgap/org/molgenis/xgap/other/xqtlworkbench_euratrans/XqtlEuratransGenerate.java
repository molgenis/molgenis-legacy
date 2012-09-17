package org.molgenis.xgap.other.xqtlworkbench_euratrans;


import org.molgenis.Molgenis;

public class XqtlEuratransGenerate
{
	public static void main(String[] args) throws Exception
	{
		try
		{
			new Molgenis("handwritten/apps/org/molgenis/xgap/xqtlworkbench_euratrans/xqtleuratrans.properties").generate();
			
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
