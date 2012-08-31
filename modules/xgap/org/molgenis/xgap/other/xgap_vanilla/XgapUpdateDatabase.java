package org.molgenis.xgap.other.xgap_vanilla;
import org.molgenis.Molgenis;

public class XgapUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("handwritten/apps/org/molgenis/xgap/xgap.properties").updateDb();
	}
}
