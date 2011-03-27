package lifelines;


import org.molgenis.Molgenis;


public class LifelinesGenerate
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("excludedFromTest/lifelines/lifelines.molgenis.properties").generate();
	}
}
