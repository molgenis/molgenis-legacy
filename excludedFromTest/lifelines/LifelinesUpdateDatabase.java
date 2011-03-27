package lifelines;


import org.molgenis.Molgenis;

public class LifelinesUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("handwritten/apps/org/molgenis/lifelines/lifelines.molgenis.properties").updateDb();
	}
}
