package org.molgenis.xgap.other.xqtlworkbench_vanilla;


import org.molgenis.Molgenis;

public class XqtlWorkbenchGenerate
{
	public static void main(String[] args) throws Exception
	{
		try
		{
			new Molgenis("handwritten/apps/org/molgenis/xgap/xqtlworkbench/xqtlworkbench.properties").generate();
			
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
