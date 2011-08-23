package org.molgenis.xgap.other.xqtlworkbench_lifelines;


import org.molgenis.Molgenis;

public class XWBLLGenerate
{
	public static void main(String[] args) throws Exception
	{
		try
		{
			new Molgenis("apps/xgap/org/molgenis/xgap/other/xqtlworkbench_lifelines/xwbll.properties").generate();
			
		} catch(Exception e)
		{
			e.printStackTrace(); 
		}
	}
}
