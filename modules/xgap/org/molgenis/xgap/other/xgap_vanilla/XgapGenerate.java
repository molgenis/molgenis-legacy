package org.molgenis.xgap.other.xgap_vanilla;


import org.molgenis.Molgenis;

public class XgapGenerate
{
	public static void main(String[] args) throws Exception
	{
		try
		{
			new Molgenis("handwritten/apps/org/molgenis/xgap/xgap.properties").generate();
			
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
