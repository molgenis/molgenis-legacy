package org.molgenis.xgap.test;


import org.molgenis.Molgenis;

public class XgapTestGenerate
{
	public static void main(String[] args) throws Exception
	{
		try
		{
			new Molgenis("handwritten/apps/org/molgenis/xgap/xgap.test.properties").generate();
			
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
