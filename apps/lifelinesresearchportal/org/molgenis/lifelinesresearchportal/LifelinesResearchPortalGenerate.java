package org.molgenis.lifelinesresearchportal;


import org.molgenis.Molgenis;
import org.molgenis.generators.db.DatabaseFactoryGen;
import org.molgenis.generators.db.JDBCDatabaseGen;
import org.molgenis.generators.tests.TestCsvGen;
import org.molgenis.generators.tests.TestDataSetGen;
import org.molgenis.generators.tests.TestDatabaseGen;

public class LifelinesResearchPortalGenerate
{
	public static void main(String[] args) throws Exception
	{
		try
		{
//                    new Molgenis("apps/xgap/org/molgenis/xgap/other/xqtlworkbench_lifelines/xwbll.properties",  
//                            JDBCDatabaseGen.class,                            
//                            DatabaseFactoryGen.class, DatabaseFactoryGen.class, TestCsvGen.class, TestDataSetGen.class, TestDatabaseGen.class).generate();
                    new Molgenis("apps/lifelinesresearchportal/org/molgenis/lifelinesresearchportal/lifelinesresearchportal.properties").generate();
			
		} catch(Exception e)
		{
			e.printStackTrace(); 
		}
	}
}
