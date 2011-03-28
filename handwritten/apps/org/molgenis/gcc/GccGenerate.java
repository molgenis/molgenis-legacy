package org.molgenis.gcc;


import org.molgenis.Molgenis;

public class GccGenerate
{
	public static void main(String[] args) throws Exception
	{
		try
		{
			new Molgenis("handwritten/apps/org/molgenis/gcc/gcc.properties").generate();
			//new Molgenis("handwritten/apps/org/molgenis/gcc/gcc.properties", JpaDataTypeGen.class).generate();
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
