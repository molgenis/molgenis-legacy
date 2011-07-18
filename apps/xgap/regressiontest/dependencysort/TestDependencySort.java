package regressiontest.dependencysort;

import java.util.ArrayList;
import java.util.Vector;

import org.junit.Test;
import org.molgenis.MolgenisOptions;
import org.molgenis.model.MolgenisModel;
import org.molgenis.model.elements.Entity;
import org.molgenis.model.elements.Model;


public class TestDependencySort {
	@Test
	public void TestSort() throws Exception
	{
	
		Model model = MolgenisModel.parse(new MolgenisOptions("xgap.properties"));
		
		Vector<Entity> toSort = model.getEntities();
		MolgenisModel.sortEntitiesByDependency(toSort, model);
		
		
		for(Entity e: toSort)
		{
			System.out.println(e.getName());
		}
		
		
	}

}
