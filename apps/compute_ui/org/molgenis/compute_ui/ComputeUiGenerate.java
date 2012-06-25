package org.molgenis.compute_ui;


import org.molgenis.Molgenis;


public class ComputeUiGenerate
{
	public static void main(String[] args) throws Exception
	{
		Molgenis m = new Molgenis("apps/compute_ui/compute_ui.properties");
//		m.getGenerators().add(new ComputeContextListenerGen());
		
		m.generate();
	}
}