package org.molgenis.gcc;

import org.molgenis.Molgenis;
import org.molgenis.generators.db.PersistenceGen;
import org.molgenis.generators.tests.TestCsvGen;

public class GccGenerate
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/gcc/org/molgenis/gcc/gcc.properties").generate();
	}
}
