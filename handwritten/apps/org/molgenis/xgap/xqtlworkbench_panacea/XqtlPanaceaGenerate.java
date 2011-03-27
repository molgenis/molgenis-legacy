package org.molgenis.xgap.xqtlworkbench_panacea;


import org.molgenis.Molgenis;

public class XqtlPanaceaGenerate
{
	public static void main(String[] args) throws Exception
	{
		try
		{
			new Molgenis("handwritten/apps/org/molgenis/xgap/xqtlworkbench_panacea/xqtlpanacea.properties").generate();
			
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
