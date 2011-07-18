package org.molgenis.gcc;

import org.molgenis.Molgenis;
import org.molgenis.generators.doc.DotDocModuleDependencyGen;

public class GccGenerateDocsOnly
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/gcc/org/molgenis/gcc/gcc.properties",
				DotDocModuleDependencyGen.class).generate();
		
//		new Molgenis("modules/gcc/org/molgenis/gcc/gcc.properties",
//				DotDocGen.class, FileFormatDocGen.class,
//				DotDocMinimalGen.class, ObjectModelDocGen.class).generate();
	}
}
