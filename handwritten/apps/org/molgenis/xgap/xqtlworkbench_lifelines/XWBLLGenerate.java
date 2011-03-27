package org.molgenis.xgap.xqtlworkbench_lifelines;


import org.molgenis.Molgenis;

public class XWBLLGenerate
{
	public static void main(String[] args) throws Exception
	{
		try
		{
			new Molgenis("handwritten/apps/org/molgenis/xgap/xqtlworkbench_lifelines/xwbll.properties").generate();
			
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
