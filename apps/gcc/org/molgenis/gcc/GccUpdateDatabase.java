package org.molgenis.gcc;


import org.molgenis.Molgenis;

public class GccUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/gcc/org/molgenis/gcc/gcc.properties").updateDb(true);
	}
}
