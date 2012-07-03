package org.molgenis.compute_ui;


import org.molgenis.Molgenis;
import org.molgenis.framework.db.jpa.JpaUtil;

public class ComputeUiUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/compute_ui/compute_ui.properties").updateDb(false);
	}
}
