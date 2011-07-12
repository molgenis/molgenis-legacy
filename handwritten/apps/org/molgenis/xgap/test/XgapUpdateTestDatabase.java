package org.molgenis.xgap.test;
import org.molgenis.Molgenis;

public class XgapUpdateTestDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("handwritten/apps/org/molgenis/xgap/xgap.test.properties").updateDb();
	}
}
