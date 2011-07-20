package org.molgenis.phenoflow;


import org.molgenis.Molgenis;

import app.JDBCDatabase;


public class PhenoflowUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/phenoflow/phenoflow.properties").updateDb(true);
		
		JDBCDatabase db = new JDBCDatabase("apps/phenoflow/phenoflow.properties");
		
		//TODO : do batch import
	}
}
